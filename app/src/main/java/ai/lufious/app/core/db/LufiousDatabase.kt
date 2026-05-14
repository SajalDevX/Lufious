package ai.lufious.app.core.db

import ai.lufious.app.core.db.dao.PlantDao
import ai.lufious.app.core.db.entity.PlantEntity
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlantEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LufiousDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
