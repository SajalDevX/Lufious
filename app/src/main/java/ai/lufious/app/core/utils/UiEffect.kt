package ai.lufious.app.core.utils

sealed class UiEffect {
    data class ShowError(val message: String): UiEffect()
    data class Navigate(val route: String): UiEffect()
}
object LaunchGoogleSignIn : UiEffect()
object LaunchFacebookSignIn : UiEffect()