package ai.lufious.app.core.utils


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import kotlin.math.min

const val DESIGN_WIDTH = 375
const val DESIGN_HEIGHT = 812

data class ResponsiveDimensions(
    val screenWidth: Int,
    val screenHeight: Int
) {
    fun wR(value: Float): Float =
        (value / DESIGN_WIDTH) * screenWidth

    fun hR(value: Float): Float =
        (value / DESIGN_HEIGHT) * screenHeight

    fun R(value: Float): Float {
        val ratioWidth = screenWidth.toFloat() / DESIGN_WIDTH
        val ratioHeight = screenHeight.toFloat() / DESIGN_HEIGHT
        return value * min(ratioWidth, ratioHeight)
    }

    fun widthFraction(fraction: Float): Float =
        screenWidth * fraction

    fun heightFraction(fraction: Float): Float =
        screenHeight * fraction
}

@Composable
fun rememberResponsiveDimensions(): ResponsiveDimensions {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    return remember(configuration) {
        ResponsiveDimensions(screenWidth, screenHeight)
    }
}

fun Number.wR(dimensions: ResponsiveDimensions): Float =
    dimensions.wR(this.toFloat())

fun Number.hR(dimensions: ResponsiveDimensions): Float =
    dimensions.hR(this.toFloat())

fun Number.R(dimensions: ResponsiveDimensions): Float =
    dimensions.R(this.toFloat())

fun Number.widthFraction(dimensions: ResponsiveDimensions): Float =
    dimensions.widthFraction(this.toFloat())

fun Number.heightFraction(dimensions: ResponsiveDimensions): Float =
    dimensions.heightFraction(this.toFloat())