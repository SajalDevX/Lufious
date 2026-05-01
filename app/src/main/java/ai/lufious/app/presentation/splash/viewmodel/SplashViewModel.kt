package ai.lufious.app.presentation.splash.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.Screen
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.auth.data.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val cache: LocalCacheManager,
    private val firebaseAuth: FirebaseAuth,
    dispatchers: DispatcherProvider
) : BaseViewModel<SplashEvent, SplashState>(
    initialState = SplashState(),
    dispatchers = dispatchers
) {

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    override suspend fun handleEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.CheckAuth -> {
                setState { copy(isLoading = true) }
                ioLaunch {
                    val cachedUser = cache.getUser()
                    val firebaseUser = firebaseAuth.currentUser

                    val user: UserModel? = when {
                        cachedUser != null -> cachedUser
                        firebaseUser != null -> {
                            val rebuilt = UserModel(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                                displayName = firebaseUser.displayName,
                                phoneNumber = firebaseUser.phoneNumber,
                                photoUrl = firebaseUser.photoUrl?.toString(),
                                creationTimestamp = firebaseUser.metadata?.creationTimestamp,
                                lastSignInTimestamp = firebaseUser.metadata?.lastSignInTimestamp
                            )
                            cache.saveUser(rebuilt)
                            rebuilt
                        }
                        else -> null
                    }

                    _isReady.value = true
                    if (user != null) {
                        if (cache.isPostOnboardingComplete()) {
                            emitEffect(UiEffect.Navigate(MAIN_GRAPH))
                        } else {
                            emitEffect(UiEffect.Navigate(Screen.PostOnboarding.route))
                        }
                    } else {
                        emitEffect(UiEffect.Navigate("onboarding"))
                    }
                }
            }
        }
    }
}

sealed class SplashEvent {
    object CheckAuth : SplashEvent()
}

data class SplashState(
    val isLoading: Boolean = false
)
