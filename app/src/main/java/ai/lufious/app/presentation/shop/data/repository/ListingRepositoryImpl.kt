package ai.lufious.app.presentation.shop.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.shop.data.datasource.ListingDataSource
import ai.lufious.app.presentation.shop.data.models.ListingModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class ListingRepositoryImpl @Inject constructor(
    private val dataSource: ListingDataSource
) : ListingRepository {

    override suspend fun getListings(category: String?): Result<List<ListingModel>> =
        wrap { dataSource.getListings(category) }

    override suspend fun createListing(
        title: String,
        description: String,
        price: Double,
        category: String
    ): Result<ListingModel> = wrap {
        dataSource.createListing(
            ListingModel(
                title = title,
                description = description,
                price = price,
                category = category
            )
        )
    }

    override suspend fun getListingById(listingId: String): Result<ListingModel> =
        wrap { dataSource.getListingById(listingId) }

    override suspend fun toggleWishlist(listingId: String): Result<Boolean> =
        wrap { dataSource.toggleWishlist(listingId) }

    override suspend fun getWishlistIds(): Result<List<String>> =
        wrap { dataSource.getWishlistIds() }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
}
