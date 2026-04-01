package app.krafted.prizewheel.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.krafted.prizewheel.R
import app.krafted.prizewheel.ui.screens.HomeScreen
import app.krafted.prizewheel.ui.screens.LeaderboardScreen
import app.krafted.prizewheel.ui.screens.WheelScreen
import app.krafted.prizewheel.viewmodel.WheelViewModel
import kotlinx.coroutines.delay

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Wheel : Screen("wheel")
    data object Leaderboard : Screen("leaderboard")
}

@Composable
fun NavGraph(navController: NavHostController, wheelViewModel: WheelViewModel) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashContent(
                onSplashComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val uiState by wheelViewModel.uiState.collectAsState()
            val leaderboard by wheelViewModel.leaderboardEntries.collectAsState()

            HomeScreen(
                coinBalance = uiState.coins,
                canSpin = uiState.canSpin,
                leaderboardCount = leaderboard.size,
                dailyRefillClaimed = uiState.dailyRefillClaimed,
                lastRefillTimestamp = uiState.lastRefillTimestamp,
                onNavigateToWheel = { navController.navigate(Screen.Wheel.route) },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                onRefill = { wheelViewModel.refill() }
            )
        }

        composable(Screen.Wheel.route) {
            WheelScreen(
                viewModel = wheelViewModel,
                onNavigateHome = { navController.popBackStack() }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                viewModel = wheelViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun SplashContent(onSplashComplete: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onSplashComplete()
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "splash_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(800),
        label = "splash_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF141929),
                        Color(0xFF0A0E1A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale)
        ) {
            Image(
                painter = painterResource(R.drawable.plin_banner),
                contentDescription = "Prize Wheel",
                modifier = Modifier.fillMaxWidth(0.75f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "SPIN TO WIN",
                color = Color(0xFFFFD700).copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
        }
    }
}
