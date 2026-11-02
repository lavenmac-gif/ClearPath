package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = JsonAdapters.stringListToJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = JsonAdapters.stringListFromJson(value)

    @TypeConverter
    fun fromRiskList(value: List<RiskItem>): String = JsonAdapters.riskListToJson(value)

    @TypeConverter
    fun toRiskList(value: String): List<RiskItem> = JsonAdapters.riskListFromJson(value)

    @TypeConverter
    fun fromSafetyNetList(value: List<SafetyNetAction>): String = JsonAdapters.safetyNetListToJson(value)

    @TypeConverter
    fun toSafetyNetList(value: String): List<SafetyNetAction> = JsonAdapters.safetyNetListFromJson(value)

    @TypeConverter
    fun fromMissingInfoList(value: List<MissingInfoItem>): String = JsonAdapters.missingInfoListToJson(value)

    @TypeConverter
    fun toMissingInfoList(value: String): List<MissingInfoItem> = JsonAdapters.missingInfoListFromJson(value)

    @TypeConverter
    fun fromFactList(value: List<StructuredFactItem>): String = JsonAdapters.factListToJson(value)

    @TypeConverter
    fun toFactList(value: String): List<StructuredFactItem> = JsonAdapters.factListFromJson(value)

    @TypeConverter
    fun fromConcernFilterList(value: List<ConcernFilterItem>): String = JsonAdapters.concernFilterListToJson(value)

    @TypeConverter
    fun toConcernFilterList(value: String): List<ConcernFilterItem> = JsonAdapters.concernFilterListFromJson(value)

    @TypeConverter
    fun fromMatrixList(value: List<MatrixRowItem>): String = JsonAdapters.matrixListToJson(value)

    @TypeConverter
    fun toMatrixList(value: String): List<MatrixRowItem> = JsonAdapters.matrixListFromJson(value)
}

@Entity(tableName = "decisions")
@TypeConverters(Converters::class)
data class DecisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val situationContext: String = "",
    val speedMode: String = "DEEP", // "QUICK", "RAPID", "DEEP", "LATER"
    val reversibility: String = "Partially reversible", // "Easily reversible", "Partially reversible", "Difficult to reverse", "Effectively irreversible"
    val options: List<String> = emptyList(),
    val mainConcerns: String = "",
    val biggestUpside: String = "",
    val biggestConcern: String = "",
    val risks: List<RiskItem> = emptyList(),
    val priorities: List<String> = emptyList(),
    val missingInfoItems: List<MissingInfoItem> = emptyList(),
    val safetyNets: List<SafetyNetAction> = emptyList(),
    val smallExperiment: String = "",
    val aiAnalysis: String = "",
    val holdingBackInsight: String = "",
    val whatSeemsManageable: String = "",
    val whatDeservesChecking: String = "",
    val finalChoice: String = "", // e.g., "Higher-paying internship" or "Still unsure"
    val reasoning: String = "",
    val confidenceLevel: Int = 3, // 1..5
    val userNotes: String = "",
    val whatIfChoiceFuture: String = "",
    val whatIfOtherFuture: String = "",
    val isDecided: Boolean = false,
    val isHighStakes: Boolean = false,
    val isOverthinkingDetected: Boolean = false,
    val stuckReason: String = "TRADE_OFF_GAP",
    val stuckReasonCategory: String = StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD,
    val confidencePercent: Int = 70, // 0..100%
    val widenedOptions: List<String> = emptyList(),
    val premortemFailureStory: String = "",
    val premortemFailureCauses: List<String> = emptyList(),
    val whatWouldChangeMindItems: List<String> = emptyList(),
    val isUncertaintyNotMissingInfo: Boolean = false,
    val goodEnoughCriteria: List<String> = emptyList(),
    val reflection10Minutes: String = "",
    val reflection10Months: String = "",
    val reflection10Years: String = "",
    val reopenConditions: List<String> = emptyList(),
    val reopenHistory: String = "",
    val detectedThinkingPattern: String = "",
    val thinkingPatternEvidence: String = "",
    val thinkingPatternQuestion: String = "",
    val isThinkingPatternDismissed: Boolean = false,
    val similarPastDecisionId: Long? = null,
    val similarPastDecisionTitle: String? = null,
    val mustHaves: List<String> = emptyList(),
    val niceToHaves: List<String> = emptyList(),
    val structuredFacts: List<StructuredFactItem> = emptyList(),
    val concernFilterItems: List<ConcernFilterItem> = emptyList(),
    val matrixRows: List<MatrixRowItem> = emptyList(),
    val decisionDepth: String = "GUIDED", // "QUICK", "GUIDED", "DEEP"
    val pauseReason: String = "",
    val reopenReason: String = "",
    val smallestMissingInfo: String = "",
    val isInformationGap: Boolean = false,
    val whatIfScenarioParked: Boolean = false,
    val waitingForReason: String = "",
    val reminderDateMillis: Long? = null,
    val deadlineDateMillis: Long? = null,
    val clearPathReflection: String = "",
    val learningNotes: String = "",
    val reviewOutcome: String? = null, // "Much better than expected", "Better than expected", "About what I expected", "Worse than expected", "Much worse than expected"
    val reviewReflection: String? = null,
    val reviewedAt: Long? = null,
    val status: String = "Decide Later", // "Decide Later", "In Progress", "Ready to Decide", "Decided", "Reviewed"
    val savedStep: Int = 1,
    val politeScripts: List<String> = emptyList(),
    val contentDiscussion: String = "",
    val fearAddressed: String = "",
    val isDilemmaReframed: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
