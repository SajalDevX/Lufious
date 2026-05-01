package ai.lufious.app.core.db.dao

import ai.lufious.app.core.db.entity.PlantEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants WHERE userId = :userId ORDER BY addedAt DESC")
    fun observeForUser(userId: String): Flow<List<PlantEntity>>

    @Query("SELECT * FROM plants WHERE userId = :userId ORDER BY addedAt DESC")
    suspend fun listForUser(userId: String): List<PlantEntity>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun findById(id: String): PlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<PlantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: PlantEntity)

    @Query("DELETE FROM plants WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM plants WHERE userId = :userId")
    suspend fun clearForUser(userId: String)
}
