package ai.lufious.app.presentation.onboarding.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.LocationPatchRequest
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.MAIN_GRAPH
import ai.lufious.app.core.utils.UiEffect
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.TimeZone
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PostOnboardingViewModel @Inject constructor(
    private val localCache: LocalCacheManager,
    private val api: LufiousApi,
    dispatchers: DispatcherProvider
) : BaseViewModel<PostOnboardingEvent, PostOnboardingState>(PostOnboardingState(), dispatchers) {

    fun onEvent(event: PostOnboardingEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: PostOnboardingEvent) {
        when (event) {
            PostOnboardingEvent.NextStep -> {
                val next = state.value.currentStep + 1
                setState { copy(currentStep = next) }
            }
            PostOnboardingEvent.Complete -> {
                localCache.setPostOnboardingComplete()
                emitEffect(UiEffect.Navigate(MAIN_GRAPH))
            }
            is PostOnboardingEvent.SaveLocation -> {
                ioLaunch {
                    runCatching {
                        api.patchLocation(
                            LocationPatchRequest(
                                lat = event.lat,
                                lon = event.lon,
                                timezone = TimeZone.getDefault().id
                            )
                        )
                    }
                }
            }
        }
    }
}
