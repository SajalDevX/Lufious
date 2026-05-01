package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.usecases.GetPlantsUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPlants: GetPlantsUseCase,
    private val localCache: LocalCacheManager,
    dispatchers: DispatcherProvider
) : BaseViewModel<HomeEvent, HomeState>(HomeState(), dispatchers) {

    init {
        onEvent(HomeEvent.LoadDashboard)
    }

    fun onEvent(event: HomeEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadDashboard -> {
                val user = localCache.getUser()
                val userName = user?.displayName
                    ?: user?.email?.substringBefore("@")
                    ?: "Gardener"
                setState { copy(userName = userName, isLoading = true) }
                ioLaunch {
                    when (val result = getPlants()) {
                        is Result.Success -> {
                            val plants = result.data ?: emptyList()
                            val now = System.currentTimeMillis()
                            val needsWater = plants.filter { plant ->
                                plant.wateringIntervalDays > 0 &&
                                    (now - plant.lastWatered) >= plant.wateringIntervalDays * 86_400_000L
                            }
                            setState {
                                copy(
                                    totalPlants = plants.size,
                                    plantsNeedingWater = needsWater,
                                    isLoading = false
                                )
                            }
                        }
                        is Result.Error ->
                            setState { copy(isLoading = false) }
                    }
                }
            }
        }
    }
}
