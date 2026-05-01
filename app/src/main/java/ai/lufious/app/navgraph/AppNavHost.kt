package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.onboarding.ui.OnBoardingPage
import ai.lufious.app.presentation.onboarding.ui.PostOnboardingScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController: NavHostController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    NavHost(navController, startDestination = Screen.Onboarding.route) {
        animatedComposable(
            route = Screen.Onboarding.route) {
            OnBoardingPage(
                onLogin = { navController.navigate(Screen.Login.route) },
                onGetStarted = { navController.navigate(Screen.Signup.route) }
            )
        }

        authNavGraph(
            navController = navController,
            launchGoogleIntent = launchGoogleIntent,
            launchFacebookIntent = launchFacebookIntent
        )

        animatedComposable(Screen.PostOnboarding.route) {
            PostOnboardingScreen(navController = navController)
        }

        mainNavGraph(navController)
    }
}
