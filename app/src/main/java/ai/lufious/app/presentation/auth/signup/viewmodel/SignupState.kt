package ai.lufious.app.presentation.auth.signup.viewmodel

import ai.lufious.app.core.utils.ValidationResult

data class SignupState(
    val email: String = "",
    val password: String = "",
    val emailValidation: ValidationResult = ValidationResult.Valid,
    val passwordValidation: ValidationResult = ValidationResult.Valid,
    val isSubmitEnabled: Boolean = false,
    val isLoading: Boolean = false
)