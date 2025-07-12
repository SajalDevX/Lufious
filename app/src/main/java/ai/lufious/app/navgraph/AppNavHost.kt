package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.presentation.splash.ui.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        authNavGraph(navController)
        mainNavGraph(navController)

    }
}
