package com.example.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@JsonClass(generateAdapter = true)
data class RiskItem(
    val optionTitle: String = "",
    val description: String = "",
    val likelihood: String = "Possible", // Very unlikely, Unlikely, Possible, Likely, Very likely
    val impact: String = "Medium", // Low, Medium, High
    val worstRealisticOutcome: String = "",
    val recoverability: String = "Medium" // "Easy", "Medium", "Hard" (or "Yes", "Maybe", "No")
) {
    val normalizedRecoverability: String
        get() = when {
            recoverability.contains("Easy", ignoreCase = true) || recoverability.equals("Yes", ignoreCase = true) -> "Easy"
            recoverability.contains("Hard", ignoreCase = true) || recoverability.equals("No", ignoreCase = true) -> "Hard"
            else -> "Medium"
        }

    val riskCategory: String
        get() {
            val l = likelihood.lowercase()
            val imp = impact.lowercase()
            val rec = normalizedRecoverability.lowercase()

            return if ((imp.contains("high") && (l.contains("likely") || l.contains("high"))) ||
                (imp.contains("high") && rec.contains("hard"))) {
                "Needs attention"
            } else if (imp.contains("high") || l.contains("likely") || rec.contains("hard")) {
                "Important"
            } else if (l.contains("possible") || imp.contains("medium") || rec.contains("medium")) {
                "Worth checking"
            } else {
                "Low concern"
            }
        }

    val whyItMattersExplanation: String
        get() {
            val recText = when (normalizedRecoverability) {
                "Easy" -> "easy recoverability"
                "Hard" -> "difficult recoverability"
                else -> "only moderate recoverability"
            }
            return "$likelihood likelihood + ${impact.lowercase()} impact + $recText."
        }
}

@JsonClass(generateAdapter = true)
data class SafetyNetAction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val isChecked: Boolean = false
)

@JsonClass(generateAdapter = true)
data class MissingInfoItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val status: String = "NEED_TO_CHECK" // "KNOW_THIS", "NEED_TO_CHECK", "NOT_IMPORTANT"
)

@JsonClass(generateAdapter = true)
data class SmallExperiment(
    val title: String = "",
    val whatToLearn: String = "",
    val timeRequired: String = "",
    val whatWouldChangeDecision: String = ""
)

@JsonClass(generateAdapter = true)
data class TrustedPerspective(
    val name: String,
    val description: String,
    val questions: List<String>
)

@JsonClass(generateAdapter = true)
data class DecisionPatternItem(
    val title: String,
    val description: String,
    val category: String // "Risk Perception", "Priority Alignment", "Confidence Habit", "Learning"
)

@JsonClass(generateAdapter = true)
data class StructuredFactItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val category: String = "KNOWN" // "KNOWN", "ASSUMPTION", "UNKNOWN", "CONCERN", "EVIDENCE"
)

@JsonClass(generateAdapter = true)
data class ConcernFilterItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String = "",
    val resolutionImpact: String = "MAYBE", // "YES", "MAYBE", "NO"
    val status: String = "INVESTIGATE" // "ACTIVE", "INVESTIGATE", "PARKED"
)

@JsonClass(generateAdapter = true)
data class MatrixRowItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val factor: String = "",
    val importance: String = "High", // "High", "Medium", "Low"
    val optionANotes: String = "",
    val optionBNotes: String = "",
    val reasoning: String = ""
)

// Adaptive Decision Framework Models

object StuckStateCatalog {
    const val AFRAID_OF_CHOOSING_WRONG = "I'm afraid I'll choose wrong"
    const val BOTH_OPTIONS_SEEM_GOOD = "Both options seem good"
    const val MISSING_INFORMATION = "I need more information"
    const val KEEPS_CHANGING_MIND = "I keep changing my mind"
    const val DOESNT_KNOW_WANTS = "I don't know what I actually want"
    const val TOO_MANY_THINGS = "There are too many things to consider"
    const val QUICK_DECISION_NEEDED = "I just need a quick decision"
    const val NOT_SURE = "I'm not sure"

    val ALL_STUCK_STATES = listOf(
        AFRAID_OF_CHOOSING_WRONG,
        BOTH_OPTIONS_SEEM_GOOD,
        MISSING_INFORMATION,
        KEEPS_CHANGING_MIND,
        DOESNT_KNOW_WANTS,
        TOO_MANY_THINGS,
        QUICK_DECISION_NEEDED,
        NOT_SURE
    )

    // Backward compatibility constants
    const val TOO_MANY_WORRIES = "Too many possible worries"
    const val FEELS_IRREVERSIBLE = "Decision feels irreversible"
    const val EMOTIONAL_PRESSURE = "Emotional pressure"
    const val CANNOT_COMMIT_ENOUGH_INFO = "Already has enough info but cannot commit"

    fun getExplanation(stuckState: String): String = when (stuckState) {
        AFRAID_OF_CHOOSING_WRONG -> "Focusing heavily on making a mistake. Examining realistic downside and recoverability usually brings immediate grounding."
        BOTH_OPTIONS_SEEM_GOOD -> "Both paths have real upside. Defining what is 'good enough' and widening the frame prevents endless comparison."
        MISSING_INFORMATION -> "Feeling unready because of missing facts. Identifying what would actually change your mind prevents endless research."
        KEEPS_CHANGING_MIND -> "Oscillating between short-term feelings and longer-term logic. A time-horizon view provides steady footing."
        DOESNT_KNOW_WANTS -> "Unclear on core priorities. Clarifying non-negotiables cuts through the mental noise."
        TOO_MANY_THINGS, TOO_MANY_WORRIES -> "A cloud of possible scenarios and trade-offs. Filtering out what wouldn't actually change your decision brings clarity."
        QUICK_DECISION_NEEDED -> "A decision that needs swift resolution. Checking downside manageability gives you confidence to act."
        NOT_SURE -> "A blend of uncertainty and hesitation. Stepping through the core trade-offs will help locate the real friction."
        FEELS_IRREVERSIBLE -> "Treating the choice as irreversible. Identifying a small experiment or safety net builds forward momentum."
        EMOTIONAL_PRESSURE -> "External urgency or high expectations clouding your internal perspective."
        CANNOT_COMMIT_ENOUGH_INFO -> "You likely already have the necessary facts. A clear stopping rule resolves analysis paralysis."
        else -> "Weighing trade-offs and seeking a clear path forward."
    }
}

object PauseReasonCatalog {
    const val NEED_INFO = "I need more information"
    const val TIMING_NOT_RIGHT = "The timing isn't right"
    const val WAITING_ON_OTHERS = "Someone else needs to respond"
    const val NEED_TIME_TO_THINK = "I need time to think"
    const val DONT_WANT_TO_CHOOSE = "I just don't want to choose yet"

    val ALL_REASONS = listOf(
        NEED_INFO,
        TIMING_NOT_RIGHT,
        WAITING_ON_OTHERS,
        NEED_TIME_TO_THINK,
        DONT_WANT_TO_CHOOSE
    )
}

object ReopenReasonCatalog {
    const val NEW_INFO = "New information"
    const val CIRCUMSTANCE_CHANGE = "Major circumstance change"
    const val ASSUMPTION_WRONG = "An important assumption was wrong"
    const val SOMETHING_ELSE = "Something else"

    val ALL_REASONS = listOf(
        NEW_INFO,
        CIRCUMSTANCE_CHANGE,
        ASSUMPTION_WRONG,
        SOMETHING_ELSE
    )
}

object ReversibilityLevels {
    const val EASY = "Easy to reverse"
    const val SOMEWHAT = "Somewhat reversible"
    const val DIFFICULT = "Difficult to reverse"
    const val VERY_DIFFICULT = "Very difficult to reverse"

    val ALL_LEVELS = listOf(EASY, SOMEWHAT, DIFFICULT, VERY_DIFFICULT)

    fun getGuidance(level: String): String = when (level) {
        EASY -> "Cost of delay is usually greater than the cost of a reversible mistake. A quick check is recommended."
        SOMEWHAT -> "Can be undone or adjusted with moderate effort. Focus on the core trade-off."
        DIFFICULT -> "Undoing this requires substantial friction. Evaluating worst-case recoverability is warranted."
        else -> "Evaluating reversibility determines how much thinking this decision actually needs."
    }
}

@JsonClass(generateAdapter = true)
data class WidenedOption(
    val title: String,
    val type: String, // "negotiate", "delay", "test", "modify", "partially_commit", "gather_info", "choose_neither"
    val rationale: String
)

@JsonClass(generateAdapter = true)
data class PremortemAnalysis(
    val failureStory: String = "",
    val potentialCauses: List<String> = emptyList(),
    val preventiveSafetyActions: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChangeMindItem(
    val question: String,
    val isObtainable: Boolean = true,
    val impactOnDecision: String = "High", // "High", "Moderate", "Low"
    val notes: String = ""
)

@JsonClass(generateAdapter = true)
data class GoodEnoughRequirement(
    val requirement: String,
    val satisfiedByOptionA: Boolean = true,
    val satisfiedByOptionB: Boolean = true
)

@JsonClass(generateAdapter = true)
data class ThinkingPatternItem(
    val patternName: String,
    val evidence: String,
    val reflectiveQuestion: String,
    val isDismissed: Boolean = false
)

@JsonClass(generateAdapter = true)
data class SimilarPastDecision(
    val id: Long,
    val title: String,
    val whatMattered: String,
    val whatChosen: String,
    val confidencePercent: Int,
    val outcome: String? = null
)

@JsonClass(generateAdapter = true)
data class DecisionLockState(
    val isLocked: Boolean = true,
    val reopenConditions: List<String> = listOf(
        "New information",
        "Major circumstance change",
        "An important assumption was wrong"
    ),
    val lockedAt: Long = System.currentTimeMillis()
)

object JsonAdapters {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    private val riskListType = Types.newParameterizedType(List::class.java, RiskItem::class.java)
    private val riskListAdapter = moshi.adapter<List<RiskItem>>(riskListType)

    private val safetyNetListType = Types.newParameterizedType(List::class.java, SafetyNetAction::class.java)
    private val safetyNetListAdapter = moshi.adapter<List<SafetyNetAction>>(safetyNetListType)

    private val missingInfoListType = Types.newParameterizedType(List::class.java, MissingInfoItem::class.java)
    private val missingInfoListAdapter = moshi.adapter<List<MissingInfoItem>>(missingInfoListType)

    private val factListType = Types.newParameterizedType(List::class.java, StructuredFactItem::class.java)
    private val factListAdapter = moshi.adapter<List<StructuredFactItem>>(factListType)

    private val concernFilterListType = Types.newParameterizedType(List::class.java, ConcernFilterItem::class.java)
    private val concernFilterListAdapter = moshi.adapter<List<ConcernFilterItem>>(concernFilterListType)

    private val matrixListType = Types.newParameterizedType(List::class.java, MatrixRowItem::class.java)
    private val matrixListAdapter = moshi.adapter<List<MatrixRowItem>>(matrixListType)

    fun stringListToJson(list: List<String>): String = stringListAdapter.toJson(list)
    fun stringListFromJson(json: String?): List<String> = if (json.isNullOrBlank()) emptyList() else try {
        stringListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun riskListToJson(list: List<RiskItem>): String = riskListAdapter.toJson(list)
    fun riskListFromJson(json: String?): List<RiskItem> = if (json.isNullOrBlank()) emptyList() else try {
        riskListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun safetyNetListToJson(list: List<SafetyNetAction>): String = safetyNetListAdapter.toJson(list)
    fun safetyNetListFromJson(json: String?): List<SafetyNetAction> = if (json.isNullOrBlank()) emptyList() else try {
        safetyNetListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun missingInfoListToJson(list: List<MissingInfoItem>): String = missingInfoListAdapter.toJson(list)
    fun missingInfoListFromJson(json: String?): List<MissingInfoItem> = if (json.isNullOrBlank()) emptyList() else try {
        missingInfoListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun factListToJson(list: List<StructuredFactItem>): String = factListAdapter.toJson(list)
    fun factListFromJson(json: String?): List<StructuredFactItem> = if (json.isNullOrBlank()) emptyList() else try {
        factListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun concernFilterListToJson(list: List<ConcernFilterItem>): String = concernFilterListAdapter.toJson(list)
    fun concernFilterListFromJson(json: String?): List<ConcernFilterItem> = if (json.isNullOrBlank()) emptyList() else try {
        concernFilterListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }

    fun matrixListToJson(list: List<MatrixRowItem>): String = matrixListAdapter.toJson(list)
    fun matrixListFromJson(json: String?): List<MatrixRowItem> = if (json.isNullOrBlank()) emptyList() else try {
        matrixListAdapter.fromJson(json) ?: emptyList()
    } catch (_: Exception) { emptyList() }
}
