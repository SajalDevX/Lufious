package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.shop.data.usecases.GetListingsUseCase
import ai.lufious.app.presentation.shop.data.usecases.GetWishlistIdsUseCase
import ai.lufious.app.presentation.shop.data.usecases.ToggleWishlistUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val getListings: GetListingsUseCase,
    private val getWishlistIds: GetWishlistIdsUseCase,
    private val toggleWishlist: ToggleWishlistUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<ShopEvent, ShopState>(ShopState(), dispatchers) {

    init {
        loadMarketplace()
    }

    fun onEvent(event: ShopEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ShopEvent) {
        when (event) {
            is ShopEvent.CategoryChanged -> {
                setState { copy(selectedCategory = event.category) }
                loadMarketplace()
            }

            ShopEvent.Refresh -> loadMarketplace()

            is ShopEvent.ToggleWishlist -> {
                ioLaunch {
                    when (val result = toggleWishlist(event.listingId)) {
                        is Result.Success -> {
                            val updated = state.value.wishlistIds.toMutableSet().apply {
                                if (result.data == true) add(event.listingId) else remove(event.listingId)
                            }
                            setState { copy(wishlistIds = updated) }
                        }

                        is Result.Error ->
                            emitEffect(
                                UiEffect.ShowError(
                                    result.message ?: "Failed to update wishlist"
                                )
                            )
                    }
                }
            }
        }
    }

    private fun loadMarketplace() {
        setState { copy(isLoading = true, error = null) }
        ioLaunch {
            val listingsResult = getListings(state.value.selectedCategory)
            val wishlistResult = getWishlistIds()

            when (listingsResult) {
                is Result.Success -> {
                    val wishlistIds = when (wishlistResult) {
                        is Result.Success -> wishlistResult.data?.toSet() ?: emptySet()
                        is Result.Error -> emptySet()
                    }
                    setState {
                        copy(
                            listings = listingsResult.data ?: emptyList(),
                            wishlistIds = wishlistIds,
                            isLoading = false,
                            error = null
                        )
                    }
                    if (wishlistResult is Result.Error) {
                        emitEffect(
                            UiEffect.ShowError(
                                wishlistResult.message ?: "Failed to load wishlist"
                            )
                        )
                    }
                }

                is Result.Error ->
                    setState {
                        copy(
                            isLoading = false,
                            error = listingsResult.message ?: "Failed to load marketplace"
                        )
                    }
            }
        }
    }
}
