package ai.lufious.app.core.utils

sealed class ValidationResult {
    object Valid: ValidationResult()
    data class Invalid(val reason: String): ValidationResult()
}