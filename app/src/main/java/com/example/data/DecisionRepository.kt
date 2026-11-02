package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.json.JSONArray
import org.json.JSONObject

class DecisionRepository(
    private val decisionDao: DecisionDao,
    private val syncQueueDao: SyncQueueDao? = null
) {
    val allDecisions: Flow<List<DecisionEntity>> = decisionDao.getAllDecisions()

    fun getDecisionById(id: Long): Flow<DecisionEntity?> = decisionDao.getDecisionById(id)

    suspend fun getDecisionOnce(id: Long): DecisionEntity? = decisionDao.getDecisionOnce(id)

    suspend fun getAllDecisionsOnce(): List<DecisionEntity> = decisionDao.getAllDecisionsOnce()

    suspend fun insert(decision: DecisionEntity): Long {
        val id = decisionDao.insertDecision(decision)
        syncQueueDao?.enqueue(
            SyncQueueItem(
                entityType = "DECISION",
                entityId = id,
                operation = "CREATE",
                payloadJson = decision.title
            )
        )
        return id
    }

    suspend fun update(decision: DecisionEntity) {
        decisionDao.updateDecision(decision)
        syncQueueDao?.enqueue(
            SyncQueueItem(
                entityType = "DECISION",
                entityId = decision.id,
                operation = "UPDATE",
                payloadJson = decision.title
            )
        )
    }

    suspend fun delete(decision: DecisionEntity) {
        decisionDao.deleteDecision(decision)
        syncQueueDao?.enqueue(
            SyncQueueItem(
                entityType = "DECISION",
                entityId = decision.id,
                operation = "DELETE"
            )
        )
    }

    suspend fun deleteById(id: Long) {
        decisionDao.deleteDecisionById(id)
        syncQueueDao?.enqueue(
            SyncQueueItem(
                entityType = "DECISION",
                entityId = id,
                operation = "DELETE"
            )
        )
    }

    suspend fun deleteAllData() {
        decisionDao.deleteAllDecisions()
        syncQueueDao?.clearAll()
    }

    fun getPendingSyncCount(): Flow<Int> =
        syncQueueDao?.getPendingCountFlow() ?: flowOf(0)

    suspend fun syncPendingQueue(isOnline: Boolean): Boolean {
        if (!isOnline || syncQueueDao == null) return false
        val pending = syncQueueDao.getPendingItems()
        if (pending.isEmpty()) return true

        for (item in pending) {
            if (item.retryCount >= 3) {
                syncQueueDao.update(item.copy(syncStatus = "FAILED", lastError = "Max retries exceeded"))
            } else {
                syncQueueDao.update(item.copy(syncStatus = "SYNCED"))
            }
        }
        syncQueueDao.clearCompleted()
        return true
    }

    suspend fun exportAllDecisionsAsJson(): String {
        val decisions = decisionDao.getAllDecisionsOnce()
        val array = JSONArray()
        for (d in decisions) {
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("title", d.title)
            obj.put("situationContext", d.situationContext)
            obj.put("status", d.status)
            obj.put("decisionDepth", d.decisionDepth)
            obj.put("finalChoice", d.finalChoice)
            obj.put("reasoning", d.reasoning)
            obj.put("confidenceLevel", d.confidenceLevel)
            obj.put("reversibility", d.reversibility)
            obj.put("stuckReasonCategory", d.stuckReasonCategory)
            obj.put("createdAt", d.createdAt)
            obj.put("updatedAt", d.updatedAt)
            obj.put("options", JSONArray(d.options))
            obj.put("mustHaves", JSONArray(d.mustHaves))
            obj.put("niceToHaves", JSONArray(d.niceToHaves))
            obj.put("reopenReason", d.reopenReason)
            obj.put("pauseReason", d.pauseReason)
            obj.put("clearPathReflection", d.clearPathReflection)
            obj.put("reviewOutcome", d.reviewOutcome ?: "")
            obj.put("reviewReflection", d.reviewReflection ?: "")
            obj.put("learningNotes", d.learningNotes)
            array.put(obj)
        }
        val exportWrapper = JSONObject()
        exportWrapper.put("appName", "ClearPath")
        exportWrapper.put("version", "2.0")
        exportWrapper.put("exportedAt", System.currentTimeMillis())
        exportWrapper.put("totalDecisions", decisions.size)
        exportWrapper.put("decisions", array)
        return exportWrapper.toString(2)
    }
}
