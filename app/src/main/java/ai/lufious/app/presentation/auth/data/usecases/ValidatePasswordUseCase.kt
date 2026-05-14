package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.utils.ValidationResult
import javax.inject.Inject

class ValidatePasswordUseCase @Inject constructor() {
    operator fun invoke(password: String) =
        if (password.length >= 6) ValidationResult.Valid
        else ValidationResult.Invalid("Min 6 chars")
}