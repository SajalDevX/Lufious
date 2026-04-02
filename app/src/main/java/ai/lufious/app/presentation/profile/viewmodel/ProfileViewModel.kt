package ai.lufious.app.presentation.profile.viewmodel

import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.utils.AUTH_GRAPH
import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.auth.data.usecases.LogoutUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logout: LogoutUseCase,
    private val localCache: LocalCacheManager,
    dispatchers: DispatcherProvider
) : BaseViewModel<ProfileEvent, ProfileState>(ProfileState(), dispatchers) {

    init {
        val user = localCache.getUser()
        setState {
            copy(
                displayName = user?.displayName ?: user?.email?.substringBefore("@") ?: "Gardener",
                email = user?.email ?: ""
            )
        }
    }

    fun onEvent(event: ProfileEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Logout -> {
                setState { copy(isLoading = true) }
                logout()
                emitEffect(UiEffect.Navigate(AUTH_GRAPH))
            }
        }
    }
}
