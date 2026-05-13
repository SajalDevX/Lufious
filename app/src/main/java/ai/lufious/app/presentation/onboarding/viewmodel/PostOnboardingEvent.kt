package ai.lufious.app.presentation.onboarding.viewmodel

sealed class PostOnboardingEvent {
    object NextStep : PostOnboardingEvent()
    object PrevStep : PostOnboardingEvent()
    data class GoToStep(val step: Int) : PostOnboardingEvent()
    object Complete : PostOnboardingEvent()
    data class SaveLocation(val lat: Double, val lon: Double) : PostOnboardingEvent()
    data class SetLevel(val level: String) : PostOnboardingEvent()
    data class ToggleInterest(val category: String) : PostOnboardingEvent()
    data class SetGoal(val goal: String) : PostOnboardingEvent()
    data class SetClimate(val zone: String) : PostOnboardingEvent()
    data class SetSpace(val space: String) : PostOnboardingEvent()
    data class ToggleSpecies(val speciesId: String) : PostOnboardingEvent()
}
