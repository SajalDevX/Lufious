package ai.lufious.app.presentation.splash.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.UiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val cache: LocalCacheManager,
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
                    val user = cache.getUser()
                    _isReady.value = true
                    if (user != null) emitEffect(UiEffect.Navigate("home"))
                    else emitEffect(UiEffect.Navigate("onboarding"))
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
