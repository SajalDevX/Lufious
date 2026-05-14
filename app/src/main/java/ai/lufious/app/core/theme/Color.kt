package ai.lufious.app.core.theme

import androidx.compose.ui.graphics.Color

// Legacy Material3 wizard defaults — unused at runtime.
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Brand — emerald primary, deeper than the dark-theme variant so it reads on a cream stage.
val PrimaryColor = Color(0xFF15803D)
val DeepForestGreen = Color(0xFF064E3B)
val LeafGreen = Color(0xFF22C55E)
val ButtonShadowColor = Color(0x4015803D)
val LimeAccent = Color(0xFF65A30D)

// Stage — cool cream-grey base + white cards (Prepairo pattern, farming-flavored).
val Background = Color(0xFFDCE3EC)
val Surface = Color(0xFFFFFFFF)
val SurfaceHigh = Color(0xFFEEF3F7)
val CardBorder = Color(0xFFDCE3EC)
val BgCard = Color(0x0A000000)
val BgCardDark = Color(0x14000000)

// Pastel category tints (mirrored from Prepairo testimonial cards).
val TintSage = Color(0xFFDBFCE5)
val TintButter = Color(0xFFFFF9C1)
val TintPeach = Color(0xFFFFECD4)
val TintSky = Color(0xFFDFE6FF)
val TintBlush = Color(0xFFFDE6F4)
val TintLavender = Color(0xFFF3E7FF)

// Semantic text tokens — use these instead of Color.White on the light stage.
val TextPrimary = Color(0xFF0A1A0F)
val TextSecondary = Color(0xFF4E5B52)
val TextMuted = Color(0xFF7A8C82)

// Legacy aliases so existing imports keep resolving; "White" now means "text on light surface".
val White = TextPrimary
val WhiteDim = TextSecondary
val WhiteFaint = TextMuted

// Supporting tones.
val SkyBlue = Color(0xFF0EA5E9)
val SunlightYellow = Color(0xFFF59E0B)
val SoilBrown = Color(0xFFC2410C)

// Misc / legacy.
val LightMistGrey = Color(0xFFF1F8E9)
val DarkTextGrey = Color(0xFF37474F)
val MutedGrey = Color(0xFF90A4AE)

// Status / state.
val HealthyGreen = Color(0xFF10B981)
val WarningOrange = Color(0xFFEA580C)
val CriticalRed = Color(0xFFDC2626)
