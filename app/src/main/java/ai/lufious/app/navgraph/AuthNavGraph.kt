package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.auth.login.ui.EmailLoginPage
import ai.lufious.app.presentation.auth.login.ui.EmailLoginScreen
import ai.lufious.app.presentation.auth.login.ui.LoginPage
import ai.lufious.app.presentation.auth.signup.ui.EmailSignupPage
import ai.lufious.app.presentation.auth.signup.ui.SignupPage
import ai.lufious.app.presentation.onboarding.ui.OnBoardingPage
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    navigation(
        startDestination = Screen.Login.route,
        route = AUTH_GRAPH
    ) {

        animatedComposable(Screen.Login.route) {
            LoginPage(
                navController = navController,
                launchGoogleIntent = launchGoogleIntent,
                launchFacebookIntent = launchFacebookIntent
            )
        }

        animatedComposable(Screen.EmailLogin.route) {
            EmailLoginPage(navController = navController)
        }

        animatedComposable(Screen.Signup.route) {
            SignupPage(
                navController = navController,
                launchGoogleIntent = launchGoogleIntent,
                launchFacebookIntent = launchFacebookIntent
            )
        }

        animatedComposable(Screen.EmailSignup.route) {
            EmailSignupPage(navController = navController)
        }
    }
}
