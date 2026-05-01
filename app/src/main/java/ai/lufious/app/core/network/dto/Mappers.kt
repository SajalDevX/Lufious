package ai.lufious.app.core.network.dto

import ai.lufious.app.core.db.entity.PlantEntity
import ai.lufious.app.presentation.garden.data.models.CareLogModel
import ai.lufious.app.presentation.garden.data.models.PlantModel

fun PlantDto.toModel(): PlantModel = PlantModel(
    id = id,
    nickname = nickname,
    species = species,
    photoUrl = photoUrl ?: "",
    locationTag = locationTag,
    wateringIntervalDays = wateringIntervalDays,
    fertilizingIntervalDays = fertilizingIntervalDays,
    lastWatered = lastWatered,
    lastFertilized = lastFertilized,
    addedAt = addedAt,
    healthStatus = healthStatus
)

fun PlantDto.toEntity(userId: String, now: Long = System.currentTimeMillis()): PlantEntity =
    PlantEntity(
        id = id,
        userId = userId,
        nickname = nickname,
        species = species,
        photoUrl = photoUrl,
        locationTag = locationTag,
        wateringIntervalDays = wateringIntervalDays,
        fertilizingIntervalDays = fertilizingIntervalDays,
        lastWatered = lastWatered,
        lastFertilized = lastFertilized,
        addedAt = addedAt,
        healthStatus = healthStatus,
        cachedAt = now
    )

fun PlantEntity.toModel(): PlantModel = PlantModel(
    id = id,
    nickname = nickname,
    species = species,
    photoUrl = photoUrl ?: "",
    locationTag = locationTag,
    wateringIntervalDays = wateringIntervalDays,
    fertilizingIntervalDays = fertilizingIntervalDays,
    lastWatered = lastWatered,
    lastFertilized = lastFertilized,
    addedAt = addedAt,
    healthStatus = healthStatus
)

fun CareLogDto.toModel(): CareLogModel = CareLogModel(
    id = id,
    type = type,
    note = note,
    timestamp = timestamp
)
