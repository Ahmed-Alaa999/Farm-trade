package com.farmtrade.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** kind: 0 توقف، 1 متجر، 2 إعدادات */
@Composable
fun DrawnIcon(kind: Int, onClick: () -> Unit) {
    Canvas(
        Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2E7D32)).clickable(onClick = onClick)
    ) {
        val s = size.width
        val w = Color.White
        val st = Stroke(width = s * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        when (kind) {
            0 -> {
                drawRoundRect(w, Offset(s * 0.30f, s * 0.25f), Size(s * 0.13f, s * 0.5f), CornerRadius(4f))
                drawRoundRect(w, Offset(s * 0.57f, s * 0.25f), Size(s * 0.13f, s * 0.5f), CornerRadius(4f))
            }
            1 -> {
                val p = Path().apply {
                    moveTo(s * 0.18f, s * 0.25f); lineTo(s * 0.30f, s * 0.25f)
                    lineTo(s * 0.38f, s * 0.60f); lineTo(s * 0.75f, s * 0.60f)
                    lineTo(s * 0.80f, s * 0.35f); lineTo(s * 0.33f, s * 0.35f)
                }
                drawPath(p, w, style = st)
                drawCircle(w, s * 0.06f, Offset(s * 0.43f, s * 0.74f))
                drawCircle(w, s * 0.06f, Offset(s * 0.70f, s * 0.74f))
            }
            else -> {
                val c = center
                for (i in 0 until 8) {
                    val a = i * Math.PI / 4
                    val ca = Math.cos(a).toFloat(); val sa = Math.sin(a).toFloat()
                    drawLine(w, Offset(c.x + s * 0.24f * ca, c.y + s * 0.24f * sa),
                        Offset(c.x + s * 0.36f * ca, c.y + s * 0.36f * sa),
                        strokeWidth = s * 0.1f, cap = StrokeCap.Round)
                }
                drawCircle(w, s * 0.26f, c, style = st)
                drawCircle(w, s * 0.09f, c)
            }
        }
    }
}