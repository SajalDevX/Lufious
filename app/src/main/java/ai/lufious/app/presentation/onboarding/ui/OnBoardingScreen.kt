package ai.lufious.app.presentation.onboarding.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ai.lufious.app.R
import ai.lufious.app.core.theme.ClashDisplay
import ai.lufious.app.core.theme.LeafGreen
import ai.lufious.app.core.utils.ResponsiveDimensions
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val LightGreenBg = Color(0xFFF0FAF2)
private val PillBackground = Color(0xFF1A2A1A)
private val TitleDark = Color(0xFF0A1A0F)
private val SubtitleGrey = Color(0xFF4E5B52)
private val MutedChevron = Color(0x66FFFFFF)

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    val dims = rememberResponsiveDimensions()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGreenBg)
            .safeDrawingPadding()
    ) {
        // Lufious logo — top right
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Lufious logo",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = dims.hR(20f).dp,
                    end = dims.wR(20f).dp
                )
                .size(dims.R(44f).dp)
        )

        // Headline — top left
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = dims.wR(20f).dp,
                    top = dims.hR(60f).dp
                )
        ) {
            Text(
                text = "Nature",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(44f).sp,
                    color = TitleDark,
                    lineHeight = dims.R(48f).sp
                )
            )
            Text(
                text = "Your Plant",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Bold,
                    fontSize = dims.R(44f).sp,
                    color = LeafGreen,
                    lineHeight = dims.R(48f).sp
                )
            )
            Spacer(modifier = Modifier.height(dims.hR(10f).dp))
            Text(
                text = "Smart AI care for every\nleaf, root, and bloom.",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(14f).sp,
                    color = SubtitleGrey,
                    lineHeight = dims.R(22f).sp
                )
            )
        }

        // Plant mascot — centered
        Image(
            painter = painterResource(id = R.drawable.welcome_mascot),
            contentDescription = "Plant mascot",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = dims.hR(60f).dp)
                .size(dims.heightFraction(0.52f).dp)
        )

        // Bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = dims.wR(16f).dp,
                    end = dims.wR(16f).dp,
                    bottom = dims.hR(28f).dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SlideToGetStarted(
                onSlideComplete = onGetStarted,
                dims = dims,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(dims.hR(14f).dp))

            TextButton(onClick = onLogin) {
                Text(
                    text = "I already have an account",
                    style = TextStyle(
                        fontFamily = ClashDisplay,
                        fontWeight = FontWeight.Medium,
                        fontSize = dims.R(13f).sp,
                        color = SubtitleGrey
                    )
                )
            }
        }
    }
}

@Composable
private fun SlideToGetStarted(
    onSlideComplete: () -> Unit,
    dims: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val circleSizeDp = dims.R(48f).dp
    val hPadDp = dims.wR(10f).dp
    val vPadDp = dims.hR(10f).dp

    val circleSizePx = with(density) { circleSizeDp.toPx() }
    val hPadPx = with(density) { hPadDp.toPx() }

    var pillWidthPx by remember { mutableFloatStateOf(0f) }
    val maxDragPx by remember(pillWidthPx) {
        derivedStateOf { (pillWidthPx - 2 * hPadPx - circleSizePx).coerceAtLeast(0f) }
    }

    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val progress by remember(offsetX.value, maxDragPx) {
        derivedStateOf { if (maxDragPx > 0f) (offsetX.value / maxDragPx).coerceIn(0f, 1f) else 0f }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(PillBackground)
            .onSizeChanged { pillWidthPx = it.width.toFloat() }
    ) {
        // Static label — fades as circle advances
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = hPadDp, vertical = vPadDp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.size(circleSizeDp))
            Text(
                text = "Get Started",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = dims.R(18f).sp,
                    color = Color.White.copy(alpha = 1f - progress)
                ),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Text(
                text = ">>>",
                style = TextStyle(
                    fontFamily = ClashDisplay,
                    fontWeight = FontWeight.Normal,
                    fontSize = dims.R(16f).sp,
                    color = MutedChevron.copy(alpha = 1f - progress),
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(end = dims.wR(8f).dp)
            )
        }

        // Draggable circle
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset((hPadPx + offsetX.value).roundToInt(), 0) }
                .padding(vertical = vPadDp)
                .size(circleSizeDp)
                .clip(CircleShape)
                .background(LeafGreen)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch {
                            offsetX.snapTo((offsetX.value + delta).coerceIn(0f, maxDragPx))
                        }
                    },
                    onDragStopped = {
                        scope.launch {
                            if (progress >= 0.85f) {
                                offsetX.animateTo(maxDragPx)
                                delay(150)
                                onSlideComplete()
                                offsetX.animateTo(
                                    0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                            } else {
                                offsetX.animateTo(
                                    0f,
                                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                )
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Slide to get started",
                tint = Color.White,
                modifier = Modifier.size(dims.R(22f).dp)
            )
        }
    }
}
