package ai.lufious.app.presentation.auth.signup.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.LaunchFacebookSignIn
import ai.lufious.app.core.utils.LaunchGoogleSignIn
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect.Navigate
import ai.lufious.app.core.utils.UiEffect.ShowError
import ai.lufious.app.core.utils.ValidationResult
import ai.lufious.app.presentation.auth.data.models.UserModel
import ai.lufious.app.presentation.auth.data.usecases.LoginWithFacebookUseCase
import ai.lufious.app.presentation.auth.data.usecases.SignupUseCase
import ai.lufious.app.presentation.auth.data.usecases.SignupWithGoogleUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidateEmailUseCase
import ai.lufious.app.presentation.auth.data.usecases.ValidatePasswordUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val signupUseCase: SignupUseCase,
    private val googleSignupUC: SignupWithGoogleUseCase,
    private val fbUC: LoginWithFacebookUseCase,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase,
    private val localCache: LocalCacheManager,
    private val api: LufiousApi,
    dispatchers: DispatcherProvider
) : BaseViewModel<SignupEvent, SignupState>(
    initialState = SignupState(),
    dispatchers = dispatchers
) {
    fun onEvent(event: SignupEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: SignupEvent) {
        when (event) {
            is SignupEvent.EmailChanged -> {
                val emailValid = validateEmail(event.email)
                val canSubmit = emailValid is ValidationResult.Valid &&
                        state.value.passwordValidation is ValidationResult.Valid
                setState {
                    copy(
                        email = event.email,
                        emailValidation = emailValid,
                        isSubmitEnabled = canSubmit
                    )
                }
            }

            is SignupEvent.PasswordChanged -> {
                val pwdValid = validatePassword(event.password)
                val canSubmit = pwdValid is ValidationResult.Valid &&
                        state.value.emailValidation is ValidationResult.Valid
                setState {
                    copy(
                        password = event.password,
                        passwordValidation = pwdValid,
                        isSubmitEnabled = canSubmit
                    )
                }
            }

            SignupEvent.Submit -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = signupUseCase(state.value.email, state.value.password)) {
                        is Result.Success -> {
                            res.data?.let { cacheAndNavigate(it) }
                                ?: run {
                                    setState { copy(isLoading = false) }
                                    emitEffect(ShowError("Sign-up succeeded but no user data"))
                                }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(ShowError(res.message ?: "Sign-up failed"))
                        }
                    }
                }
            }

            SignupEvent.GoogleSignUpClicked -> {
                if (state.value.isLoading) return
                setState { copy(isLoading = true) }
                emitEffect(LaunchGoogleSignIn)
            }

            SignupEvent.FacebookSignUpClicked -> {
                emitEffect(LaunchFacebookSignIn)
            }

            is SignupEvent.GoogleSignUpResult -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = googleSignupUC(event.idToken)) {
                        is Result.Success -> {
                            res.data?.let { cacheAndNavigate(it) }
                                ?: run {
                                    setState { copy(isLoading = false) }
                                    emitEffect(ShowError("Google sign-up succeeded but no user data"))
                                }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(ShowError(res.message.orEmpty()))
                        }
                    }
                }
            }

            is SignupEvent.FacebookSignUpResult -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (val res = fbUC(event.accessToken)) {
                        is Result.Success -> {
                            res.data?.let { cacheAndNavigate(it) }
                                ?: run {
                                    setState { copy(isLoading = false) }
                                    emitEffect(ShowError("Facebook sign-up succeeded but no user data"))
                                }
                        }
                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(ShowError(res.message ?: "Facebook sign-up failed"))
                        }
                    }
                }
            }
        }
    }

    /** Save user & fresh Firebase token, sync with backend, then navigate home **/
    private fun cacheAndNavigate(user: UserModel) {
        localCache.saveUser(user)
        viewModelScope.launch {
            com.google.firebase.auth.FirebaseAuth
                .getInstance()
                .currentUser
                ?.getIdToken(true)
                ?.let { task ->
                    runCatching { com.google.android.gms.tasks.Tasks.await(task) }
                        .getOrNull()
                        ?.token
                        ?.let(localCache::saveAuthToken)
                }
            runCatching { api.authSync() }
            emitEffect(Navigate("home"))
        }
    }
}
