package com.farmtrade.game

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.runtime.*
import kotlin.random.Random

class Db(c: Context) : SQLiteOpenHelper(c, "farm.db", null, 1) {
    override fun onCreate(d: SQLiteDatabase) { d.execSQL("CREATE TABLE kv(k TEXT PRIMARY KEY, v TEXT)") }
    override fun onUpgrade(d: SQLiteDatabase, o: Int, n: Int) {}
    fun put(k: String, v: String) {
        writableDatabase.insertWithOnConflict("kv", null,
            ContentValues().apply { put("k", k); put("v", v) }, SQLiteDatabase.CONFLICT_REPLACE)
    }
    fun get(k: String): String? =
        readableDatabase.rawQuery("SELECT v FROM kv WHERE k=?", arrayOf(k)).use { if (it.moveToFirst()) it.getString(0) else null }
}

data class Plot(val plant: Int = -1, val planted: Long = 0, val watered: Long = 0, val pests: Long = 0)
data class Basket(val plant: Int = -1, val count: Int = 0)
data class Order(val plant: Int, val qty: Int, val price: Int)
/** kind: 1 جرذان، 2 أداة معطلة، 3 انقطاع كهرباء، 4 انفجار ماسورة */
class Problem(val kind: Int, val at: Long)

class Game(ctx: Context) {
    private val db = Db(ctx)
    var sfx: (String) -> Unit = {}
    var coins by mutableStateOf(100)
    var now by mutableStateOf(System.currentTimeMillis())
    var paused by mutableStateOf(false)
    var msg by mutableStateOf("")
    var irrigation by mutableStateOf(false)
    var sprayers by mutableStateOf(0)
    var traps by mutableStateOf(0)
    val plots = mutableStateListOf<Plot>()
    val baskets = mutableStateListOf<Basket>()
    val seeds = mutableStateMapOf<Int, Int>()
    val problems = mutableStateListOf<Problem>()
    val orders = mutableStateListOf<Order>()
    private var ticks = 0
    private val CAP = 30
    private val PEST_MS = 10 * 60_000L
    private val POWER_MS = 4 * 60_000L

    val irrigationOn get() = irrigation && problems.none { it.kind == 3 || it.kind == 4 }

    init {
        try {
            coins = db.get("coins")?.toIntOrNull() ?: 100
            irrigation = db.get("irr") == "true"
            sprayers = db.get("spr")?.toIntOrNull() ?: 0
            traps = db.get("trap")?.toIntOrNull() ?: 0
            val pl = db.get("plots")?.split(";")?.filter { it.isNotBlank() }?.map {
                val a = it.split(","); Plot(a[0].toInt(), a[1].toLong(), a[2].toLong(), a[3].toLong()) }
            val bk = db.get("baskets")?.split(";")?.filter { it.isNotBlank() }?.map {
                val a = it.split(","); Basket(a[0].toInt(), a[1].toInt()) }
            repeat(12) { plots.add(pl?.getOrNull(it) ?: Plot()) }
            repeat(10) { baskets.add(bk?.getOrNull(it) ?: Basket()) }
            val sd = db.get("seeds")
            if (sd == null) { seeds[0] = 3; seeds[14] = 3; seeds[28] = 3 }
            else sd.split(";").filter { it.isNotBlank() }.forEach { val a = it.split(":"); seeds[a[0].toInt()] = a[1].toInt() }
            db.get("prob")?.split(";")?.filter { it.isNotBlank() }?.forEach {
                val a = it.split(","); problems.add(Problem(a[0].toInt(), a[1].toLong())) }
        } catch (e: Exception) {
            plots.clear(); baskets.clear(); repeat(12) { plots.add(Plot()) }; repeat(10) { baskets.add(Basket()) }
        }
    }

    fun save() {
        db.put("coins", coins.toString()); db.put("irr", irrigation.toString())
        db.put("spr", sprayers.toString()); db.put("trap", traps.toString())
        db.put("plots", plots.joinToString(";") { "${it.plant},${it.planted},${it.watered},${it.pests}" })
        db.put("baskets", baskets.joinToString(";") { "${it.plant},${it.count}" })
        db.put("seeds", seeds.entries.joinToString(";") { "${it.key}:${it.value}" })
        db.put("prob", problems.joinToString(";") { "${it.kind},${it.at}" })
    }

    private fun status(p: Plot): Int {
        val pl = Plants.all[p.plant]
        return Native.calc(now, p.planted, p.watered, pl.growMin * 60_000L, pl.wiltMin * 60_000L)
    }
    fun statusOf(p: Plot) = status(p)

    fun tick() {
        now = System.currentTimeMillis()
        if (irrigationOn) for (i in plots.indices) {
            val p = plots[i]; if (p.plant >= 0 && status(p) / 1000 != 3) plots[i] = p.copy(watered = now)
        }
        for (i in plots.indices) { val p = plots[i]; if (p.pests > 0 && now - p.pests > PEST_MS) plots[i] = Plot() }
        problems.removeAll { it.kind == 3 && now - it.at > POWER_MS }
        if (!paused) rollEvent()
        if (problems.any { it.kind == 1 } && ticks % 45 == 0) {
            val full = baskets.indices.filter { baskets[it].count > 0 }
            if (full.isNotEmpty()) { val k = full.random(); val b = baskets[k]
                baskets[k] = if (b.count <= 1) Basket() else b.copy(count = b.count - 1) }
        }
        if (orders.size < 3 && Random.nextInt(15) == 0) {
            val pl = Plants.all.random(); val q = Random.nextInt(2, 9)
            orders.add(Order(pl.id, q, (q * pl.sell * (1.0 + Random.nextDouble() * 0.5)).toInt()))
        }
        if (++ticks % 5 == 0) save()
    }

    private fun rollEvent() {
        if (Random.nextInt(150) != 0) return
        val k = Random.nextInt(5)
        if (k != 0 && problems.any { it.kind == k }) return
        when (k) {
            0 -> { val idx = plots.indices.filter { plots[it].plant >= 0 }.shuffled().take(3)
                if (idx.isEmpty()) return
                idx.forEach { plots[it] = plots[it].copy(pests = now) }
                msg = "حشرات هاجمت المزرعة! رشّ المبيد قبل 10 دقائق" }
            1 -> { if (baskets.none { it.count > 0 }) return
                problems.add(Problem(1, now)); msg = "جرذان تسرق المحصول! استخدم فخاً" }
            2 -> { problems.add(Problem(2, now)); msg = "تعطلت أداة الزراعة" }
            3 -> { if (!irrigation) return
                problems.add(Problem(3, now)); msg = "انقطعت الكهرباء عن نظام الري" }
            4 -> { if (!irrigation) return
                problems.add(Problem(4, now)); msg = "انفجرت ماسورة المياه!" }
        }
        sfx("alarm")
    }

    fun fix(p: Problem) {
        when (p.kind) {
            1 -> if (traps > 0) { traps--; problems.remove(p); msg = "تم الإمساك بالجرذان" } else msg = "تحتاج فخاً من المتجر"
            2 -> pay(30) { problems.remove(p) }
            4 -> pay(40) { problems.remove(p) }
            3 -> msg = "انتظر عودة الكهرباء"
        }
    }

    private fun pay(c: Int, f: () -> Unit) {
        if (coins >= c) { coins -= c; f(); sfx("buy") } else msg = "لا تملك عملات كافية"
    }

    fun interact(i: Int) {
        val p = plots[i]; val pl = Plants.all[p.plant]
        val st = status(p); val s = st / 1000; val pct = st % 1000
        when {
            s == 3 -> { plots[i] = Plot(); msg = "أُزيلت النبتة الذابلة" }
            p.pests > 0 -> if (sprayers > 0) { sprayers--; plots[i] = p.copy(pests = 0); sfx("water"); msg = "تم رش المبيد" }
                           else msg = "حشرات! تحتاج مبيداً من المتجر"
            pct >= 100 -> harvest(i, pl)
            else -> { plots[i] = p.copy(watered = now); sfx("water"); msg = "تم السقي" }
        }
    }

    private fun harvest(i: Int, pl: Plant) {
        var bi = baskets.indexOfFirst { it.plant == pl.id && it.count < CAP }
        if (bi < 0) bi = baskets.indexOfFirst { it.plant == -1 }
        if (bi < 0) { msg = "لا توجد سلة مناسبة"; return }
        val b = baskets[bi]
        baskets[bi] = Basket(pl.id, minOf(CAP, b.count + 3))
        plots[i] = Plot(); sfx("harvest")
    }

    fun plant(i: Int, id: Int) {
        val n = seeds[id] ?: 0; if (n <= 0) return
        seeds[id] = n - 1; plots[i] = Plot(id, now, now, 0); sfx("plant")
    }

    fun buySeed(id: Int) { pay(Plants.all[id].seed) { seeds[id] = (seeds[id] ?: 0) + 1 } }

    fun buyTool(t: Int) {
        when (t) {
            0 -> pay(25) { sprayers++ }
            1 -> pay(30) { traps++ }
            else -> if (irrigation) msg = "نظام الري مثبت بالفعل" else pay(150) { irrigation = true }
        }
    }

    fun sell(o: Order) {
        if (baskets.filter { it.plant == o.plant }.sumOf { it.count } < o.qty) { msg = "ليس لديك كمية كافية"; return }
        var left = o.qty
        for (i in baskets.indices) { val b = baskets[i]
            if (b.plant == o.plant && left > 0) { val t = minOf(left, b.count); left -= t
                baskets[i] = if (b.count - t <= 0) Basket() else b.copy(count = b.count - t) } }
        coins += o.price; orders.remove(o); sfx("sell")
    }
}