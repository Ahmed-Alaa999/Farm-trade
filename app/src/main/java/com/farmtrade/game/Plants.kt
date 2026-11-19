package com.farmtrade.game

/** shape = رقم شكل الرسم، c1/c2 ألوان، n = عدد البتلات، kind: 0 فاكهة 1 خضار 2 ورد */
data class Plant(val id: Int, val name: String, val shape: Int, val c1: Long, val c2: Long,
                 val n: Int, val kind: Int, val growMin: Int, val wiltMin: Int, val seed: Int) {
    val sell get() = seed * 2 + 2
}

object Plants {
    // الاسم|الشكل|لون1|لون2|بتلات|النوع|نمو(د)|ذبول(د)|سعر البذرة
    private val raw = listOf(
        "تفاح|0|E53935|43A047|0|0|12|300|10", "موز|2|FDD835|6D4C41|0|0|10|240|9",
        "عنب|3|7B1FA2|43A047|0|0|14|200|12", "فراولة|4|D32F2F|43A047|0|0|8|150|8",
        "برتقال|0|FB8C00|43A047|0|0|13|360|11", "ليمون|1|FDD835|689F38|0|0|11|420|9",
        "بطيخ|5|E53935|2E7D32|0|0|20|180|18", "خوخ|0|FFAB91|43A047|0|0|15|220|14",
        "كرز|22|B71C1C|43A047|0|0|9|160|10", "أناناس|6|F9A825|2E7D32|0|0|25|480|25",
        "مانجو|1|FFA726|43A047|0|0|22|400|22", "كيوي|1|8D6E63|7CB342|0|0|16|260|16",
        "شمام|1|FFCC80|43A047|0|0|18|200|17", "كمثرى|7|C0CA33|43A047|0|0|12|320|10",
        "طماطم|0|F44336|2E7D32|0|1|6|240|6", "جزر|8|EF6C00|2E7D32|0|1|5|480|5",
        "ذرة|9|FDD835|558B2F|0|1|9|300|8", "بطاطس|23|BCAAA4|795548|0|1|7|600|6",
        "فلفل|10|C62828|2E7D32|0|1|8|260|9", "خيار|10|43A047|2E7D32|0|1|5|180|5",
        "بروكلي|11|2E7D32|8BC34A|0|1|10|200|10", "ثوم|7|ECEFF1|9CCC65|0|1|6|540|7",
        "بصل|7|CE93D8|689F38|0|1|6|560|6", "باذنجان|7|4A148C|2E7D32|0|1|9|280|9",
        "خس|12|9CCC65|558B2F|0|1|4|120|4", "فطر|13|8D6E63|ECEFF1|0|1|5|100|8",
        "فول سوداني|14|D7B98E|8D6E63|0|1|11|420|11", "قرع|15|F57C00|2E7D32|0|1|15|500|15",
        "وردة|16|C2185B|F9A825|8|2|8|90|12", "توليب|17|E91E63|43A047|0|2|6|80|10",
        "عباد الشمس|16|FDD835|4E342E|14|2|10|150|9", "زهرة الكرز|16|F8BBD0|E91E63|5|2|9|100|11",
        "بابونج|16|FFFFFF|FBC02D|10|2|5|70|6", "ياسمين|16|FAFAFA|FFE082|6|2|7|75|8",
        "هيبيسكس|16|FF4081|FFEB3B|5|2|8|95|10", "قرنفل|16|FF7043|BF360C|9|2|7|110|9",
        "باقة|16|BA68C8|FFD54F|7|2|12|85|15", "نفل|19|43A047|2E7D32|0|2|4|130|5",
        "ريحان|20|66BB6A|2E7D32|0|2|3|160|4", "سنبل|21|E6C84C|689F38|0|2|6|120|7"
    )
    val all: List<Plant> = raw.mapIndexed { i, s ->
        val p = s.split("|")
        Plant(i, p[0], p[1].toInt(), p[2].toLong(16) or 0xFF000000L, p[3].toLong(16) or 0xFF000000L,
            p[4].toInt(), p[5].toInt(), p[6].toInt(), p[7].toInt(), p[8].toInt())
    }
}

object Native {
    private var ok = false
    init { try { System.loadLibrary("farmcore"); ok = true } catch (_: Throwable) {} }
    private external fun status(now: Long, planted: Long, watered: Long, grow: Long, wilt: Long): Int

    fun calc(now: Long, planted: Long, watered: Long, grow: Long, wilt: Long): Int {
        if (ok) try { return status(now, planted, watered, grow, wilt) } catch (_: Throwable) {}
        val dry = now - watered
        val pct = ((now - planted) * 100 / maxOf(grow, 1)).coerceIn(0, 100).toInt()
        val s = when { dry > wilt -> 3; dry > wilt / 2 -> 2; pct >= 100 -> 1; else -> 0 }
        return s * 1000 + pct
    }
}