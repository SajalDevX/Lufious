package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.navgraph.utils.animatedComposable
import ai.lufious.app.presentation.garden.ui.AddPlantScreen
import ai.lufious.app.presentation.main.ui.MainScreen
import ai.lufious.app.presentation.scan.ui.AiChatScreen
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        composable(
            route = "garden/add_plant?species={species}",
            arguments = listOf(
                navArgument("species") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) {
            AddPlantScreen(navController = navController)
        }
        composable(
            route = Screen.AiChat.route,
            arguments = listOf(navArgument("scanId") { type = NavType.StringType }),
            // ChatGPT/Claude-style entrance: slide up + fade in.
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { full -> full / 4 },
                    animationSpec = tween(durationMillis = 420)
                ) + fadeIn(animationSpec = tween(durationMillis = 280))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 200))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { full -> full / 4 },
                    animationSpec = tween(durationMillis = 320)
                ) + fadeOut(animationSpec = tween(durationMillis = 220))
            }
        ) {
            AiChatScreen(navController = navController)
        }
    }
}
