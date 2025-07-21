package ai.lufious.app.core.utils

import ai.lufious.app.core.theme.ButtonShadowColor
import ai.lufious.app.core.theme.PrimaryColor
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.Dp

@Composable
fun ShadowButton(
    text: String,
    onClick: (() -> Unit)?,
    responsive: ResponsiveDimensions,
    modifier: Modifier = Modifier,
    isUpperCase: Boolean = true,
    isLoading: Boolean = false,
    debounceDuration: Long = 800L,
    cornerRadius: Float = 12f,
    btnHeight: Float = 45f,
    surfaceColor: Color = PrimaryColor,
    shadowColor: Color = ButtonShadowColor,
    textColor: Color = Color.White,
    textStyle: TextStyle = MaterialTheme.typography.body1,
    borderColor: Color = Color.Transparent,
    shadowDepth: Float = 3.5f,
    iconBefore: (@Composable (() -> Unit))? = null,
    iconAfter: (@Composable (() -> Unit))? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val btnHeight = responsive.R(btnHeight).dp
    val loadingSize = responsive.hR(24f).dp
    val spacing = responsive.wR(8f).dp
    val radius = responsive.R(cornerRadius).dp
    val shadowOffset = responsive.R(shadowDepth).dp
    val animationSpec = tween<Dp>(durationMillis = 50, easing = androidx.compose.animation.core.LinearOutSlowInEasing)

    val animatedHeight by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) btnHeight else btnHeight - shadowOffset,
        animationSpec = animationSpec
    )

    val animatedOffset by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isPressed) shadowOffset else 0.dp,
        animationSpec = animationSpec
    )

    Box(
        modifier = modifier
            .height(btnHeight)
            .fillMaxWidth()
            .padding(4.dp)
            .pointerInput(onClick) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        if (debounceJob?.isActive != true) {
                            onClick?.invoke()
                            debounceJob = scope.launch {
                                delay(debounceDuration)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.BottomCenter
    ) {
//        if (!isPressed) {
            Box(
                modifier = Modifier
                    .offset(y = shadowOffset)
                    .fillMaxWidth()
                    .height(btnHeight)
                    .background(
                        color = shadowColor,
                        shape = RoundedCornerShape(radius)
                    )
            )
//        }
        // Button layer (moves down and shrinks slightly when pressed)
        Box(
            modifier = Modifier
                .offset(y = animatedOffset)
                .fillMaxWidth()
                .height(animatedHeight)
                .background(
                    color = surfaceColor,
                    shape = RoundedCornerShape(radius)
                )
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(radius)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = textColor,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(loadingSize)
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (iconBefore != null) {
                        iconBefore()
                        Spacer(Modifier.width(spacing))
                    }

                    Text(
                        text = if (isUpperCase) text.uppercase() else text,
                        color = textColor,
                        style = textStyle
                    )

                    if (iconAfter != null) {
                        Spacer(Modifier.width(spacing))
                        iconAfter()
                    }
                }
            }
        }

    }
}

