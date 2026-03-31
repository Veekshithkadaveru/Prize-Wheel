package app.krafted.prizewheel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.prizewheel.data.SpinResultEntity
import app.krafted.prizewheel.game.WheelSegment
import app.krafted.prizewheel.viewmodel.WheelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: WheelViewModel,
    onNavigateBack: () -> Unit
) {
    val history by viewModel.spinHistory.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E1A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(48.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "SPIN HISTORY",
                        color = Color(0xFFFFD700),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Last ${history.size} spins",
                        color = Color(0xFF8890A5),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No spins yet",
                            color = Color(0xFF555D73),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Go spin the wheel!",
                            color = Color(0xFF3A4160),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                // Column headers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Spacer(Modifier.width(52.dp)) // icon space
                    Text(
                        text = "RESULT",
                        color = Color(0xFF555D73),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "COINS",
                        color = Color(0xFF555D73),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(0.6f)
                    )
                    Text(
                        text = "TIME",
                        color = Color(0xFF555D73),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(history) { _, result ->
                        HistoryCard(result = result)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(result: SpinResultEntity) {
    val segment = try {
        WheelSegment.valueOf(result.segmentName)
    } catch (_: Exception) {
        null
    }
    val segmentColor = segment?.colour ?: Color(0xFF607D8B)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF252B3F),
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color(0xFF141929))
            .padding(horizontal = 12.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Segment color indicator + icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(segmentColor.copy(alpha = 0.15f))
                    .border(1.dp, segmentColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                segment?.let {
                    Image(
                        painter = painterResource(id = it.symbolRes),
                        contentDescription = it.name,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Segment name
            Text(
                text = result.segmentName,
                color = segmentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            // Coins won
            Text(
                text = "+${"%,d".format(result.coinsWon)}",
                color = Color(0xFFFFD700),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(0.6f)
            )

            // Timestamp
            Text(
                text = formatTimestamp(result.timestamp),
                color = Color(0xFF8890A5),
                fontSize = 12.sp,
                modifier = Modifier.weight(0.8f)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}
