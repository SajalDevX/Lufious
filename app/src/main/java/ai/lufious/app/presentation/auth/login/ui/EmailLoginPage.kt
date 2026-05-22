package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun EmailLoginPage(navController: NavController) {
    EmailLoginScreen(
        onSignUp = {
            navController.navigate(Screen.GetStarted.route) {
                popUpTo(Screen.GetStarted.route) { inclusive = false }
                launchSingleTop = true
            }
        },
        onNavigate = {
            navController.navigate(MAIN_GRAPH) {
                popUpTo(AUTH_GRAPH) { inclusive = true }
                launchSingleTop = true
            }
        },
        onBack = { navController.navigateUp() }
    )
}
