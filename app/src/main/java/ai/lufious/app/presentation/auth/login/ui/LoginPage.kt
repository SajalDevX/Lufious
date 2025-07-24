package ai.lufious.app.presentation.auth.login.ui

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LoginPage(
    navController: NavController,
    launchGoogleIntent: () -> Unit,
    launchFacebookIntent: () -> Unit
) {
    LoginSelectionScreen(
        navigateToHome = {
            navController.navigate(MAIN_GRAPH) {
                popUpTo(AUTH_GRAPH) { inclusive = true }
                launchSingleTop = true
            }
        },
        onEmailLogin={navController.navigate(Screen.EmailLogin.route) },
        launchGoogleSignIn = launchGoogleIntent,
        launchFacebookSignIn = launchFacebookIntent
    )
}

