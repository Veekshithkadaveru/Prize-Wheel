package app.krafted.prizewheel.ui.navigation

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.krafted.prizewheel.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.krafted.prizewheel.game.WheelSegment
import app.krafted.prizewheel.ui.components.PrizeWheel
import app.krafted.prizewheel.ui.screens.HomeScreen
import app.krafted.prizewheel.ui.screens.LeaderboardScreen
import app.krafted.prizewheel.ui.screens.WheelScreen
import app.krafted.prizewheel.ui.theme.Gold
import app.krafted.prizewheel.ui.theme.GoldDark
import app.krafted.prizewheel.ui.theme.GoldLight
import app.krafted.prizewheel.viewmodel.WheelViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

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
        delay(2800)
        onSplashComplete()
    }

    val contentAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(900),
        label = "splash_alpha"
    )
    val contentScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.82f,
        animationSpec = tween(900),
        label = "splash_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splashAnim")

    val wheelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashWheelRotation"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "splashGlow"
    )
    val particleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "splashParticles"
    )

    data class Particle(
        val xFrac: Float,
        val yStart: Float,
        val speed: Float,
        val size: Float,
        val alpha: Float
    )

    val particles = remember {
        List(26) {
            Particle(
                xFrac = Math.random().toFloat(),
                yStart = Math.random().toFloat(),
                speed = 0.25f + Math.random().toFloat() * 0.65f,
                size = 1.4f + Math.random().toFloat() * 3.2f,
                alpha = 0.18f + Math.random().toFloat() * 0.45f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color(0xFF1A0E2E),
                        0.5f to Color(0xFF0A0E1A),
                        1f to Color(0xFF050810)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val y = ((p.yStart + particleTime * p.speed) % 1.1f) * size.height
                val x =
                    p.xFrac * size.width + sin((particleTime + p.xFrac) * 2 * PI.toFloat()) * 18f
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = p.alpha),
                    radius = p.size.dp.toPx(),
                    center = Offset(x, size.height - y)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(340.dp)
                .graphicsLayer { alpha = 0.22f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(Gold, Color(0x004488FF))
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(contentAlpha)
                .scale(contentScale)
        ) {

            Box(
                modifier = Modifier
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFF3C144B), Color(0xFF1B0A2F))
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(Gold, GoldLight, Color(0xFF67D4FF), Gold, GoldDark, Gold)
                        ),
                        shape = CircleShape
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                PrizeWheel(
                    rotation = wheelRotation,
                    segments = WheelSegment.entries.toList(),
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(30.dp))

            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.title_prize),
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        color = Gold.copy(alpha = glowAlpha * 0.35f)
                    ),
                    modifier = Modifier.blur(12.dp)
                )
                Text(
                    text = stringResource(R.string.title_prize),
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 8.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFF52D9), Gold, Color(0xFF67D4FF))
                        )
                    )
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.title_wheel),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 12.sp,
                    color = Color(0xFFFF52D9).copy(alpha = 0.9f)
                )
            )

            Spacer(Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.spin_to_win),
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp,
                    color = Gold.copy(alpha = 0.6f)
                )
            )
        }
    }
}
