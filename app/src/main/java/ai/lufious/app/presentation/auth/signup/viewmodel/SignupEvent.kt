package ai.lufious.app.presentation.auth.signup.viewmodel


sealed class SignupEvent {
    data class EmailChanged(val email: String) : SignupEvent()
    data class PasswordChanged(val password: String) : SignupEvent()
    object Submit : SignupEvent()

    object GoogleSignUpClicked : SignupEvent()
    object FacebookSignUpClicked : SignupEvent()

    data class GoogleSignUpResult(val idToken: String) : SignupEvent()
    data class FacebookSignUpResult(val accessToken: String) : SignupEvent()
}
