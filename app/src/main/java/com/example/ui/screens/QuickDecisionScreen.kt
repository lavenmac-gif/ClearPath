package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.collectAsState
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
import com.example.ui.components.ClearPathHeader
import com.example.ui.components.RecoverabilitySelector
import com.example.ui.components.VoiceMicButton
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.RecoverableGreen
import com.example.ui.theme.RecoverableGreenBg
import com.example.ui.theme.SeriousTerracotta
import com.example.ui.theme.SeriousTerracottaBg
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.companion.ClintPetView
import com.example.ui.companion.ClintSanctuarySheet
import com.example.ui.companion.CompanionEmotion
import com.example.ui.companion.FeelingState
import com.example.ui.companion.OfflineCompanionRepository
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.WarmIvory
import com.example.ui.viewmodel.DecisionViewModel

@Composable
fun QuickDecisionScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    val quickState by viewModel.quickState.collectAsState()
    val scrollState = rememberScrollState()

    var step by remember { mutableStateOf(1) } // 1: Q1 & Q2, 2: Q3, 3: Result
    var attemptedNext by remember { mutableStateOf(false) }

    val q1Text = quickState.question1Decision
    val q2Text = quickState.question2WorstDownside

    val q1Error = attemptedNext && q1Text.isBlank()
    val q2Error = attemptedNext && q2Text.isBlank()
    val canProceed = q1Text.isNotBlank() && q2Text.isNotBlank()

    BackHandler {
        if (step > 1) {
            step -= 1
        } else {
            onBack()
        }
    }

    var isClintSheetOpen by remember { mutableStateOf(false) }

    if (isClintSheetOpen) {
        ClintSanctuarySheet(
            initialFeeling = FeelingState.AFRAID_WRONG_CHOICE,
            onDismiss = { isClintSheetOpen = false }
        )
    }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("quick_decision_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "10-second check",
                subtitle = "Three fast questions to clear hesitation",
                onBack = {
                    if (step > 1) step -= 1 else onBack()
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SoftSage.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { isClintSheetOpen = true }
                            .testTag("quick_clint_button")
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
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                if (step == 1) {
                    // Question 1
                    Text(
                        text = "Question 1",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SoftSage,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What are you deciding?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = q1Text,
                            onValueChange = { viewModel.updateQuickQuestion1(it) },
                            placeholder = {
                                Text(
                                    "e.g. Should I send this message?",
                                    color = CharcoalMuted
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = DeepCharcoal,
                                fontSize = 16.sp
                            ),
                            singleLine = false,
                            maxLines = 4,
                            isError = q1Error,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_decision_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepCharcoal,
                                unfocusedTextColor = DeepCharcoal,
                                disabledTextColor = CharcoalMuted,
                                focusedContainerColor = CreamWhite,
                                unfocusedContainerColor = CreamWhite,
                                cursorColor = SoftSageDark,
                                focusedBorderColor = if (q1Error) SeriousTerracotta else SoftSage,
                                unfocusedBorderColor = if (q1Error) SeriousTerracotta else CharcoalBorder,
                                focusedPlaceholderColor = CharcoalMuted,
                                unfocusedPlaceholderColor = CharcoalMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        VoiceMicButton(
                            currentText = q1Text,
                            onSpokenText = { viewModel.updateQuickQuestion1(it) },
                            sizeDp = 48,
                            promptMessage = "What are you deciding?",
                            testTag = "voice_mic_button_q1"
                        )
                    }

                    if (q1Error) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please enter what you are deciding before continuing.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SeriousTerracotta,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.testTag("quick_q1_error")
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Question 2
                    Text(
                        text = "Question 2",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SoftSage,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "What's the biggest thing that could realistically go wrong?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = q2Text,
                            onValueChange = { viewModel.updateQuickQuestion2(it) },
                            placeholder = {
                                Text(
                                    "e.g. They might misunderstand my tone",
                                    color = CharcoalMuted
                                )
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = DeepCharcoal,
                                fontSize = 16.sp
                            ),
                            singleLine = false,
                            maxLines = 4,
                            isError = q2Error,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quick_downside_input"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepCharcoal,
                                unfocusedTextColor = DeepCharcoal,
                                disabledTextColor = CharcoalMuted,
                                focusedContainerColor = CreamWhite,
                                unfocusedContainerColor = CreamWhite,
                                cursorColor = SoftSageDark,
                                focusedBorderColor = if (q2Error) SeriousTerracotta else SoftSage,
                                unfocusedBorderColor = if (q2Error) SeriousTerracotta else CharcoalBorder,
                                focusedPlaceholderColor = CharcoalMuted,
                                unfocusedPlaceholderColor = CharcoalMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        VoiceMicButton(
                            currentText = q2Text,
                            onSpokenText = { viewModel.updateQuickQuestion2(it) },
                            sizeDp = 48,
                            promptMessage = "What's the biggest thing that could realistically go wrong?",
                            testTag = "voice_mic_button_q2"
                        )
                    }

                    if (q2Error) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please enter a potential downside before continuing.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = SeriousTerracotta,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.testTag("quick_q2_error")
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            attemptedNext = true
                            if (canProceed) {
                                step = 2
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
                            .testTag("quick_continue_to_q3")
                    ) {
                        Text(
                            text = "Next: Could you handle it? →",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                } else if (step == 2) {
                    // Question 3
                    Text(
                        text = "Question 3",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = SoftSage,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Could you handle it if it happened?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The downside you named: \"${quickState.question2WorstDownside}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CharcoalMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RecoverabilitySelector(
                        selected = quickState.question3CanHandle,
                        onSelect = { choice ->
                            viewModel.evaluateQuickQuestion3(choice)
                            step = 3
                        }
                    )
                } else if (step == 3) {
                    // Result / Perspective
                    val (verdictColor, verdictBg) = when (quickState.verdict) {
                        "Proceed" -> RecoverableGreen to RecoverableGreenBg
                        "Pause" -> SeriousTerracotta to SeriousTerracottaBg
                        else -> MaybeOchre to MaybeOchreBg
                    }

                    // Clint encouraging companion avatar
                    ClintPetView(
                        size = 64.dp,
                        emotion = CompanionEmotion.HAPPY,
                        backgroundColor = CreamWhite,
                        faceColor = DarkForestSage,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamWhite),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, verdictColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = verdictBg,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = quickState.verdict,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = verdictColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = quickState.verdictExplanation,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = DeepCharcoal,
                                    lineHeight = 26.sp
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = SoftSageLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Remember: This is not an instruction. It is an observation of your own capacity to handle the downside.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DeepCharcoal,
                                        lineHeight = 18.sp
                                    ),
                                    modifier = Modifier.padding(14.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            viewModel.saveQuickDecision {
                                onComplete()
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
                            .testTag("save_quick_decision")
                    ) {
                        Text(
                            text = "Save to decision journal →",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CreamWhite,
                            contentColor = DeepCharcoal
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}
