package ai.lufious.app.presentation.garden.data.repository

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.datasource.PlantDataSource
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class PlantRepositoryImpl @Inject constructor(
    private val ds: PlantDataSource
) : PlantRepository {

    override suspend fun addPlant(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel> = wrap {
        ds.addPlant(
            PlantModel(
                nickname = nickname,
                species = species,
                locationTag = locationTag,
                wateringIntervalDays = wateringIntervalDays
            )
        )
    }

    override suspend fun getPlants(): Result<List<PlantModel>> = wrap { ds.getPlants() }

    override suspend fun getPlantById(plantId: String): Result<PlantModel> =
        wrap { ds.getPlantById(plantId) }

    override suspend fun logCareAction(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel> = wrap {
        ds.addCareLog(plantId, CareLogModel(type = type, note = note))
    }

    override suspend fun getCareLogs(plantId: String): Result<List<CareLogModel>> =
        wrap { ds.getCareLogs(plantId) }

    private suspend fun <T> wrap(block: suspend () -> T): Result<T> =
        try {
            Result.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred")
        }
}
