package com.example.ai

import com.example.data.ReversibilityLevels
import com.example.data.StuckStateCatalog
import com.example.data.ThinkingPatternItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null,
    val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    val temperature: Float = 0.4f,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 1200
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

data class ExtractedUnderstanding(
    val decision: String,
    val optionA: String,
    val optionB: String,
    val mainConcern: String,
    val context: String,
    val biggestUpside: String = "",
    val reversibility: String = ReversibilityLevels.SOMEWHAT,
    val stuckType: String = "TRADE_OFF_GAP",
    val stuckReasonCategory: String = StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD,
    val stuckDescription: String = "Both options seem good or have advantages.",
    val thinkingPattern: ThinkingPatternItem? = null,
    val isHighStakes: Boolean = false,
    val highStakesNotice: String? = null,
    val facts: List<com.example.data.StructuredFactItem> = emptyList(),
    val decisionDepth: String = "GUIDED", // "QUICK", "GUIDED", "DEEP"
    val isInformationGap: Boolean = false,
    val smallestMissingInfo: String = "",
    val politeScripts: List<String> = emptyList(),
    val contentDiscussion: String = "",
    val originalDilemmaVibe: String = ""
)

data class CrystallizedDilemma(
    val pinnedDecision: String,
    val optionA: String,
    val optionB: String,
    val coreTension: String,
    val priorityFocus: String,
    val isDilemmaReframed: Boolean,
    val contentDiscussion: String,
    val politeScripts: List<String> = emptyList(),
    val fearAddressed: String = ""
)

data class WhatIfScenario(
    val optionTitle: String,
    val likelyExperience: String,
    val mainUpside: String,
    val mainDownside: String,
    val whatYouGiveUp: String,
    val whatYouCanControl: String,
    val whatRemainsUncertain: String
)

data class PriorityComparison(
    val priority: String,
    val favoredOption: String, // e.g. "Option A", "Option B", "Both", "Neither"
    val comparisonReason: String
)

data class AnalyzedRisk(
    val optionTitle: String = "",
    val whatCouldHappen: String = "",
    val whyItMatters: String = "",
    val likelihood: String = "possible", // unlikely, possible, plausible, likely
    val severity: String = "moderate", // low, moderate, high
    val recoverability: String = "moderate", // high, moderate, low
    val existingSafetyNet: String = "",
    val riskReductionAction: String = ""
)

data class InformationSufficiency(
    val isInformationProblem: Boolean = false,
    val message: String = "You may already have enough information to make this decision.",
    val actionableVerifications: List<String> = emptyList()
)

data class DecisionRecommendation(
    val recommendationTitle: String = "ClearPath's read",
    val recommendedPath: String = "",
    val whyPoints: List<String> = emptyList(),
    val whatCouldChangeThis: List<String> = emptyList(),
    val userAutonomyNotice: String = "This is ClearPath's read based on what you've shared. The final decision is always yours."
)

data class GeneratedAnalysis(
    val biggestConcern: String,
    val biggestUpside: String = "",
    val likelihoodSummary: String,
    val recoverabilitySummary: String,
    val holdingBackInsight: String,
    val whatSeemsManageable: String,
    val whatDeservesChecking: String,
    val suggestedSafetyNets: List<String>,
    val leaningSummary: String,
    val reversibilityDepthMessage: String = "",
    val shouldPauseOrDelay: Boolean = false,
    val delayReason: String = "",
    val whatIfScenarios: List<WhatIfScenario> = emptyList(),

    // Strengthened decision analysis layer
    val centralTradeOff: String = "",
    val pullFactors: Map<String, String> = emptyMap(),
    val holdBackFactors: Map<String, String> = emptyMap(),
    val hesitationDriver: String = "",
    val hesitationCategory: String = "",
    val hesitationNature: String = "",
    val nonObviousInsights: List<String> = emptyList(),
    val priorityComparisons: List<PriorityComparison> = emptyList(),
    val conflictingPriorities: String = "",
    val dominantPriority: String = "",
    val analyzedRisks: List<AnalyzedRisk> = emptyList(),
    val infoSufficiency: InformationSufficiency = InformationSufficiency(),
    val recommendation: DecisionRecommendation = DecisionRecommendation(),
    val decisionReadiness: String = "READY TO DECIDE", // "READY TO DECIDE", "NEEDS A LITTLE MORE INFORMATION", "NEEDS A SMALL EXPERIMENT", "NOT CLEAR ENOUGH YET"
    val readinessExplanation: String = "",
    val isOverwhelmedByManyInterests: Boolean = false,
    val sequencingAdvice: String? = null
)

