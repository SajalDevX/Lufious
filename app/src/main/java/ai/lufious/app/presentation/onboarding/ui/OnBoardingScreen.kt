package ai.lufious.app.presentation.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ai.lufious.app.core.theme.TextPrimary
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.lufious.app.R
import ai.lufious.app.core.theme.Background
import ai.lufious.app.core.theme.PrimaryColor
import ai.lufious.app.core.utils.ShadowButton
import ai.lufious.app.core.utils.rememberResponsiveDimensions
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit
) {
    val dims = rememberResponsiveDimensions()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .safeDrawingPadding()
    ) {

        Surface(
            shape = RoundedCornerShape(dims.R(16f).dp),
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(
                    horizontal = dims.wR(8f).dp,
                    vertical = dims.hR(12f).dp
                )
                .zIndex(1f)
        ) {
            Column(
                modifier = Modifier
                    .height(dims.heightFraction(0.35f).dp)
                    .fillMaxWidth()
                    .padding(
                        horizontal = dims.R(12f).dp,
                        vertical = dims.R(20f).dp
                    ),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Smarter Plant Care With",
                    style = MaterialTheme.typography.h4.copy(
                        fontSize = dims.R(20f).sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = dims.hR(4f).dp)
                )

                Text(
                    text = "Lufious",
                    style = MaterialTheme.typography.h3.copy(
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor
                    ),
                    modifier = Modifier.padding(bottom = dims.hR(4f).dp)
                )

                Text(
                    text = "Smart AI care for every leaf, root, and bloom",
                    style = MaterialTheme.typography.body2.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = dims.R(14f).sp
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                ShadowButton(
                    text = "GET STARTED",
                    onClick = onGetStarted,
                    responsive = dims,
                    modifier = Modifier.fillMaxWidth(),
                    isUpperCase = true,
                    cornerRadius = 8f,
                    shadowDepth = 4f,
                )

                Spacer(modifier = Modifier.height(dims.hR(6f).dp))

                ShadowButton(
                    text = "I ALREADY HAVE AN ACCOUNT",
                    onClick = onLogin,
                    responsive = dims,
                    modifier = Modifier.fillMaxWidth(),
                    surfaceColor = MaterialTheme.colors.onBackground,
                    shadowColor = PrimaryColor,
                    borderColor = PrimaryColor,
                    textColor = PrimaryColor,
                    isUpperCase = true,
                    cornerRadius = 8f,
                    shadowDepth = 4f
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.welcome_mascot),
            contentDescription = "Mascot",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = dims.heightFraction(0.15f).dp)
                .size(dims.heightFraction(0.6f).dp) // mascot size as 60% of screen width
                .zIndex(0f)
        )
    }
}
