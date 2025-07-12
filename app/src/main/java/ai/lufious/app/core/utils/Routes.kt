package ai.lufious.app.core.utils

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object GetStarted : Screen("get_started")
    object Login    : Screen("auth/login")
    object Signup : Screen("signup")
    object Home     : Screen("main/home")
    object Profile  : Screen("main/profile")
}
const val AUTH_GRAPH = "auth_graph"
const val MAIN_GRAPH = "main_graph"
