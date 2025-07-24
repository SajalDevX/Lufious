package ai.lufious.app.presentation.auth.login.viewmodel


sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object Submit : LoginEvent()
    object GoogleSignInClicked : LoginEvent()
    object FacebookSignInClicked : LoginEvent()
    data class GoogleSignInResult(val idToken: String) : LoginEvent()
    data class FacebookSignInResult(val accessToken: String) : LoginEvent()
}
