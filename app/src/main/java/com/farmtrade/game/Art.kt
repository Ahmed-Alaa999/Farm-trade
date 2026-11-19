package com.farmtrade.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

private val BROWN = Color(0xFF5D4037)
private val DK = Color(0x33000000)
private val HI = Color(0x55FFFFFF)

fun DrawScope.leaf(x: Float, y: Float, l: Float, ang: Float, col: Color) {
    rotate(ang, Offset(x, y)) {
        val p = Path().apply {
            moveTo(x, y)
            quadraticBezierTo(x + l * 0.5f, y - l * 0.35f, x + l, y)
            quadraticBezierTo(x + l * 0.5f, y + l * 0.35f, x, y)
            close()
        }
        drawPath(p, col)
    }
}

fun DrawScope.drawPlant(pl: Plant, cx: Float, cy: Float, r: Float, tint: Float = 0f) {
    val a = lerp(Color(pl.c1), Color(0xFF8D7B68), tint)
    val b = lerp(Color(pl.c2), Color(0xFF6D5E4B), tint)
    when (pl.shape) {
        0 -> {
            drawCircle(a, r * 0.72f, Offset(cx, cy + r * 0.12f))
            drawCircle(HI, r * 0.15f, Offset(cx - r * 0.28f, cy - r * 0.05f))
            drawLine(BROWN, Offset(cx, cy - r * 0.55f), Offset(cx + r * 0.06f, cy - r * 0.85f), r * 0.09f, StrokeCap.Round)
            leaf(cx + r * 0.04f, cy - r * 0.7f, r * 0.55f, -25f, b)
        }
        1 -> {
            drawOval(a, Offset(cx - r * 0.85f, cy - r * 0.55f), Size(r * 1.7f, r * 1.1f))
            drawOval(HI, Offset(cx - r * 0.5f, cy - r * 0.35f), Size(r * 0.5f, r * 0.25f))
            leaf(cx, cy - r * 0.5f, r * 0.5f, -40f, b)
        }
        2 -> {
            val p = Path().apply {
                moveTo(cx - r * 0.8f, cy - r * 0.1f)
                quadraticBezierTo(cx, cy + r * 1.0f, cx + r * 0.8f, cy - r * 0.5f)
                quadraticBezierTo(cx + r * 0.1f, cy + r * 0.45f, cx - r * 0.8f, cy - r * 0.1f)
                close()
            }
            drawPath(p, a)
            drawCircle(b, r * 0.09f, Offset(cx + r * 0.8f, cy - r * 0.5f))
            drawCircle(b, r * 0.09f, Offset(cx - r * 0.8f, cy - r * 0.1f))
        }
        3 -> {
            for ((dx, dy) in listOf(-0.4f to -0.3f, 0f to -0.3f, 0.4f to -0.3f, -0.2f to 0.05f, 0.2f to 0.05f, 0f to 0.4f)) {
                drawCircle(a, r * 0.27f, Offset(cx + dx * r, cy + dy * r))
                drawCircle(HI, r * 0.07f, Offset(cx + dx * r - r * 0.08f, cy + dy * r - r * 0.08f))
            }
            drawLine(BROWN, Offset(cx, cy - r * 0.55f), Offset(cx, cy - r * 0.85f), r * 0.09f, StrokeCap.Round)
            leaf(cx, cy - r * 0.7f, r * 0.55f, -20f, b)
        }
        4 -> {
            val p = Path().apply {
                moveTo(cx - r * 0.75f, cy - r * 0.45f)
                quadraticBezierTo(cx, cy - r * 0.85f, cx + r * 0.75f, cy - r * 0.45f)
                quadraticBezierTo(cx + r * 0.65f, cy + r * 0.4f, cx, cy + r * 0.9f)
                quadraticBezierTo(cx - r * 0.65f, cy + r * 0.4f, cx - r * 0.75f, cy - r * 0.45f)
                close()
            }
            drawPath(p, a)
            for ((dx, dy) in listOf(-0.3f to -0.1f, 0.3f to -0.1f, 0f to 0.15f, -0.2f to 0.4f, 0.2f to 0.4f))
                drawCircle(Color(0xFFFFF59D), r * 0.05f, Offset(cx + dx * r, cy + dy * r))
            for (ang in listOf(-150f, -90f, -30f)) leaf(cx, cy - r * 0.55f, r * 0.5f, ang, b)
        }
        5 -> {
            val m = cy - r * 0.45f
            drawArc(b, 0f, 180f, true, Offset(cx - r, m - r), Size(r * 2f, r * 2f))
            drawArc(a, 0f, 180f, true, Offset(cx - r * 0.82f, m - r * 0.82f), Size(r * 1.64f, r * 1.64f))
            for ((dx, dy) in listOf(-0.4f to 0.3f, 0f to 0.5f, 0.4f to 0.3f))
                drawCircle(Color(0xFF212121), r * 0.06f, Offset(cx + dx * r, m + dy * r))
        }
        6 -> {
            for (ang in listOf(-125f, -108f, -90f, -72f, -55f)) leaf(cx, cy - r * 0.25f, r * 0.75f, ang, b)
            drawOval(a, Offset(cx - r * 0.52f, cy - r * 0.3f), Size(r * 1.04f, r * 1.2f))
            for (k in -1..1) drawLine(DK, Offset(cx + k * r * 0.3f, cy - r * 0.2f), Offset(cx + k * r * 0.3f + r * 0.15f, cy + r * 0.8f), 3f)
            drawLine(DK, Offset(cx - r * 0.4f, cy + r * 0.15f), Offset(cx + r * 0.4f, cy + r * 0.15f), 3f)
            drawLine(DK, Offset(cx - r * 0.4f, cy + r * 0.5f), Offset(cx + r * 0.4f, cy + r * 0.5f), 3f)
        }
        7 -> {
            val p = Path().apply {
                moveTo(cx, cy - r * 0.9f)
                cubicTo(cx + r * 0.2f, cy - r * 0.4f, cx + r * 0.75f, cy - r * 0.1f, cx + r * 0.6f, cy + r * 0.4f)
                cubicTo(cx + r * 0.45f, cy + r * 0.9f, cx - r * 0.45f, cy + r * 0.9f, cx - r * 0.6f, cy + r * 0.4f)
                cubicTo(cx - r * 0.75f, cy - r * 0.1f, cx - r * 0.2f, cy - r * 0.4f, cx, cy - r * 0.9f)
                close()
            }
            drawPath(p, a)
            drawCircle(HI, r * 0.12f, Offset(cx - r * 0.25f, cy + r * 0.2f))
            leaf(cx, cy - r * 0.85f, r * 0.45f, -50f, b)
        }
        8 -> {
            for (ang in listOf(-120f, -90f, -60f)) leaf(cx, cy - r * 0.5f, r * 0.65f, ang, b)
            val p = Path().apply {
                moveTo(cx - r * 0.42f, cy - r * 0.5f); lineTo(cx + r * 0.42f, cy - r * 0.5f)
                lineTo(cx, cy + r * 0.95f); close()
            }
            drawPath(p, a)
            drawLine(DK, Offset(cx - r * 0.25f, cy - r * 0.1f), Offset(cx - r * 0.05f, cy - r * 0.1f), 3f)
            drawLine(DK, Offset(cx + r * 0.05f, cy + r * 0.25f), Offset(cx + r * 0.2f, cy + r * 0.25f), 3f)
        }
        9 -> {
            leaf(cx, cy + r * 0.9f, r * 1.2f, -120f, b)
            leaf(cx, cy + r * 0.9f, r * 1.2f, -60f, b)
            drawOval(a, Offset(cx - r * 0.35f, cy - r * 0.85f), Size(r * 0.7f, r * 1.6f))
            for (i in 0..3) for (j in -1..1)
                drawCircle(DK, r * 0.06f, Offset(cx + j * r * 0.2f, cy - r * 0.55f + i * r * 0.35f))
        }
        10 -> rotate(-35f, Offset(cx, cy)) {
            drawRoundRect(a, Offset(cx - r * 0.95f, cy - r * 0.3f), Size(r * 1.9f, r * 0.6f), CornerRadius(r * 0.3f))
            drawRoundRect(HI, Offset(cx - r * 0.6f, cy - r * 0.2f), Size(r * 1.0f, r * 0.1f), CornerRadius(r * 0.05f))
            drawRoundRect(b, Offset(cx - r * 1.05f, cy - r * 0.14f), Size(r * 0.28f, r * 0.28f), CornerRadius(r * 0.08f))
        }
        11 -> {
            drawRoundRect(b, Offset(cx - r * 0.18f, cy + r * 0.1f), Size(r * 0.36f, r * 0.8f), CornerRadius(r * 0.1f))
            for ((dx, dy, rad) in listOf(Triple(-0.4f, -0.1f, 0.38f), Triple(0.4f, -0.1f, 0.38f),
                Triple(0f, -0.45f, 0.42f), Triple(-0.2f, 0.1f, 0.3f), Triple(0.2f, 0.1f, 0.3f)))
                drawCircle(a, r * rad, Offset(cx + dx * r, cy + dy * r))
        }
        12 -> {
            for ((i, ang) in listOf(-155f, -125f, -90f, -55f, -25f).withIndex())
                leaf(cx, cy + r * 0.7f, r * 1.3f, ang, if (i % 2 == 0) a else b)
        }
        13 -> {
            drawRoundRect(b, Offset(cx - r * 0.27f, cy - r * 0.05f), Size(r * 0.54f, r * 0.85f), CornerRadius(r * 0.15f))
            drawArc(a, 180f, 180f, true, Offset(cx - r * 0.9f, cy - r * 0.75f), Size(r * 1.8f, r * 1.5f))
            for ((dx, dy) in listOf(-0.4f to -0.3f, 0.1f to -0.5f, 0.45f to -0.2f))
                drawCircle(b, r * 0.1f, Offset(cx + dx * r, cy + dy * r))
        }
        14 -> rotate(-25f, Offset(cx, cy)) {
            drawRoundRect(a, Offset(cx - r * 0.85f, cy - r * 0.38f), Size(r * 1.7f, r * 0.76f), CornerRadius(r * 0.38f))
            drawRoundRect(DK, Offset(cx - r * 0.1f, cy - r * 0.38f), Size(r * 0.2f, r * 0.76f), CornerRadius(r * 0.08f))
            for (k in listOf(-0.5f, -0.25f, 0.3f, 0.55f)) drawCircle(b, r * 0.05f, Offset(cx + k * r, cy))
        }
        15 -> {
            drawOval(a, Offset(cx - r * 0.9f, cy - r * 0.5f), Size(r * 0.9f, r * 1.1f))
            drawOval(a, Offset(cx, cy - r * 0.5f), Size(r * 0.9f, r * 1.1f))
            drawOval(lerp(a, Color.White, 0.15f), Offset(cx - r * 0.4f, cy - r * 0.58f), Size(r * 0.8f, r * 1.25f))
            drawLine(DK, Offset(cx - r * 0.35f, cy - r * 0.4f), Offset(cx - r * 0.35f, cy + r * 0.45f), 3f)
            drawLine(DK, Offset(cx + r * 0.35f, cy - r * 0.4f), Offset(cx + r * 0.35f, cy + r * 0.45f), 3f)
            drawRoundRect(b, Offset(cx - r * 0.1f, cy - r * 0.85f), Size(r * 0.2f, r * 0.35f), CornerRadius(4f))
        }
        16 -> {
            val pr = if (pl.n <= 6) r * 0.36f else if (pl.n <= 10) r * 0.28f else r * 0.2f
            val dist = if (pl.n > 10) r * 0.62f else r * 0.55f
            for (i in 0 until pl.n) {
                val t = (2.0 * Math.PI * i / pl.n).toFloat()
                val c = Offset(cx + cos(t) * dist, cy + sin(t) * dist)
                drawCircle(a, pr, c)
                drawCircle(DK, pr, c, style = Stroke(2f))
            }
            drawCircle(b, if (pl.n > 10) r * 0.4f else r * 0.27f, Offset(cx, cy))
        }
        17 -> {
            drawLine(b, Offset(cx, cy + r * 0.9f), Offset(cx, cy + r * 0.1f), r * 0.1f, StrokeCap.Round)
            leaf(cx, cy + r * 0.8f, r * 0.8f, -55f, b)
            leaf(cx, cy + r * 0.8f, r * 0.8f, -125f, b)
            val p = Path().apply {
                moveTo(cx - r * 0.5f, cy - r * 0.5f); lineTo(cx - r * 0.5f, cy)
                quadraticBezierTo(cx, cy + r * 0.55f, cx + r * 0.5f, cy)
                lineTo(cx + r * 0.5f, cy - r * 0.5f); lineTo(cx + r * 0.25f, cy - r * 0.25f)
                lineTo(cx, cy - r * 0.55f); lineTo(cx - r * 0.25f, cy - r * 0.25f); close()
            }
            drawPath(p, a)
        }
        19 -> {
            drawLine(b, Offset(cx, cy + r * 0.9f), Offset(cx, cy + r * 0.2f), r * 0.08f, StrokeCap.Round)
            for ((dx, dy) in listOf(-0.3f to -0.3f, 0.3f to -0.3f, -0.3f to 0.15f, 0.3f to 0.15f))
                drawCircle(a, r * 0.36f, Offset(cx + dx * r, cy + dy * r))
            drawCircle(b, r * 0.1f, Offset(cx, cy - r * 0.07f))
        }
        20 -> {
            drawLine(b, Offset(cx, cy + r * 0.9f), Offset(cx, cy - r * 0.8f), r * 0.08f, StrokeCap.Round)
            for (i in 0..3) {
                val y = cy + r * 0.6f - i * r * 0.45f
                leaf(cx, y, r * 0.6f, -35f, a); leaf(cx, y, r * 0.6f, -145f, a)
            }
            leaf(cx, cy - r * 0.8f, r * 0.5f, -90f, a)
        }
        21 -> {
            drawLine(b, Offset(cx, cy + r * 0.9f), Offset(cx, cy - r * 0.6f), r * 0.07f, StrokeCap.Round)
            for (i in 0..4) {
                val y = cy - r * 0.65f + i * r * 0.32f
                drawOval(a, Offset(cx - r * 0.38f, y - r * 0.1f), Size(r * 0.3f, r * 0.2f))
                drawOval(a, Offset(cx + r * 0.08f, y - r * 0.1f), Size(r * 0.3f, r * 0.2f))
            }
            drawOval(a, Offset(cx - r * 0.1f, cy - r * 0.95f), Size(r * 0.2f, r * 0.4f))
        }
        22 -> {
            drawLine(BROWN, Offset(cx - r * 0.4f, cy + r * 0.2f), Offset(cx, cy - r * 0.75f), r * 0.06f, StrokeCap.Round)
            drawLine(BROWN, Offset(cx + r * 0.35f, cy + r * 0.25f), Offset(cx, cy - r * 0.75f), r * 0.06f, StrokeCap.Round)
            drawCircle(a, r * 0.38f, Offset(cx - r * 0.4f, cy + r * 0.4f))
            drawCircle(a, r * 0.38f, Offset(cx + r * 0.35f, cy + r * 0.45f))
            drawCircle(HI, r * 0.08f, Offset(cx - r * 0.5f, cy + r * 0.3f))
            drawCircle(HI, r * 0.08f, Offset(cx + r * 0.25f, cy + r * 0.35f))
            leaf(cx, cy - r * 0.75f, r * 0.5f, -20f, b)
        }
        23 -> {
            drawOval(a, Offset(cx - r * 0.85f, cy - r * 0.5f), Size(r * 1.7f, r * 1.05f))
            for ((dx, dy) in listOf(-0.4f to -0.1f, 0.1f to 0.15f, 0.45f to -0.15f, -0.1f to -0.25f))
                drawCircle(b, r * 0.06f, Offset(cx + dx * r, cy + dy * r))
        }
        else -> drawCircle(a, r * 0.6f, Offset(cx, cy))
    }
}

fun DrawScope.drawSprout(cx: Float, cy: Float, r: Float, g: Float) {
    val h = r * (0.3f + 0.7f * g)
    drawLine(Color(0xFF558B2F), Offset(cx, cy + r * 0.5f), Offset(cx, cy + r * 0.5f - h), r * 0.08f, StrokeCap.Round)
    leaf(cx, cy + r * 0.5f - h * 0.8f, r * 0.5f * (0.5f + g), -30f, Color(0xFF7CB342))
    leaf(cx, cy + r * 0.5f - h * 0.6f, r * 0.5f * (0.5f + g), -150f, Color(0xFF9CCC65))
}

fun DrawScope.drawDead(cx: Float, cy: Float, r: Float) {
    val c = Color(0xFF8D6E63)
    val p = Path().apply {
        moveTo(cx, cy + r * 0.6f)
        quadraticBezierTo(cx, cy - r * 0.4f, cx + r * 0.5f, cy - r * 0.2f)
    }
    drawPath(p, c, style = Stroke(r * 0.1f, cap = StrokeCap.Round))
    leaf(cx + r * 0.5f, cy - r * 0.2f, r * 0.4f, 60f, Color(0xFF6D4C41))
    leaf(cx, cy + r * 0.2f, r * 0.4f, 150f, Color(0xFF795548))
}

fun DrawScope.drawDrop(cx: Float, cy: Float, r: Float, col: Color) {
    val p = Path().apply {
        moveTo(cx, cy - r)
        cubicTo(cx + r * 0.9f, cy + r * 0.1f, cx + r * 0.6f, cy + r * 0.9f, cx, cy + r * 0.9f)
        cubicTo(cx - r * 0.6f, cy + r * 0.9f, cx - r * 0.9f, cy + r * 0.1f, cx, cy - r)
        close()
    }
    drawPath(p, col)
}

fun DrawScope.drawBug(cx: Float, cy: Float, r: Float) {
    val d = Color(0xFF3E2723)
    for (k in listOf(-1f, 0f, 1f)) {
        drawLine(d, Offset(cx - r * 0.4f, cy + k * r * 0.4f), Offset(cx - r * 1.1f, cy + k * r * 0.7f), 3f)
        drawLine(d, Offset(cx + r * 0.4f, cy + k * r * 0.4f), Offset(cx + r * 1.1f, cy + k * r * 0.7f), 3f)
    }
    drawOval(Color(0xFF8E1B1B), Offset(cx - r * 0.55f, cy - r * 0.5f), Size(r * 1.1f, r * 1.3f))
    drawLine(d, Offset(cx, cy - r * 0.4f), Offset(cx, cy + r * 0.75f), 2f)
    drawCircle(d, r * 0.38f, Offset(cx, cy - r * 0.65f))
    drawLine(d, Offset(cx - r * 0.15f, cy - r * 0.9f), Offset(cx - r * 0.5f, cy - r * 1.3f), 2f)
    drawLine(d, Offset(cx + r * 0.15f, cy - r * 0.9f), Offset(cx + r * 0.5f, cy - r * 1.3f), 2f)
}

fun DrawScope.drawRat(cx: Float, cy: Float, r: Float) {
    val g = Color(0xFF9E9E9E); val pink = Color(0xFFF8BBD0)
    val tail = Path().apply {
        moveTo(cx + r * 0.6f, cy + r * 0.2f)
        quadraticBezierTo(cx + r * 1.2f, cy + r * 0.8f, cx + r * 1.3f, cy - r * 0.1f)
    }
    drawPath(tail, pink, style = Stroke(r * 0.12f, cap = StrokeCap.Round))
    drawOval(g, Offset(cx - r * 0.4f, cy - r * 0.35f), Size(r * 1.2f, r * 0.8f))
    drawCircle(g, r * 0.38f, Offset(cx - r * 0.55f, cy - r * 0.05f))
    drawCircle(pink, r * 0.16f, Offset(cx - r * 0.35f, cy - r * 0.4f))
    drawCircle(Color(0xFF212121), r * 0.06f, Offset(cx - r * 0.68f, cy - r * 0.12f))
    drawCircle(Color(0xFFE91E63), r * 0.07f, Offset(cx - r * 0.92f, cy + r * 0.02f))
}

fun DrawScope.drawWrench(cx: Float, cy: Float, r: Float) {
    val g = Color(0xFFCFD8DC)
    rotate(45f, Offset(cx, cy)) {
        drawLine(g, Offset(cx, cy + r * 0.9f), Offset(cx, cy - r * 0.2f), r * 0.3f, StrokeCap.Round)
        drawCircle(g, r * 0.4f, Offset(cx, cy - r * 0.55f), style = Stroke(r * 0.22f))
    }
}

fun DrawScope.drawBolt(cx: Float, cy: Float, r: Float) {
    val p = Path().apply {
        moveTo(cx + r * 0.2f, cy - r); lineTo(cx - r * 0.5f, cy + r * 0.1f); lineTo(cx - r * 0.05f, cy + r * 0.1f)
        lineTo(cx - r * 0.2f, cy + r); lineTo(cx + r * 0.5f, cy - r * 0.15f); lineTo(cx + r * 0.05f, cy - r * 0.15f); close()
    }
    drawPath(p, Color(0xFFFFEB3B))
}

fun DrawScope.drawPipe(cx: Float, cy: Float, r: Float) {
    drawRoundRect(Color(0xFFB0BEC5), Offset(cx - r, cy - r * 0.8f), Size(r * 2f, r * 0.5f), CornerRadius(5f))
    drawRect(Color(0xFF78909C), Offset(cx - r * 0.15f, cy - r * 0.9f), Size(r * 0.3f, r * 0.7f))
    drawDrop(cx, cy + r * 0.2f, r * 0.5f, Color(0xFF29B6F6))
}

fun DrawScope.drawCoin(cx: Float, cy: Float, r: Float) {
    drawCircle(Color(0xFFFFB300), r, Offset(cx, cy))
    drawCircle(Color(0xFFFFE082), r * 0.72f, Offset(cx, cy), style = Stroke(r * 0.14f))
    drawCircle(Color(0xFFFFCA28), r * 0.3f, Offset(cx, cy))
}

fun DrawScope.drawBasket(x: Float, y: Float, w: Float, h: Float) {
    val p = Path().apply {
        moveTo(x, y); lineTo(x + w, y); lineTo(x + w * 0.85f, y + h); lineTo(x + w * 0.15f, y + h); close()
    }
    drawPath(p, Color(0xFF9C7A4F))
    for (k in 1..2) drawLine(Color(0x55000000), Offset(x + w * 0.04f * k, y + h * k / 3f), Offset(x + w - w * 0.04f * k, y + h * k / 3f), 3f)
    for (k in 1..3) drawLine(Color(0x33000000), Offset(x + w * k / 4f, y), Offset(x + w * (0.15f + 0.7f * k / 4f), y + h), 2f)
    drawRoundRect(Color(0xFF6D4C41), Offset(x - w * 0.03f, y - h * 0.08f), Size(w * 1.06f, h * 0.22f), CornerRadius(6f))
}

@Composable
fun PlantIcon(p: Plant, sz: Dp = 28.dp) {
    Canvas(Modifier.size(sz)) { drawPlant(p, this.size.width / 2f, this.size.height / 2f, this.size.width * 0.42f) }
}

@Composable
fun CoinIcon(sz: Dp = 18.dp) {
    Canvas(Modifier.size(sz)) { drawCoin(this.size.width / 2f, this.size.height / 2f, this.size.width * 0.45f) }
}

/** kind: 1 جرذ، 2 مفتاح، 3 صاعقة، 4 ماسورة */
@Composable
fun ProblemIcon(kind: Int, sz: Dp = 24.dp) {
    Canvas(Modifier.size(sz)) {
        val c = this.size.width / 2f; val r = this.size.width * 0.38f
        when (kind) { 1 -> drawRat(c, c, r); 2 -> drawWrench(c, c, r); 3 -> drawBolt(c, c, r); else -> drawPipe(c, c, r) }
    }
}

/** kind: 0 مبيد، 1 فخ، 2 نظام ري */
@Composable
fun ToolIcon(kind: Int, sz: Dp = 32.dp) {
    Canvas(Modifier.size(sz)) {
        val w = this.size.width; val h = this.size.height
        when (kind) {
            0 -> {
                drawRoundRect(Color(0xFF42A5F5), Offset(w * 0.2f, h * 0.4f), Size(w * 0.4f, h * 0.5f), CornerRadius(6f))
                drawRect(Color(0xFF616161), Offset(w * 0.3f, h * 0.25f), Size(w * 0.2f, h * 0.15f))
                drawLine(Color(0xFF616161), Offset(w * 0.5f, h * 0.3f), Offset(w * 0.8f, h * 0.3f), 5f)
                for (k in 0..2) drawCircle(Color(0xFF90CAF9), 2.5f, Offset(w * 0.85f + k * 2f, h * (0.2f + k * 0.1f)))
            }
            1 -> {
                drawRoundRect(Color(0xFF8D6E63), Offset(w * 0.1f, h * 0.55f), Size(w * 0.8f, h * 0.3f), CornerRadius(4f))
                drawLine(Color(0xFFB0BEC5), Offset(w * 0.2f, h * 0.55f), Offset(w * 0.2f, h * 0.25f), 4f)
                drawLine(Color(0xFFB0BEC5), Offset(w * 0.2f, h * 0.25f), Offset(w * 0.8f, h * 0.25f), 4f)
                drawLine(Color(0xFFB0BEC5), Offset(w * 0.8f, h * 0.25f), Offset(w * 0.8f, h * 0.55f), 4f)
                drawCircle(Color(0xFFFFCC80), w * 0.07f, Offset(w * 0.5f, h * 0.7f))
            }
            else -> drawPipe(w / 2f, h / 2f, w * 0.4f)
        }
    }
}

@Composable
fun BasketView(b: Basket, modifier: Modifier) {
    Canvas(modifier) {
        val w = this.size.width; val h = this.size.height; val top = h * 0.6f
        if (b.plant >= 0) {
            val pl = Plants.all[b.plant]; val rr = w * 0.1f
            for (i in 0 until minOf(b.count, 12)) {
                val col = i % 4; val row = i / 4
                drawPlant(pl, w * 0.2f + col * w * 0.2f + (row % 2) * w * 0.05f, top - row * rr * 1.0f, rr)
            }
        }
        drawBasket(0f, top, w, h - top)
    }
}