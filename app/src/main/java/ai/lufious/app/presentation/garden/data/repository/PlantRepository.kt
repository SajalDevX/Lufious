package ai.lufious.app.presentation.garden.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel

interface PlantRepository {
    suspend fun addPlant(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel>

    suspend fun getPlants(): Result<List<PlantModel>>

    suspend fun getPlantById(plantId: String): Result<PlantModel>

    suspend fun logCareAction(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel>

    suspend fun getCareLogs(plantId: String): Result<List<CareLogModel>>
}
