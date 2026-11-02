package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.RecoverableGreen
import com.example.ui.theme.RecoverableGreenBg
import com.example.ui.theme.SeriousTerracotta
import com.example.ui.theme.SeriousTerracottaBg
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSandLight
import com.example.ui.viewmodel.DecisionViewModel

@Composable
fun FutureReviewScreen(
    decision: DecisionEntity,
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onReviewCompleted: () -> Unit
) {
    val scrollState = rememberScrollState()

    // 8. Confidence Calibration & Outcome Options
    val outcomeOptions = listOf(
        "Better than expected",
        "About as expected",
        "Worse than expected",
        "Mixed",
        "Not sure yet"
    )

    // 9. "I'm glad I decided" vs "The outcome was good"
    val processOptions = listOf(
        "Yes, I stand by the decision process",
        "Mixed / I would gather different info next time",
        "I rushed or doubted too much"
    )

    var selectedOutcome by remember { mutableStateOf(decision.reviewOutcome ?: "About as expected") }
    var selectedProcessSatisfaction by remember {
        mutableStateOf(
            if (decision.learningNotes.contains("Process:")) {
                decision.learningNotes.substringAfter("Process:").substringBefore("\n").trim()
            } else {
                "Yes, I stand by the decision process"
            }
        )
    }
    var learningNotesText by remember {
        mutableStateOf(
            if (decision.learningNotes.contains("Notes:")) {
                decision.learningNotes.substringAfter("Notes:").trim()
            } else {
                decision.learningNotes.ifBlank { decision.reviewReflection ?: "" }
            }
        )
    }
    var isOriginalReflectionExpanded by remember { mutableStateOf(true) }

    val confidencePercent = if (decision.confidencePercent > 0) decision.confidencePercent else (decision.confidenceLevel * 20)

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("future_review_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "Decision review",
                subtitle = decision.title,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Section 7: WHAT I THOUGHT THEN (Preserved Reflection)
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("what_i_thought_then_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isOriginalReflectionExpanded = !isOriginalReflectionExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HistoryEdu,
                                    contentDescription = null,
                                    tint = SoftSageDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "WHAT I THOUGHT THEN",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = SoftSageDark,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Your original reflection",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = DeepCharcoal
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isOriginalReflectionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isOriginalReflectionExpanded) "Collapse" else "Expand",
                                tint = CharcoalMuted
                            )
                        }

                        AnimatedVisibility(visible = isOriginalReflectionExpanded) {
                            Column(modifier = Modifier.padding(top = 14.dp)) {
                                if (decision.finalChoice.isNotBlank()) {
                                    Surface(
                                        color = SoftSageLight,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Chosen: ${decision.finalChoice}",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = DarkForestSage
                                            ),
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                val reflectionToDisplay = decision.clearPathReflection.ifBlank {
                                    decision.aiAnalysis.ifBlank { "You evaluated the options and committed to your path." }
                                }
                                ReflectionFormattedSections(rawText = reflectionToDisplay)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 8: CONFIDENCE CALIBRATION
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSandLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Confidence at the time: $confidencePercent%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "A calibrated record of your certainty when deciding — not a test of correctness.",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // WHAT HAPPENED LATER: Outcome selection
                Text(
                    text = "WHAT HAPPENED LATER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CharcoalMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "How did the outcome compare to what you anticipated?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Outcome options
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    outcomeOptions.forEach { option ->
                        val isSelected = selectedOutcome == option
                        val (chipColor, chipBg) = when (option) {
                            "Better than expected" -> RecoverableGreen to RecoverableGreenBg
                            "About as expected" -> DeepCharcoal to WarmSandLight
                            "Worse than expected" -> SeriousTerracotta to SeriousTerracottaBg
                            "Mixed" -> MaybeOchre to MaybeOchreBg
                            else -> CharcoalMuted to CreamWhite
                        }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) chipBg else CreamWhite
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) chipColor else CharcoalBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedOutcome = option }
                                .testTag("review_outcome_${option.take(6)}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) chipColor else DeepCharcoal
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = chipColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Section 9: "I'm glad I decided" vs "The outcome was good"
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = SoftSageDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Process vs. Outcome",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "A decision can be thoughtful and sound even if the outcome brought surprises. Did you make a thoughtful choice with the information you had then?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalMuted,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            processOptions.forEach { proc ->
                                val isSelected = selectedProcessSatisfaction == proc
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) SoftSageLight else WarmIvory,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) SoftSage else CharcoalBorder
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedProcessSatisfaction = proc }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Check else Icons.Default.HistoryEdu,
                                            contentDescription = null,
                                            tint = if (isSelected) DarkForestSage else CharcoalMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = proc,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (isSelected) DarkForestSage else DeepCharcoal,
                                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                                fontSize = 13.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Optional Learning Record
                Text(
                    text = "What did you learn? (Optional learning record)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Not a score. Notes for your future self on how you reason through uncertainty.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = learningNotesText,
                    onValueChange = { learningNotesText = it },
                    placeholder = {
                        Text("e.g. Did the worst downside happen? Was the safety net helpful?")
                    },
                    minLines = 4,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamWhite,
                        unfocusedContainerColor = CreamWhite,
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("review_reflection_input")
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        val combinedNotes = "Process: $selectedProcessSatisfaction\nNotes: $learningNotesText"
                        viewModel.submitDecisionReview(
                            decisionId = decision.id,
                            outcome = selectedOutcome,
                            reflection = learningNotesText,
                            learningNotes = combinedNotes,
                            onDone = onReviewCompleted
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkForestSage,
                        contentColor = CreamWhite
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_review_button")
                ) {
                    Text(
                        text = "Save review to learning record",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CreamWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
