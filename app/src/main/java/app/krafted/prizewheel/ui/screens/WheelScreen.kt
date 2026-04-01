package app.krafted.prizewheel.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.prizewheel.game.WheelSegment
import app.krafted.prizewheel.ui.components.PointerArrow
import app.krafted.prizewheel.ui.components.PrizeWheel
import app.krafted.prizewheel.ui.components.WinBanner
import app.krafted.prizewheel.viewmodel.WheelViewModel

@Composable
fun WheelScreen(
    viewModel: WheelViewModel,
    onNavigateHome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val rotation by viewModel.rotation.asState()
    var showNameOverlay by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }

    LaunchedEffect(uiState.sessionComplete, uiState.isSpinning) {
        if (uiState.sessionComplete && !uiState.isSpinning) {
            showNameOverlay = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.spinEvents.collect { event ->
            viewModel.rotation.animateTo(
                targetValue = event.targetRotation,
                animationSpec = tween(
                    durationMillis = 3500,
                    easing = FastOutSlowInEasing
                )
            )
            viewModel.onAnimationComplete(event.result)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = uiState.currentBackground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF00A0E1A),
                            Color(0x900A0E1A),
                            Color(0x500A0E1A),
                            Color(0xA00A0E1A),
                            Color(0xF00A0E1A)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF52D9).copy(alpha = 0.12f),
                            Color(0xFF67D4FF).copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        radius = 1200f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoinBalancePill(coins = uiState.coins)
                SpinCountPill(used = uiState.paidSpinsUsed, total = 10)
            }

            Spacer(Modifier.height(16.dp))

            Box(contentAlignment = Alignment.TopCenter) {
                Box(
                    modifier = Modifier
                        .size(360.dp)
                        .align(Alignment.Center)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF52D9).copy(alpha = 0.25f),
                                    Color(0xFF67D4FF).copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PointerArrow(modifier = Modifier.offset(y = 6.dp))
                    PrizeWheel(
                        rotation = rotation,
                        segments = WheelSegment.entries,
                        modifier = Modifier.size(300.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                AnimatedVisibility(
                    visible = uiState.showWinBanner,
                    enter = scaleIn(tween(300)) + fadeIn(tween(300)),
                    exit = scaleOut(tween(200)) + fadeOut(tween(200))
                ) {
                    uiState.lastResult?.let { result ->
                        WinBanner(
                            segment = result,
                            coinsWon = result.coinReward,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                SpinButton(
                    onClick = { viewModel.spin() },
                    enabled = uiState.canSpin && !uiState.isSpinning && !uiState.sessionComplete,
                    isSpinning = uiState.isSpinning
                )

                AnimatedVisibility(
                    visible = !uiState.canSpin && !uiState.sessionComplete && !uiState.isSpinning,
                    enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(12.dp))
                        RefillButton(
                            text = "REFILL COINS",
                            onClick = { viewModel.refill() }
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        // Session complete — name entry overlay
        AnimatedVisibility(
            visible = showNameOverlay,
            enter = fadeIn(tween(400)),
            exit = fadeOut(tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xE5080B14)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E1040), Color(0xFF0F0A24))
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700).copy(alpha = 0.6f), Color(0xFF67D4FF).copy(alpha = 0.4f))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SESSION COMPLETE",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "+${"%,d".format(uiState.sessionCoinsWon)} coins won",
                        color = Color(0xFF67D4FF),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "ENTER YOUR NAME",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    androidx.compose.material3.TextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        singleLine = true,
                        placeholder = {
                            Text("Your name", color = Color.White.copy(alpha = 0.3f))
                        },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color(0xFF1A1035),
                            unfocusedContainerColor = Color(0xFF1A1035),
                            focusedIndicatorColor = Color(0xFFFFD700),
                            unfocusedIndicatorColor = Color(0xFF3A4160)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF52D9), Color(0xFFFFB347))
                                )
                            )
                            .clickable {
                                val name = nameInput.trim().ifEmpty { "Anonymous" }
                                viewModel.submitSession(name)
                                showNameOverlay = false
                                nameInput = ""
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SAVE & NEW GAME",
                            color = Color(0xFF080B14),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                            .clickable {
                                val name = nameInput.trim().ifEmpty { "Anonymous" }
                                viewModel.submitSession(name)
                                showNameOverlay = false
                                nameInput = ""
                                onNavigateHome()
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HOME SCREEN",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CoinBalancePill(coins: Int) {
    val animatedBalance by animateIntAsState(
        targetValue = coins,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "coinBalance"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF67D4FF).copy(alpha = 0.5f),
                        Color(0xFFFF52D9).copy(alpha = 0.3f),
                        Color(0xFFFFD700).copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xAA140C2A),
                        Color(0xAA2A1242)
                    )
                )
            )
            .padding(horizontal = 28.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFFFE44D), Color(0xFFDAA520))
                        ),
                        CircleShape
                    )
                    .border(1.dp, Color(0xFFFFE44D), CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "%,d".format(animatedBalance),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SpinCountPill(used: Int, total: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "SPINS: $used/$total",
            color = if (used >= total) Color(0xFFFF52D9) else Color(0xFF67D4FF),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun SpinButton(onClick: () -> Unit, enabled: Boolean, isSpinning: Boolean, spinCost: Int = WheelViewModel.SPIN_COST) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "spin_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    Box(contentAlignment = Alignment.Center) {
        if (enabled) {
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .size(130.dp)
                    .graphicsLayer { rotationZ = ringRotation }
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0xFFFF52D9),
                            Color.Transparent,
                            Color(0xFF67D4FF),
                            Color.Transparent,
                            Color(0xFFFFD700),
                            Color.Transparent
                        )
                    ),
                    radius = size.width / 2f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8f)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(buttonScale)
                .shadow(
                    elevation = if (enabled) 20.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFFFF52D9).copy(alpha = glowAlpha),
                    spotColor = Color(0xFFFF52D9)
                )
                .clip(CircleShape)
                .background(
                    if (enabled) {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFFF52D9), Color(0xFFFFB347), Color(0xFFFF7B00))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF555555), Color(0xFF333333))
                        )
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (enabled) Color.White.copy(alpha = 0.8f) else Color(0xFF666666),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isSpinning) "..." else "SPIN",
                    color = Color.White,
                    fontSize = if (isSpinning) 28.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
                if (!isSpinning && enabled) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .width(50.dp)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$spinCost COINS",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RefillButton(text: String, onClick: () -> Unit) {
    val shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp)
    Box(
        modifier = Modifier
            .clip(shape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFDAA520))
                ),
                shape = shape
            )
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF2A1242), Color(0xFF140C2A))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 28.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFFFD700),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

