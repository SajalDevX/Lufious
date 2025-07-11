package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Home.route,
        route = MAIN_GRAPH
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onLogout = {
                    // e.g. via ViewModel
                    navController.navigate(AUTH_GRAPH) {
                        popUpTo(MAIN_GRAPH) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
