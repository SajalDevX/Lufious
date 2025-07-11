package ai.lufious.app.presentation.auth.login.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.core.utils.UiEffect.*
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.presentation.auth.data.usecases.LoginUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidateEmailUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<LoginEvent, LoginState>(
    initialState = LoginState(),
    dispatchers = dispatchers,
) {
    override suspend fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Submit -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = loginUseCase(state.value.email, state.value.password)) {
                        is Result.Success -> {
                            setState { copy(isLoading = false) }
                            emitEffect(Navigate("home"))
                        }

                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(
                                ShowError(
                                    res.message
                                        ?: "Unknown error"
                                )
                            )
                        }
                    }
                }
            }

            is LoginEvent.EmailChanged -> {
                val emailValid = validateEmail(event.email)
                setState {
                    copy(
                        email = event.email,
                        emailValidation = emailValid,
                        isSubmitEnabled = emailValid is ValidationResult.Valid && passwordValidation is ValidationResult.Valid
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                val pwdValid = validatePassword(event.password)
                setState {
                    copy(
                        password = event.password,
                        passwordValidation = pwdValid,
                        isSubmitEnabled = pwdValid is ValidationResult.Valid
                                && emailValidation is ValidationResult.Valid
                    )
                }
            }
        }
    }
}