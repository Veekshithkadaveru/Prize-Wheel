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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                val scaled = Bitmap.createScaledBitmap(original, 80, 80, true)
                original.recycle()
                scaled.asImageBitmap()
            }
        }
    }

    Canvas(modifier = modifier.rotate(rotation)) {
        val segmentAngle = 360f / segments.size
        val outerRimWidth = size.minDimension * 0.06f
        val radius = size.minDimension / 2f
        val innerRadius = radius - outerRimWidth
        val center = Offset(size.width / 2f, size.height / 2f)
        val arcSize = Size(innerRadius * 2f, innerRadius * 2f)
        val arcTopLeft = Offset(center.x - innerRadius, center.y - innerRadius)

        // Soft ambient shadow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x50000000), Color.Transparent),
                center = center,
                radius = radius * 1.15f
            ),
            radius = radius * 1.15f,
            center = center
        )

        // Matte base rim
        drawCircle(
            color = Color(0xFF181824),
            radius = radius,
            center = center
        )

        // Premium metallic sweep gradient for the main rim
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFE5E5E5), // Platinum
                    Color(0xFF8C8C8C), // Steel
                    Color(0xFFFFD700), // Gold
                    Color(0xFFB59A45), // Dark Gold
                    Color(0xFFE5E5E5)  // Back to Platinum
                ),
                center = center
            ),
            radius = radius,
            center = center,
            style = Stroke(width = outerRimWidth)
        )

        // Inner glowing white gold border
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0x60FFFFFF)),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            ),
            radius = innerRadius + 2f,
            center = center,
            style = Stroke(width = 3f)
        )

        // Draw segments
        segments.forEachIndexed { index, segment ->
            val startAngle = index * segmentAngle - 90f

            // Segment wedge with slightly darker edge for depth
            drawArc(
                color = segment.colour,
                startAngle = startAngle,
                sweepAngle = segmentAngle,
                useCenter = true,
                topLeft = arcTopLeft,
                size = arcSize
            )

            // Refined dark inner edge for depth
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color(0x40000000)),
                    center = center,
                    radius = innerRadius
                ),
                startAngle = startAngle,
                sweepAngle = segmentAngle,
                useCenter = true,
                topLeft = arcTopLeft,
                size = arcSize
            )

            // Metallic divider lines
            val lineAngleRad = Math.toRadians(startAngle.toDouble())
            val lineEnd = Offset(
                center.x + innerRadius * cos(lineAngleRad).toFloat(),
                center.y + innerRadius * sin(lineAngleRad).toFloat()
            )
            val lineBrush = Brush.linearGradient(
                colors = listOf(Color(0xFFE5E5E5), Color(0xFF8C8C8C)),
                start = center,
                end = lineEnd
            )
            drawLine(
                brush = lineBrush,
                start = center,
                end = lineEnd,
                strokeWidth = 3f
            )

            // Symbol image at segment midpoint
            val midAngleRad = Math.toRadians((startAngle + segmentAngle / 2f).toDouble())
            val symbolDist = innerRadius * 0.6f
            val symbolOffset = Offset(
                center.x + symbolDist * cos(midAngleRad).toFloat() - 40f,
                center.y + symbolDist * sin(midAngleRad).toFloat() - 40f
            )
            bitmaps[index]?.let { bitmap ->
                drawImage(bitmap, topLeft = symbolOffset)
            }
        }

        // Elegant glass crescent reflection over the wheel
        drawArc(
            brush = Brush.linearGradient(
                colors = listOf(Color(0x30FFFFFF), Color.Transparent),
                start = Offset(center.x, center.y - innerRadius),
                end = Offset(center.x, center.y + innerRadius)
            ),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = arcTopLeft,
            size = arcSize
        )

        // Professional glowing tick marks
        val tickCount = 42
        val tickRadius = radius - outerRimWidth / 2f
        for (i in 0 until tickCount) {
            val angle = Math.toRadians((i * 360.0 / tickCount))
            val tickCenter = Offset(
                center.x + tickRadius * cos(angle).toFloat(),
                center.y + tickRadius * sin(angle).toFloat()
            )
            val isMajor = i % 6 == 0
            val dotRadius = if (isMajor) 4.5f else 2.5f

            // Major dots are platinum, minor are warm gold
            val baseColor = if (isMajor) Color(0xFFFFFFFF) else Color(0xFFFFD700)
            val glowColor = if (isMajor) Color(0x60FFFFFF) else Color(0x40FFD700)

            // Soft glow
            drawCircle(color = glowColor, radius = dotRadius * 2f, center = tickCenter)
            // Core dot
            drawCircle(color = baseColor, radius = dotRadius, center = tickCenter)
            // Inner punch
            if (isMajor) {
                drawCircle(
                    color = Color(0xFFE5E5E5),
                    radius = dotRadius * 0.5f,
                    center = tickCenter
                )
            }
        }

        // Centre shadow ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x80000000), Color.Transparent),
                center = center,
                radius = 75f
            ),
            radius = 75f,
            center = center
        )

        // Outer brushed metal ring
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFE5E5E5), Color(0xFF8C8C8C)),
                start = Offset(center.x - 55f, center.y - 55f),
                end = Offset(center.x + 55f, center.y + 55f)
            ),
            radius = 55f,
            center = center
        )

        // Inner dark metallic bezel
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF181824), Color(0xFF2B2B3C)),
                start = Offset(center.x - 48f, center.y - 48f),
                end = Offset(center.x + 48f, center.y + 48f)
            ),
            radius = 48f,
            center = center
        )

        // Solid brass core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFD700), Color(0xFFB59A45)),
                center = center,
                radius = 36f
            ),
            radius = 36f,
            center = center
        )

        // Inner bevel highlight
        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0x70FFFFFF), Color.Transparent),
                start = Offset(center.x - 20f, center.y - 20f),
                end = Offset(center.x, center.y)
            ),
            radius = 32f,
            center = center
        )
    }
}
