package ai.lufious.app.presentation.shop.viewmodel

import ai.lufious.app.core.utils.BaseViewModel
import ai.lufious.app.core.utils.DispatcherProvider
import ai.lufious.app.core.utils.Result
import ai.lufious.app.core.utils.UiEffect
import ai.lufious.app.presentation.shop.data.usecases.CreateListingUseCase
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val createListing: CreateListingUseCase,
    dispatchers: DispatcherProvider
) : BaseViewModel<CreateListingEvent, CreateListingState>(CreateListingState(), dispatchers) {

    fun onEvent(event: CreateListingEvent) {
        viewModelScope.launch { handleEvent(event) }
    }

    override suspend fun handleEvent(event: CreateListingEvent) {
        when (event) {
            is CreateListingEvent.TitleChanged ->
                setState {
                    copy(title = event.value).withValidation()
                }

            is CreateListingEvent.DescriptionChanged ->
                setState {
                    copy(description = event.value).withValidation()
                }

            is CreateListingEvent.PriceChanged ->
                setState {
                    copy(price = event.value).withValidation()
                }

            is CreateListingEvent.CategoryChanged ->
                setState {
                    copy(category = event.value).withValidation()
                }

            CreateListingEvent.Submit -> {
                val price = state.value.price.toDoubleOrNull()
                if (price == null || price <= 0.0) {
                    emitEffect(UiEffect.ShowError("Enter a valid price"))
                    return
                }
                setState { copy(isLoading = true) }
                ioLaunch {
                    when (
                        val result = createListing(
                            title = state.value.title.trim(),
                            description = state.value.description.trim(),
                            price = price,
                            category = state.value.category
                        )
                    ) {
                        is Result.Success -> {
                            setState { copy(isLoading = false) }
                            emitEffect(UiEffect.Navigate("back"))
                        }

                        is Result.Error -> {
                            setState { copy(isLoading = false) }
                            emitEffect(
                                UiEffect.ShowError(
                                    result.message ?: "Failed to create listing"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun CreateListingState.withValidation(): CreateListingState {
        val validPrice = price.toDoubleOrNull()?.let { it > 0.0 } == true
        return copy(
            isSubmitEnabled = title.isNotBlank() &&
                description.isNotBlank() &&
                category.isNotBlank() &&
                validPrice
        )
    }
}
