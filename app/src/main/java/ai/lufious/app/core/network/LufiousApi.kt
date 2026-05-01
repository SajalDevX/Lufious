package ai.lufious.app.core.network

import ai.lufious.app.core.network.dto.CareLogCreateRequest
import ai.lufious.app.core.network.dto.CareLogDto
import ai.lufious.app.core.network.dto.CareLogListResponse
import ai.lufious.app.core.network.dto.HomeDashboardDto
import ai.lufious.app.core.network.dto.PlantCreateRequest
import ai.lufious.app.core.network.dto.PlantDto
import ai.lufious.app.core.network.dto.PlantListResponse
import ai.lufious.app.core.network.dto.PlantPatchRequest
import ai.lufious.app.core.network.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
}
