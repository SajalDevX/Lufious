package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.main.ui.MainScreen
import ai.lufious.app.presentation.profile.ui.ProfileScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Screen.Home.route,
        route = MAIN_GRAPH
    ) {
        animatedComposable(Screen.Home.route) {
            MainScreen(outerNavController = navController)
        }
        animatedComposable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}
