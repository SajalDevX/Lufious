package ai.lufious.app.presentation.auth.login.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.LaunchFacebookSignIn
import ai.lufious.app.core.utils.LaunchGoogleSignIn
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect.*
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.presentation.auth.data.usecases.LoginUseCase
import ai.lufious.app.presentation.auth.data.usecases.LoginWithFacebookUseCase
import ai.lufious.app.presentation.auth.data.usecases.LoginWithGoogleUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidateEmailUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidatePasswordUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val googleUC: LoginWithGoogleUseCase,
    private val fbUC: LoginWithFacebookUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<LoginEvent, LoginState>(
    initialState = LoginState(),
    dispatchers = dispatchers,
) {
    fun onEvent(event: LoginEvent) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }


    override suspend fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                val emailValid = validateEmail(event.email)
                val canSubmit = emailValid is ValidationResult.Valid &&
                        state.value.passwordValidation is ValidationResult.Valid

                setState {
                    copy(
                        email = event.email,
                        emailValidation = emailValid,
                        isSubmitEnabled = canSubmit,
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                val pwdValid = validatePassword(event.password)
                val canSubmit = pwdValid is ValidationResult.Valid &&
                        state.value.emailValidation is ValidationResult.Valid

                setState {
                    copy(
                        password = event.password,
                        passwordValidation = pwdValid,
                        isSubmitEnabled = canSubmit,
                    )
                }
            }

            LoginEvent.Submit -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = loginUseCase(state.value.email, state.value.password)) {
                        is Result.Success -> emitEffect(Navigate("home"))
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(
                                ShowError(res.message ?: "Login failed")
                            )
                        }
                    }
                }
            }

            LoginEvent.GoogleSignInClicked -> {
                emitEffect(LaunchGoogleSignIn)
            }

            LoginEvent.FacebookSignInClicked -> {
                emitEffect(LaunchFacebookSignIn)
            }

            is LoginEvent.GoogleSignInResult -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = googleUC(event.idToken)) {
                        is Result.Success -> emitEffect(Navigate("home"))
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(ShowError(res.message ?: "Google login failed"))
                        }
                    }
                }
            }

            is LoginEvent.FacebookSignInResult -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = fbUC(event.accessToken)) {
                        is Result.Success -> emitEffect(Navigate("home"))
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(ShowError(res.message ?: "Facebook login failed"))
                        }
                    }
                }
            }
        }
    }
}