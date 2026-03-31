package app.krafted.prizewheel.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PointerArrow(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(32.dp, 40.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2f, size.height)
            lineTo(0f, 0f)
            lineTo(size.width, 0f)
            close()
        }
        drawPath(path = path, color = Color(0xFFFFD700))
        drawPath(path = path, color = Color.White, style = Stroke(width = 2f))
    }
}
