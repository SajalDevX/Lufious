package ai.lufious.app.presentation.onboarding.viewmodel

sealed class PostOnboardingEvent {
    object NextStep : PostOnboardingEvent()
    object Complete : PostOnboardingEvent()
    data class SaveLocation(val lat: Double, val lon: Double) : PostOnboardingEvent()
}
