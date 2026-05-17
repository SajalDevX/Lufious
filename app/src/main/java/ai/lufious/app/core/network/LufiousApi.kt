package ai.lufious.app.core.network

import ai.lufious.app.core.network.dto.CareLogCreateRequest
import ai.lufious.app.core.network.dto.CareLogDto
import ai.lufious.app.core.network.dto.CareLogListResponse
import ai.lufious.app.core.network.dto.HomeDashboardDto
import ai.lufious.app.core.network.dto.PlantCreateRequest
import ai.lufious.app.core.network.dto.PlantDto
import ai.lufious.app.core.network.dto.PlantListResponse
import ai.lufious.app.core.network.dto.PlantPatchRequest
import ai.lufious.app.core.network.dto.ListingCreateRequest
import ai.lufious.app.core.network.dto.ListingDto
import ai.lufious.app.core.network.dto.ListingPageDto
import ai.lufious.app.core.network.dto.ListingPatchRequest
import ai.lufious.app.core.network.dto.ScanCreateRequest
import ai.lufious.app.core.network.dto.ScanDto
import ai.lufious.app.core.network.dto.ScanListResponse
import ai.lufious.app.core.network.dto.ScanMessagePairDto
import ai.lufious.app.core.network.dto.ScanMessageRequest
import ai.lufious.app.core.network.dto.SignedUploadRequest
import ai.lufious.app.core.network.dto.SignedUploadResponse
import ai.lufious.app.core.network.dto.UserDto
import ai.lufious.app.core.network.dto.MessageCreateRequest
import ai.lufious.app.core.network.dto.MessageDto
import ai.lufious.app.core.network.dto.MessageListResponse
import ai.lufious.app.core.network.dto.ThreadCreateRequest
import ai.lufious.app.core.network.dto.ThreadDto
import ai.lufious.app.core.network.dto.ThreadListResponse
import ai.lufious.app.core.network.dto.AiTipDto
import ai.lufious.app.core.network.dto.FcmTokenRequest
import ai.lufious.app.core.network.dto.LocationPatchRequest
import ai.lufious.app.core.network.dto.NotificationPrefsDto
import ai.lufious.app.core.network.dto.NotificationPrefsPatchRequest
import ai.lufious.app.core.network.dto.ProfilePatchRequest
import ai.lufious.app.core.network.dto.WeatherDto
import ai.lufious.app.core.network.dto.WishlistResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface LufiousApi {

    @POST("api/auth/sync")
    suspend fun authSync(): UserDto

    @GET("api/dashboard/home")
    suspend fun homeDashboard(): HomeDashboardDto

    @GET("api/plants")
    suspend fun listPlants(): PlantListResponse

    @POST("api/plants")
    suspend fun createPlant(@Body body: PlantCreateRequest): PlantDto

    @GET("api/plants/{id}")
    suspend fun getPlant(@Path("id") id: String): PlantDto

    @PATCH("api/plants/{id}")
    suspend fun patchPlant(@Path("id") id: String, @Body body: PlantPatchRequest): PlantDto

    @HTTP(method = "DELETE", path = "api/plants/{id}", hasBody = false)
    suspend fun deletePlant(@Path("id") id: String): retrofit2.Response<Unit>

    @GET("api/plants/{id}/logs")
    suspend fun listLogs(@Path("id") id: String): CareLogListResponse

    @POST("api/plants/{id}/logs")
    suspend fun addLog(@Path("id") id: String, @Body body: CareLogCreateRequest): CareLogDto

    @POST("api/uploads/sign")
    suspend fun signUpload(@Body body: SignedUploadRequest): SignedUploadResponse

    @GET("api/scans")
    suspend fun listScans(): ScanListResponse

    @POST("api/scans")
    suspend fun createScan(@Body body: ScanCreateRequest): ScanDto

    @GET("api/scans/{id}")
    suspend fun getScan(@Path("id") id: String): ScanDto

    @POST("api/scans/{id}/messages")
    suspend fun postScanMessage(
        @Path("id") id: String,
        @Body body: ScanMessageRequest
    ): ScanMessagePairDto

    @GET("api/listings")
    suspend fun listListings(
        @Query("category") category: String? = null,
        @Query("q") q: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("sellerId") sellerId: String? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ListingPageDto

    @POST("api/listings")
    suspend fun createListing(@Body body: ListingCreateRequest): ListingDto

    @GET("api/listings/{id}")
    suspend fun getListing(@Path("id") id: String): ListingDto

    @PATCH("api/listings/{id}")
    suspend fun patchListing(@Path("id") id: String, @Body body: ListingPatchRequest): ListingDto

    @HTTP(method = "DELETE", path = "api/listings/{id}", hasBody = false)
    suspend fun deleteListing(@Path("id") id: String): retrofit2.Response<Unit>

    @GET("api/wishlist")
    suspend fun getWishlist(): WishlistResponse

    @POST("api/wishlist/{listingId}")
    suspend fun addToWishlist(@Path("listingId") listingId: String): WishlistResponse

    @HTTP(method = "DELETE", path = "api/wishlist/{listingId}", hasBody = false)
    suspend fun removeFromWishlist(@Path("listingId") listingId: String): WishlistResponse

    @GET("api/threads")
    suspend fun listThreads(): ThreadListResponse

    @POST("api/threads")
    suspend fun createThread(@Body body: ThreadCreateRequest): ThreadDto

    @GET("api/threads/{id}/messages")
    suspend fun listMessages(
        @Path("id") threadId: String,
        @Query("before") before: Long? = null
    ): MessageListResponse

    @POST("api/threads/{id}/messages")
    suspend fun postMessage(
        @Path("id") threadId: String,
        @Body body: MessageCreateRequest
    ): MessageDto

    @POST("api/threads/{id}/read")
    suspend fun markThreadRead(@Path("id") threadId: String): ThreadDto

    @GET("api/me/profile")
    suspend fun getProfile(): UserDto

    @PATCH("api/me/profile")
    suspend fun patchProfile(@Body body: ProfilePatchRequest): UserDto

    @PATCH("api/me/location")
    suspend fun patchLocation(@Body body: LocationPatchRequest): retrofit2.Response<Unit>

    @GET("api/me/preferences")
    suspend fun getPreferences(): NotificationPrefsDto

    @PATCH("api/me/preferences")
    suspend fun patchPreferences(@Body body: NotificationPrefsPatchRequest): NotificationPrefsDto

    @GET("api/weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherDto

    @GET("api/ai/tips")
    suspend fun getAiTip(): AiTipDto

    @POST("api/devices/fcm")
    suspend fun registerFcmToken(@Body body: FcmTokenRequest): retrofit2.Response<Unit>
}
