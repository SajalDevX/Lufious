package ai.lufious.app.presentation.garden.data.usecases

import ai.lufious.app.core.utils.Result
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import ai.lufious.app.presentation.garden.data.repository.PlantRepository
import jakarta.inject.Inject

class AddPlantUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(
        nickname: String,
        species: String,
        locationTag: String,
        wateringIntervalDays: Int
    ): Result<PlantModel> = repo.addPlant(nickname, species, locationTag, wateringIntervalDays)
}

class GetPlantsUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(): Result<List<PlantModel>> = repo.getPlants()
}

class GetPlantByIdUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(plantId: String): Result<PlantModel> =
        repo.getPlantById(plantId)
}

class LogCareActionUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(
        plantId: String,
        type: String,
        note: String
    ): Result<CareLogModel> = repo.logCareAction(plantId, type, note)
}

class GetCareLogsUseCase @Inject constructor(private val repo: PlantRepository) {
    suspend operator fun invoke(plantId: String): Result<List<CareLogModel>> =
        repo.getCareLogs(plantId)
}
