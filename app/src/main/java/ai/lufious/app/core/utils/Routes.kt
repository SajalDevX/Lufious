package ai.lufious.app.core.utils

sealed class Screen(val route: String) {
    object GetStarted : Screen("get_started")
    object Login    : Screen("auth/login")
    object Register : Screen("auth/register")
    object Home     : Screen("main/home")
    object Profile  : Screen("main/profile")
}
const val AUTH_GRAPH = "auth_graph"
const val MAIN_GRAPH = "main_graph"
