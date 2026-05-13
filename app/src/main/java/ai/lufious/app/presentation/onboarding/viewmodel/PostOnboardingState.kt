package ai.lufious.app.presentation.onboarding.viewmodel

const val TOTAL_ONBOARDING_STEPS = 10

data class PostOnboardingState(
    val currentStep: Int = 0,
    val gardenerLevel: String? = null,
    val interestCategories: Set<String> = emptySet(),
    val gardenerGoal: String? = null,
    val climateZone: String? = null,
    val livingSpace: String? = null,
    val selectedSpeciesIds: Set<String> = emptySet(),
    val isCompleting: Boolean = false
)
