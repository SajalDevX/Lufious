package ai.lufious.app.navgraph

import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.MAIN_GRAPH
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) MAIN_GRAPH else AUTH_GRAPH
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(MAIN_GRAPH) {
                popUpTo(AUTH_GRAPH) { inclusive = true }
            }
        } else {
            navController.navigate(AUTH_GRAPH) {
                popUpTo(MAIN_GRAPH) { inclusive = true }
            }
        }
    }


}