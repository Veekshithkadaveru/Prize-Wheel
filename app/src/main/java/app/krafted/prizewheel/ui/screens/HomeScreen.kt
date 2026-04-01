package app.krafted.prizewheel.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.prizewheel.R
import app.krafted.prizewheel.game.WheelSegment
import app.krafted.prizewheel.ui.components.PrizeWheel
import app.krafted.prizewheel.ui.theme.Gold
import app.krafted.prizewheel.ui.theme.GoldDark
import app.krafted.prizewheel.ui.theme.GoldLight
import app.krafted.prizewheel.viewmodel.WheelViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val HomeTopOverlay = Color(0xF02F0C3E)
private val HomeBottomOverlay = Color(0xF0100B24)
private val HomeCardTop = Color(0xFF2A1242)
private val HomeCardBottom = Color(0xFF140C2A)
private val HomeCream = Color(0xFFFFF5DE)
private val HomePink = Color(0xFFFF52D9)
private val HomeBlue = Color(0xFF67D4FF)
private val HomeGoldBronze = Color(0xFFB8860B)

@Composable
fun HomeScreen(
    coinBalance: Int,
    canSpin: Boolean,
    leaderboardCount: Int,
    dailyRefillClaimed: Boolean,
    lastRefillTimestamp: Long,
    onNavigateToWheel: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onRefill: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = LocalContext.current as? Activity
    BackHandler { activity?.finish() }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.plin_back_1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Image(
            painter = painterResource(id = R.drawable.plin_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.18f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0f to HomeTopOverlay,
                            0.32f to HomeTopOverlay.copy(alpha = 0.72f),
                            0.62f to Color(0x8C240D33),
                            1f to HomeBottomOverlay
                        )
                    )
                )
        )

        GoldenParticles()

        Box(
            modifier = Modifier
                .size(420.dp)
                .align(Alignment.Center)
                .offset(y = (-20).dp)
                .graphicsLayer { alpha = 0.2f }
                .background(
                    Brush.radialGradient(
                        colors = listOf(Gold, HomeBlue.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )


        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(34.dp))

            OrnamentalDivider()

            Spacer(modifier = Modifier.height(14.dp))

            PrizeWheelTitle()



            OrnamentalDivider()

            Spacer(modifier = Modifier.height(24.dp))

            PremiumCoinDisplay(balance = coinBalance)

            Spacer(modifier = Modifier.height(26.dp))

            PrizeShowcase()

            Spacer(modifier = Modifier.height(16.dp))

            EpicPlayButton(
                canSpin = canSpin,
                onClick = {
                    if (!canSpin && !dailyRefillClaimed) {
                        onRefill()
                    }
                    onNavigateToWheel()
                }
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DailyRewardCard(
                    claimed = dailyRefillClaimed,
                    lastRefillTimestamp = lastRefillTimestamp,
                    onClaim = onRefill,
                    modifier = Modifier.weight(1f)
                )
                PremiumNavCard(
                    label = stringResource(R.string.nav_leaderboard),
                    subtitle = if (leaderboardCount > 0)
                        stringResource(R.string.players_count, leaderboardCount)
                    else
                        stringResource(R.string.leaderboard_empty_title),
                    accentColor = HomeBlue,
                    hasBadge = false,
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}



@Composable
private fun GoldenParticles() {
    data class Particle(
        val xFrac: Float,
        val yStart: Float,
        val speed: Float,
        val size: Float,
        val alpha: Float
    )

    val particles = remember {
        List(28) {
            Particle(
                xFrac = Math.random().toFloat(),
                yStart = Math.random().toFloat(),
                speed = 0.25f + Math.random().toFloat() * 0.65f,
                size = 1.4f + Math.random().toFloat() * 3.2f,
                alpha = 0.18f + Math.random().toFloat() * 0.45f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "homeParticles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = ((particle.yStart + time * particle.speed) % 1.1f) * size.height
            val x = particle.xFrac * size.width + sin((time + particle.xFrac) * 2 * PI.toFloat()) * 18f
            drawCircle(
                color = Gold.copy(alpha = particle.alpha),
                radius = particle.size.dp.toPx(),
                center = Offset(x, size.height - y)
            )
        }
    }
}

@Composable
private fun OrnamentalDivider() {
    val infiniteTransition = rememberInfiniteTransition(label = "homeDivider")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dividerShimmer"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth(0.72f)
            .height(3.dp)
    ) {
        val width = size.width
        val centerY = size.height / 2

        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    HomeGoldBronze.copy(alpha = 0.58f),
                    Gold.copy(alpha = 0.92f),
                    HomePink.copy(alpha = 0.45f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.5f
        )

        drawCircle(
            brush = Brush.radialGradient(listOf(GoldLight, Gold, HomeBlue)),
            radius = 4.dp.toPx(),
            center = Offset(width / 2, centerY)
        )

        drawCircle(
            color = GoldLight.copy(alpha = 0.6f),
            radius = 12.dp.toPx(),
            center = Offset(shimmer * width, centerY)
        )
    }
}

@Composable
private fun PrizeWheelTitle() {
    val infiniteTransition = rememberInfiniteTransition(label = "homeTitle")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2_500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleGlow"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.title_prize),
                style = TextStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    color = Gold.copy(alpha = glowAlpha * 0.35f)
                ),
                modifier = Modifier.blur(12.dp)
            )
            Text(
                text = stringResource(R.string.title_prize),
                style = TextStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 8.sp,
                    brush = Brush.linearGradient(
                        colors = listOf(HomePink, Gold, HomeBlue)
                    ),
                    shadow = Shadow(
                        color = HomeBlue.copy(alpha = 0.32f),
                        offset = Offset(0f, 4f),
                        blurRadius = 14f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = stringResource(R.string.title_wheel),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 12.sp,
                color = HomePink.copy(alpha = 0.9f),
                shadow = Shadow(
                    color = HomeBlue.copy(alpha = 0.6f),
                    offset = Offset(0f, 4f),
                    blurRadius = 12f
                )
            )
        )
    }
}

@Composable
private fun PremiumCoinDisplay(balance: Int) {
    val animatedBalance by animateIntAsState(
        targetValue = balance,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "homeCoinBalance"
    )

    val shape = RoundedCornerShape(28.dp)

    Row(
        modifier = Modifier
            .shadow(14.dp, shape, ambientColor = Gold.copy(alpha = 0.18f))
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF240B38),
                        Color(0xFF3B1056),
                        Color(0xFF240B38)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        HomeGoldBronze.copy(alpha = 0.4f),
                        Gold.copy(alpha = 0.82f),
                        HomeBlue.copy(alpha = 0.28f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 28.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(GoldLight, Gold, HomeGoldBronze)
                    )
                )
                .border(1.dp, GoldLight.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\u00A2",
                color = HomeBottomOverlay,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "%,d".format(animatedBalance),
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                brush = Brush.verticalGradient(
                    listOf(GoldLight, Gold)
                ),
                shadow = Shadow(
                    color = Gold.copy(alpha = 0.38f),
                    blurRadius = 8f
                )
            )
        )
    }
}

@Composable
private fun PrizeShowcase() {
    val infiniteTransition = rememberInfiniteTransition(label = "homeShowcase")
    val orbitRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbitRotation"
    )
    val wheelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15_500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheelRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(210.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                Gold.copy(alpha = 0.18f),
                                HomeBlue.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        ),
                        radius = size.maxDimension
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF3C144B),
                                Color(0xFF1B0A2F)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            listOf(Gold, GoldLight, HomeBlue, Gold, GoldDark, Gold)
                        ),
                        shape = CircleShape
                    )
                    .padding(10.dp)
            ) {
                PrizeWheel(
                    rotation = wheelRotation,
                    segments = WheelSegment.entries.toList(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        WheelSegment.entries.forEachIndexed { index, segment ->
            val angle = orbitRotation + (index * 360f / WheelSegment.entries.size)
            val radians = angle * PI.toFloat() / 180f
            val radiusX = 132f
            val radiusY = 58f
            val x = cos(radians) * radiusX
            val y = sin(radians) * radiusY
            val depthScale = 0.66f + 0.34f * ((sin(radians) + 1f) / 2f)
            val depthAlpha = 0.5f + 0.5f * ((sin(radians) + 1f) / 2f)

            Box(
                modifier = Modifier
                    .offset(x = x.dp, y = y.dp)
                    .graphicsLayer {
                        scaleX = depthScale
                        scaleY = depthScale
                        alpha = depthAlpha
                    }
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                segment.colour.copy(alpha = 0.3f),
                                Color(0xFF170A29)
                            )
                        )
                    )
                    .border(1.5.dp, Gold.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = segment.symbolRes),
                    contentDescription = segment.displayName,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun EpicPlayButton(
    canSpin: Boolean,
    onClick: () -> Unit
) {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val debouncedClick = {
        val now = System.currentTimeMillis()
        if (now - lastClickTime > 500L) {
            lastClickTime = now
            onClick()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "homePlayButton")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "buttonRingRotation"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1_800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonGlow"
    )
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonPressScale"
    )

    val buttonSize = 148.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(buttonSize + 42.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(buttonSize + 30.dp)
                    .graphicsLayer { alpha = glowAlpha * 0.24f }
                    .blur(20.dp)
                    .clip(CircleShape)
                    .background(if (canSpin) Gold else HomeBlue)
            )

            Canvas(
                modifier = Modifier
                    .size(buttonSize + 16.dp)
                    .graphicsLayer { rotationZ = ringRotation }
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            HomePink,
                            Color.Transparent,
                            HomeBlue,
                            Color.Transparent,
                            Gold,
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                )
            }

            Box(
                modifier = Modifier
                    .size(buttonSize + 8.dp)
                    .clip(CircleShape)
                    .border(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                GoldLight.copy(alpha = 0.9f),
                                GoldDark.copy(alpha = 0.65f),
                                HomeBlue.copy(alpha = 0.6f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .graphicsLayer {
                        scaleX = pulseScale * pressScale
                        scaleY = pulseScale * pressScale
                    }
                    .shadow(
                        elevation = 20.dp,
                        shape = CircleShape,
                        ambientColor = Gold.copy(alpha = 0.35f),
                        spotColor = Gold
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF52D9),
                                Color(0xFFFFB347),
                                Color(0xFFFF7B00)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.58f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.18f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = debouncedClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 12.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (canSpin) stringResource(R.string.btn_spin) else stringResource(R.string.btn_refill),
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = HomeBottomOverlay,
                            shadow = Shadow(
                                color = Gold.copy(alpha = 0.45f),
                                offset = Offset(0f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )

                    Box(
                        modifier = Modifier
                            .width(66.dp)
                            .height(1.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        HomeBottomOverlay.copy(alpha = 0.45f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (canSpin) stringResource(R.string.btn_spin_subtitle) else stringResource(R.string.btn_refill_subtitle),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                            color = HomeBottomOverlay.copy(alpha = 0.72f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.coins_per_spin, WheelViewModel.SPIN_COST),
            color = HomeCream.copy(alpha = 0.56f),
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun PremiumNavCard(
    label: String,
    subtitle: String,
    accentColor: Color,
    hasBadge: Boolean,
    locked: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "navCardScale"
    )

    val shape = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .shadow(12.dp, shape, ambientColor = accentColor.copy(alpha = 0.14f))
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(HomeCardTop, HomeCardBottom, accentColor.copy(alpha = 0.15f))
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.62f),
                            accentColor.copy(alpha = 0.18f)
                        )
                    ),
                    shape = shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = HomeCream,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = accentColor,
                fontSize = 9.sp,
                fontWeight = if (hasBadge) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
        }

        if (locked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF555D73)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.cd_locked),
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        if (hasBadge) {
            val infiniteTransition = rememberInfiniteTransition(label = "navBadge")
            val badgeScale by infiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(700),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "badgePulse"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .graphicsLayer {
                        scaleX = badgeScale
                        scaleY = badgeScale
                    }
                    .size(14.dp)
                    .shadow(4.dp, CircleShape, ambientColor = Gold)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(GoldLight, Gold))
                    )
                    .border(1.dp, HomeBottomOverlay, CircleShape)
            )
        }
    }
}

@Composable
private fun DailyRewardCard(
    claimed: Boolean,
    lastRefillTimestamp: Long,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val countdown = rememberCountdown24h(lastRefillTimestamp)
    val accentColor = if (claimed) Color(0xFF555D73) else GoldLight
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "dailyCardScale"
    )
    val shape = CutCornerShape(topStart = 24.dp, bottomEnd = 24.dp)

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .shadow(12.dp, shape, ambientColor = accentColor.copy(alpha = 0.14f))
                .clip(shape)
                .background(
                    Brush.linearGradient(
                        listOf(HomeCardTop, HomeCardBottom, accentColor.copy(alpha = 0.15f))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.verticalGradient(
                        listOf(accentColor.copy(alpha = 0.62f), accentColor.copy(alpha = 0.18f))
                    ),
                    shape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !claimed,
                    onClick = onClaim
                )
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.daily_reward),
                color = HomeCream,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (claimed) {
                Text(
                    text = countdown,
                    color = HomeBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            } else {
                Text(
                    text = stringResource(R.string.daily_reward_claim, WheelViewModel.DAILY_REFILL_AMOUNT),
                    color = accentColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }
        }

        if (claimed) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-6).dp, y = (-4).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF555D73)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.cd_locked),
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

@Composable
private fun rememberCountdown24h(lastRefillTimestamp: Long): String {
    val countdown by produceState(
        initialValue = calcCountdown24h(lastRefillTimestamp),
        key1 = lastRefillTimestamp
    ) {
        while (true) {
            kotlinx.coroutines.delay(1_000L)
            value = calcCountdown24h(lastRefillTimestamp)
            if ((lastRefillTimestamp + 24 * 3_600_000L - System.currentTimeMillis()) <= 0L) break
        }
    }
    return countdown
}

private fun calcCountdown24h(lastRefillTimestamp: Long): String {
    val diff = (lastRefillTimestamp + 24 * 3_600_000L - System.currentTimeMillis()).coerceAtLeast(0L)
    val h = diff / 3_600_000
    val m = (diff % 3_600_000) / 60_000
    val s = (diff % 60_000) / 1000
    return "%02dh %02dm %02ds".format(h, m, s)
}
