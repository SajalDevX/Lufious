package ai.lufious.app.core.theme

import ai.lufious.app.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ClashDisplay = FontFamily(
    Font(R.font.clash_display_extralight, FontWeight.ExtraLight),
    Font(R.font.clash_display_light, FontWeight.Light),
    Font(R.font.clash_display_regular, FontWeight.Normal),
    Font(R.font.clash_display_medium, FontWeight.Medium),
    Font(R.font.clash_display_semibold, FontWeight.SemiBold),
    Font(R.font.clash_display_bold, FontWeight.Bold),
    Font(R.font.clash_display_bold, FontWeight.ExtraBold),
    Font(R.font.clash_display_bold, FontWeight.Black)
)

private fun ts(size: Int, weight: FontWeight, line: Int = size + 4, letter: Double = 0.0) = TextStyle(
    fontFamily = ClashDisplay,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = line.sp,
    letterSpacing = letter.sp
)

val Typography = Typography(
    displayLarge = ts(57, FontWeight.Bold, 64, -0.25),
    displayMedium = ts(45, FontWeight.Bold, 52),
    displaySmall = ts(36, FontWeight.SemiBold, 44),
    headlineLarge = ts(32, FontWeight.SemiBold, 40),
    headlineMedium = ts(28, FontWeight.SemiBold, 36),
    headlineSmall = ts(24, FontWeight.SemiBold, 32),
    titleLarge = ts(22, FontWeight.SemiBold, 28),
    titleMedium = ts(16, FontWeight.SemiBold, 24, 0.15),
    titleSmall = ts(14, FontWeight.Medium, 20, 0.1),
    bodyLarge = ts(16, FontWeight.Normal, 24, 0.5),
    bodyMedium = ts(14, FontWeight.Normal, 20, 0.25),
    bodySmall = ts(12, FontWeight.Normal, 16, 0.4),
    labelLarge = ts(14, FontWeight.Medium, 20, 0.1),
    labelMedium = ts(12, FontWeight.Medium, 16, 0.5),
    labelSmall = ts(11, FontWeight.Medium, 16, 0.5)
)
