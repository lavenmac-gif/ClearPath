package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RiskItem
import com.example.ui.components.ClearPathHeader
import com.example.ui.components.ConfidenceRatingBar
import com.example.ui.components.ImpactSelector
import com.example.ui.components.LikelihoodSelector
import com.example.ui.components.PriorityChip
import com.example.ui.components.RecoverabilityBadge
import com.example.ui.components.RecoverabilitySelector
import com.example.ui.components.RiskMapSection
import com.example.ui.components.SafetyNetCheckboxRow
import com.example.ui.components.VoiceMicButton
import com.example.ui.components.WhyWeAskCard
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueDark
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.RecoverableGreen
import com.example.ui.theme.RecoverableGreenBg
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayDark
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.ui.motion.EditorialSlicedCard
import com.example.ui.companion.ClintPetView
import com.example.ui.companion.ClintSanctuarySheet
import com.example.ui.companion.CompanionEmotion
import com.example.ui.companion.FeelingState
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.viewmodel.DecisionViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DecisionGuidedFlowScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onFinishDecision: (Long, Boolean) -> Unit
) {
    val wizardState by viewModel.wizardState.collectAsState()
    val scrollState = rememberScrollState()

    // Guided step machine:
    // 1: What's on your mind? (Input)
    // 2: Here's what I understand (Confirmation / Edit)
    // 3: Options (Option A, Option B, Add Option)
    // 4: What could realistically go wrong? (Downside risks)
    // 5: Downside details (Likelihood, Worst realistic outcome, Recoverability)
    // 6: What matters most? (Priorities)
    // 7: Safety net checklist
    // 8: AI analysis & synthesis
    // 9: Final decision leaning & choice
    // 10: "What If?" exploration
    // 11: Journal reflection & confidence
    var currentStep by remember(wizardState.savedStep) {
        mutableIntStateOf(if (wizardState.savedStep in 1..14) wizardState.savedStep else 1)
    }
    var showDecideLaterDialog by remember { mutableStateOf(false) }
    var selectedReminderDays by remember { mutableIntStateOf(1) }

    BackHandler {
        if (currentStep > 1) {
            currentStep -= 1
        } else {
            onBack()
        }
    }

    var isClintSanctuaryOpen by remember { mutableStateOf(false) }

    if (isClintSanctuaryOpen) {
        ClintSanctuarySheet(
            initialFeeling = FeelingState.OVERWHELMED,
            onDismiss = { isClintSanctuaryOpen = false }
        )
    }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("guided_decision_flow")
    ) {
        if (showDecideLaterDialog) {
            AlertDialog(
                onDismissRequest = { showDecideLaterDialog = false },
                title = {
                    Text(
                        text = "Save progress & decide later",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "You don't have to decide right now. We'll hold your place at Step $currentStep so you can return anytime.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "When would you like to revisit it?",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf(
                            Pair("Later today", 0),
                            Pair("Tomorrow", 1),
                            Pair("This week", 3),
                            Pair("In 1 week", 7),
                            Pair("No reminder", -1)
                        ).forEach { (label, days) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReminderDays = days }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedReminderDays == days,
                                    onClick = { selectedReminderDays = days },
                                    colors = RadioButtonDefaults.colors(selectedColor = SoftSage)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val now = System.currentTimeMillis()
                            val reminderMillis = when (selectedReminderDays) {
                                0 -> now + (4 * 3600000L)
                                1 -> now + (24 * 3600000L)
                                3 -> now + (3 * 86400000L)
                                7 -> now + (7 * 86400000L)
                                else -> null
                            }
                            viewModel.saveCurrentProgressAsDecideLater(
                                currentStep = currentStep,
                                reminderMillis = reminderMillis,
                                onSaved = { savedId ->
                                    showDecideLaterDialog = false
                                    onFinishDecision(savedId, false)
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSage)
                    ) {
                        Text("Save and exit", color = CreamWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDecideLaterDialog = false }) {
                        Text("Keep going", color = CharcoalMuted)
                    }
                },
                containerColor = CreamWhite
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = when (currentStep) {
                    1 -> "Step 1 of 12: What's on your mind?"
                    2 -> "Step 2: Here's what I understand"
                    3 -> "Step 3: Options"
                    4 -> "Step 4: Possible downsides"
                    5 -> "Step 5: Assessing the downside"
                    6 -> "Step 6: What matters most?"
                    7 -> "Step 7: Your safety net"
                    8 -> "Step 8: AI synthesis & insights"
                    9 -> "Step 9: Decision summary"
                    10 -> "Step 10: Your decision"
                    11 -> "Step 11: What If? Future view"
                    12 -> "Step 12: Decision journal"
                    else -> "Decision journal"
                },
                onBack = {
                    if (currentStep > 1) {
                        currentStep -= 1
                    } else {
                        onBack()
                    }
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SoftSageLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { isClintSanctuaryOpen = true }
                                .testTag("flow_clint_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClintPetView(
                                    size = 20.dp,
                                    emotion = CompanionEmotion.HAPPY,
                                    isInteractive = false,
                                    showContainer = false,
                                    faceColor = DarkForestSage
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "c:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SoftSageDark,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        TextButton(
                            onClick = { showDecideLaterDialog = true },
                            modifier = Modifier.testTag("action_decide_later")
                        ) {
                            Text(
                                text = "Decide later",
                                color = SoftSageDark,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            )

            // Step Progress Bar
            val totalSteps = if (wizardState.speedMode == "RAPID") 8 else 14
            val targetProgress = (currentStep.coerceAtMost(totalSteps).toFloat() / totalSteps.toFloat()).coerceIn(0.07f, 1f)
            val animatedProgress by androidx.compose.animation.core.animateFloatAsState(
                targetValue = targetProgress,
                animationSpec = androidx.compose.animation.core.tween(350),
                label = "step_progress"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(WarmSand)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(3.dp)
                        .background(SoftSage)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                if (wizardState.resumeWelcomeBanner != null) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSageLight),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("resume_welcome_banner")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Welcome back.",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SoftSageDark
                                    )
                                )
                                IconButton(
                                    onClick = { viewModel.dismissWelcomeBanner() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Dismiss",
                                        tint = SoftSageDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Last time, you were thinking about:",
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                            )
                            Text(
                                text = wizardState.decisionTitle.ifBlank { wizardState.resumeWelcomeBanner ?: "" },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Let's continue where you left off.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CharcoalMuted,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                        }
                    }
                }

                when (currentStep) {
                    1 -> Step1StartingInput(
                        rawInput = wizardState.rawInput,
                        isLoading = wizardState.isLoadingAi,
                        onUpdateInput = { viewModel.updateRawInput(it) },
                        onContinue = {
                            viewModel.processInputWithAi {
                                currentStep = 2
                            }
                        }
                    )
                    2 -> Step2Confirmation(
                        decision = wizardState.decisionTitle,
                        optA = wizardState.optionA,
                        optB = wizardState.optionB,
                        mainConcern = wizardState.mainConcern,
                        context = wizardState.contextDescription,
                        reversibility = wizardState.reversibility,
                        isHighStakes = wizardState.isHighStakes,
                        highStakesNotice = wizardState.highStakesNotice,
                        isOverthinking = wizardState.isOverthinkingDetected,
                        isInformationGap = wizardState.isInformationGap || wizardState.stuckReasonCategory == com.example.data.StuckStateCatalog.MISSING_INFORMATION,
                        smallestMissingInfo = wizardState.smallestMissingInfo,
                        decisionDepth = wizardState.decisionDepth,
                        politeScripts = wizardState.politeScripts,
                        contentDiscussion = wizardState.contentDiscussion,
                        fearAddressed = wizardState.fearAddressed,
                        isDilemmaReframed = wizardState.isDilemmaReframed,
                        onSelectReversibility = { viewModel.setReversibility(it) },
                        onUpdate = { d, a, b, c ->
                            viewModel.updateUnderstanding(d, a, b, c)
                        },
                        onQuickPath = {
                            // Low-stakes fast-track jumps straight to clarity and decision!
                            currentStep = 10
                        },
                        onMissingInfoPath = {
                            // Jump directly to Missing Information & Safety Net!
                            currentStep = 8
                        },
                        onPauseForInfo = {
                            showDecideLaterDialog = true
                        },
                        onLooksGood = {
                            currentStep = 3
                        }
                    )
                    3 -> StepWhyAmIStuck(
                        stuckType = wizardState.stuckReason,
                        stuckDescription = wizardState.stuckReasonDescription,
                        onSelectStuckType = { type, desc ->
                            viewModel.setStuckReason(type, desc)
                        },
                        onContinue = {
                            currentStep = 4
                        }
                    )
                    4 -> Step3Options(
                        optionA = wizardState.optionA,
                        optionB = wizardState.optionB,
                        additionalOptions = wizardState.additionalOptions,
                        widenedOptions = wizardState.widenedOptions,
                        isLoadingAi = wizardState.isLoadingAi,
                        onAddOption = { viewModel.addOption(it) },
                        onLoadWidenedOptions = { viewModel.loadWidenedOptions() },
                        onRequestAiIdeas = {
                            viewModel.requestAiAlternativeOptions { ideas ->
                                ideas.forEach { viewModel.addOption(it) }
                            }
                        },
                        onContinue = {
                            currentStep = 5
                        }
                    )
                    5 -> Step4WhatCouldGoWrong(
                        options = listOfNotNull(
                            wizardState.optionA.ifBlank { null },
                            wizardState.optionB.ifBlank { null }
                        ) + wizardState.additionalOptions,
                        risks = wizardState.risks,
                        isLoadingAi = wizardState.isLoadingAi,
                        onAddRisk = { viewModel.addRisk(it) },
                        onRemoveRisk = { viewModel.removeRisk(it) },
                        onRequestSuggestions = { opt ->
                            viewModel.requestDownsideSuggestions(opt) { items ->
                                items.forEach { desc ->
                                    viewModel.addRisk(
                                        RiskItem(
                                            optionTitle = opt,
                                            description = desc,
                                            likelihood = "Possible",
                                            worstRealisticOutcome = desc,
                                            recoverability = "Maybe"
                                        )
                                    )
                                }
                            }
                        },
                        onContinue = {
                            if (wizardState.risks.isEmpty()) {
                                viewModel.addRisk(
                                    RiskItem(
                                        optionTitle = wizardState.optionA.ifBlank { "Option A" },
                                        description = "It might take longer to see results or adapt",
                                        likelihood = "Possible",
                                        worstRealisticOutcome = "Feeling temporary friction during the adjustment period",
                                        recoverability = "Yes"
                                    )
                                )
                            }
                            currentStep = 6
                        }
                    )
                    6 -> Step5DownsideAssessment(
                        risks = wizardState.risks,
                        onUpdateRisk = { idx, r -> viewModel.updateRisk(idx, r) },
                        onContinue = { currentStep = 7 }
                    )
                    7 -> Step6WhatMattersMost(
                        selectedPriorities = wizardState.priorities,
                        mustHaves = wizardState.mustHaves,
                        niceToHaves = wizardState.niceToHaves,
                        dontCares = wizardState.dontCares,
                        onToggle = { viewModel.togglePriority(it) },
                        onToggleFilterCategory = { item, cat -> viewModel.toggleFilterCategory(item, cat) },
                        onAddCustom = { viewModel.addCustomPriority(it) },
                        onContinue = {
                            viewModel.generateSafetyNetAndAnalysis {
                                currentStep = 8
                            }
                        },
                        isLoading = wizardState.isLoadingAi
                    )
                    8 -> Step7SafetyNet(
                        safetyNets = wizardState.safetyNets,
                        missingInfoItems = wizardState.missingInfoItems,
                        smallExperiment = wizardState.smallExperiment,
                        changeMindItems = wizardState.changeMindItems,
                        isUncertaintyNotMissingInfo = wizardState.isUncertaintyNotMissingInfo,
                        goodEnoughRequirements = wizardState.goodEnoughRequirements,
                        premortem = wizardState.premortem,
                        onToggle = { viewModel.toggleSafetyNetAction(it) },
                        onAddCustom = { viewModel.addCustomSafetyNetAction(it) },
                        onLoadMissingInfo = { viewModel.loadMissingInformation() },
                        onUpdateMissingInfoStatus = { id, s -> viewModel.updateMissingInfoStatus(id, s) },
                        onGenerateExperiment = { viewModel.loadSmallExperiment() },
                        onLoadWhatWouldChangeMind = { viewModel.loadWhatWouldChangeMind() },
                        onLoadGoodEnoughThreshold = { viewModel.loadGoodEnoughThreshold() },
                        onLoadPremortem = { viewModel.loadPremortem() },
                        onAddPremortemActions = { viewModel.addPremortemSafetyActionsToSafetyNets() },
                        onContinue = { currentStep = 9 }
                    )
                    9 -> Step8AiAnalysis(
                        analysis = wizardState.analysis,
                        risks = wizardState.risks,
                        isSpeaking = viewModel.isTtsSpeaking.collectAsState().value,
                        onSpeak = { text -> viewModel.speakText(text) },
                        onStopSpeak = { viewModel.stopSpeaking() },
                        perspectives = wizardState.trustedPerspectives,
                        selectedPerspective = wizardState.selectedPerspective,
                        onLoadPerspectives = { viewModel.loadTrustedPerspectives() },
                        onSelectPerspective = { viewModel.selectTrustedPerspective(it) },
                        onContinue = { currentStep = 10 }
                    )
                    10 -> StepArriveClarity(
                        optionA = wizardState.optionA,
                        optionB = wizardState.optionB,
                        clarityStatus = wizardState.clarityStatus,
                        waitingForReason = wizardState.waitingForReason,
                        regretOptionAGain = wizardState.regretOptionAGain,
                        regretOptionAMiss = wizardState.regretOptionAMiss,
                        regretOptionBGain = wizardState.regretOptionBGain,
                        regretOptionBMiss = wizardState.regretOptionBMiss,
                        regretHarderToAccept = wizardState.regretHarderToAccept,
                        reflection101010 = wizardState.reflection101010,
                        onLoad101010 = { viewModel.loadReflection101010() },
                        onSetClarityStatus = { viewModel.setClarityStatus(it) },
                        onSetWaitingForReason = { viewModel.setWaitingForReason(it) },
                        onUpdateRegretCheck = { aG, aM, bG, bM, h ->
                            viewModel.updateRegretCheck(aG, aM, bG, bM, h)
                        },
                        onReadyToDecide = { currentStep = 11 },
                        onNeedOneAnswer = { currentStep = 8 },
                        onWantToTest = { currentStep = 8 }
                    )
                    11 -> DecisionSummaryContent(
                        decisionTitle = wizardState.decisionTitle,
                        options = listOfNotNull(
                            wizardState.optionA.ifBlank { null },
                            wizardState.optionB.ifBlank { null }
                        ) + wizardState.additionalOptions,
                        reversibility = wizardState.reversibility,
                        biggestUpside = wizardState.biggestUpside.ifBlank { wizardState.analysis?.biggestUpside ?: "Positive forward momentum and growth" },
                        biggestConcern = wizardState.biggestConcern.ifBlank { wizardState.analysis?.biggestConcern ?: wizardState.mainConcern },
                        risks = wizardState.risks,
                        whatToVerify = wizardState.missingInfoItems.map { it.text },
                        nextSmallStep = wizardState.smallExperiment?.title ?: (wizardState.safetyNets.firstOrNull()?.text ?: "Take one small step forward"),
                        onProceedToDecide = { currentStep = 12 },
                        onBack = { currentStep = 10 },
                        analysis = wizardState.analysis
                    )
                    12 -> Step10WhatIf(
                        choice = wizardState.finalChoice,
                        optionA = wizardState.optionA,
                        optionB = wizardState.optionB,
                        scenarios = wizardState.analysis?.whatIfScenarios ?: emptyList(),
                        futureChoice = wizardState.whatIfChoiceFuture,
                        futureOther = wizardState.whatIfOtherFuture,
                        onUpdateWhatIf = { a, b -> viewModel.updateWhatIf(a, b) },
                        onContinue = { currentStep = 13 }
                    )
                    13 -> Step9FinalDecision(
                        options = listOfNotNull(
                            wizardState.optionA.ifBlank { null },
                            wizardState.optionB.ifBlank { null }
                        ) + wizardState.additionalOptions,
                        analysis = wizardState.analysis,
                        finalChoice = wizardState.finalChoice,
                        onSelectChoice = { choice ->
                            viewModel.setFinalChoice(choice)
                            currentStep = 14
                        },
                        onStillUnsure = {
                            viewModel.setFinalChoice("Still unsure")
                            currentStep = 14
                        },
                        onPauseDecision = {
                            showDecideLaterDialog = true
                        }
                    )
                    14 -> Step11DecisionJournalSave(
                        confidence = wizardState.confidenceLevel,
                        notes = wizardState.userNotes,
                        reopenConditions = wizardState.reopenConditions,
                        onUpdate = { c, n -> viewModel.updateJournalReflection(c, n) },
                        onUpdateReopenConditions = { viewModel.updateReopenConditions(it) },
                        onSave = {
                            viewModel.saveDecisionToJournal { savedId ->
                                onFinishDecision(savedId, true)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// Step 1: Starting Input ("What's on your mind?")
// ----------------------------------------------------
@Composable
fun Step1StartingInput(
    rawInput: String,
    isLoading: Boolean,
    onUpdateInput: (String) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "What's on your mind?",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "Speak naturally or write down the decision weighing on you. The app will help organize your thoughts.",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = CharcoalMuted,
            lineHeight = 22.sp
        )
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Large Voice Input Centerpiece (PRD Section 6 & 21)
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VoiceMicButton(
                currentText = rawInput,
                onSpokenText = onUpdateInput,
                sizeDp = 72,
                promptMessage = "Tell me what's on your mind..."
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Tap microphone to speak your situation",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = SoftSageDark,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Text Input Field
    OutlinedTextField(
        value = rawInput,
        onValueChange = onUpdateInput,
        placeholder = {
            Text("Or type here: e.g., I have two internship offers. One pays more but the other gives better experience...")
        },
        minLines = 4,
        maxLines = 8,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = DeepCharcoal,
            unfocusedTextColor = DeepCharcoal,
            cursorColor = SoftSageDark,
            focusedContainerColor = CreamWhite,
            unfocusedContainerColor = CreamWhite,
            focusedBorderColor = SoftSage,
            unfocusedBorderColor = CharcoalBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("decision_raw_input")
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Sample Example Prompts for Instant Discovery
    Text(
        text = "Try an example:",
        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
    )
    Spacer(modifier = Modifier.height(8.dp))
    val examples = listOf(
        "Should I accept this internship offer?",
        "Should I switch to the new project or stay?",
        "Should I study abroad next term?"
    )
    examples.forEach { example ->
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = WarmSandLight,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onUpdateInput(example) }
        ) {
            Text(
                text = example,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DeepCharcoal,
                    fontSize = 13.sp
                ),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        enabled = rawInput.isNotBlank() && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = SoftSage,
            contentColor = CreamWhite
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_confirmation")
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = CreamWhite,
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Listening & organizing...")
        } else {
            Text(
                text = "Continue →",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

// ----------------------------------------------------
// Step 2: Confirmation ("Here's what I understand")
// ----------------------------------------------------
@Composable
fun Step2Confirmation(
    decision: String,
    optA: String,
    optB: String,
    mainConcern: String,
    context: String,
    reversibility: String,
    isHighStakes: Boolean,
    highStakesNotice: String?,
    isOverthinking: Boolean,
    isInformationGap: Boolean = false,
    smallestMissingInfo: String = "",
    decisionDepth: String = "GUIDED",
    politeScripts: List<String> = emptyList(),
    contentDiscussion: String = "",
    fearAddressed: String = "",
    isDilemmaReframed: Boolean = false,
    onSelectReversibility: (String) -> Unit,
    onUpdate: (String, String, String, String) -> Unit,
    onQuickPath: () -> Unit,
    onMissingInfoPath: () -> Unit = {},
    onPauseForInfo: () -> Unit = {},
    onLooksGood: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editDecision by remember { mutableStateOf(decision) }
    var editOptA by remember { mutableStateOf(optA) }
    var editOptB by remember { mutableStateOf(optB) }
    var editConcern by remember { mutableStateOf(mainConcern) }

    Text(
        text = "Here's what I understand:",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Never silently assuming — check if this captures your real dilemma.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 0. Low-Stakes 60s Fast Path Callout (Scenario A & E)
    val isLowStakes = reversibility.contains("Easily", ignoreCase = true) || decisionDepth == "QUICK"
    if (isLowStakes && !isHighStakes) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = RecoverableGreenBg),
            border = androidx.compose.foundation.BorderStroke(1.dp, RecoverableGreen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .testTag("fast_path_callout")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = RecoverableGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Low Stakes Decision — 60s Fast Path",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This decision has easy reversibility and minimal downside. You don't need a heavy 12-step analysis for this.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onQuickPath,
                    colors = ButtonDefaults.buttonColors(containerColor = RecoverableGreen, contentColor = CreamWhite),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("take_fast_path_button")
                ) {
                    Text("Take 60-Second Fast Path →", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // 0B. Information Gap Callout (Scenario D)
    if (isInformationGap) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSandLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .testTag("information_gap_callout")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = SoftSageDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Information Problem Detected",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This appears to be an information problem rather than a decision problem. You are trying to choose between options while missing critical factual details that could make the answer obvious.",
                    style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CreamWhite,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Key questions to ask before deciding:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftSageDark
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. What information would actually resolve this?\n   → ${smallestMissingInfo.ifBlank { "Actual daily schedule and day-to-day expectations" }}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "2. Can that information be obtained before deciding?\n   → Usually yes (e.g. a 15-minute candid chat or clarifying email)",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, fontSize = 12.sp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onPauseForInfo,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Decide Later", fontSize = 12.sp, color = DeepCharcoal)
                    }
                    Button(
                        onClick = onMissingInfoPath,
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f).testTag("explore_missing_info_button")
                    ) {
                        Text("Find Answers →", fontSize = 12.sp, color = CreamWhite)
                    }
                }
            }
        }
    }

    // 1. High-Stakes Notice (Safety & Boundaries)
    if (isHighStakes) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SoftClayLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = SoftClay,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "High-stakes consideration",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = highStakesNotice ?: "This decision involves significant legal, health, financial, or emergency aspects. ClearPath helps organize your thoughts and questions, but cannot replace licensed professional advice.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCharcoal,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }

    // 2. Overthinking Detector (Empathetic reflection)
    if (isOverthinking) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MutedBlueLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, MutedBlue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = MutedBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Circling around this decision?",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "You've reflected on similar dilemmas recently. Often more analysis won't produce certainty. Consider checking what information is truly missing, or trying a small reversible test.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCharcoal,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }

    if (!isEditing) {
        if (isDilemmaReframed || politeScripts.isNotEmpty() || contentDiscussion.isNotBlank()) {
            EditorialSlicedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                triggerKey = decision
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSandLight),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSageDark.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("crystallized_dilemma_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ClintPetView(
                                    emotion = CompanionEmotion.COMFORTING,
                                    size = 32.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Clint's Gentle Reframing",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SoftSageDark,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                    Text(
                                        text = "Pinned to Decision-Making",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepCharcoal
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SoftSageLight,
                                modifier = Modifier.padding(start = 6.dp)
                            ) {
                                Text(
                                    text = "100% Offline",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftSageDark,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Decision: $decision",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                        )

                        if (contentDiscussion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = CreamWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "Why your priority matters:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DarkForestSage
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = contentDiscussion,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DeepCharcoal,
                                            lineHeight = 18.sp
                                        )
                                    )
                                }
                            }
                        }

                        if (fearAddressed.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SoftClayLight.copy(alpha = 0.6f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = SoftClayDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Fear addressed:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SoftClayDark
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = fearAddressed,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DeepCharcoal,
                                                lineHeight = 17.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        if (politeScripts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Polite Scripts (Ready to Copy):",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCharcoal
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            val clipboardManager = LocalClipboardManager.current
                            var copiedIndex by remember { mutableStateOf<Int?>(null) }

                            politeScripts.forEachIndexed { index, script ->
                                val isCopied = copiedIndex == index
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = CreamWhite,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCopied) RecoverableGreen else CharcoalBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = script,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DeepCharcoal,
                                                lineHeight = 17.sp
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(script))
                                                copiedIndex = index
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (isCopied) RecoverableGreen else SoftSageDark
                                            ),
                                            modifier = Modifier.testTag("copy_script_button_$index")
                                        ) {
                                            Icon(
                                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                                contentDescription = "Copy script",
                                                tint = if (isCopied) RecoverableGreen else SoftSageDark,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isCopied) "Copied" else "Copy",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = if (isCopied) RecoverableGreen else SoftSageDark,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                UnderstandItem(label = "Decision", text = decision)
                UnderstandItem(label = "Option A", text = optA)
                UnderstandItem(label = "Option B", text = optB)
                UnderstandItem(label = "Main concern", text = mainConcern)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        WhyWeAskCard(
            questionContext = "Can this decision be undone?",
            explanation = "Easily reversible decisions need little analysis — make the choice quickly or test with a small experiment. Hard-to-reverse decisions deserve careful thought. Clarifying reversibility early prevents days of overthinking."
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Reversibility Selector & Adaptive Depth
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSandLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "REVERSIBILITY (HOW EASY TO UNDO?)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SoftSageDark,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                val reversibilityLevels = listOf(
                    "Easily reversible",
                    "Partially reversible",
                    "Difficult to reverse",
                    "Effectively irreversible"
                )

                reversibilityLevels.forEach { level ->
                    val isSelected = reversibility.contains(level, ignoreCase = true) ||
                            (reversibility.isBlank() && level == "Partially reversible")
                    val badgeColor = when {
                        level.contains("Easily", ignoreCase = true) -> RecoverableGreen
                        level.contains("Partially", ignoreCase = true) -> MutedBlue
                        level.contains("Difficult", ignoreCase = true) -> WarmSand
                        else -> SoftClay
                    }
                    val badgeBg = when {
                        level.contains("Easily", ignoreCase = true) -> RecoverableGreenBg
                        level.contains("Partially", ignoreCase = true) -> MutedBlueLight
                        level.contains("Difficult", ignoreCase = true) -> WarmSandLight
                        else -> SoftClayLight
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) badgeBg else CreamWhite,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) badgeColor else CharcoalBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectReversibility(level) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) badgeColor else Color.Transparent)
                                    .border(1.dp, badgeColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = level,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = DeepCharcoal
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                val explanation = when {
                    reversibility.contains("Easily", ignoreCase = true) ->
                        "🌱 Low stakes — you can test this with minimal downside. A 1-minute quick evaluation is often enough."
                    reversibility.contains("Partially", ignoreCase = true) ->
                        "🌊 Moderate permanence — you can change course later, though it may take extra time or effort."
                    reversibility.contains("Difficult", ignoreCase = true) ->
                        "⏳ High commitment — changing course later requires significant friction. A thorough review is recommended."
                    else ->
                        "🧱 High permanence — once done, this cannot easily be undone. Focus closely on worst-case survivability."
                }
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        lineHeight = 18.sp
                    )
                )

                if (reversibility.contains("Easily", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onQuickPath,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RecoverableGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚡ Low risk: Jump directly to safety net & test →",
                            color = RecoverableGreen,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                } else if (reversibility.contains("Difficult", ignoreCase = true) || reversibility.contains("irreversible", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SoftClayLight.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = SoftClay,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "High permanence: We recommend completing the full downside risk assessment and safety net.",
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, fontSize = 12.5.sp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    editDecision = decision
                    editOptA = optA
                    editOptB = optB
                    editConcern = mainConcern
                    isEditing = true
                },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("edit_understanding_button")
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit", color = DeepCharcoal)
            }

            Button(
                onClick = onLooksGood,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp)
                    .testTag("looks_good_button")
            ) {
                Text(
                    text = "Looks good →",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    } else {
        // Editing Mode
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = DeepCharcoal,
                unfocusedTextColor = DeepCharcoal,
                cursorColor = SoftSageDark,
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                focusedBorderColor = SoftSage,
                unfocusedBorderColor = CharcoalBorder
            )
            OutlinedTextField(
                value = editDecision,
                onValueChange = { editDecision = it },
                label = { Text("Decision") },
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editOptA,
                onValueChange = { editOptA = it },
                label = { Text("Option A") },
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editOptB,
                onValueChange = { editOptB = it },
                label = { Text("Option B") },
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = editConcern,
                onValueChange = { editConcern = it },
                label = { Text("Main concern") },
                colors = fieldColors,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                onUpdate(editDecision, editOptA, editOptB, editConcern)
                isEditing = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save changes")
        }
    }
}

@Composable
fun UnderstandItem(label: String, text: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = SoftSageDark,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = DeepCharcoal,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

// ----------------------------------------------------
// Step 3: Why Am I Stuck? (PRD Section 4: Stuck Type Diagnosis)
// ----------------------------------------------------
@Composable
fun StepWhyAmIStuck(
    stuckType: String,
    stuckDescription: String,
    onSelectStuckType: (String, String) -> Unit,
    onContinue: () -> Unit
) {
    val stuckOptions = listOf(
        Pair(com.example.data.StuckStateCatalog.AFRAID_OF_CHOOSING_WRONG, "Afraid of choosing wrong — Worrying about making a mistake or facing regret."),
        Pair(com.example.data.StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD, "Both options seem good — Competing upside; need to widen the frame or define 'good enough'."),
        Pair(com.example.data.StuckStateCatalog.MISSING_INFORMATION, "Missing information — Unsure if you have enough facts, or searching for unattainable certainty."),
        Pair(com.example.data.StuckStateCatalog.TOO_MANY_WORRIES, "Too many possible worries — Cloud of anxious scenarios; need realistic probability grounding."),
        Pair(com.example.data.StuckStateCatalog.KEEPS_CHANGING_MIND, "Keeps changing mind — Fluctuating emotions and logic; 10-10-10 perspective needed."),
        Pair(com.example.data.StuckStateCatalog.DOESNT_KNOW_WANTS, "Doesn't know what they actually want — Need to name core priorities and non-negotiables."),
        Pair(com.example.data.StuckStateCatalog.FEELS_IRREVERSIBLE, "Decision feels irreversible — Treating choice as a trap; need small experiments or partial steps."),
        Pair(com.example.data.StuckStateCatalog.EMOTIONAL_PRESSURE, "Emotional pressure — External urgency or expectations clouding quiet intuition."),
        Pair(com.example.data.StuckStateCatalog.CANNOT_COMMIT_ENOUGH_INFO, "Already has enough info but cannot commit — Analysis paralysis; needs a clear stopping rule.")
    )

    var currentSelection by remember {
        mutableStateOf(stuckType.ifBlank { com.example.data.StuckStateCatalog.AFRAID_OF_CHOOSING_WRONG })
    }

    Text(
        text = "Why are you stuck?",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Different reasons for hesitation need different kinds of thinking. ClearPath adapts the reasoning engine to your exact block.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    val currentExplanation = com.example.data.StuckStateCatalog.getExplanation(currentSelection)
    Spacer(modifier = Modifier.height(16.dp))
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSageLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = SoftSageDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Adaptive Reasoning Focus",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftSageDark
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (stuckDescription.isNotBlank() && currentSelection == stuckType) stuckDescription else currentExplanation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DeepCharcoal,
                        lineHeight = 18.sp
                    )
                )

                val specificTip = when (currentSelection) {
                    com.example.data.StuckStateCatalog.MISSING_INFORMATION ->
                        "Key inquiry: What information would actually resolve this, and can it be obtained before deciding? If it cannot be known, it is unavoidable uncertainty rather than missing information."
                    com.example.data.StuckStateCatalog.BOTH_OPTIONS_SEEM_GOOD ->
                        "Key inquiry: When two good options compete, there is no single 'flawless' choice. Which trade-off are you more willing to embrace right now?"
                    com.example.data.StuckStateCatalog.AFRAID_OF_CHOOSING_WRONG ->
                        "Key inquiry: Separate realistic downside from catastrophic fear. If the worst realistic case happens, can you recover?"
                    com.example.data.StuckStateCatalog.TOO_MANY_THINGS ->
                        "Key inquiry: When options feel overwhelming, don't look for perfection. What is one small, low-risk test to take in the next 24 hours?"
                    else -> null
                }
                if (specificTip != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = CreamWhite,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = specificTip,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Select what best describes your hesitation:",
        style = MaterialTheme.typography.labelMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        stuckOptions.forEach { (typeKey, description) ->
            val isSelected = currentSelection == typeKey
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CreamWhite else CreamWhite.copy(alpha = 0.8f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) SoftSage else CharcoalBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentSelection = typeKey
                        val exp = com.example.data.StuckStateCatalog.getExplanation(typeKey)
                        onSelectStuckType(typeKey, exp)
                    }
                    .testTag("stuck_type_${typeKey.take(10)}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SoftSage else Color.Transparent)
                            .border(1.5.dp, if (isSelected) SoftSage else CharcoalBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CreamWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = DeepCharcoal,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_from_stuck_screen")
    ) {
        Text("Continue with this focus →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

// ----------------------------------------------------
// Step 4: Option Identification
// ----------------------------------------------------
@Composable
fun Step3Options(
    optionA: String,
    optionB: String,
    additionalOptions: List<String>,
    widenedOptions: List<com.example.data.WidenedOption> = emptyList(),
    isLoadingAi: Boolean,
    onAddOption: (String) -> Unit,
    onLoadWidenedOptions: () -> Unit = {},
    onRequestAiIdeas: () -> Unit,
    onContinue: () -> Unit
) {
    var newOptionText by remember { mutableStateOf("") }

    Text(
        text = "What are the choices you're considering?",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
        text = "You can evaluate two or more paths.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(20.dp))

    OptionDisplayCard(index = "A", title = optionA)
    Spacer(modifier = Modifier.height(10.dp))
    OptionDisplayCard(index = "B", title = optionB)

    additionalOptions.forEachIndexed { idx, opt ->
        Spacer(modifier = Modifier.height(10.dp))
        val letter = ('C' + idx).toString()
        OptionDisplayCard(index = letter, title = opt)
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Add another option field
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = newOptionText,
            onValueChange = { newOptionText = it },
            placeholder = { Text("Add another option...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                focusedBorderColor = SoftSage,
                unfocusedBorderColor = CharcoalBorder
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (newOptionText.isNotBlank()) {
                    onAddOption(newOptionText)
                    newOptionText = ""
                }
            },
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(SoftSageLight)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add option", tint = SoftSageDark)
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Widen the Frame Section (Requirement 2)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Widen the frame",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Are these really your only choices? People often get stuck because they view decisions as binary either/or traps.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            if (widenedOptions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    widenedOptions.forEach { wOpt ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CreamWhite,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Surface(
                                        color = SoftSageLight,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = wOpt.type,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SoftSageDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    TextButton(
                                        onClick = { onAddOption(wOpt.title) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("+ Add option", fontSize = 12.sp, color = SoftSageDark, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = wOpt.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = wOpt.rationale,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 16.sp)
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLoadWidenedOptions,
                    enabled = !isLoadingAi,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLoadingAi) "Widening perspective..." else "Explore 3rd paths & phased approaches",
                        color = SoftSageDark
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // "I'm not sure what my options are" (PRD Section 7)
    OutlinedButton(
        onClick = onRequestAiIdeas,
        enabled = !isLoadingAi,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SoftClay, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isLoadingAi) "Exploring possibilities..." else "I'm not sure what my options are",
            color = DeepCharcoal
        )
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_downsides")
    ) {
        Text("Continue to downsides →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun OptionDisplayCard(index: String, title: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = SoftSageLight,
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = index,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = SoftSageDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = DeepCharcoal
                )
            )
        }
    }
}

// ----------------------------------------------------
// Step 4: What Could Go Wrong? (PRD Section 8)
// ----------------------------------------------------
@Composable
fun Step4WhatCouldGoWrong(
    options: List<String>,
    risks: List<RiskItem>,
    isLoadingAi: Boolean,
    onAddRisk: (RiskItem) -> Unit,
    onRemoveRisk: (Int) -> Unit,
    onRequestSuggestions: (String) -> Unit,
    onContinue: () -> Unit
) {
    var selectedOptionForNewRisk by remember { mutableStateOf(options.firstOrNull() ?: "Option A") }
    var newRiskText by remember { mutableStateOf("") }

    Text(
        text = "Let's look at the possible downsides.",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "What could realistically go wrong? Let's look at the difficult version calmly.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // List existing risks
    if (risks.isNotEmpty()) {
        Text(
            text = "Identified downsides:",
            style = MaterialTheme.typography.labelMedium.copy(color = SoftSageDark, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        risks.forEachIndexed { index, risk ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = risk.optionTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MutedBlueDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = risk.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal)
                        )
                    }
                    IconButton(onClick = { onRemoveRisk(index) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = CharcoalMuted)
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Add Downside Card
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Add a potential downside:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option selector chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { opt ->
                    val isSel = selectedOptionForNewRisk == opt
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSel) SoftSage else CreamWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) SoftSageDark else CharcoalBorder),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedOptionForNewRisk = opt }
                    ) {
                        Text(
                            text = opt.take(18) + if (opt.length > 18) "..." else "",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSel) CreamWhite else DeepCharcoal
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = newRiskText,
                onValueChange = { newRiskText = it },
                placeholder = { Text("What downside could happen?") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CreamWhite,
                    unfocusedContainerColor = CreamWhite,
                    focusedBorderColor = SoftSage,
                    unfocusedBorderColor = CharcoalBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { onRequestSuggestions(selectedOptionForNewRisk) },
                    enabled = !isLoadingAi
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftSage, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Suggest realistic downsides", color = SoftSageDark, fontSize = 13.sp)
                }

                Button(
                    onClick = {
                        if (newRiskText.isNotBlank()) {
                            onAddRisk(
                                RiskItem(
                                    optionTitle = selectedOptionForNewRisk,
                                    description = newRiskText.trim(),
                                    likelihood = "Possible",
                                    worstRealisticOutcome = newRiskText.trim(),
                                    recoverability = "Maybe"
                                )
                            )
                            newRiskText = ""
                        }
                    },
                    enabled = newRiskText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Add")
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_assessment")
    ) {
        Text("Assess downside & recoverability →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

// ----------------------------------------------------
// Step 5: Probability, Worst Realistic Outcome, Recoverability
// (PRD Sections 9, 10, 11)
// ----------------------------------------------------
@Composable
fun Step5DownsideAssessment(
    risks: List<RiskItem>,
    onUpdateRisk: (Int, RiskItem) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "Worst realistic outcome & recoverability",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Not the absolute worst thing imaginable — the worst outcome that could realistically happen.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    WhyWeAskCard(
        questionContext = "How likely is this downside, and could you recover if it happens?",
        explanation = "A bad outcome isn't automatically a reason to avoid a decision. What matters is how realistic it is, how much it would affect you, and whether you could handle the consequences."
    )

    Spacer(modifier = Modifier.height(16.dp))

    risks.forEachIndexed { index, risk ->
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "${risk.optionTitle}: ${risk.description}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Probability (PRD Section 9)
                Text(
                    text = "How likely is this to happen? (Your estimate)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                LikelihoodSelector(
                    selected = risk.likelihood,
                    onSelect = { lvl ->
                        onUpdateRisk(index, risk.copy(likelihood = lvl))
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Impact (Severity)
                Text(
                    text = "Impact (If it happens, how serious would it be?)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                ImpactSelector(
                    selected = risk.impact,
                    onSelect = { imp ->
                        onUpdateRisk(index, risk.copy(impact = imp))
                    }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Worst Realistic Outcome (PRD Section 10)
                Text(
                    text = "What is the worst realistic outcome?",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = risk.worstRealisticOutcome,
                    onValueChange = {
                        onUpdateRisk(index, risk.copy(worstRealisticOutcome = it))
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Recoverability / "Can I Live With It?" (PRD Section 11)
                Text(
                    text = "If this happens, could you recover?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                RecoverabilitySelector(
                    selected = risk.recoverability,
                    onSelect = { rec ->
                        onUpdateRisk(index, risk.copy(recoverability = rec))
                    }
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // PRD Section 14: Signature Question
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SoftSageDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "The Signature Question",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "“If the most serious realistic downside happened, could you live with it?”",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    lineHeight = 24.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "A bad outcome is not automatically a reason to avoid a decision. Separating survivable friction from catastrophic fear is how clarity is found.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_priorities")
    ) {
        Text("What matters most to you? →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

// ----------------------------------------------------
// Step 7: What Matters Most? (PRD Section 5 & 12)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step6WhatMattersMost(
    selectedPriorities: Set<String>,
    mustHaves: Set<String>,
    niceToHaves: Set<String>,
    dontCares: Set<String>,
    onToggle: (String) -> Unit,
    onToggleFilterCategory: (String, String) -> Unit,
    onAddCustom: (String) -> Unit,
    onContinue: () -> Unit,
    isLoading: Boolean
) {
    var customPriorityText by remember { mutableStateOf("") }
    val defaultPriorities = listOf(
        "Learning",
        "Career growth",
        "Money",
        "Time",
        "Relationships",
        "Happiness",
        "Safety",
        "Stability",
        "Independence",
        "Personal values"
    )

    Text(
        text = "What matters?",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Separate what is essential from what is simply nice to have. Don't evaluate every detail with the same weight.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Must-Have / Nice-to-Have / Don't-Care Filters (PRD Section 5: Filter)
    Text(
        text = "Filter your factors by importance:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = SoftSageDark)
    )
    Spacer(modifier = Modifier.height(10.dp))

    val allFactors = (defaultPriorities + selectedPriorities).distinct()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        allFactors.take(6).forEach { factor ->
            val isMust = mustHaves.contains(factor)
            val isNice = niceToHaves.contains(factor)
            val isDont = dontCares.contains(factor)

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = factor,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilterPill(
                            label = "Must",
                            selected = isMust,
                            activeColor = SoftSageDark,
                            onClick = { onToggleFilterCategory(factor, "MUST_HAVE") }
                        )
                        FilterPill(
                            label = "Nice",
                            selected = isNice,
                            activeColor = MutedBlue,
                            onClick = { onToggleFilterCategory(factor, "NICE_TO_HAVE") }
                        )
                        FilterPill(
                            label = "Skip",
                            selected = isDont,
                            activeColor = CharcoalMuted,
                            onClick = { onToggleFilterCategory(factor, "DONT_CARE") }
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Priorities Selection
    Text(
        text = "Core priorities guiding this decision (tap up to 3):",
        style = MaterialTheme.typography.labelMedium.copy(color = CharcoalMuted)
    )
    Spacer(modifier = Modifier.height(10.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        defaultPriorities.forEach { p ->
            PriorityChip(
                text = p,
                isSelected = selectedPriorities.contains(p) || mustHaves.contains(p),
                onClick = { onToggle(p) }
            )
        }
        selectedPriorities.filter { !defaultPriorities.contains(it) }.forEach { custom ->
            PriorityChip(
                text = custom,
                isSelected = true,
                onClick = { onToggle(custom) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Add custom factor
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = customPriorityText,
            onValueChange = { customPriorityText = it },
            placeholder = { Text("Add custom factor...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                focusedBorderColor = SoftSage,
                unfocusedBorderColor = CharcoalBorder
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (customPriorityText.isNotBlank()) {
                    onAddCustom(customPriorityText)
                    onToggleFilterCategory(customPriorityText, "MUST_HAVE")
                    customPriorityText = ""
                }
            },
            enabled = customPriorityText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add")
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // PRD Section 12: The Good-Enough Check
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSageLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = SoftSageDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "The Good-Enough Check",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "What would make an option good enough to choose?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "If an option meets everything you've marked as a Must-Have, you don't need to prove that it is perfect to move forward.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        enabled = (selectedPriorities.isNotEmpty() || mustHaves.isNotEmpty()) && !isLoading,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_safety_net")
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = CreamWhite, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generating practical safety net...")
        } else {
            Text("Generate safety net actions →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}

@Composable
fun FilterPill(label: String, selected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (selected) activeColor else CreamWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) activeColor else CharcoalBorder),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) CreamWhite else DeepCharcoal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// ----------------------------------------------------
// Step 7: Safety Net (PRD Section 13) + Missing Info + Small Experiment
// ----------------------------------------------------
@Composable
fun Step7SafetyNet(
    safetyNets: List<com.example.data.SafetyNetAction>,
    missingInfoItems: List<com.example.data.MissingInfoItem>,
    smallExperiment: com.example.data.SmallExperiment?,
    changeMindItems: List<com.example.data.ChangeMindItem> = emptyList(),
    isUncertaintyNotMissingInfo: Boolean = false,
    goodEnoughRequirements: List<com.example.data.GoodEnoughRequirement> = emptyList(),
    premortem: com.example.data.PremortemAnalysis? = null,
    onToggle: (Int) -> Unit,
    onAddCustom: (String) -> Unit,
    onLoadMissingInfo: () -> Unit,
    onUpdateMissingInfoStatus: (String, String) -> Unit,
    onGenerateExperiment: () -> Unit,
    onLoadWhatWouldChangeMind: () -> Unit = {},
    onLoadGoodEnoughThreshold: () -> Unit = {},
    onLoadPremortem: () -> Unit = {},
    onAddPremortemActions: () -> Unit = {},
    onContinue: () -> Unit
) {
    var newActionText by remember { mutableStateOf("") }

    Text(
        text = "Your safety net",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "What would make this decision feel safer? Focus on reducing uncertainty, not eliminating all risk.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 1. Safety Net Actions Checklist
    safetyNets.forEachIndexed { index, action ->
        SafetyNetCheckboxRow(
            text = action.text,
            isChecked = action.isChecked,
            onToggle = { onToggle(index) },
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = newActionText,
            onValueChange = { newActionText = it },
            placeholder = { Text("Add another safety step...") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CreamWhite,
                unfocusedContainerColor = CreamWhite,
                focusedBorderColor = SoftSage,
                unfocusedBorderColor = CharcoalBorder
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (newActionText.isNotBlank()) {
                    onAddCustom(newActionText)
                    newActionText = ""
                }
            },
            enabled = newActionText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add")
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Premortem Analysis (Requirement 8)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SoftClayLight.copy(alpha = 0.4f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = SoftClay, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Premortem analysis (Failure prevention)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Imagine it is six months from now and this decision went wrong. What realistically broke down?",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            if (premortem == null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLoadPremortem,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftClayDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = SoftClayDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Run premortem simulation", color = SoftClayDark, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Scenario: ${premortem.failureStory}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Key vulnerabilities:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DeepCharcoal)
                        )
                        premortem.potentialCauses.forEach { cause ->
                            Text(
                                text = "• $cause",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }

                        if (premortem.preventiveSafetyActions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onAddPremortemActions,
                                colors = ButtonDefaults.buttonColors(containerColor = SoftSageDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Add preventive steps to safety net checklist", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // What Would Change Your Mind Section (Requirement 5)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = MutedBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "What would actually change your mind?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Are you waiting for information that is findable, or attempting to eliminate inherently unpredictable future uncertainty?",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            if (changeMindItems.isEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLoadWhatWouldChangeMind,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBlueDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Analyze potential pivot triggers", color = MutedBlueDark, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                if (isUncertaintyNotMissingInfo) {
                    Surface(
                        color = WarmSandLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "Insight: None of these triggers represent realistically missing data. This is inherent future uncertainty that can only be handled with your safety net, not endless research.",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
                changeMindItems.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = WarmSandLight.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.question,
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (item.isObtainable) RecoverableGreenBg else SoftClayLight
                            ) {
                                Text(
                                    text = if (item.isObtainable) "Findable Fact" else "Future Uncertainty",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (item.isObtainable) RecoverableGreen else SoftClay,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Good-Enough (Satisficing) Threshold (Requirement 7)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Good-enough (satisficing) threshold",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "When an option meets your essential needs, further comparison wastes energy. Define what is 'good enough'.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            if (goodEnoughRequirements.isEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLoadGoodEnoughThreshold,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Define good-enough criteria", color = SoftSageDark)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                goodEnoughRequirements.forEach { req ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = req.requirement, style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal))
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 2. Missing-Information Detector Section (PRD Section 6)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MutedBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Missing information detector",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            WhyWeAskCard(
                questionContext = "What facts would actually change your mind?",
                explanation = "Overthinking often comes from waiting for certainty about things you cannot predict. Separating checkable facts from unknown variables helps you know when to stop researching and act."
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Uncertainty often comes from a few critical unknowns. Check which ones apply to you:",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (missingInfoItems.isEmpty()) {
                OutlinedButton(
                    onClick = onLoadMissingInfo,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedBlueDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MutedBlueDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan for critical unknowns", color = MutedBlueDark, fontWeight = FontWeight.SemiBold)
                }
            } else {
                missingInfoItems.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = item.text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = DeepCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val statuses = listOf(
                                "I know this" to RecoverableGreen,
                                "Need to check" to SoftClay,
                                "Not important" to CharcoalMuted
                            )
                            statuses.forEach { (statusText, statusColor) ->
                                val isSelected = item.status == statusText
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) statusColor.copy(alpha = 0.15f) else WarmSandLight,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) statusColor else CharcoalBorder
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { onUpdateMissingInfoStatus(item.id, statusText) }
                                ) {
                                    Text(
                                        text = statusText,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) statusColor else DeepCharcoal,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3. Small Experiment Mode (PRD Section 7)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = SoftSageDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Small experiment mode",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Before making an irreversible commitment, can you run a safe, low-stakes test?",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (smallExperiment == null) {
                OutlinedButton(
                    onClick = onGenerateExperiment,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Suggest a 20–30 min test", color = SoftSageDark)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = smallExperiment.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🎯 What to learn: ${smallExperiment.whatToLearn}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⏱ Time required: ${smallExperiment.timeRequired}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔄 Changes mind if: ${smallExperiment.whatWouldChangeDecision}",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { onAddCustom("Run experiment: ${smallExperiment.title}") },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add experiment to safety net checklist", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_ai_analysis")
    ) {
        Text("See synthesis & insights →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

// ----------------------------------------------------
// Step 8: AI Analysis (PRD Section 14) + Audio TTS + Trusted Perspectives + Risk Map
// ----------------------------------------------------
@Composable
fun Step8AiAnalysis(
    analysis: com.example.ai.GeneratedAnalysis?,
    risks: List<RiskItem> = emptyList(),
    isSpeaking: Boolean,
    onSpeak: (String) -> Unit,
    onStopSpeak: () -> Unit,
    perspectives: List<com.example.data.TrustedPerspective>,
    selectedPerspective: com.example.data.TrustedPerspective?,
    onLoadPerspectives: () -> Unit,
    onSelectPerspective: (com.example.data.TrustedPerspective) -> Unit,
    onContinue: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Decision Analysis",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "ClearPath's interpretation of what appears to actually be happening.",
                style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
            )
        }

        // Voice Read Aloud Button
        Surface(
            shape = CircleShape,
            color = if (isSpeaking) SoftSage else WarmSandLight,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSpeaking) SoftSage else CharcoalBorder),
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable {
                    if (isSpeaking) {
                        onStopSpeak()
                    } else {
                        val speechText = buildString {
                            append("Here is ClearPath's decision analysis. ")
                            if (analysis != null) {
                                if (analysis.centralTradeOff.isNotBlank()) {
                                    append("The central trade-off: ${analysis.centralTradeOff}. ")
                                }
                                if (analysis.hesitationDriver.isNotBlank()) {
                                    append("What appears to be holding you back: ${analysis.hesitationDriver}. ")
                                }
                                analysis.nonObviousInsights.firstOrNull()?.let {
                                    append("Key insight: $it. ")
                                }
                                if (analysis.recommendation.recommendationTitle.isNotBlank()) {
                                    append("${analysis.recommendation.recommendationTitle}. ")
                                }
                            } else {
                                append("Remember, ClearPath helps you understand your decision. You make the final choice.")
                            }
                        }
                        onSpeak(speechText)
                    }
                }
                .testTag("tts_reflection_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                    contentDescription = if (isSpeaking) "Stop voice" else "Read aloud",
                    tint = if (isSpeaking) CreamWhite else DeepCharcoal,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // 1. ClearPath's Recommendation & Decision Readiness
    if (analysis?.recommendation?.recommendationTitle?.isNotBlank() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SoftSageLight.copy(alpha = 0.55f)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSageDark),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_recommendation_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CLEARPATH'S READ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Surface(
                        color = SoftSageDark,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = analysis.decisionReadiness,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CreamWhite,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = analysis.recommendation.recommendationTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        fontSize = 17.sp,
                        lineHeight = 23.sp
                    )
                )

                if (analysis.recommendation.whyPoints.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Why:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark
                        )
                    )
                    analysis.recommendation.whyPoints.forEach { pt ->
                        Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "• ", style = MaterialTheme.typography.bodySmall.copy(color = SoftSageDark, fontWeight = FontWeight.Bold))
                            Text(text = pt, style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 19.sp))
                        }
                    }
                }

                if (analysis.recommendation.whatCouldChangeThis.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "What could change this:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalMuted
                        )
                    )
                    analysis.recommendation.whatCouldChangeThis.forEach { ch ->
                        Row(modifier = Modifier.padding(top = 3.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "• ", style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted))
                            Text(text = ch, style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = analysis.recommendation.userAutonomyNotice,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CharcoalMuted,
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 2. The Central Trade-Off
    if (analysis?.centralTradeOff?.isNotBlank() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSageDark),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_central_tradeoff_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SoftSageDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WHAT THIS FUNDAMENTALLY COMES DOWN TO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = analysis.centralTradeOff,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 16.sp,
                        lineHeight = 23.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 3. What is Actually Driving Your Hesitation
    if (analysis?.hesitationDriver?.isNotBlank() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_hesitation_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = SoftClay,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WHAT APPEARS TO BE HOLDING YOU BACK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftClay,
                                fontSize = 11.5.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                    }
                    if (analysis.hesitationNature.isNotBlank()) {
                        Surface(
                            color = WarmSandLight,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CharcoalBorder)
                        ) {
                            Text(
                                text = analysis.hesitationNature,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal,
                                    fontSize = 10.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = analysis.hesitationDriver,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCharcoal,
                        fontSize = 14.5.sp,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 4. Non-Obvious Insights
    if (analysis?.nonObviousInsights?.isNotEmpty() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_insights_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = DarkForestSage,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NON-OBVIOUS INSIGHTS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkForestSage,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                analysis.nonObviousInsights.forEachIndexed { idx, ins ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SoftSageLight,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftSageDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ins,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 5. Options Compared Against User Priorities
    if (analysis?.priorityComparisons?.isNotEmpty() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_priorities_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SoftSageDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOW OPTIONS MATCH WHAT MATTERS TO YOU",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                analysis.priorityComparisons.forEach { comp ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(WarmIvory.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = comp.priority,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCharcoal,
                                    fontSize = 12.sp
                                )
                            )
                            Surface(
                                color = SoftSageLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Favors ${comp.favoredOption}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftSageDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = comp.comparisonReason,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp
                            )
                        )
                    }
                }
                if (analysis.conflictingPriorities.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Conflicting priorities: ${analysis.conflictingPriorities}",
                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                    )
                }
                if (analysis.dominantPriority.isNotBlank()) {
                    Text(
                        text = "Dominant compass: ${analysis.dominantPriority}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SoftSageDark, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 6. Deep Risk Analysis
    if (analysis?.analyzedRisks?.isNotEmpty() == true) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_risks_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SoftClay,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DECISION-RELEVANT RISKS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftClay,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                analysis.analyzedRisks.forEach { r ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(WarmIvory.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = r.whatCouldHappen,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Why it matters: ${r.whyItMatters}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalMuted,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = WarmSandLight, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "Likelihood: ${r.likelihood}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = DeepCharcoal, fontSize = 10.5.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(color = SoftSageLight, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "Recoverability: ${r.recoverability}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = SoftSageDark, fontSize = 10.5.sp, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 7. Information Sufficiency
    if (analysis != null) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CreamWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_info_sufficiency_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = if (analysis.infoSufficiency.isInformationProblem) DarkForestSage else SoftSageDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (analysis.infoSufficiency.isInformationProblem) "ACTIONABLE INFORMATION GAPS" else "INFORMATION SUFFICIENCY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (analysis.infoSufficiency.isInformationProblem) DarkForestSage else SoftSageDark,
                            fontSize = 11.5.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = analysis.infoSufficiency.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCharcoal,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                )
                if (analysis.infoSufficiency.isInformationProblem && analysis.infoSufficiency.actionableVerifications.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Specific verifications that could change your mind:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark
                        )
                    )
                    analysis.infoSufficiency.actionableVerifications.forEach { chk ->
                        Row(modifier = Modifier.padding(top = 3.dp), verticalAlignment = Alignment.Top) {
                            Text(text = "• ", style = MaterialTheme.typography.bodySmall.copy(color = SoftSageDark, fontWeight = FontWeight.Bold))
                            Text(text = chk, style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 8. Special Case 11: Multi-Interest Overwhelm (if detected)
    if (analysis?.isOverwhelmedByManyInterests == true && !analysis.sequencingAdvice.isNullOrBlank()) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSandLight.copy(alpha = 0.5f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analysis_overwhelmed_interests_card")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SEQUENCING MULTIPLE PASSIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftClay,
                        fontSize = 11.5.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = analysis.sequencingAdvice,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCharcoal,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Trusted-Person Perspective Section (PRD Section 8)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmIvory),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "LOOK THROUGH ANOTHER LENS",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SoftSageDark,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Step outside your own head by considering how others would view this choice:",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (perspectives.isEmpty()) {
                OutlinedButton(
                    onClick = onLoadPerspectives,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reveal trusted perspectives", color = SoftSageDark)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    perspectives.forEach { p ->
                        val isSelected = (selectedPerspective?.name ?: perspectives.firstOrNull()?.name) == p.name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) SoftSage else CreamWhite,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) SoftSage else CharcoalBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectPerspective(p) }
                        ) {
                            Text(
                                text = p.name.replace("A ", "").replace("Your ", ""),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) CreamWhite else DeepCharcoal,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val activePerspective = selectedPerspective ?: perspectives.firstOrNull()
                if (activePerspective != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = CreamWhite,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = activePerspective.description,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SoftSageDark,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            activePerspective.questions.forEach { question ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SoftSage,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "\"$question\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = DeepCharcoal,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 20.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (risks.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        RiskMapSection(risks = risks)
    }

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_to_summary")
    ) {
        Text("Review Decision Summary →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun AnalysisBlock(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                color = SoftSageDark,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = DeepCharcoal,
                lineHeight = 22.sp
            )
        )
    }
}

// ----------------------------------------------------
// Step 10: Arrive & Clarity Check (PRD Section 10, 11 & 13)
// ----------------------------------------------------
@Composable
fun StepArriveClarity(
    optionA: String,
    optionB: String,
    clarityStatus: String,
    waitingForReason: String,
    regretOptionAGain: String,
    regretOptionAMiss: String,
    regretOptionBGain: String,
    regretOptionBMiss: String,
    regretHarderToAccept: String,
    reflection101010: Triple<String, String, String>? = null,
    onLoad101010: () -> Unit = {},
    onSetClarityStatus: (String) -> Unit,
    onSetWaitingForReason: (String) -> Unit,
    onUpdateRegretCheck: (String, String, String, String, String) -> Unit,
    onReadyToDecide: () -> Unit,
    onNeedOneAnswer: () -> Unit,
    onWantToTest: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(clarityStatus.ifBlank { "READY" }) }
    var waitingReason by remember { mutableStateOf(waitingForReason) }
    var rOptAGain by remember { mutableStateOf(regretOptionAGain) }
    var rOptAMiss by remember { mutableStateOf(regretOptionAMiss) }
    var rOptBGain by remember { mutableStateOf(regretOptionBGain) }
    var rOptBMiss by remember { mutableStateOf(regretOptionBMiss) }
    var harderChoice by remember { mutableStateOf(regretHarderToAccept) }

    Text(
        text = "Do you have enough clarity to move forward?",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "You don't need 100% certainty. The goal is simply enough clarity to act.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(20.dp))

    val clarityOptions = listOf(
        Pair("READY", "I'm ready to move forward"),
        Pair("NEED_ANSWER", "I need one more specific answer"),
        Pair("WANT_TO_TEST", "I want to test it with a small experiment"),
        Pair("STILL_STUCK", "I'm still stuck / overthinking")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        clarityOptions.forEach { (key, label) ->
            val isSelected = selectedStatus == key
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CreamWhite else CreamWhite.copy(alpha = 0.8f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) SoftSage else CharcoalBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedStatus = key
                        onSetClarityStatus(key)
                    }
                    .testTag("clarity_status_$key")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SoftSage else Color.Transparent)
                            .border(1.5.dp, if (isSelected) SoftSage else CharcoalBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CreamWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = DeepCharcoal
                        )
                    )
                }
            }
        }
    }

    // 10-10-10 Perspective (Requirement 6)
    Spacer(modifier = Modifier.height(20.dp))
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "10-10-10 Perspective",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Gain emotional distance by viewing this across three distinct time horizons.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            if (reflection101010 == null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onLoad101010,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apply 10-10-10 perspective", color = SoftSageDark)
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CreamWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "⏱ In 10 minutes: ${reflection101010.first}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🗓 In 10 months: ${reflection101010.second}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "🌟 In 10 years: ${reflection101010.third}",
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                        )
                    }
                }
            }
        }
    }

    // Unfold Anti-Overthinking Flow when STILL_STUCK is chosen
    if (selectedStatus == "STILL_STUCK") {
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WarmSandLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Anti-Overthinking Check",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You've already explored the main trade-offs. More comparison may not give you much more clarity. Usually, when we stay stuck here, we're waiting for something specific.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCharcoal,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "What are you waiting for?",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftSageDark
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                val waitingOptions = listOf(
                    Pair("CERTAINTY", "Certainty — \"Decisions rarely come with 100% certainty. The goal is enough clarity to act, not total predictability.\""),
                    Pair("PERMISSION", "Permission — \"Sometimes we know what we want, but hesitate waiting for validation. Does choosing this feel right to you?\""),
                    Pair("MORE_INFO", "More information — \"What specific question would change your choice? If no realistic answer would change it, you already know enough.\""),
                    Pair("CONFIDENCE", "Confidence — \"Confidence usually comes after making the decision and acting, not before.\""),
                    Pair("DONT_KNOW", "I genuinely don't know — \"That's completely okay. Take a short pause, or test one small part without committing.\"")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    waitingOptions.forEach { (wKey, text) ->
                        val isWaitSelected = waitingReason == wKey
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isWaitSelected) CreamWhite else CreamWhite.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isWaitSelected) SoftSage else CharcoalBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    waitingReason = wKey
                                    onSetWaitingForReason(wKey)
                                }
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCharcoal,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // The Regret Check (PRD Section 13)
                Text(
                    text = "The Regret Check",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftSageDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Imagine you are looking back one year from now.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "If you chose $optionA:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rOptAGain,
                    onValueChange = {
                        rOptAGain = it
                        onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                    },
                    placeholder = { Text("What would you gain?", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rOptAMiss,
                    onValueChange = {
                        rOptAMiss = it
                        onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                    },
                    placeholder = { Text("What might you miss?", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "If you chose $optionB:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, color = DeepCharcoal)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rOptBGain,
                    onValueChange = {
                        rOptBGain = it
                        onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                    },
                    placeholder = { Text("What would you gain?", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = rOptBMiss,
                    onValueChange = {
                        rOptBMiss = it
                        onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                    },
                    placeholder = { Text("What might you miss?", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Which missed possibility would be harder to accept?",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            harderChoice = optionA
                            onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (harderChoice == optionA) SoftSageLight else CreamWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (harderChoice == optionA) SoftSage else CharcoalBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Missing $optionA",
                            fontSize = 12.sp,
                            color = DeepCharcoal,
                            fontWeight = if (harderChoice == optionA) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            harderChoice = optionB
                            onUpdateRegretCheck(rOptAGain, rOptAMiss, rOptBGain, rOptBMiss, harderChoice)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (harderChoice == optionB) SoftSageLight else CreamWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (harderChoice == optionB) SoftSage else CharcoalBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Missing $optionB",
                            fontSize = 12.sp,
                            color = DeepCharcoal,
                            fontWeight = if (harderChoice == optionB) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    when (selectedStatus) {
        "NEED_ANSWER" -> {
            Button(
                onClick = onNeedOneAnswer,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Check missing information & questions →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
        "WANT_TO_TEST" -> {
            Button(
                onClick = onWantToTest,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("View low-risk test experiment →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
        else -> {
            Button(
                onClick = onReadyToDecide,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("proceed_from_clarity_check")
            ) {
                Text("Review decision summary →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}

// ----------------------------------------------------
// Step 11: Final Decision (PRD Section 15)
// ----------------------------------------------------
@Composable
fun Step9FinalDecision(
    options: List<String>,
    analysis: com.example.ai.GeneratedAnalysis?,
    finalChoice: String,
    onSelectChoice: (String) -> Unit,
    onStillUnsure: () -> Unit,
    onPauseDecision: () -> Unit
) {
    Text(
        text = "The choice is yours.",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSageLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = analysis?.leaningSummary ?: "Based on what you've shared, your responses suggest one option aligns more closely with what matters to you.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = DeepCharcoal,
                    lineHeight = 26.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Remember: The app helps you understand your decision. You make the decision.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = SoftSageDark,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Choose your path:",
        style = MaterialTheme.typography.labelMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(10.dp))

    options.forEach { opt ->
        Button(
            onClick = { onSelectChoice(opt) },
            colors = ButtonDefaults.buttonColors(
                containerColor = CreamWhite,
                contentColor = DeepCharcoal
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSage),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .height(54.dp)
                .testTag("choose_option_${opt.take(10)}")
        ) {
            Text(
                text = "I choose: $opt →",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // "I'm still unsure" (PRD Section 15)
    OutlinedButton(
        onClick = onStillUnsure,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("still_unsure_button")
    ) {
        Text("I'm still unsure — explore one more question", color = DeepCharcoal)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // "You don't have to decide now" (PRD Priority 3)
    OutlinedButton(
        onClick = onPauseDecision,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("pause_decision_button")
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = CharcoalMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("You don't have to decide now — Pause & revisit", color = DeepCharcoal)
    }
}

// ----------------------------------------------------
// Step 12: "What If?" Feature (PRD Section 19: Scenario Exploration)
// ----------------------------------------------------
@Composable
fun Step10WhatIf(
    choice: String,
    optionA: String,
    optionB: String,
    scenarios: List<com.example.ai.WhatIfScenario> = emptyList(),
    futureChoice: String,
    futureOther: String,
    onUpdateWhatIf: (String, String) -> Unit,
    onContinue: () -> Unit
) {
    var choiceFutureText by remember { mutableStateOf(futureChoice) }
    var otherFutureText by remember { mutableStateOf(futureOther) }

    Text(
        text = "What If? Mode",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Explore both futures without committing. Notice what feels manageable and what feels heavy.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, lineHeight = 22.sp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Notice: Scenarios, not predictions
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SoftSageLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "These are calm scenario forecasts, not predictions. You remain in control.",
                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Scenario forecasts from AI or structured fallback
    val displayScenarios = if (scenarios.isNotEmpty()) {
        scenarios
    } else {
        listOf(
            com.example.ai.WhatIfScenario(
                optionTitle = optionA.ifBlank { "Option A" },
                likelyExperience = "One possible outcome: You lean into fresh learning and initial friction, creating new momentum.",
                mainUpside = "Progress toward growth and self-discovery.",
                mainDownside = "Temporary discomfort while establishing new habits.",
                whatYouGiveUp = "The immediate predictability of $optionB.",
                whatYouCanControl = "Your preparation, effort, boundaries, and how quickly you seek clarity.",
                whatRemainsUncertain = "Long-term external dynamics."
            ),
            com.example.ai.WhatIfScenario(
                optionTitle = optionB.ifBlank { "Option B" },
                likelyExperience = "One possible outcome: You maintain stability and conserve cognitive energy for other life priorities.",
                mainUpside = "Familiarity, proven routines, and lower immediate stress.",
                mainDownside = "Wondering whether a change would have brought more momentum.",
                whatYouGiveUp = "The unique upside and stretch opportunities tied to $optionA.",
                whatYouCanControl = "How you use your saved energy to pursue goals outside this choice.",
                whatRemainsUncertain = "Whether staying in place will eventually trigger the same decision later."
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        displayScenarios.forEachIndexed { index, sc ->
            val isOptA = index == 0
            val accentColor = if (isOptA) SoftSageDark else MutedBlue

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (isOptA) SoftSage else CharcoalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Scenario: ${sc.optionTitle}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = sc.likelyExperience,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DeepCharcoal,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    WhatIfPoint(label = "Main upside", text = sc.mainUpside, color = RecoverableGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                    WhatIfPoint(label = "Main downside", text = sc.mainDownside, color = SoftClayDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    WhatIfPoint(label = "What you give up", text = sc.whatYouGiveUp, color = CharcoalMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    WhatIfPoint(label = "What you can control", text = sc.whatYouCanControl, color = SoftSageDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    WhatIfPoint(label = "What remains uncertain", text = sc.whatRemainsUncertain, color = CharcoalMuted)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your reflection on this path:",
                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted, fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = if (isOptA) choiceFutureText else otherFutureText,
                        onValueChange = {
                            if (isOptA) {
                                choiceFutureText = it
                            } else {
                                otherFutureText = it
                            }
                            onUpdateWhatIf(choiceFutureText, otherFutureText)
                        },
                        placeholder = { Text("How does this scenario realistically feel to you?", fontSize = 12.sp) },
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onContinue,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("continue_from_what_if")
    ) {
        Text("Proceed to choice →", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
    }
}

@Composable
fun WhatIfPoint(label: String, text: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                color = DeepCharcoal,
                lineHeight = 17.sp
            )
        )
    }
}

// ----------------------------------------------------
// Step 11: Decision Journal Save (PRD Section 17)
// ----------------------------------------------------
@Composable
fun Step11DecisionJournalSave(
    confidence: Int,
    notes: String,
    reopenConditions: List<String> = emptyList(),
    onUpdate: (Int, String) -> Unit,
    onUpdateReopenConditions: (List<String>) -> Unit = {},
    onSave: () -> Unit
) {
    var confLevel by remember { mutableIntStateOf(confidence) }
    var notesText by remember { mutableStateOf(notes) }
    var newConditionText by remember { mutableStateOf("") }
    var currentConditions by remember {
        mutableStateOf(
            if (reopenConditions.isNotEmpty()) reopenConditions
            else listOf(
                "New significant, verified facts emerge that contradict core assumptions",
                "Financial cost or time commitment exceeds 20% of estimated budget",
                "A core stakeholder changes terms or withdraws commitments"
            )
        )
    }

    Text(
        text = "Decision journal",
        style = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = DeepCharcoal
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "Recording your confidence, reasons, and stopping conditions now protects you from second-guessing later.",
        style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted)
    )

    Spacer(modifier = Modifier.height(24.dp))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How confident are you right now?",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )
            Spacer(modifier = Modifier.height(14.dp))
            ConfidenceRatingBar(
                confidence = confLevel,
                onConfidenceChanged = {
                    confLevel = it
                    onUpdate(confLevel, notesText)
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (confLevel) {
                    5 -> "Very confident & at peace"
                    4 -> "Confident with manageable uncertainty"
                    3 -> "Balanced perspective"
                    2 -> "Still feeling slight hesitation"
                    else -> "Need more time to sit with it"
                },
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Stopping Rule & Decision Lock (Requirements 9 & 10)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WarmSandLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stopping rule & decision lock",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Under what specific conditions will you reopen this decision? Unless one of these triggers occurs, this decision remains committed.",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            currentConditions.forEachIndexed { idx, cond ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = SoftSageDark)
                        )
                        Text(
                            text = cond,
                            style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val updated = currentConditions.toMutableList().also { it.removeAt(idx) }
                            currentConditions = updated
                            onUpdateReopenConditions(updated)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Remove", tint = CharcoalMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newConditionText,
                    onValueChange = { newConditionText = it },
                    placeholder = { Text("Add custom reopen condition...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamWhite,
                        unfocusedContainerColor = CreamWhite,
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newConditionText.isNotBlank()) {
                            val updated = currentConditions + newConditionText.trim()
                            currentConditions = updated
                            onUpdateReopenConditions(updated)
                            newConditionText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSageDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add", fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = notesText,
        onValueChange = {
            notesText = it
            onUpdate(confLevel, notesText)
        },
        placeholder = { Text("Optional thoughts or notes to remember for your future self...") },
        minLines = 3,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CreamWhite,
            unfocusedContainerColor = CreamWhite,
            focusedBorderColor = SoftSage,
            unfocusedBorderColor = CharcoalBorder
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(28.dp))

    Button(
        onClick = onSave,
        colors = ButtonDefaults.buttonColors(containerColor = SoftSage, contentColor = CreamWhite),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("save_journal_entry")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save & commit decision",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
