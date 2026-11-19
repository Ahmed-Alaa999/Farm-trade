package com.farmtrade.game

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private var game: Game? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val g = Game(applicationContext); g.sfx = Sfx(this)::play; game = g
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                MaterialTheme {
                    var isLoading by remember { mutableStateOf(true) }
                    if (isLoading) {
                        SplashScreen(onFinished = { isLoading = false })
                    } else {
                        GameScreen(g)
                    }
                }
            }
        }
    }
    override fun onPause() { super.onPause(); game?.save() }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var startAnim by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1800),
        label = "loading"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1B5E20), Color(0xFF388E3C), Color(0xFF689F38))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // شعار مرئي مرسوم من عناصر اللعبة
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlantIcon(Plants.all[0], 54.dp)
                    Spacer(Modifier.width(4.dp))
                    CoinIcon(48.dp)
                }
            }

            Text(
                text = "Farm trading",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "ازرع • احصد • تاجر",
                fontSize = 16.sp,
                color = Color(0xFFDCEDC8)
            )

            Spacer(Modifier.height(20.dp))

            // شريط التحميل
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(220.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFFFFD54F),
                trackColor = Color(0x44000000)
            )

            Text(
                text = "جاري تجهيز المزرعة... ${(progress * 100).toInt()}%",
                fontSize = 13.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun Coins(n: Int, color: Color = Color.White, fs: TextUnit = 18.sp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CoinIcon(20.dp); Spacer(Modifier.width(4.dp)); Text("$n", color = color, fontSize = fs)
    }
}

@Composable
fun GameScreen(g: Game) {
    var dialog by remember { mutableStateOf("") }
    var plotIdx by remember { mutableStateOf(-1) }
    LaunchedEffect(Unit) { while (true) { g.tick(); delay(1000) } }
    LaunchedEffect(g.msg) { if (g.msg.isNotEmpty()) { delay(3500); g.msg = "" } }

    Box(Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            GLSurfaceView(ctx).apply {
                setEGLContextClientVersion(2); setRenderer(SkyRenderer())
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            }
        }, modifier = Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Coins(g.coins, Color.White, 22.sp)
                Spacer(Modifier.weight(1f))
                DrawnIcon(0) { g.paused = true; dialog = "pause" }
                Spacer(Modifier.width(6.dp))
                DrawnIcon(1) { dialog = "shop" }
                Spacer(Modifier.width(6.dp))
                DrawnIcon(2) { dialog = "settings" }
            }
            if (g.msg.isNotEmpty()) Text(g.msg, color = Color.White,
                modifier = Modifier.background(Color(0x99000000)).padding(6.dp))

            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                g.problems.toList().forEach { p ->
                    val label = when (p.kind) { 1 -> "جرذان: ضع فخاً"; 2 -> "أداة معطلة: إصلاح 30"; 3 -> "انقطاع الكهرباء"; else -> "ماسورة: إصلاح 40" }
                    Button(onClick = { g.fix(p) }, contentPadding = PaddingValues(10.dp, 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))) {
                        ProblemIcon(p.kind, 24.dp); Spacer(Modifier.width(6.dp)); Text(label)
                    }
                }
            }

            Farm(g, Modifier.weight(1f).fillMaxWidth()) { i ->
                val p = g.plots[i]
                if (p.plant < 0) {
                    if (g.problems.any { it.kind == 2 }) g.msg = "الأداة معطلة، أصلحها أولاً" else plotIdx = i
                } else g.interact(i)
            }

            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (g.orders.isEmpty()) Text("بانتظار المشترين...", color = Color.White)
                g.orders.toList().forEach { o ->
                    val pl = Plants.all[o.plant]
                    Card { Row(Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${o.qty}×")
                        PlantIcon(pl, 28.dp)
                        Text(" ${pl.name} = ${o.price}")
                        CoinIcon(16.dp)
                        Spacer(Modifier.width(6.dp))
                        Button(onClick = { g.sell(o) }, contentPadding = PaddingValues(8.dp, 2.dp)) { Text("بيع") }
                    } }
                }
            }

            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                g.baskets.forEach { b ->
                    Card(Modifier.width(96.dp).height(92.dp)) {
                        Column(Modifier.padding(4.dp)) {
                            BasketView(b, Modifier.fillMaxWidth().height(62.dp))
                            Text(if (b.plant >= 0) "${Plants.all[b.plant].name} ×${b.count}" else "فارغة", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (plotIdx >= 0) AlertDialog(onDismissRequest = { plotIdx = -1 },
        confirmButton = { TextButton(onClick = { plotIdx = -1 }) { Text("إغلاق") } },
        title = { Text("اختر بذرة") },
        text = {
            val owned = g.seeds.filter { it.value > 0 }.keys.sorted()
            if (owned.isEmpty()) Text("لا توجد بذور! اشترِ من المتجر")
            else LazyColumn(Modifier.heightIn(max = 350.dp)) {
                items(owned) { id -> val pl = Plants.all[id]
                    TextButton(onClick = { g.plant(plotIdx, id); plotIdx = -1 }) {
                        PlantIcon(pl, 32.dp); Spacer(Modifier.width(8.dp)); Text("${pl.name}  (×${g.seeds[id]})") } }
            }
        })

    if (dialog == "pause") AlertDialog(onDismissRequest = { g.paused = false; dialog = "" },
        confirmButton = { Button(onClick = { g.paused = false; dialog = "" }) { Text("متابعة") } },
        title = { Text("اللعبة متوقفة") },
        text = { Text("المشاكل العشوائية متوقفة، لكن الوقت الحقيقي يمر والنباتات ستذبل إن تُركت طويلاً.") })

    if (dialog == "settings") AlertDialog(onDismissRequest = { dialog = "" },
        confirmButton = { TextButton(onClick = { dialog = "" }) { Text("حسناً") } },
        title = { Text("طريقة اللعب") },
        text = { Text("""
• اضغط على أرض فارغة لزراعة بذرة.
• اضغط على نبتة لسقيها، وإذا اكتمل نموها (الشريط الأخضر ممتلئ) اضغط لحصادها داخل سلة.
• كل سلة تحمل نوعاً واحداً فقط (10 سلال)، وعدد الثمار المرسومة فيها يساوي كمية المحصول.
• النباتات تذبل بالوقت الحقيقي للهاتف حتى لو أغلقت اللعبة. الورد يذبل أسرع من الخضار والفاكهة. قطرة الماء فوق النبتة تعني أنها عطشانة.
• بِع المحصول للمشترين لتكسب العملات.
• من المتجر اشترِ البذور والمبيد والفخ ونظام الري (يسقي النباتات تلقائياً).
• المشاكل: حشرات (اضغط على النبتة المصابة لرشها)، جرذان (فخ)، أداة معطلة (إصلاح بعملات)، انقطاع كهرباء (انتظر)، انفجار ماسورة (إصلاح بعملات). الأخيران يعطلان الري.
""".trimIndent(), modifier = Modifier.verticalScroll(rememberScrollState())) })

    if (dialog == "shop") {
        var tab by remember { mutableStateOf(0) }
        AlertDialog(onDismissRequest = { dialog = "" },
            confirmButton = { TextButton(onClick = { dialog = "" }) { Text("إغلاق") } },
            title = { Row(verticalAlignment = Alignment.CenterVertically) {
                Text("المتجر"); Spacer(Modifier.weight(1f)); Coins(g.coins, Color.Black, 16.sp) } },
            text = { Column {
                Row { TextButton(onClick = { tab = 0 }) { Text("البذور") }; TextButton(onClick = { tab = 1 }) { Text("الأدوات") } }
                if (tab == 0) LazyColumn(Modifier.heightIn(max = 380.dp)) {
                    items(Plants.all) { p ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlantIcon(p, 30.dp); Spacer(Modifier.width(6.dp))
                            Text("${p.name} (${g.seeds[p.id] ?: 0})", Modifier.weight(1f))
                            Button(onClick = { g.buySeed(p.id) }, contentPadding = PaddingValues(8.dp, 2.dp)) {
                                Text("${p.seed}"); Spacer(Modifier.width(4.dp)); CoinIcon(14.dp) }
                        }
                    }
                } else Column {
                    val tools = listOf("مبيد حشري (${g.sprayers})" to 25, "فخ جرذان (${g.traps})" to 30,
                        (if (g.irrigation) "نظام الري (مثبت)" else "نظام الري التلقائي") to 150)
                    tools.forEachIndexed { i, (name, cost) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ToolIcon(i, 34.dp); Spacer(Modifier.width(6.dp))
                            Text(name, Modifier.weight(1f))
                            Button(onClick = { g.buyTool(i) }) { Text("$cost"); Spacer(Modifier.width(4.dp)); CoinIcon(14.dp) }
                        }
                    }
                }
            } })
    }
}

@Composable
fun Farm(g: Game, modifier: Modifier, onTap: (Int) -> Unit) {
    Canvas(modifier.pointerInput(Unit) {
        detectTapGestures { o ->
            val cw = size.width / 4f; val ch = size.height / 3f
            onTap((o.y / ch).toInt().coerceIn(0, 2) * 4 + (o.x / cw).toInt().coerceIn(0, 3))
        }
    }) {
        val cw = size.width / 4f; val ch = size.height / 3f
        for (i in 0 until 12) {
            val x = (i % 4) * cw; val y = (i / 4) * ch; val p = g.plots[i]
            val cx = x + cw / 2f; val cy = y + ch / 2f; val r = minOf(cw, ch) * 0.36f
            drawRoundRect(Color(0xFF4E7D34), Offset(x + 3f, y + 3f), Size(cw - 6f, ch - 6f), CornerRadius(26f))
            drawRoundRect(Color(0xFF6D4C41), Offset(x + 10f, y + 10f), Size(cw - 20f, ch - 20f), CornerRadius(20f))
            for (k in 1..3) {
                val ly = y + 10f + k * (ch - 20f) / 4f
                drawLine(Color(0x33000000), Offset(x + 22f, ly), Offset(x + cw - 22f, ly), 3f)
            }
            if (p.plant < 0) {
                drawLine(Color(0x66FFFFFF), Offset(cx - r * 0.35f, cy), Offset(cx + r * 0.35f, cy), 5f, StrokeCap.Round)
                drawLine(Color(0x66FFFFFF), Offset(cx, cy - r * 0.35f), Offset(cx, cy + r * 0.35f), 5f, StrokeCap.Round)
            } else {
                val pl = Plants.all[p.plant]
                val st = g.statusOf(p); val s = st / 1000; val pct = st % 1000
                if (s == 3) drawDead(cx, cy, r)
                else {
                    if (pct >= 100) drawCircle(Color(0x66FFEB3B), r * 1.1f, Offset(cx, cy))
                    if (pct < 35) drawSprout(cx, cy, r, pct / 35f)
                    else drawPlant(pl, cx, cy, r * (0.6f + 0.4f * pct / 100f), if (s == 2) 0.6f else 0f)
                    drawRoundRect(Color(0x55000000), Offset(x + 16f, y + ch - 26f), Size(cw - 32f, 8f), CornerRadius(4f))
                    drawRoundRect(Color(0xFF66BB6A), Offset(x + 16f, y + ch - 26f), Size((cw - 32f) * pct / 100f, 8f), CornerRadius(4f))
                }
                if (s == 2) drawDrop(x + cw * 0.2f, y + ch * 0.28f, cw * 0.07f, Color(0xFF29B6F6))
                if (p.pests > 0) drawBug(x + cw * 0.8f, y + ch * 0.28f, cw * 0.07f)
            }
        }
    }
}
