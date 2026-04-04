package ai.lufious.app.presentation.onboarding.viewmodel

data class PostOnboardingState(
    val currentStep: Int = 0  // 0 = location, 1 = add plant prompt, 2 = notifications
)
