package app.krafted.prizewheel.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.prizewheel.R
import app.krafted.prizewheel.data.LeaderboardEntry
import app.krafted.prizewheel.viewmodel.WheelViewModel

private val rankColors = listOf(
    Color(0xFFFFD700),
    Color(0xFFC0C0C0),
    Color(0xFFCD7F32)
)

@Composable
fun LeaderboardScreen(
    viewModel: WheelViewModel,
    onNavigateBack: () -> Unit
) {
    val entries by viewModel.leaderboardEntries.collectAsState()

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = Color(0xFFFFD700)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stringResource(R.string.nav_leaderboard),
                        color = Color(0xFFFFD700),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = stringResource(R.string.players_count, entries.size),
                        color = Color(0xFF8890A5),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.leaderboard_empty_title),
                            color = Color(0xFF555D73),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.leaderboard_empty_cta),
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
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                ) {
                    Spacer(Modifier.width(36.dp))
                    Text(
                        text = stringResource(R.string.leaderboard_col_player),
                        color = Color(0xFF555D73),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.leaderboard_col_coins),
                        color = Color(0xFF555D73),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(entries) { index, entry ->
                        LeaderboardRow(rank = index + 1, entry = entry)
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(rank: Int, entry: LeaderboardEntry) {
    val rankColor = rankColors.getOrElse(rank - 1) { Color(0xFF555D73) }
    val isTopThree = rank <= 3

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (rank == 1)
                    Brush.horizontalGradient(listOf(Color(0xFF221B08), Color(0xFF141929)))
                else
                    Brush.horizontalGradient(listOf(Color(0xFF141929), Color(0xFF141929)))
            )
            .border(
                width = if (isTopThree) 1.dp else 0.5.dp,
                color = if (isTopThree) rankColor.copy(alpha = 0.35f) else Color(0xFF252B3F),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        // Rank badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (isTopThree)
                        Brush.radialGradient(listOf(rankColor.copy(alpha = 0.3f), Color.Transparent))
                    else
                        Brush.radialGradient(listOf(Color(0xFF252B3F), Color.Transparent))
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$rank",
                color = rankColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.width(14.dp))

        // Avatar initial
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(rankColor.copy(alpha = 0.1f))
                .border(1.dp, rankColor.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.playerName.take(1).uppercase(),
                color = rankColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = entry.playerName,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "%,d".format(entry.totalCoins),
            color = Color(0xFFFFD700),
            fontSize = 17.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = stringResource(R.string.leaderboard_pts),
            color = Color(0xFF8890A5),
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
