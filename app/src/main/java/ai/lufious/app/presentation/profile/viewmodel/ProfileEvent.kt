package ai.lufious.app.presentation.profile.viewmodel

sealed class ProfileEvent {
    object Logout : ProfileEvent()
}
