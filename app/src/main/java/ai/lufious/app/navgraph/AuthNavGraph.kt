package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.auth.login.ui.LoginPage
import ai.lufious.app.presentation.auth.signup.ui.SignupPage
import ai.lufious.app.presentation.onboarding.ui.OnBoardingPage
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit) {
    navigation(
        startDestination = Screen.Onboarding.route,
        route = AUTH_GRAPH
    ) {

        composable(Screen.Login.route) {
            LoginPage(
                navController = navController,
                launchGoogleIntent = launchGoogleIntent,
                launchFacebookIntent = launchFacebookIntent
            )
        }


        composable(Screen.Signup.route) {
            SignupPage(
//                onSignedUp = {
//                    navController.navigate(MAIN_GRAPH) {
//                        popUpTo(AUTH_GRAPH) { inclusive = true }
//                        launchSingleTop = true
//                    }
//                },
//                onBack = { navController.popBackStack() }
            )
        }
    }
}
