package app.krafted.prizewheel.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import app.krafted.prizewheel.game.WheelSegment
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PrizeWheel(
    rotation: Float,
    segments: List<WheelSegment>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmaps = remember(segments) {
        segments.map { segment ->
            BitmapFactory.decodeResource(context.resources, segment.symbolRes)?.let { original ->
                val scaled = Bitmap.createScaledBitmap(original, 96, 96, true)
                original.recycle()
                scaled.asImageBitmap()
            }
        }
    }

    Canvas(modifier = modifier.rotate(rotation)) {
        val segmentAngle = 360f / segments.size
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val arcSize = Size(radius * 2f, radius * 2f)
        val arcTopLeft = Offset(center.x - radius, center.y - radius)

        segments.forEachIndexed { index, segment ->
            val startAngle = index * segmentAngle - 90f

            drawArc(
                color = segment.colour,
                startAngle = startAngle,
                sweepAngle = segmentAngle,
                useCenter = true,
                topLeft = arcTopLeft,
                size = arcSize
            )

            val lineAngleRad = Math.toRadians(startAngle.toDouble())
            drawLine(
                color = Color.White,
                start = center,
                end = Offset(
                    center.x + radius * cos(lineAngleRad).toFloat(),
                    center.y + radius * sin(lineAngleRad).toFloat()
                ),
                strokeWidth = 2f
            )

            val midAngleRad = Math.toRadians((startAngle + segmentAngle / 2f).toDouble())
            val symbolOffset = Offset(
                center.x + size.width * 0.33f * cos(midAngleRad).toFloat() - 48f,
                center.y + size.width * 0.33f * sin(midAngleRad).toFloat() - 48f
            )
            bitmaps[index]?.let { bitmap ->
                drawImage(bitmap, topLeft = symbolOffset)
            }
        }

        drawCircle(
            color = Color.White,
            radius = 52f,
            center = center
        )

        drawCircle(
            color = Color(0xFFFFD700),
            radius = 44f,
            center = center
        )
    }
}
