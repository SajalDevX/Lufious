package ai.lufious.app.presentation.onboarding.viewmodel

sealed class PostOnboardingEvent {
    object NextStep : PostOnboardingEvent()
    object Complete : PostOnboardingEvent()
}
