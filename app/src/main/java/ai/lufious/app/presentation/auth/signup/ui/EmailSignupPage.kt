package ai.lufious.app.presentation.auth.signup.ui

import ai.lufious.app.core.utils.Screen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun EmailSignupPage(navController: NavController) {
    EmailSignupScreen(
        onLogin = { navController.navigate(Screen.Login.route) },
        onNavigate = { navController.navigate(Screen.Home.route) },
        onBack = { navController.navigateUp() }
    )
}
