package ai.lufious.app.core.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class PlantEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val nickname: String,
    val species: String,
    val photoUrl: String?,
    val locationTag: String,
    val wateringIntervalDays: Int,
    val fertilizingIntervalDays: Int,
    val lastWatered: Long,
    val lastFertilized: Long,
    val addedAt: Long,
    val healthStatus: String,
    val cachedAt: Long
)
