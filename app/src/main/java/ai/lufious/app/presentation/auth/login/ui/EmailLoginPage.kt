package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.core.utils.Screen
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun EmailLoginPage(navController: NavController) {
    EmailLoginScreen(
        onSignUp = { navController.navigate(Screen.Signup.route) },
        onNavigate = { navController.navigate(Screen.Home.route) },
    )
}