package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.flow.Flow

@JsonClass(generateAdapter = true)
@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityType: String = "DECISION", // "DECISION", "OUTCOME_REVIEW", "NOTE"
    val entityId: Long = 0,
    val operation: String = "UPDATE", // "CREATE", "UPDATE", "DELETE"
    val payloadJson: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val lastError: String? = null,
    val syncStatus: String = "PENDING" // "PENDING", "SYNCING", "FAILED", "SYNCED"
)

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE syncStatus = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingItemsFlow(): Flow<List<SyncQueueItem>>

    @Query("SELECT * FROM sync_queue WHERE syncStatus = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingItems(): List<SyncQueueItem>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE syncStatus = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueItem): Long

    @Update
    suspend fun update(item: SyncQueueItem)

    @Delete
    suspend fun delete(item: SyncQueueItem)

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sync_queue WHERE syncStatus = 'SYNCED'")
    suspend fun clearCompleted()

    @Query("DELETE FROM sync_queue")
    suspend fun clearAll()
}
