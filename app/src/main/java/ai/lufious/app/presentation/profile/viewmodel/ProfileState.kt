package ai.lufious.app.presentation.profile.viewmodel

data class ProfileState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false
)
