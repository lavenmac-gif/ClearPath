package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.DecisionAnalysisEngine
import com.example.ai.ExtractedUnderstanding
import com.example.ai.GeneratedAnalysis
import com.example.data.ChangeMindItem
import com.example.data.ClearPathDatabase
import com.example.data.DecisionEntity
import com.example.data.DecisionPatternItem
import com.example.data.DecisionRepository
import com.example.data.GoodEnoughRequirement
import com.example.data.MissingInfoItem
import com.example.data.PremortemAnalysis
import com.example.data.ReversibilityLevels
import com.example.data.RiskItem
import com.example.data.SafetyNetAction
import com.example.data.SimilarPastDecision
import com.example.data.SmallExperiment
import com.example.data.StuckStateCatalog
import com.example.data.ThinkingPatternItem
import com.example.data.TrustedPerspective
import com.example.data.WidenedOption
import com.example.util.LiveNetworkMonitor
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class DecisionWizardState(
    val speedMode: String = "GUIDED", // "QUICK", "GUIDED", "DEEP"
    val decisionDepth: String = "GUIDED", // "QUICK", "GUIDED", "DEEP"
    val rawInput: String = "",
    val decisionTitle: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val additionalOptions: List<String> = emptyList(),
    val mainConcern: String = "",
    val biggestUpside: String = "",
    val biggestConcern: String = "",
    val contextDescription: String = "",
    val reversibility: String = ReversibilityLevels.SOMEWHAT,
    val stuckReason: String = "TRADE_OFF_GAP",
    val stuckReasonCategory: String = StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD,
    val stuckReasonDescription: String = StuckStateCatalog.getExplanation(StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD),
    val mustHaves: Set<String> = emptySet(),
    val niceToHaves: Set<String> = emptySet(),
    val dontCares: Set<String> = emptySet(),
    val isHighStakes: Boolean = false,
    val highStakesNotice: String? = null,
    val risks: List<RiskItem> = emptyList(),
    val priorities: Set<String> = emptySet(),
    val missingInfoItems: List<MissingInfoItem> = emptyList(),
    val safetyNets: List<SafetyNetAction> = emptyList(),
    val smallExperiment: SmallExperiment? = null,
    val trustedPerspectives: List<TrustedPerspective> = emptyList(),
    val selectedPerspective: TrustedPerspective? = null,
    val analysis: GeneratedAnalysis? = null,
    val clarityStatus: String = "", // "READY", "NEED_ONE_ANSWER", "WANT_TO_TEST", "STILL_STUCK"
    val waitingForReason: String = "", // "Certainty", "Permission", "More information", "Confidence", "I genuinely don't know"
    val regretOptionAGain: String = "",
    val regretOptionAMiss: String = "",
    val regretOptionBGain: String = "",
    val regretOptionBMiss: String = "",
    val regretHarderToAccept: String = "",
    val finalChoice: String = "",
    val userNotes: String = "",
    val confidenceLevel: Int = 4,
    val confidencePercent: Int = 75,
    val whatIfChoiceFuture: String = "",
    val whatIfOtherFuture: String = "",
    val isOverthinkingDetected: Boolean = false,
    val isPausedOrDelayed: Boolean = false,
    val deadlineDateText: String = "",
    val isLoadingAi: Boolean = false,
    val isRecordingVoice: Boolean = false,
    val editingDecisionId: Long? = null,
    val resumeWelcomeBanner: String? = null,
    val savedStep: Int = 1,
    val reminderDateMillis: Long? = null,
    val status: String = "In Progress",
    // ClearPath 2.0 core state extensions:
    val structuredFacts: List<com.example.data.StructuredFactItem> = emptyList(),
    val concernFilterItems: List<com.example.data.ConcernFilterItem> = emptyList(),
    val matrixRows: List<com.example.data.MatrixRowItem> = emptyList(),
    val pauseReason: String = "",
    val reopenReason: String = "",
    val smallestMissingInfo: String = "",
    val isInformationGap: Boolean = false,
    val whatIfScenarioParked: Boolean = false,
    val isReopenVerified: Boolean = false,
    val isOperatingOffline: Boolean = false,
    val offlineNotice: String? = null,
    // Adaptive Decision Companion extensions:
    val widenedOptions: List<WidenedOption> = emptyList(),
    val premortem: PremortemAnalysis? = null,
    val changeMindItems: List<ChangeMindItem> = emptyList(),
    val isUncertaintyNotMissingInfo: Boolean = false,
    val goodEnoughRequirements: List<GoodEnoughRequirement> = emptyList(),
    val reflection101010: Triple<String, String, String>? = null,
    val thinkingPattern: ThinkingPatternItem? = null,
    val isThinkingPatternDismissed: Boolean = false,
    val similarPastDecision: SimilarPastDecision? = null,
    val reopenConditions: List<String> = listOf(
        "New information",
        "Major circumstance change",
        "An important assumption was wrong"
    ),
    val reopenHistory: String = "",
    val clearPathReflection: String = "",
    val isReflectionGenerating: Boolean = false,
    val politeScripts: List<String> = emptyList(),
    val contentDiscussion: String = "",
    val fearAddressed: String = "",
    val isDilemmaReframed: Boolean = false
)

data class QuickDecisionState(
    val question1Decision: String = "",
    val question2WorstDownside: String = "",
    val question3CanHandle: String = "", // "Yes", "Maybe", "No"
    val verdict: String = "", // "Looks manageable", "Pause briefly", "Worth checking one thing"
    val verdictExplanation: String = "",
    val isHighStakes: Boolean = false,
    val highStakesWarning: String? = null,
    val clearPathReflection: String = "",
    val savedId: Long? = null
)

class DecisionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DecisionRepository
    val networkMonitor: NetworkMonitor
    val isOnline: StateFlow<Boolean>
    val pendingSyncCount: StateFlow<Int>
    val syncStatusText: StateFlow<String>
    private var tts: TextToSpeech? = null
    private val _isTtsSpeaking = MutableStateFlow(false)
    val isTtsSpeaking: StateFlow<Boolean> = _isTtsSpeaking.asStateFlow()

    init {
        val db = ClearPathDatabase.getDatabase(application, viewModelScope)
        repository = DecisionRepository(db.decisionDao(), db.syncQueueDao())
        networkMonitor = LiveNetworkMonitor(application)

        isOnline = networkMonitor.isOnline.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            networkMonitor.isCurrentlyOnline()
        )

        pendingSyncCount = repository.getPendingSyncCount().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

        syncStatusText = combine(isOnline, pendingSyncCount) { online, pending ->
            if (!online) {
                "Offline — changes saved on this device"
            } else if (pending > 0) {
                "Syncing $pending pending changes..."
            } else {
                "Online — synced with local vault"
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            "Saved on device"
        )

        try {
            tts = TextToSpeech(application) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.getDefault()
                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isTtsSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isTtsSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _isTtsSpeaking.value = false
                        }
                    })
                }
            }
        } catch (_: Exception) {}

        viewModelScope.launch {
            isOnline.collect { online ->
                if (online) {
                    repository.syncPendingQueue(true)
                }
            }
        }
    }

    fun exportAllDecisionsJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportAllDecisionsAsJson()
            onResult(json)
        }
    }

    fun clearAllLocalData(onCompleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllData()
            _wizardState.value = DecisionWizardState()
            _selectedDecision.value = null
            onCompleted()
        }
    }

    fun retryCloudSync() {
        viewModelScope.launch {
            repository.syncPendingQueue(networkMonitor.isCurrentlyOnline())
        }
    }

    fun speakText(text: String) {
        if (text.isBlank()) return
        try {
            tts?.stop()
            _isTtsSpeaking.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ClearPathSpeech")
        } catch (_: Exception) {}
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
            _isTtsSpeaking.value = false
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }

    val allDecisions: StateFlow<List<DecisionEntity>> = repository.allDecisions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val decisionPatterns: StateFlow<List<DecisionPatternItem>> = allDecisions
        .map { decisions -> DecisionAnalysisEngine.derivePatterns(decisions) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val decideLaterDecisions: StateFlow<List<DecisionEntity>> = allDecisions
        .map { list ->
            list.filter { !it.isDecided || it.status == "Decide Later" || it.status == "In Progress" || it.status == "Ready to Decide" }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val pendingReminderDecision: StateFlow<DecisionEntity?> = allDecisions
        .map { list ->
            val now = System.currentTimeMillis()
            list.firstOrNull { !it.isDecided && it.reminderDateMillis != null && it.reminderDateMillis <= now }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _wizardState = MutableStateFlow(DecisionWizardState())
    val wizardState: StateFlow<DecisionWizardState> = _wizardState.asStateFlow()

    private val _quickState = MutableStateFlow(QuickDecisionState())
    val quickState: StateFlow<QuickDecisionState> = _quickState.asStateFlow()

    private val _selectedDecision = MutableStateFlow<DecisionEntity?>(null)
    val selectedDecision: StateFlow<DecisionEntity?> = _selectedDecision.asStateFlow()

    fun selectDecision(decision: DecisionEntity?) {
        _selectedDecision.value = decision
    }

    fun startNewDecision(mode: String = "DEEP") {
        stopSpeaking()
        _wizardState.value = DecisionWizardState(speedMode = mode)
    }

    fun startNewDecisionWithInput(input: String, mode: String = "GUIDED", onProcessed: () -> Unit = {}) {
        stopSpeaking()
        _wizardState.value = DecisionWizardState(
            speedMode = mode,
            rawInput = input,
            savedStep = 2
        )
        analyzeInitialInput {
            onProcessed()
        }
    }

    fun resumeDecision(decision: DecisionEntity, onResumed: (Int) -> Unit) {
        stopSpeaking()
        val optA = decision.options.getOrNull(0) ?: ""
        val optB = decision.options.getOrNull(1) ?: ""
        val moreOpts = if (decision.options.size > 2) decision.options.drop(2) else emptyList()

        val stepToResume = when {
            decision.savedStep in 1..12 -> decision.savedStep
            decision.finalChoice.isNotBlank() -> 10
            decision.safetyNets.isNotEmpty() -> 7
            decision.priorities.isNotEmpty() -> 6
            decision.risks.isNotEmpty() -> 4
            optA.isNotBlank() || optB.isNotBlank() -> 3
            decision.stuckReason.isNotBlank() -> 2
            decision.title.isNotBlank() -> 2
            else -> 1
        }

        _wizardState.value = DecisionWizardState(
            speedMode = if (decision.speedMode == "LATER") "GUIDED" else decision.speedMode,
            decisionDepth = decision.decisionDepth.ifBlank { "GUIDED" },
            rawInput = decision.situationContext.ifBlank { decision.title },
            decisionTitle = decision.title,
            optionA = optA,
            optionB = optB,
            additionalOptions = moreOpts,
            mainConcern = decision.mainConcerns,
            biggestUpside = decision.biggestUpside,
            biggestConcern = decision.biggestConcern.ifBlank { decision.mainConcerns },
            contextDescription = decision.situationContext,
            reversibility = decision.reversibility,
            stuckReason = decision.stuckReason.ifBlank { "TRADE_OFF_GAP" },
            stuckReasonCategory = decision.stuckReasonCategory.ifBlank { StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD },
            stuckReasonDescription = DecisionAnalysisEngine.getStuckTypeDescription(decision.stuckReason.ifBlank { "TRADE_OFF_GAP" }),
            mustHaves = decision.mustHaves.toSet(),
            niceToHaves = decision.niceToHaves.toSet(),
            priorities = decision.priorities.toSet(),
            risks = decision.risks,
            missingInfoItems = decision.missingInfoItems,
            safetyNets = decision.safetyNets,
            finalChoice = decision.finalChoice,
            userNotes = decision.userNotes,
            confidenceLevel = decision.confidenceLevel,
            whatIfChoiceFuture = decision.whatIfChoiceFuture,
            whatIfOtherFuture = decision.whatIfOtherFuture,
            isHighStakes = decision.isHighStakes,
            editingDecisionId = decision.id,
            resumeWelcomeBanner = decision.title,
            savedStep = stepToResume,
            reminderDateMillis = decision.reminderDateMillis,
            structuredFacts = decision.structuredFacts,
            concernFilterItems = decision.concernFilterItems,
            matrixRows = decision.matrixRows,
            pauseReason = decision.pauseReason,
            reopenReason = decision.reopenReason,
            smallestMissingInfo = decision.smallestMissingInfo,
            isInformationGap = decision.isInformationGap,
            whatIfScenarioParked = decision.whatIfScenarioParked,
            politeScripts = decision.politeScripts,
            contentDiscussion = decision.contentDiscussion,
            fearAddressed = decision.fearAddressed,
            isDilemmaReframed = decision.isDilemmaReframed,
            status = "In Progress"
        )

        // Also update status to "In Progress" in Room
        viewModelScope.launch {
            repository.update(decision.copy(status = "In Progress", updatedAt = System.currentTimeMillis()))
        }

        onResumed(stepToResume)
    }

    fun dismissWelcomeBanner() {
        _wizardState.value = _wizardState.value.copy(resumeWelcomeBanner = null)
    }

    fun setAdaptiveDepth(depth: String) {
        _wizardState.value = _wizardState.value.copy(decisionDepth = depth, speedMode = depth)
    }

    fun setReversibility(level: String) {
        val depth = DecisionAnalysisEngine.classifyAdaptiveDepth(
            reversibility = level,
            stuckCategory = _wizardState.value.stuckReasonCategory,
            isHighStakes = _wizardState.value.isHighStakes,
            input = _wizardState.value.rawInput
        )
        _wizardState.value = _wizardState.value.copy(
            reversibility = level,
            decisionDepth = depth,
            speedMode = depth
        )
    }

    fun updateRawInput(input: String) {
        _wizardState.value = _wizardState.value.copy(rawInput = input)
    }

    fun analyzeInitialInput(onComplete: () -> Unit) {
        viewModelScope.launch {
            val currentlyOnline = networkMonitor.isCurrentlyOnline()
            _wizardState.value = _wizardState.value.copy(
                isLoadingAi = true,
                isOperatingOffline = !currentlyOnline,
                offlineNotice = if (!currentlyOnline) "Working offline — decision structured locally on device." else null
            )
            val extracted = DecisionAnalysisEngine.extractUnderstanding(_wizardState.value.rawInput)

            // Cross-reference with past decisions
            val similarPast = DecisionAnalysisEngine.findSimilarPastDecision(
                extracted.decision,
                extracted.context,
                allDecisions.value
            )

            val pastSimilarCount = allDecisions.value.count {
                it.title.contains(extracted.decision.take(20), ignoreCase = true)
            }
            val isOverthinking = pastSimilarCount >= 2

            val concernItems = DecisionAnalysisEngine.generateConcernFilterItems(
                decision = extracted.decision,
                mainConcern = extracted.mainConcern,
                risks = emptyList()
            )

            _wizardState.value = _wizardState.value.copy(
                decisionTitle = extracted.decision,
                optionA = extracted.optionA,
                optionB = extracted.optionB,
                mainConcern = extracted.mainConcern,
                biggestUpside = extracted.biggestUpside,
                biggestConcern = extracted.mainConcern,
                contextDescription = extracted.context,
                reversibility = extracted.reversibility,
                stuckReason = extracted.stuckType,
                stuckReasonCategory = extracted.stuckReasonCategory,
                stuckReasonDescription = extracted.stuckDescription,
                thinkingPattern = extracted.thinkingPattern,
                similarPastDecision = similarPast,
                isHighStakes = extracted.isHighStakes,
                highStakesNotice = extracted.highStakesNotice,
                isOverthinkingDetected = isOverthinking,
                structuredFacts = extracted.facts,
                decisionDepth = extracted.decisionDepth,
                speedMode = extracted.decisionDepth,
                isInformationGap = extracted.isInformationGap,
                smallestMissingInfo = extracted.smallestMissingInfo,
                concernFilterItems = concernItems,
                politeScripts = extracted.politeScripts,
                contentDiscussion = extracted.contentDiscussion,
                fearAddressed = extracted.originalDilemmaVibe,
                isDilemmaReframed = extracted.politeScripts.isNotEmpty() || extracted.contentDiscussion.isNotBlank(),
                isOperatingOffline = !currentlyOnline,
                offlineNotice = if (!currentlyOnline) "Working offline — decision structured locally on device." else null,
                isLoadingAi = false
            )

            // Asynchronously preload widened alternatives
            launch {
                try {
                    val widened = DecisionAnalysisEngine.suggestWidenedAlternatives(
                        extracted.decision,
                        extracted.optionA,
                        extracted.optionB
                    )
                    _wizardState.value = _wizardState.value.copy(widenedOptions = widened)
                } catch (_: Exception) {}
            }

            onComplete()
        }
    }

    // Fact / Assumption / Unknown / Concern Management
    fun updateFactCategory(factId: String, newCategory: String) {
        val updated = _wizardState.value.structuredFacts.map {
            if (it.id == factId) it.copy(category = newCategory) else it
        }
        _wizardState.value = _wizardState.value.copy(structuredFacts = updated)
    }

    fun addCustomFact(text: String, category: String) {
        if (text.isBlank()) return
        val item = com.example.data.StructuredFactItem(text = text.trim(), category = category)
        val current = _wizardState.value.structuredFacts.toMutableList()
        current.add(item)
        _wizardState.value = _wizardState.value.copy(structuredFacts = current)
    }

    fun removeFact(factId: String) {
        val updated = _wizardState.value.structuredFacts.filterNot { it.id == factId }
        _wizardState.value = _wizardState.value.copy(structuredFacts = updated)
    }

    // Concern Filter Management (Active, Investigate, Parked)
    fun updateConcernImpact(id: String, impact: String) {
        val newStatus = when (impact) {
            "YES" -> "ACTIVE"
            "NO" -> "PARKED"
            else -> "INVESTIGATE"
        }
        val updated = _wizardState.value.concernFilterItems.map {
            if (it.id == id) it.copy(resolutionImpact = impact, status = newStatus) else it
        }
        _wizardState.value = _wizardState.value.copy(concernFilterItems = updated)
    }

    fun addCustomConcern(text: String) {
        if (text.isBlank()) return
        val item = com.example.data.ConcernFilterItem(
            text = text.trim(),
            resolutionImpact = "YES",
            status = "ACTIVE"
        )
        val current = _wizardState.value.concernFilterItems.toMutableList()
        current.add(item)
        _wizardState.value = _wizardState.value.copy(concernFilterItems = current)
    }

    // Decision Matrix Management (Optional visualization)
    fun updateMatrixRow(id: String, optANotes: String, optBNotes: String, reasoning: String) {
        val updated = _wizardState.value.matrixRows.map {
            if (it.id == id) it.copy(optionANotes = optANotes, optionBNotes = optBNotes, reasoning = reasoning) else it
        }
        _wizardState.value = _wizardState.value.copy(matrixRows = updated)
    }

    fun addMatrixRow(factor: String, importance: String = "High") {
        if (factor.isBlank()) return
        val row = com.example.data.MatrixRowItem(factor = factor.trim(), importance = importance)
        val current = _wizardState.value.matrixRows.toMutableList()
        current.add(row)
        _wizardState.value = _wizardState.value.copy(matrixRows = current)
    }

    fun removeMatrixRow(id: String) {
        val updated = _wizardState.value.matrixRows.filterNot { it.id == id }
        _wizardState.value = _wizardState.value.copy(matrixRows = updated)
    }

    fun parkWhatIfScenario() {
        _wizardState.value = _wizardState.value.copy(whatIfScenarioParked = true)
    }

    fun setPauseReason(reason: String) {
        _wizardState.value = _wizardState.value.copy(pauseReason = reason)
    }

    fun setReopenReason(reason: String) {
        _wizardState.value = _wizardState.value.copy(reopenReason = reason)
    }

    fun setStuckReasonCategory(category: String) {
        val explanation = StuckStateCatalog.getExplanation(category)
        val legacyType = DecisionAnalysisEngine.mapStuckStateToLegacyType(category)
        _wizardState.value = _wizardState.value.copy(
            stuckReasonCategory = category,
            stuckReason = legacyType,
            stuckReasonDescription = explanation
        )
    }

    fun setStuckReason(type: String, description: String = "") {
        val desc = if (description.isNotBlank()) description else DecisionAnalysisEngine.getStuckTypeDescription(type)
        _wizardState.value = _wizardState.value.copy(
            stuckReason = type,
            stuckReasonDescription = desc
        )
    }

    fun dismissThinkingPattern() {
        _wizardState.value = _wizardState.value.copy(isThinkingPatternDismissed = true)
    }

    fun setConfidencePercent(percent: Int) {
        _wizardState.value = _wizardState.value.copy(
            confidencePercent = percent.coerceIn(0, 100),
            confidenceLevel = (percent / 20).coerceIn(1, 5)
        )
    }

    fun updateReopenConditions(conditions: List<String>) {
        _wizardState.value = _wizardState.value.copy(reopenConditions = conditions)
    }

    fun loadWidenedOptions(onLoaded: (List<WidenedOption>) -> Unit = {}) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val w = _wizardState.value
            val result = DecisionAnalysisEngine.suggestWidenedAlternatives(
                w.decisionTitle,
                w.optionA.ifBlank { "Option A" },
                w.optionB.ifBlank { "Option B" }
            )
            _wizardState.value = _wizardState.value.copy(
                widenedOptions = result,
                isLoadingAi = false
            )
            onLoaded(result)
        }
    }

    fun loadPremortem(option: String = "") {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val w = _wizardState.value
            val targetOpt = option.ifBlank { w.optionA.ifBlank { "Selected Option" } }
            val premortem = DecisionAnalysisEngine.generatePremortem(w.decisionTitle, targetOpt)
            _wizardState.value = _wizardState.value.copy(
                premortem = premortem,
                isLoadingAi = false
            )
        }
    }

    fun addPremortemSafetyActionsToSafetyNets() {
        val w = _wizardState.value
        val actions = w.premortem?.preventiveSafetyActions ?: return
        val currentNets = w.safetyNets.toMutableList()
        val existingTexts = currentNets.map { it.text }
        for (action in actions) {
            if (!existingTexts.contains(action)) {
                currentNets.add(SafetyNetAction(text = action, isChecked = true))
            }
        }
        _wizardState.value = _wizardState.value.copy(safetyNets = currentNets)
    }

    fun loadWhatWouldChangeMind() {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val w = _wizardState.value
            val items = DecisionAnalysisEngine.analyzeWhatWouldChangeMind(
                w.decisionTitle,
                w.contextDescription.ifBlank { w.rawInput },
                w.optionA.ifBlank { "Option A" },
                w.optionB.ifBlank { "Option B" }
            )
            val allUnobtainable = items.isNotEmpty() && items.none { it.isObtainable }
            _wizardState.value = _wizardState.value.copy(
                changeMindItems = items,
                isUncertaintyNotMissingInfo = allUnobtainable,
                isLoadingAi = false
            )
        }
    }

    fun loadGoodEnoughThreshold() {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val w = _wizardState.value
            val reqs = DecisionAnalysisEngine.generateGoodEnoughThreshold(
                w.decisionTitle,
                w.optionA.ifBlank { "Option A" },
                w.optionB.ifBlank { "Option B" },
                w.priorities.toList()
            )
            _wizardState.value = _wizardState.value.copy(
                goodEnoughRequirements = reqs,
                isLoadingAi = false
            )
        }
    }

    fun loadReflection101010() {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val w = _wizardState.value
            val reflection = DecisionAnalysisEngine.generateReflection101010(
                w.decisionTitle,
                w.optionA.ifBlank { "Option A" },
                w.optionB.ifBlank { "Option B" }
            )
            _wizardState.value = _wizardState.value.copy(
                reflection101010 = reflection,
                isLoadingAi = false
            )
        }
    }

    fun toggleFilterCategory(item: String, category: String) {
        val m = _wizardState.value.mustHaves.toMutableSet()
        val n = _wizardState.value.niceToHaves.toMutableSet()
        val d = _wizardState.value.dontCares.toMutableSet()

        when (category) {
            "MUST_HAVE" -> {
                if (m.contains(item)) m.remove(item) else {
                    m.add(item)
                    n.remove(item)
                    d.remove(item)
                }
            }
            "NICE_TO_HAVE" -> {
                if (n.contains(item)) n.remove(item) else {
                    n.add(item)
                    m.remove(item)
                    d.remove(item)
                }
            }
            "DONT_CARE" -> {
                if (d.contains(item)) d.remove(item) else {
                    d.add(item)
                    m.remove(item)
                    n.remove(item)
                }
            }
        }
        _wizardState.value = _wizardState.value.copy(
            mustHaves = m,
            niceToHaves = n,
            dontCares = d,
            priorities = (m + n)
        )
    }

    fun setClarityStatus(status: String) {
        _wizardState.value = _wizardState.value.copy(clarityStatus = status)
    }

    fun setWaitingForReason(reason: String) {
        _wizardState.value = _wizardState.value.copy(waitingForReason = reason)
    }

    fun updateRegretCheck(
        optAGain: String,
        optAMiss: String,
        optBGain: String,
        optBMiss: String,
        harderToAccept: String
    ) {
        _wizardState.value = _wizardState.value.copy(
            regretOptionAGain = optAGain,
            regretOptionAMiss = optAMiss,
            regretOptionBGain = optBGain,
            regretOptionBMiss = optBMiss,
            regretHarderToAccept = harderToAccept
        )
    }

    fun processInputWithAi(onComplete: () -> Unit) = analyzeInitialInput(onComplete)

    fun addOption(option: String) {
        if (option.isNotBlank()) {
            val current = _wizardState.value.additionalOptions.toMutableList()
            current.add(option.trim())
            _wizardState.value = _wizardState.value.copy(additionalOptions = current)
        }
    }

    fun requestAiAlternativeOptions(onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val suggestions = DecisionAnalysisEngine.suggestAlternativeOptions(_wizardState.value.decisionTitle)
            _wizardState.value = _wizardState.value.copy(isLoadingAi = false)
            onResult(suggestions)
        }
    }

    fun addRisk(risk: RiskItem) {
        val current = _wizardState.value.risks.toMutableList()
        current.add(risk)
        _wizardState.value = _wizardState.value.copy(risks = current)
    }

    fun updateRisk(index: Int, risk: RiskItem) {
        val current = _wizardState.value.risks.toMutableList()
        if (index in current.indices) {
            current[index] = risk
            _wizardState.value = _wizardState.value.copy(risks = current)
        }
    }

    fun updateUnderstanding(
        decision: String,
        optionA: String,
        optionB: String,
        mainConcern: String,
        biggestUpside: String = ""
    ) {
        _wizardState.value = _wizardState.value.copy(
            decisionTitle = decision,
            optionA = optionA,
            optionB = optionB,
            mainConcern = mainConcern,
            biggestConcern = mainConcern,
            biggestUpside = if (biggestUpside.isNotBlank()) biggestUpside else _wizardState.value.biggestUpside
        )
    }

    fun updateUpside(upside: String) {
        _wizardState.value = _wizardState.value.copy(biggestUpside = upside)
    }

    fun updateConcern(concern: String) {
        _wizardState.value = _wizardState.value.copy(biggestConcern = concern, mainConcern = concern)
    }

    fun loadMissingInformation() {
        viewModelScope.launch {
            val items = DecisionAnalysisEngine.detectMissingInformation(
                decision = _wizardState.value.decisionTitle,
                optionA = _wizardState.value.optionA,
                optionB = _wizardState.value.optionB
            )
            _wizardState.value = _wizardState.value.copy(missingInfoItems = items)
        }
    }

    fun updateMissingInfoStatus(itemId: String, status: String) {
        val updated = _wizardState.value.missingInfoItems.map {
            if (it.id == itemId) it.copy(status = status) else it
        }
        _wizardState.value = _wizardState.value.copy(missingInfoItems = updated)

        // If marked "NEED_TO_CHECK", automatically feed into safety net!
        if (status == "NEED_TO_CHECK") {
            val item = _wizardState.value.missingInfoItems.find { it.id == itemId }
            if (item != null && _wizardState.value.safetyNets.none { it.text.contains(item.text) }) {
                addCustomSafetyNetAction("Find out: ${item.text}")
            }
        }
    }

    fun loadSmallExperiment() {
        viewModelScope.launch {
            val exp = DecisionAnalysisEngine.generateSmallExperiment(
                decision = _wizardState.value.decisionTitle,
                option = _wizardState.value.optionA
            )
            _wizardState.value = _wizardState.value.copy(smallExperiment = exp)
        }
    }

    fun loadTrustedPerspectives() {
        val perspectives = DecisionAnalysisEngine.generateTrustedPerspectives(
            decision = _wizardState.value.decisionTitle,
            optionA = _wizardState.value.optionA,
            concern = _wizardState.value.mainConcern
        )
        _wizardState.value = _wizardState.value.copy(
            trustedPerspectives = perspectives,
            selectedPerspective = perspectives.firstOrNull()
        )
    }

    fun selectTrustedPerspective(perspective: TrustedPerspective) {
        _wizardState.value = _wizardState.value.copy(selectedPerspective = perspective)
    }

    fun addRisk(optionTitle: String, description: String) {
        if (description.isNotBlank()) {
            val current = _wizardState.value.risks.toMutableList()
            current.add(
                RiskItem(
                    optionTitle = optionTitle,
                    description = description.trim(),
                    likelihood = "Possible",
                    worstRealisticOutcome = description.trim(),
                    recoverability = "Yes"
                )
            )
            _wizardState.value = _wizardState.value.copy(risks = current)
        }
    }

    fun updateRisk(index: Int, likelihood: String, worstOutcome: String, recoverability: String) {
        val current = _wizardState.value.risks.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            current[index] = item.copy(
                likelihood = likelihood,
                worstRealisticOutcome = worstOutcome.ifBlank { item.worstRealisticOutcome },
                recoverability = recoverability
            )
            _wizardState.value = _wizardState.value.copy(risks = current)
        }
    }

    fun updateRisk(index: Int, likelihood: String, impact: String, worstOutcome: String, recoverability: String) {
        val current = _wizardState.value.risks.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            current[index] = item.copy(
                likelihood = likelihood,
                impact = impact,
                worstRealisticOutcome = worstOutcome.ifBlank { item.worstRealisticOutcome },
                recoverability = recoverability
            )
            _wizardState.value = _wizardState.value.copy(risks = current)
        }
    }

    fun removeRisk(index: Int) {
        val current = _wizardState.value.risks.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _wizardState.value = _wizardState.value.copy(risks = current)
        }
    }

    fun requestDownsideSuggestions(option: String, onResult: (List<String>) -> Unit) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val items = DecisionAnalysisEngine.suggestDownsides(_wizardState.value.decisionTitle, option)
            _wizardState.value = _wizardState.value.copy(isLoadingAi = false)
            onResult(items)
        }
    }

    fun togglePriority(priority: String) {
        val current = _wizardState.value.priorities.toMutableSet()
        if (current.contains(priority)) {
            current.remove(priority)
        } else {
            if (current.size < 3) {
                current.add(priority)
            }
        }
        _wizardState.value = _wizardState.value.copy(priorities = current)
    }

    fun addCustomPriority(priority: String) {
        if (priority.isNotBlank() && _wizardState.value.priorities.size < 3) {
            val current = _wizardState.value.priorities.toMutableSet().apply { add(priority.trim()) }
            _wizardState.value = _wizardState.value.copy(priorities = current)
        }
    }

    fun generateSafetyNetAndAnalysis(onComplete: () -> Unit) {
        viewModelScope.launch {
            _wizardState.value = _wizardState.value.copy(isLoadingAi = true)
            val allOptions = listOfNotNull(
                _wizardState.value.optionA.ifBlank { null },
                _wizardState.value.optionB.ifBlank { null }
            ) + _wizardState.value.additionalOptions

            val analysis = DecisionAnalysisEngine.generateAnalysis(
                decision = _wizardState.value.decisionTitle,
                options = allOptions.ifEmpty { listOf("Option A", "Option B") },
                risks = _wizardState.value.risks,
                priorities = _wizardState.value.priorities.toList(),
                stuckCategory = _wizardState.value.stuckReasonCategory,
                context = _wizardState.value.contextDescription.ifBlank { _wizardState.value.rawInput },
                mustHaves = _wizardState.value.mustHaves,
                niceToHaves = _wizardState.value.niceToHaves,
                structuredFacts = _wizardState.value.structuredFacts,
                safetyNets = _wizardState.value.safetyNets,
                missingInfoItems = _wizardState.value.missingInfoItems,
                whatWouldChangeMind = _wizardState.value.changeMindItems,
                reversibility = _wizardState.value.reversibility,
                clarityStatus = _wizardState.value.clarityStatus,
                waitingForReason = _wizardState.value.waitingForReason
            )

            val existingSafetyTexts = _wizardState.value.safetyNets.map { it.text }
            val combinedNets = _wizardState.value.safetyNets.toMutableList()
            analysis.suggestedSafetyNets.forEach { text ->
                if (!existingSafetyTexts.contains(text)) {
                    combinedNets.add(SafetyNetAction(text = text, isChecked = false))
                }
            }

            _wizardState.value = _wizardState.value.copy(
                analysis = analysis,
                safetyNets = combinedNets,
                isLoadingAi = false
            )
            onComplete()
        }
    }

    fun toggleSafetyNetAction(index: Int) {
        val current = _wizardState.value.safetyNets.toMutableList()
        if (index in current.indices) {
            val item = current[index]
            current[index] = item.copy(isChecked = !item.isChecked)
            _wizardState.value = _wizardState.value.copy(safetyNets = current)
        }
    }

    fun addCustomSafetyNetAction(text: String) {
        if (text.isNotBlank()) {
            val current = _wizardState.value.safetyNets.toMutableList().apply {
                add(SafetyNetAction(text = text.trim(), isChecked = true))
            }
            _wizardState.value = _wizardState.value.copy(safetyNets = current)
        }
    }

    fun setFinalChoice(choice: String) {
        _wizardState.value = _wizardState.value.copy(finalChoice = choice)
    }

    fun updateWhatIf(choiceFuture: String, otherFuture: String) {
        _wizardState.value = _wizardState.value.copy(
            whatIfChoiceFuture = choiceFuture,
            whatIfOtherFuture = otherFuture
        )
    }

    fun updateJournalReflection(confidence: Int, notes: String) {
        _wizardState.value = _wizardState.value.copy(
            confidenceLevel = confidence,
            userNotes = notes
        )
    }

    fun saveDecisionToJournal(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val w = _wizardState.value
            val allOptions = listOfNotNull(
                w.optionA.ifBlank { null },
                w.optionB.ifBlank { null }
            ) + w.additionalOptions

            val isDecided = w.finalChoice.isNotBlank() && !w.finalChoice.contains("unsure", ignoreCase = true)
            val newStatus = if (isDecided) "Decided" else "In Progress"
            val title = w.decisionTitle.ifBlank { w.rawInput.ifBlank { "Untitled Decision" } }
            val reflection = w.clearPathReflection.ifBlank {
                DecisionAnalysisEngine.generateDeterministicReflection(
                    decisionTitle = title,
                    stuckReason = w.mainConcern.ifBlank { w.contextDescription.ifBlank { w.rawInput } },
                    priorities = w.priorities.toList(),
                    whatLearned = w.analysis?.whatSeemsManageable?.ifBlank { w.analysis?.holdingBackInsight } ?: "",
                    finalChoice = w.finalChoice.ifBlank { "Undecided" },
                    remainingUncertainty = w.analysis?.whatDeservesChecking ?: "",
                    safetyNets = w.safetyNets.map { it.text },
                    reopenConditions = w.reopenConditions
                )
            }

            val existingId = w.editingDecisionId
            if (existingId != null) {
                val existing = repository.getDecisionOnce(existingId)
                val updated = (existing ?: DecisionEntity(id = existingId, title = title)).copy(
                    title = title,
                    situationContext = w.contextDescription.ifBlank { w.rawInput },
                    speedMode = w.speedMode,
                    reversibility = w.reversibility,
                    options = allOptions,
                    mainConcerns = w.mainConcern,
                    biggestUpside = w.biggestUpside.ifBlank { w.analysis?.biggestUpside ?: "" },
                    biggestConcern = w.biggestConcern.ifBlank { w.mainConcern },
                    risks = w.risks,
                    priorities = w.priorities.toList(),
                    missingInfoItems = w.missingInfoItems,
                    safetyNets = w.safetyNets,
                    smallExperiment = w.smallExperiment?.title ?: "",
                    aiAnalysis = w.analysis?.let { DecisionAnalysisEngine.formatAnalysisSummary(it) } ?: "",
                    holdingBackInsight = w.analysis?.holdingBackInsight ?: "",
                    whatSeemsManageable = w.analysis?.whatSeemsManageable ?: "",
                    whatDeservesChecking = w.analysis?.whatDeservesChecking ?: "",
                    finalChoice = w.finalChoice.ifBlank { "Undecided" },
                    reasoning = w.analysis?.leaningSummary ?: "",
                    confidenceLevel = w.confidenceLevel,
                    userNotes = w.userNotes,
                    whatIfChoiceFuture = w.whatIfChoiceFuture,
                    whatIfOtherFuture = w.whatIfOtherFuture,
                    isDecided = isDecided,
                    isHighStakes = w.isHighStakes,
                    isOverthinkingDetected = w.isOverthinkingDetected,
                    stuckReason = w.stuckReason,
                    stuckReasonCategory = w.stuckReasonCategory,
                    confidencePercent = w.confidencePercent,
                    widenedOptions = w.widenedOptions.map { "${it.type}|${it.title}|${it.rationale}" },
                    premortemFailureStory = w.premortem?.failureStory ?: "",
                    premortemFailureCauses = w.premortem?.potentialCauses ?: emptyList(),
                    whatWouldChangeMindItems = w.changeMindItems.map { "${it.question}|${it.isObtainable}|${it.impactOnDecision}" },
                    isUncertaintyNotMissingInfo = w.isUncertaintyNotMissingInfo,
                    goodEnoughCriteria = w.goodEnoughRequirements.map { "${it.requirement}|${it.satisfiedByOptionA}|${it.satisfiedByOptionB}" },
                    reflection10Minutes = w.reflection101010?.first ?: "",
                    reflection10Months = w.reflection101010?.second ?: "",
                    reflection10Years = w.reflection101010?.third ?: "",
                    reopenConditions = w.reopenConditions,
                    reopenHistory = w.reopenHistory,
                    detectedThinkingPattern = w.thinkingPattern?.patternName ?: "",
                    thinkingPatternEvidence = w.thinkingPattern?.evidence ?: "",
                    thinkingPatternQuestion = w.thinkingPattern?.reflectiveQuestion ?: "",
                    isThinkingPatternDismissed = w.isThinkingPatternDismissed,
                    similarPastDecisionId = w.similarPastDecision?.id,
                    similarPastDecisionTitle = w.similarPastDecision?.title,
                    mustHaves = w.mustHaves.toList(),
                    niceToHaves = w.niceToHaves.toList(),
                    structuredFacts = w.structuredFacts,
                    concernFilterItems = w.concernFilterItems,
                    matrixRows = w.matrixRows,
                    decisionDepth = w.decisionDepth,
                    pauseReason = w.pauseReason,
                    reopenReason = w.reopenReason,
                    smallestMissingInfo = w.smallestMissingInfo,
                    isInformationGap = w.isInformationGap,
                    whatIfScenarioParked = w.whatIfScenarioParked,
                    waitingForReason = w.waitingForReason,
                    clearPathReflection = reflection,
                    status = newStatus,
                    savedStep = 12,
                    politeScripts = w.politeScripts,
                    contentDiscussion = w.contentDiscussion,
                    fearAddressed = w.fearAddressed,
                    isDilemmaReframed = w.isDilemmaReframed,
                    updatedAt = System.currentTimeMillis()
                )
                repository.update(updated)
                _selectedDecision.value = updated
                onSaved(existingId)
            } else {
                val entity = DecisionEntity(
                    title = title,
                    situationContext = w.contextDescription.ifBlank { w.rawInput },
                    speedMode = w.speedMode,
                    reversibility = w.reversibility,
                    options = allOptions,
                    mainConcerns = w.mainConcern,
                    biggestUpside = w.biggestUpside.ifBlank { w.analysis?.biggestUpside ?: "" },
                    biggestConcern = w.biggestConcern.ifBlank { w.mainConcern },
                    risks = w.risks,
                    priorities = w.priorities.toList(),
                    missingInfoItems = w.missingInfoItems,
                    safetyNets = w.safetyNets,
                    smallExperiment = w.smallExperiment?.title ?: "",
                    aiAnalysis = w.analysis?.let { DecisionAnalysisEngine.formatAnalysisSummary(it) } ?: "",
                    holdingBackInsight = w.analysis?.holdingBackInsight ?: "",
                    whatSeemsManageable = w.analysis?.whatSeemsManageable ?: "",
                    whatDeservesChecking = w.analysis?.whatDeservesChecking ?: "",
                    finalChoice = w.finalChoice.ifBlank { "Undecided" },
                    reasoning = w.analysis?.leaningSummary ?: "",
                    confidenceLevel = w.confidenceLevel,
                    confidencePercent = w.confidencePercent,
                    widenedOptions = w.widenedOptions.map { "${it.type}|${it.title}|${it.rationale}" },
                    premortemFailureStory = w.premortem?.failureStory ?: "",
                    premortemFailureCauses = w.premortem?.potentialCauses ?: emptyList(),
                    whatWouldChangeMindItems = w.changeMindItems.map { "${it.question}|${it.isObtainable}|${it.impactOnDecision}" },
                    isUncertaintyNotMissingInfo = w.isUncertaintyNotMissingInfo,
                    goodEnoughCriteria = w.goodEnoughRequirements.map { "${it.requirement}|${it.satisfiedByOptionA}|${it.satisfiedByOptionB}" },
                    reflection10Minutes = w.reflection101010?.first ?: "",
                    reflection10Months = w.reflection101010?.second ?: "",
                    reflection10Years = w.reflection101010?.third ?: "",
                    reopenConditions = w.reopenConditions,
                    reopenHistory = w.reopenHistory,
                    detectedThinkingPattern = w.thinkingPattern?.patternName ?: "",
                    thinkingPatternEvidence = w.thinkingPattern?.evidence ?: "",
                    thinkingPatternQuestion = w.thinkingPattern?.reflectiveQuestion ?: "",
                    isThinkingPatternDismissed = w.isThinkingPatternDismissed,
                    similarPastDecisionId = w.similarPastDecision?.id,
                    similarPastDecisionTitle = w.similarPastDecision?.title,
                    userNotes = w.userNotes,
                    whatIfChoiceFuture = w.whatIfChoiceFuture,
                    whatIfOtherFuture = w.whatIfOtherFuture,
                    isDecided = isDecided,
                    isHighStakes = w.isHighStakes,
                    isOverthinkingDetected = w.isOverthinkingDetected,
                    stuckReason = w.stuckReason,
                    mustHaves = w.mustHaves.toList(),
                    niceToHaves = w.niceToHaves.toList(),
                    structuredFacts = w.structuredFacts,
                    concernFilterItems = w.concernFilterItems,
                    matrixRows = w.matrixRows,
                    decisionDepth = w.decisionDepth,
                    pauseReason = w.pauseReason,
                    reopenReason = w.reopenReason,
                    smallestMissingInfo = w.smallestMissingInfo,
                    isInformationGap = w.isInformationGap,
                    whatIfScenarioParked = w.whatIfScenarioParked,
                    waitingForReason = w.waitingForReason,
                    clearPathReflection = reflection,
                    status = newStatus,
                    savedStep = 12,
                    politeScripts = w.politeScripts,
                    contentDiscussion = w.contentDiscussion,
                    fearAddressed = w.fearAddressed,
                    isDilemmaReframed = w.isDilemmaReframed,
                    updatedAt = System.currentTimeMillis()
                )
                val id = repository.insert(entity)
                _wizardState.value = _wizardState.value.copy(editingDecisionId = id)
                _selectedDecision.value = entity.copy(id = id)
                onSaved(id)
            }
        }
    }

    // Quick Decision (10 seconds)
    fun updateQuickQuestion1(decision: String) {
        val highStakes = DecisionAnalysisEngine.detectHighStakes(decision)
        _quickState.value = _quickState.value.copy(
            question1Decision = decision,
            isHighStakes = highStakes.first,
            highStakesWarning = highStakes.second
        )
    }

    fun updateQuickQuestion2(worstDownside: String) {
        _quickState.value = _quickState.value.copy(question2WorstDownside = worstDownside)
    }

    fun evaluateQuickQuestion3(canHandle: String) {
        val (verdict, explanation) = when (canHandle) {
            "Yes" -> "Looks manageable" to "Your biggest concern seems realistic, and the downside appears manageable. You haven't identified a major reason to stop."
            "No" -> "Pause briefly" to "You've identified a consequence you don't feel ready to handle right now. Pausing gives you room to check alternatives."
            else -> "Worth checking one thing" to "There's still an important uncertainty worth checking before jumping in."
        }
        _quickState.value = _quickState.value.copy(
            question3CanHandle = canHandle,
            verdict = verdict,
            verdictExplanation = explanation
        )
    }

    fun saveQuickDecision(onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val q = _quickState.value
            val title = q.question1Decision.ifBlank { "Quick Decision" }
            val downside = q.question2WorstDownside.ifBlank { "Uncertainty on whether the downside is manageable" }
            val reflection = DecisionAnalysisEngine.generateDeterministicReflection(
                decisionTitle = title,
                stuckReason = downside,
                priorities = listOf("Immediate clarity without overthinking"),
                whatLearned = "You evaluated the worst downside and assessed recoverability as ${q.question3CanHandle}.",
                finalChoice = q.verdict,
                remainingUncertainty = "Future circumstances can be navigated as they emerge.",
                safetyNets = listOf("Pause and reconsider if fresh warning signs emerge"),
                reopenConditions = listOf("The downside turns out to be more severe than anticipated")
            )
            val entity = DecisionEntity(
                title = title,
                situationContext = "Quick 10-second check",
                speedMode = "QUICK",
                options = listOf("Proceed with action", "Pause / Wait"),
                mainConcerns = q.question2WorstDownside,
                risks = listOf(
                    RiskItem(
                        optionTitle = "Action",
                        description = q.question2WorstDownside,
                        likelihood = "Possible",
                        worstRealisticOutcome = q.question2WorstDownside,
                        recoverability = q.question3CanHandle
                    )
                ),
                priorities = listOf("Immediate clarity"),
                finalChoice = q.verdict,
                reasoning = q.verdictExplanation,
                confidenceLevel = if (q.question3CanHandle == "Yes") 4 else 2,
                confidencePercent = if (q.question3CanHandle == "Yes") 80 else 40,
                clearPathReflection = reflection,
                status = "Decided",
                isDecided = true,
                isHighStakes = q.isHighStakes
            )
            val id = repository.insert(entity)
            val savedEntity = entity.copy(id = id)
            _quickState.value = _quickState.value.copy(savedId = id)
            _selectedDecision.value = savedEntity
            onSaved(id)
        }
    }

    fun updateDecisionReflection(decisionId: Long, newReflection: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            val existing = repository.getDecisionOnce(decisionId)
            if (existing != null) {
                val updated = existing.copy(
                    clearPathReflection = newReflection,
                    updatedAt = System.currentTimeMillis()
                )
                repository.update(updated)
                _selectedDecision.value = updated
            }
            onDone?.invoke()
        }
    }

    fun resetQuickDecision() {
        _quickState.value = QuickDecisionState()
    }

    // Save Later / Pause Decision
    fun saveNewDecideLater(
        title: String,
        notes: String = "",
        reminderMillis: Long?,
        speedMode: String = "DEEP",
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val entity = DecisionEntity(
                title = title.ifBlank { "Decision to revisit" },
                situationContext = notes,
                speedMode = speedMode,
                status = "Decide Later",
                savedStep = 1,
                reminderDateMillis = reminderMillis,
                isDecided = false,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val id = repository.insert(entity)
            onSaved(id)
        }
    }

    fun saveCurrentProgressAsDecideLater(
        currentStep: Int,
        reminderMillis: Long?,
        pauseReason: String = "",
        onSaved: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val w = _wizardState.value
            val effectivePauseReason = pauseReason.ifBlank { w.pauseReason }
            val allOptions = listOfNotNull(
                w.optionA.ifBlank { null },
                w.optionB.ifBlank { null }
            ) + w.additionalOptions

            val title = w.decisionTitle.ifBlank { w.rawInput.ifBlank { "Decision in progress" } }
            val existingId = w.editingDecisionId

            if (existingId != null) {
                val existing = repository.getDecisionOnce(existingId)
                val updated = (existing ?: DecisionEntity(id = existingId, title = title)).copy(
                    title = title,
                    situationContext = w.contextDescription.ifBlank { w.rawInput },
                    speedMode = w.speedMode,
                    reversibility = w.reversibility,
                    options = allOptions,
                    mainConcerns = w.mainConcern,
                    biggestUpside = w.biggestUpside.ifBlank { w.analysis?.biggestUpside ?: "" },
                    biggestConcern = w.biggestConcern.ifBlank { w.mainConcern },
                    risks = w.risks,
                    priorities = w.priorities.toList(),
                    missingInfoItems = w.missingInfoItems,
                    safetyNets = w.safetyNets,
                    smallExperiment = w.smallExperiment?.title ?: "",
                    aiAnalysis = w.analysis?.let { DecisionAnalysisEngine.formatAnalysisSummary(it) } ?: "",
                    holdingBackInsight = w.analysis?.holdingBackInsight ?: "",
                    whatSeemsManageable = w.analysis?.whatSeemsManageable ?: "",
                    whatDeservesChecking = w.analysis?.whatDeservesChecking ?: "",
                    finalChoice = w.finalChoice,
                    reasoning = w.analysis?.leaningSummary ?: "",
                    confidenceLevel = w.confidenceLevel,
                    userNotes = w.userNotes,
                    whatIfChoiceFuture = w.whatIfChoiceFuture,
                    whatIfOtherFuture = w.whatIfOtherFuture,
                    isDecided = false,
                    isHighStakes = w.isHighStakes,
                    isOverthinkingDetected = w.isOverthinkingDetected,
                    stuckReason = w.stuckReason,
                    mustHaves = w.mustHaves.toList(),
                    niceToHaves = w.niceToHaves.toList(),
                    structuredFacts = w.structuredFacts,
                    concernFilterItems = w.concernFilterItems,
                    matrixRows = w.matrixRows,
                    decisionDepth = w.decisionDepth,
                    pauseReason = effectivePauseReason,
                    reopenReason = w.reopenReason,
                    smallestMissingInfo = w.smallestMissingInfo,
                    isInformationGap = w.isInformationGap,
                    whatIfScenarioParked = w.whatIfScenarioParked,
                    waitingForReason = w.waitingForReason,
                    status = "Decide Later",
                    savedStep = currentStep,
                    reminderDateMillis = reminderMillis ?: existing?.reminderDateMillis,
                    politeScripts = w.politeScripts,
                    contentDiscussion = w.contentDiscussion,
                    fearAddressed = w.fearAddressed,
                    isDilemmaReframed = w.isDilemmaReframed,
                    updatedAt = System.currentTimeMillis()
                )
                repository.update(updated)
                _wizardState.value = _wizardState.value.copy(
                    status = "Decide Later",
                    savedStep = currentStep,
                    pauseReason = effectivePauseReason,
                    reminderDateMillis = reminderMillis ?: existing?.reminderDateMillis
                )
                _selectedDecision.value = updated
                onSaved(existingId)
            } else {
                val entity = DecisionEntity(
                    title = title,
                    situationContext = w.contextDescription.ifBlank { w.rawInput },
                    speedMode = w.speedMode,
                    reversibility = w.reversibility,
                    options = allOptions,
                    mainConcerns = w.mainConcern,
                    biggestUpside = w.biggestUpside.ifBlank { w.analysis?.biggestUpside ?: "" },
                    biggestConcern = w.biggestConcern.ifBlank { w.mainConcern },
                    risks = w.risks,
                    priorities = w.priorities.toList(),
                    missingInfoItems = w.missingInfoItems,
                    safetyNets = w.safetyNets,
                    smallExperiment = w.smallExperiment?.title ?: "",
                    aiAnalysis = w.analysis?.let { DecisionAnalysisEngine.formatAnalysisSummary(it) } ?: "",
                    holdingBackInsight = w.analysis?.holdingBackInsight ?: "",
                    whatSeemsManageable = w.analysis?.whatSeemsManageable ?: "",
                    whatDeservesChecking = w.analysis?.whatDeservesChecking ?: "",
                    finalChoice = w.finalChoice,
                    reasoning = w.analysis?.leaningSummary ?: "",
                    confidenceLevel = w.confidenceLevel,
                    userNotes = w.userNotes,
                    whatIfChoiceFuture = w.whatIfChoiceFuture,
                    whatIfOtherFuture = w.whatIfOtherFuture,
                    isDecided = false,
                    isHighStakes = w.isHighStakes,
                    isOverthinkingDetected = w.isOverthinkingDetected,
                    stuckReason = w.stuckReason,
                    mustHaves = w.mustHaves.toList(),
                    niceToHaves = w.niceToHaves.toList(),
                    structuredFacts = w.structuredFacts,
                    concernFilterItems = w.concernFilterItems,
                    matrixRows = w.matrixRows,
                    decisionDepth = w.decisionDepth,
                    pauseReason = effectivePauseReason,
                    reopenReason = w.reopenReason,
                    smallestMissingInfo = w.smallestMissingInfo,
                    isInformationGap = w.isInformationGap,
                    whatIfScenarioParked = w.whatIfScenarioParked,
                    waitingForReason = w.waitingForReason,
                    status = "Decide Later",
                    savedStep = currentStep,
                    reminderDateMillis = reminderMillis,
                    politeScripts = w.politeScripts,
                    contentDiscussion = w.contentDiscussion,
                    fearAddressed = w.fearAddressed,
                    isDilemmaReframed = w.isDilemmaReframed,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                val id = repository.insert(entity)
                _wizardState.value = _wizardState.value.copy(
                    editingDecisionId = id,
                    status = "Decide Later",
                    savedStep = currentStep,
                    pauseReason = effectivePauseReason,
                    reminderDateMillis = reminderMillis
                )
                _selectedDecision.value = entity.copy(id = id)
                onSaved(id)
            }
        }
    }

    fun saveDecideLater(title: String, notes: String, reminderDays: Int, onSaved: (Long) -> Unit) {
        val reminderMillis = if (reminderDays > 0) System.currentTimeMillis() + (reminderDays * 86400000L) else null
        saveNewDecideLater(title, notes, reminderMillis, onSaved = onSaved)
    }

    // Post Decision Review
    fun submitDecisionReview(
        decisionId: Long,
        outcome: String,
        reflection: String,
        learningNotes: String = "",
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getDecisionOnce(decisionId)
            if (existing != null) {
                val updated = existing.copy(
                    reviewOutcome = outcome,
                    reviewReflection = reflection,
                    learningNotes = learningNotes,
                    status = "Reviewed",
                    reviewedAt = System.currentTimeMillis()
                )
                repository.update(updated)
                _selectedDecision.value = updated
            }
            onDone()
        }
    }

    fun canReopenDecision(reason: String): Pair<Boolean, String> {
        val trimmed = reason.trim()
        if (trimmed.length < 5) {
            return Pair(false, "You haven't identified anything new yet. You may be revisiting the same uncertainty rather than responding to a changed situation.")
        }
        val uncertaintyKeywords = listOf("nervous", "scared", "still wonder", "not sure", "worried", "second guessing", "doubt")
        val hasOnlyDoubt = uncertaintyKeywords.any { trimmed.contains(it, ignoreCase = true) } &&
                !trimmed.contains("new", ignoreCase = true) &&
                !trimmed.contains("changed", ignoreCase = true) &&
                !trimmed.contains("found", ignoreCase = true) &&
                !trimmed.contains("discovered", ignoreCase = true) &&
                !trimmed.contains("happened", ignoreCase = true)

        if (hasOnlyDoubt) {
            return Pair(false, "This sounds like returning uncertainty rather than a new development or changed condition. Are you sure you want to reopen?")
        }
        return Pair(true, "")
    }

    fun reopenDecision(decision: DecisionEntity, reason: String, onReopened: () -> Unit) {
        viewModelScope.launch {
            val updatedHistory = if (decision.reopenHistory.isBlank()) {
                "Reopened: $reason"
            } else {
                "${decision.reopenHistory}\nReopened: $reason"
            }
            val updated = decision.copy(
                isDecided = false,
                status = "In Progress",
                reopenHistory = updatedHistory,
                updatedAt = System.currentTimeMillis()
            )
            repository.update(updated)
            _selectedDecision.value = updated
            resumeDecision(updated) { _ ->
                onReopened()
            }
        }
    }

    fun deleteDecision(decision: DecisionEntity) {
        viewModelScope.launch {
            repository.delete(decision)
            if (_selectedDecision.value?.id == decision.id) {
                _selectedDecision.value = null
            }
        }
    }
}
