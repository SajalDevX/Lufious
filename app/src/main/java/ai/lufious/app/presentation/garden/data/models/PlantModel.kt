package ai.lufious.app.presentation.garden.data.models

data class PlantModel(
    val id: String = "",
    val nickname: String = "",
    val species: String = "",
    val photoUrl: String = "",
    val locationTag: String = "Living Room",
    val wateringIntervalDays: Int = 7,
    val fertilizingIntervalDays: Int = 30,
    val lastWatered: Long = 0L,
    val lastFertilized: Long = 0L,
    val addedAt: Long = 0L,
    val healthStatus: String = "healthy"
)
