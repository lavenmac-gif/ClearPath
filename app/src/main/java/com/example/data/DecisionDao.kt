package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
    @Query("SELECT * FROM decisions ORDER BY createdAt DESC")
    fun getAllDecisions(): Flow<List<DecisionEntity>>

    @Query("SELECT * FROM decisions WHERE id = :id LIMIT 1")
    fun getDecisionById(id: Long): Flow<DecisionEntity?>

    @Query("SELECT * FROM decisions WHERE id = :id LIMIT 1")
    suspend fun getDecisionOnce(id: Long): DecisionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecision(decision: DecisionEntity): Long

    @Update
    suspend fun updateDecision(decision: DecisionEntity)

    @Query("SELECT * FROM decisions ORDER BY createdAt DESC")
    suspend fun getAllDecisionsOnce(): List<DecisionEntity>

    @Delete
    suspend fun deleteDecision(decision: DecisionEntity)

    @Query("DELETE FROM decisions WHERE id = :id")
    suspend fun deleteDecisionById(id: Long)

    @Query("DELETE FROM decisions")
    suspend fun deleteAllDecisions()
}
