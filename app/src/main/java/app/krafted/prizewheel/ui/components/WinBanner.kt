package app.krafted.prizewheel.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.krafted.prizewheel.game.WheelSegment

@Composable
fun WinBanner(segment: WheelSegment, coinsWon: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xCC000000), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = segment.symbolRes),
            contentDescription = segment.name,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                segment.name,
                color = Color(0xFFFFD700),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "+$coinsWon coins",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
