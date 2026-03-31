package app.krafted.prizewheel.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PointerArrow(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pointer_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Canvas(modifier = modifier.size(46.dp, 58.dp)) {
        val centerPoint = size.width / 2f
        val path = Path().apply {
            moveTo(centerPoint, size.height) // Bottom sharp tip
            lineTo(size.width * 0.1f, size.height * 0.15f) // Left point
            lineTo(centerPoint, 0f) // Top concave center
            lineTo(size.width * 0.9f, size.height * 0.15f) // Right point
            close()
        }

        // Cyan/Magenta Deep Neon Glow
        drawCircle(
            color = Color(0xFFFF52D9).copy(alpha = glowAlpha),
            radius = size.width * 0.85f,
            center = Offset(centerPoint, size.height * 0.65f)
        )

        drawCircle(
            color = Color(0xFF67D4FF).copy(alpha = glowAlpha * 0.5f),
            radius = size.width * 1.1f,
            center = Offset(centerPoint, size.height * 0.3f)
        )

        // Arrow body with bright Magenta -> Orange neon gradient
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFF52D9), Color(0xFFFF7B00), Color(0xFFFFB347)),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )

        // Sleek cyan inner glow/stroke
        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF67D4FF), Color.White)
            ),
            style = Stroke(width = 3f)
        )

        // Sleek diagonal glass highlight
        val highlightPath = Path().apply {
            moveTo(centerPoint, size.height * 0.9f)
            lineTo(size.width * 0.18f, size.height * 0.18f)
            lineTo(centerPoint, size.height * 0.12f)
            close()
        }

        drawPath(
            path = highlightPath,
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                start = Offset(0f, 0f),
                end = Offset(centerPoint, size.height)
            )
        )
    }
}
