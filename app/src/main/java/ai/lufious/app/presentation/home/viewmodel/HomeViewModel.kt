package ai.lufious.app.presentation.home.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.toModel
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: LufiousApi,
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
                val cachedUser = localCache.getUser()
                val fallbackName = cachedUser?.displayName
                    ?: cachedUser?.email?.substringBefore("@")
                    ?: "Gardener"
                setState { copy(userName = fallbackName, isLoading = true, errorMessage = null) }

                ioLaunch {
                    runCatching { api.homeDashboard() }
                        .onSuccess { dash ->
                            val name = dash.user?.displayName
                                ?: dash.user?.name
                                ?: dash.user?.email?.substringBefore("@")
                                ?: fallbackName
                            setState {
                                copy(
                                    userName = name,
                                    totalPlants = dash.totalPlants,
                                    plantsNeedingWater = dash.needsWater.map { it.toModel() },
                                    recentPlants = dash.recentPlants.map { it.toModel() },
                                    weatherAlertsCount = dash.weatherAlertsCount,
                                    aiTip = dash.aiTip?.content,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            }
                        }
                        .onFailure { e ->
                            setState {
                                copy(isLoading = false, errorMessage = e.message ?: "Failed to load dashboard")
                            }
                        }
                }
            }
        }
    }
}
