package ai.lufious.app.presentation.onboarding.ui

import androidx.compose.runtime.Composable

@Composable
fun OnBoardingPage(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
) {
    OnboardingScreen(
        onGetStarted = onGetStarted,
        onLogin = onLogin
    )

}