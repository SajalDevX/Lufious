package ai.lufious.app.presentation.garden.data.datasource

import ai.lufious.app.core.db.dao.PlantDao
import ai.lufious.app.core.local_cache.LocalCacheManager
import ai.lufious.app.core.network.LufiousApi
import ai.lufious.app.core.network.dto.CareLogCreateRequest
import ai.lufious.app.core.network.dto.PlantCreateRequest
import ai.lufious.app.core.network.dto.PlantPatchRequest
import ai.lufious.app.core.network.dto.toEntity
import ai.lufious.app.core.network.dto.toModel
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantApiDataSource @Inject constructor(
    private val api: LufiousApi,
    private val dao: PlantDao,
    private val localCache: LocalCacheManager
) {
    private val uid get() = localCache.getUser()?.uid ?: error("User not logged in")

    suspend fun refreshPlants(): List<PlantModel> {
        val res = api.listPlants()
        val userId = uid
        dao.clearForUser(userId)
        dao.upsertAll(res.items.map { it.toEntity(userId) })
        return res.items.map { it.toModel() }
    }

    suspend fun cachedPlants(): List<PlantModel> =
        dao.listForUser(uid).map { it.toModel() }

    suspend fun getPlants(): List<PlantModel> {
        val cached = cachedPlants()
        return runCatching { refreshPlants() }.getOrDefault(cached)
    }

    suspend fun getPlantById(plantId: String): PlantModel {
        val dto = api.getPlant(plantId)
        dao.upsert(dto.toEntity(uid))
        return dto.toModel()
    }

    suspend fun addPlant(plant: PlantModel): PlantModel {
        val dto = api.createPlant(
            PlantCreateRequest(
                nickname = plant.nickname,
                species = plant.species,
                photoUrl = plant.photoUrl.takeIf { it.isNotEmpty() },
                locationTag = plant.locationTag,
                wateringIntervalDays = plant.wateringIntervalDays,
                fertilizingIntervalDays = plant.fertilizingIntervalDays
            )
        )
        dao.upsert(dto.toEntity(uid))
        return dto.toModel()
    }

    suspend fun patchPlant(plantId: String, patch: PlantPatchRequest): PlantModel {
        val dto = api.patchPlant(plantId, patch)
        dao.upsert(dto.toEntity(uid))
        return dto.toModel()
    }

    suspend fun deletePlant(plantId: String) {
        api.deletePlant(plantId)
        dao.deleteById(plantId)
    }

    suspend fun addCareLog(plantId: String, log: CareLogModel): CareLogModel =
        api.addLog(plantId, CareLogCreateRequest(type = log.type, note = log.note)).toModel()

    suspend fun getCareLogs(plantId: String): List<CareLogModel> =
        api.listLogs(plantId).items.map { it.toModel() }
}
