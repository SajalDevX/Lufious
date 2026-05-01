package ai.lufious.app.presentation.shop.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.shop.data.models.ListingCategory
import ai.lufious.app.presentation.shop.data.models.ListingModel
import ai.lufious.app.presentation.shop.data.repository.ListingRepository
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(private val repository: ListingRepository) {
    suspend operator fun invoke(category: String): Result<List<ListingModel>> {
        val filter = category.takeUnless { it == ListingCategory.ALL }
        return repository.getListings(filter)
    }
}

class CreateListingUseCase @Inject constructor(private val repository: ListingRepository) {
    suspend operator fun invoke(
        title: String,
        description: String,
        price: Double,
        category: String
    ): Result<ListingModel> = repository.createListing(title, description, price, category)
}

class GetListingByIdUseCase @Inject constructor(private val repository: ListingRepository) {
    suspend operator fun invoke(listingId: String): Result<ListingModel> =
        repository.getListingById(listingId)
}

class ToggleWishlistUseCase @Inject constructor(private val repository: ListingRepository) {
    suspend operator fun invoke(listingId: String): Result<Boolean> =
        repository.toggleWishlist(listingId)
}

class GetWishlistIdsUseCase @Inject constructor(private val repository: ListingRepository) {
    suspend operator fun invoke(): Result<List<String>> = repository.getWishlistIds()
}
