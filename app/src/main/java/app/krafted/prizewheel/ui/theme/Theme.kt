package app.krafted.prizewheel.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CasinoColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = CasinoDark,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = EmeraldGreen,
    onSecondary = CasinoDark,
    tertiary = SapphireBlue,
    onTertiary = Color.White,
    background = CasinoDark,
    onBackground = TextPrimary,
    surface = CasinoSurface,
    onSurface = TextPrimary,
    surfaceVariant = CasinoCard,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF3A4160),
    outlineVariant = Color(0xFF252B3F)
)

@Composable
fun PrizeWheelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use casino dark theme for immersive game feel
    val colorScheme = CasinoColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = CasinoDark.toArgb()
            window.navigationBarColor = CasinoDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
