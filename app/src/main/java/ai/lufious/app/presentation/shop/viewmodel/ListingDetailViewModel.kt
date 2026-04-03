package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.shop.data.usecases.GetListingByIdUseCase
import ai.lufious.app.presentation.shop.data.usecases.GetWishlistIdsUseCase
import ai.lufious.app.presentation.shop.data.usecases.ToggleWishlistUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getListingById: GetListingByIdUseCase,
    private val getWishlistIds: GetWishlistIdsUseCase,
    private val toggleWishlistUseCase: ToggleWishlistUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<ListingDetailEvent, ListingDetailState>(ListingDetailState(), dispatchers) {

    private val listingId: String = savedStateHandle["listingId"] ?: ""

    init {
        loadListing()
    }

    fun onEvent(event: ListingDetailEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: ListingDetailEvent) {
        when (event) {
            ListingDetailEvent.ToggleWishlist -> toggleWishlist()
        }
    }

    private fun loadListing() {
        setState { copy(isLoading = true, error = null) }
        ioLaunch {
            val listingResult = getListingById(listingId)
            val wishlistResult = getWishlistIds()

            when (listingResult) {
                is Result.Success -> {
                    val wishlisted = when (wishlistResult) {
                        is Result.Success -> wishlistResult.data?.contains(listingId) == true
                        is Result.Error -> false
                    }
                    setState {
                        copy(
                            listing = listingResult.data,
                            isWishlisted = wishlisted,
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
                            error = listingResult.message ?: "Failed to load listing"
                        )
                    }
            }
        }
    }

    private fun toggleWishlist() {
        setState { copy(isWishlistLoading = true) }
        ioLaunch {
            when (val result = toggleWishlistUseCase(listingId)) {
                is Result.Success ->
                    setState {
                        copy(
                            isWishlisted = result.data == true,
                            isWishlistLoading = false
                        )
                    }

                is Result.Error -> {
                    setState { copy(isWishlistLoading = false) }
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
