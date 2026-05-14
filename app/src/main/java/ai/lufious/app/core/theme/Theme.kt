package ai.lufious.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LufiousLightScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Surface,
    primaryContainer = TintSage,
    onPrimaryContainer = DeepForestGreen,
    secondary = LimeAccent,
    onSecondary = Surface,
    tertiary = SunlightYellow,
    onTertiary = TextPrimary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    error = CriticalRed,
    onError = Surface
)

// Kept for parity; runtime forces light. Flip later if a true dark mode is requested.
private val LufiousDarkScheme = darkColorScheme(
    primary = LeafGreen,
    onPrimary = TextPrimary,
    background = Color(0xFF0F1B11),
    surface = Color(0xFF1A2D1E),
    onBackground = Color(0xFFFAFAF0),
    onSurface = Color(0xFFFAFAF0)
)

@Composable
fun LufiousTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) LufiousDarkScheme else LufiousLightScheme,
        typography = Typography,
        content = content
    )
}
