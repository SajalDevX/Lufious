package ai.lufious.app.presentation.onboarding.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.LocationPatchRequest
import ai.lufious.app.core.network.dto.ProfilePatchRequest
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
) : BaseViewModel<PostOnboardingEvent, PostOnboardingState>(
    PostOnboardingState(
        currentStep = localCache.getOnboardingStep().coerceIn(0, TOTAL_ONBOARDING_STEPS - 1),
        gardenerLevel = localCache.getGardenerLevel(),
        interestCategories = localCache.getInterestCategories(),
        gardenerGoal = localCache.getGardenerGoal(),
        climateZone = localCache.getClimateZone(),
        livingSpace = localCache.getLivingSpace()
    ),
    dispatchers
) {

    fun onEvent(event: PostOnboardingEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: PostOnboardingEvent) {
        when (event) {
            PostOnboardingEvent.NextStep -> {
                val next = (state.value.currentStep + 1).coerceAtMost(TOTAL_ONBOARDING_STEPS - 1)
                persistStep(next)
                setState { copy(currentStep = next) }
            }
            PostOnboardingEvent.PrevStep -> {
                val prev = (state.value.currentStep - 1).coerceAtLeast(0)
                persistStep(prev)
                setState { copy(currentStep = prev) }
            }
            is PostOnboardingEvent.GoToStep -> {
                val s = event.step.coerceIn(0, TOTAL_ONBOARDING_STEPS - 1)
                persistStep(s)
                setState { copy(currentStep = s) }
            }
            PostOnboardingEvent.Complete -> {
                setState { copy(isCompleting = true) }
                localCache.setPostOnboardingComplete()
                pushPreferencesToBackend()
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
            is PostOnboardingEvent.SetLevel -> {
                localCache.setGardenerLevel(event.level)
                setState { copy(gardenerLevel = event.level) }
            }
            is PostOnboardingEvent.ToggleInterest -> {
                val updated = state.value.interestCategories.toMutableSet().apply {
                    if (!add(event.category)) remove(event.category)
                }
                localCache.setInterestCategories(updated)
                setState { copy(interestCategories = updated) }
            }
            is PostOnboardingEvent.SetGoal -> {
                localCache.setGardenerGoal(event.goal)
                setState { copy(gardenerGoal = event.goal) }
            }
            is PostOnboardingEvent.SetClimate -> {
                localCache.setClimateZone(event.zone)
                setState { copy(climateZone = event.zone) }
            }
            is PostOnboardingEvent.SetSpace -> {
                localCache.setLivingSpace(event.space)
                setState { copy(livingSpace = event.space) }
            }
            is PostOnboardingEvent.ToggleSpecies -> {
                val updated = state.value.selectedSpeciesIds.toMutableSet().apply {
                    if (!add(event.speciesId)) remove(event.speciesId)
                }
                setState { copy(selectedSpeciesIds = updated) }
            }
        }
    }

    private fun persistStep(step: Int) {
        localCache.setOnboardingStep(step)
    }

    private fun pushPreferencesToBackend() {
        val s = state.value
        val patch = ProfilePatchRequest(
            experienceLevel = s.gardenerLevel,
            followedCategories = s.interestCategories.toList().ifEmpty { null }
        )
        if (patch.experienceLevel == null && patch.followedCategories == null) return
        ioLaunch {
            runCatching { api.patchProfile(patch) }
        }
    }
}
