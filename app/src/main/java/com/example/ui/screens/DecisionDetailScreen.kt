package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecisionEntity
import com.example.ui.components.ClearPathHeader
import com.example.ui.components.RecoverabilityBadge
import com.example.ui.components.SafetyNetCheckboxRow
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueDark
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.RecoverableGreen
import com.example.ui.theme.RecoverableGreenBg
import com.example.ui.theme.SeriousTerracotta
import com.example.ui.theme.SeriousTerracottaBg
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayDark
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.viewmodel.DecisionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DecisionDetailScreen(
    decision: DecisionEntity,
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onReviewDecision: () -> Unit,
    onContinueDecision: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showReopenDialog by remember { mutableStateOf(false) }
    var reopenReason by remember { mutableStateOf("") }
    var reopenValidationMessage by remember { mutableStateOf<String?>(null) }
    var isEditingReflection by remember { mutableStateOf(false) }
    var reflectionEditText by remember(decision.clearPathReflection) {
        mutableStateOf(
            decision.clearPathReflection.ifBlank {
                com.example.ai.DecisionAnalysisEngine.generateDeterministicReflection(
                    decisionTitle = decision.title,
                    stuckReason = decision.mainConcerns.ifBlank { decision.situationContext },
                    priorities = decision.priorities,
                    whatLearned = decision.whatSeemsManageable.ifBlank { decision.holdingBackInsight },
                    finalChoice = decision.finalChoice,
                    remainingUncertainty = decision.whatDeservesChecking,
                    safetyNets = decision.safetyNets.map { it.text },
                    reopenConditions = decision.reopenConditions
                )
            }
        )
    }

    val dateFormatted = remember(decision.createdAt) {
        SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(Date(decision.createdAt))
    }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("decision_detail_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "Decision details",
                onBack = onBack,
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.deleteDecision(decision)
                            onBack()
                        },
                        modifier = Modifier.testTag("delete_decision_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete decision",
                            tint = CharcoalMuted
                        )
                    }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header Card
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = SoftSageLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = decision.speedMode,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftSageDark,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = dateFormatted,
                                style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = decision.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal,
                                fontSize = 22.sp
                            )
                        )

                        if (decision.finalChoice.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = SoftSageLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = RecoverableGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Chosen: ${decision.finalChoice}",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepCharcoal
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Confidence rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Confidence:",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                            )
                            (1..decision.confidenceLevel).forEach { _ ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaybeOchre,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // ClearPath Reflection (WHAT I THOUGHT THEN) (Requirement 6 & 7)
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth().testTag("detail_clearpath_reflection_card")
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "WHAT I THOUGHT THEN",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SoftSageDark,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Your ClearPath Reflection",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepCharcoal,
                                        fontSize = 18.sp
                                    )
                                )
                            }

                            TextButton(
                                onClick = {
                                    if (isEditingReflection) {
                                        viewModel.updateDecisionReflection(decision.id, reflectionEditText)
                                        isEditingReflection = false
                                    } else {
                                        isEditingReflection = true
                                    }
                                },
                                modifier = Modifier.testTag("detail_edit_reflection_button")
                            ) {
                                Text(
                                    text = if (isEditingReflection) "Save" else "Edit reflection",
                                    color = SoftSageDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditingReflection) {
                            OutlinedTextField(
                                value = reflectionEditText,
                                onValueChange = { reflectionEditText = it },
                                modifier = Modifier.fillMaxWidth().testTag("detail_reflection_input"),
                                minLines = 10,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = WarmIvory,
                                    unfocusedContainerColor = WarmIvory,
                                    focusedBorderColor = SoftSage,
                                    unfocusedBorderColor = CharcoalBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.updateDecisionReflection(decision.id, reflectionEditText)
                                    isEditingReflection = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkForestSage),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save edited reflection", color = CreamWhite)
                            }
                        } else {
                            ReflectionFormattedSections(rawText = reflectionEditText)
                        }
                    }
                }

                // WHAT HAPPENED LATER (Requirement 7)
                if (decision.reviewOutcome != null) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = RecoverableGreenBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RecoverableGreen),
                        modifier = Modifier.fillMaxWidth().testTag("detail_review_card")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "WHAT HAPPENED LATER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = RecoverableGreen,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = null,
                                    tint = RecoverableGreen
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Outcome: ${decision.reviewOutcome}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = RecoverableGreen
                                    )
                                )
                            }
                            if (!decision.reviewReflection.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = decision.reviewReflection,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal)
                                )
                            }
                            if (decision.learningNotes.isNotBlank() && decision.learningNotes != decision.reviewReflection) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = decision.learningNotes,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                                )
                            }
                        }
                    }
                } else if (decision.isDecided) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onReviewDecision,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CreamWhite,
                                contentColor = DeepCharcoal
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSage),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("launch_review_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.RateReview, contentDescription = null, tint = SoftSageDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "What happened? Record outcome review →",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                reopenReason = ""
                                reopenValidationMessage = null
                                showReopenDialog = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reopen_decision_button")
                        ) {
                            Icon(Icons.Default.LockOpen, contentDescription = null, tint = SoftClay, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Revisit or Reopen Decision",
                                color = DeepCharcoal,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.resumeDecision(decision) {
                                onContinueDecision()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftSage,
                            contentColor = CreamWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("detail_continue_decision_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Continue decision process",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CreamWhite
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = CreamWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Stuck Type & Roadblock
                if (decision.stuckReason.isNotBlank()) {
                    DetailSectionCard(title = "Why You Were Stuck") {
                        Text(
                            text = decision.stuckReason,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCharcoal,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )
                        )
                        if (decision.waitingForReason.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "What you were waiting for: ${decision.waitingForReason}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                            )
                        }
                    }
                }

                // Must-Haves vs Nice-to-Haves
                if (decision.mustHaves.isNotEmpty() || decision.niceToHaves.isNotEmpty()) {
                    DetailSectionCard(title = "Criteria Filter") {
                        if (decision.mustHaves.isNotEmpty()) {
                            Text(
                                text = "Must-Haves (Essential):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = SoftSageDark)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                decision.mustHaves.forEach { m ->
                                    Surface(color = SoftSageLight, shape = RoundedCornerShape(8.dp)) {
                                        Text(text = m, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = SoftSageDark, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (decision.niceToHaves.isNotEmpty()) {
                            Text(
                                text = "Nice-to-Haves:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MutedBlueDark)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                decision.niceToHaves.forEach { n ->
                                    Surface(color = MutedBlueLight, shape = RoundedCornerShape(8.dp)) {
                                        Text(text = n, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, color = MutedBlueDark, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Options Evaluated
                if (decision.options.isNotEmpty()) {
                    DetailSectionCard(title = "Options Evaluated") {
                        decision.options.forEachIndexed { i, opt ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = SoftSageLight,
                                    shape = CircleShape,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = ('A' + i).toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = SoftSageDark
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal)
                                )
                            }
                        }
                    }
                }

                // Identified Downsides & Recoverability
                if (decision.risks.isNotEmpty()) {
                    DetailSectionCard(title = "Downsides & Recoverability") {
                        decision.risks.forEach { r ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${r.optionTitle}: ${r.description}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = DeepCharcoal
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RecoverabilityBadge(recoverability = r.recoverability)
                                    Text(
                                        text = "Likelihood: ${r.likelihood}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                                    )
                                }
                            }
                        }
                    }
                }

                // Top Priorities
                if (decision.priorities.isNotEmpty()) {
                    DetailSectionCard(title = "What Mattered Most") {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            decision.priorities.forEach { p ->
                                Surface(
                                    color = SoftSageLight,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = p,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = SoftSageDark,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Safety Net Checklist
                if (decision.safetyNets.isNotEmpty()) {
                    DetailSectionCard(title = "Your Safety Net Actions") {
                        decision.safetyNets.forEachIndexed { idx, net ->
                            SafetyNetCheckboxRow(
                                text = net.text,
                                isChecked = net.isChecked,
                                onToggle = {
                                    val updatedNets = decision.safetyNets.toMutableList()
                                    updatedNets[idx] = net.copy(isChecked = !net.isChecked)
                                    viewModel.selectDecision(decision.copy(safetyNets = updatedNets))
                                },
                                modifier = Modifier.padding(vertical = 3.dp)
                            )
                        }
                    }
                }

                // Missing Information Status
                if (decision.missingInfoItems.isNotEmpty()) {
                    DetailSectionCard(title = "Missing Information Status") {
                        decision.missingInfoItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.text,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = when (item.status) {
                                        "I know this", "KNOW_THIS" -> RecoverableGreenBg
                                        "Need to check", "NEED_TO_CHECK" -> SoftClayLight
                                        else -> WarmSandLight
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = item.status,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when (item.status) {
                                                "I know this", "KNOW_THIS" -> RecoverableGreen
                                                "Need to check", "NEED_TO_CHECK" -> SoftClay
                                                else -> CharcoalMuted
                                            },
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Small Experiment
                if (decision.smallExperiment.isNotBlank()) {
                    DetailSectionCard(title = "Small Experiment") {
                        Text(
                            text = decision.smallExperiment,
                            style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal, lineHeight = 20.sp)
                        )
                    }
                }

                // Analysis & Synthesis
                if (decision.aiAnalysis.isNotBlank()) {
                    DetailSectionCard(title = "Analysis & Insights") {
                        Text(
                            text = decision.aiAnalysis,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCharcoal,
                                lineHeight = 22.sp
                            )
                        )
                        if (decision.whatSeemsManageable.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "What seems manageable: ${decision.whatSeemsManageable}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                            )
                        }
                    }
                }

                // What If Comparison
                if (decision.whatIfChoiceFuture.isNotBlank() || decision.whatIfOtherFuture.isNotBlank()) {
                    DetailSectionCard(title = "Six Months From Now") {
                        if (decision.whatIfChoiceFuture.isNotBlank()) {
                            Text(
                                text = "Chosen Path:",
                                style = MaterialTheme.typography.labelSmall.copy(color = SoftSageDark, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = decision.whatIfChoiceFuture,
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        if (decision.whatIfOtherFuture.isNotBlank()) {
                            Text(
                                text = "Alternative Path:",
                                style = MaterialTheme.typography.labelSmall.copy(color = MutedBlueDark, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = decision.whatIfOtherFuture,
                                style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal)
                            )
                        }
                    }
                }

                // User Notes
                if (decision.userNotes.isNotBlank()) {
                    DetailSectionCard(title = "Personal Notes") {
                        Text(
                            text = decision.userNotes,
                            style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal)
                        )
                    }
                }

                // Stopping Rule & Reopening Conditions
                if (decision.reopenConditions.isNotEmpty()) {
                    DetailSectionCard(title = "Stopping Rule (When to Reopen)") {
                        Text(
                            text = "This decision was locked with defined conditions under which it can be reopened. Reopening without changed conditions is discouraged to avoid second-guessing.",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        decision.reopenConditions.forEach { cond ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = cond, style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal))
                            }
                        }
                    }
                }

                // Premortem Analysis
                if (decision.premortemFailureStory.isNotBlank()) {
                    DetailSectionCard(title = "Premortem Analysis (Failure Prevention)") {
                        Text(
                            text = "Imagined worst-case scenario: ${decision.premortemFailureStory}",
                            style = MaterialTheme.typography.bodySmall.copy(color = SoftClayDark, lineHeight = 18.sp)
                        )
                        if (decision.premortemFailureCauses.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Realistic vulnerabilities identified:",
                                style = MaterialTheme.typography.labelSmall.copy(color = DeepCharcoal, fontWeight = FontWeight.SemiBold)
                            )
                            decision.premortemFailureCauses.forEach { cause ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 6.dp)
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(SoftClay)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = cause, style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal))
                                }
                            }
                        }
                    }
                }

                // Good-Enough Criteria
                if (decision.goodEnoughCriteria.isNotEmpty()) {
                    DetailSectionCard(title = "Good-Enough (Satisficing) Threshold") {
                        Text(
                            text = "Criteria that satisfy this decision without needing perfection:",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        decision.goodEnoughCriteria.forEach { crit ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SoftSageDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = crit, style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal))
                            }
                        }
                    }
                }

                // 10-10-10 Perspective
                if (decision.reflection10Minutes.isNotBlank() || decision.reflection10Months.isNotBlank() || decision.reflection10Years.isNotBlank()) {
                    DetailSectionCard(title = "10-10-10 Time Perspective") {
                        if (decision.reflection10Minutes.isNotBlank()) {
                            Text(text = "10 Minutes: ${decision.reflection10Minutes}", style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal))
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (decision.reflection10Months.isNotBlank()) {
                            Text(text = "10 Months: ${decision.reflection10Months}", style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal))
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        if (decision.reflection10Years.isNotBlank()) {
                            Text(text = "10 Years: ${decision.reflection10Years}", style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal))
                        }
                    }
                }

                // Thinking Pattern
                if (decision.detectedThinkingPattern.isNotBlank()) {
                    DetailSectionCard(title = "Thinking Pattern Reflection") {
                        Text(
                            text = "Observed pattern: ${decision.detectedThinkingPattern}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = DeepCharcoal, fontWeight = FontWeight.Medium)
                        )
                        if (decision.thinkingPatternQuestion.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Reflective prompt: ${decision.thinkingPatternQuestion}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
                            )
                        }
                    }
                }

                // Reopen History
                if (decision.reopenHistory.isNotBlank()) {
                    DetailSectionCard(title = "Reopen Log") {
                        Text(
                            text = decision.reopenHistory,
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 18.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showReopenDialog) {
        val isValidReason = viewModel.canReopenDecision(reopenReason)
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = SoftClay)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "What has changed?",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "ClearPath helps protect your peace of mind by locking decided choices. Second-guessing is completely natural, but revisiting without changed facts often increases anxiety rather than clarity.",
                        style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp)
                    )

                    OutlinedTextField(
                        value = reopenReason,
                        onValueChange = {
                            reopenReason = it
                            val check = viewModel.canReopenDecision(it)
                            reopenValidationMessage = if (it.isNotBlank() && !check.first) {
                                check.second
                            } else {
                                null
                            }
                        },
                        label = { Text("What new information or changed condition occurred?") },
                        placeholder = { Text("e.g., The cost increased beyond budget, new offer arrived...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    reopenValidationMessage?.let { msg ->
                        Surface(
                            color = SoftClayLight,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = SoftClay, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = msg,
                                    style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, fontSize = 12.sp, lineHeight = 16.sp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.reopenDecision(decision, reopenReason.ifBlank { "User requested revisit with updated perspective" }) {
                            showReopenDialog = false
                            onContinueDecision()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSageDark)
                ) {
                    Text("Reopen with changed facts")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showReopenDialog = false }) {
                    Text("Keep Decision Locked")
                }
            }
        )
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    fontSize = 16.sp
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}
