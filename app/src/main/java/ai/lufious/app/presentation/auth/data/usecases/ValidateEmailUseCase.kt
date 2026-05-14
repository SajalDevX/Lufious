package ai.lufious.app.presentation.auth.data.usecases

import ai.lufious.app.core.utils.ValidationResult
import android.util.Patterns
import javax.inject.Inject

class ValidateEmailUseCase @Inject constructor() {
    operator fun invoke(email: String) =
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
            ValidationResult.Valid
        else
            ValidationResult.Invalid("Invalid email")
}