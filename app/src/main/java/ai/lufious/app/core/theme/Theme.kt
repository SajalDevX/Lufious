package ai.lufious.app.core.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

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

@Composable
fun LufiousTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LufiousLightScheme,
        typography = Typography
    ) {
        // Force ClashDisplay on any Text() that doesn't override fontFamily — including the
        // many call sites that build their own TextStyle inline and would otherwise fall back
        // to FontFamily.Default.
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = ClashDisplay)
        ) {
            content()
        }
    }
}
