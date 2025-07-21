package ai.lufious.app.navgraph


import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.onboarding.ui.OnBoardingPage
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    navController: NavHostController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    NavHost(navController, startDestination = Screen.Onboarding.route) {
        composable(Screen.Onboarding.route) {
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
        mainNavGraph(navController)
    }
}
