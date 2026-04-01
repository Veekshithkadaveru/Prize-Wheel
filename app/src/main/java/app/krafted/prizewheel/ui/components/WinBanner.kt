package app.krafted.prizewheel.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.prizewheel.game.WheelSegment
import kotlinx.coroutines.delay

@Composable
fun WinBanner(segment: WheelSegment, coinsWon: Int, modifier: Modifier = Modifier) {
    // Scale-in animation
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(400),
        label = "banner_scale"
    )

    // Coin count-up animation
    var displayCoins by remember(coinsWon) { mutableIntStateOf(0) }
    LaunchedEffect(coinsWon) {
        val steps = 20
        val delayPerStep = 60L
        for (i in 1..steps) {
            displayCoins = (coinsWon * i) / steps
            delay(delayPerStep)
        }
        displayCoins = coinsWon
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFE44D),
                        Color(0xFFDAA520),
                        Color(0xFFFFE44D)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE6141929),
                        Color(0xF01C2235)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segment symbol in a glowing circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                segment.colour.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, segment.colour.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = segment.symbolRes),
                    contentDescription = segment.name,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = segment.displayName,
                    color = segment.colour,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+$displayCoins",
                        color = Color(0xFFFFD700),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = " coins",
                        color = Color(0xFFDAA520),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
