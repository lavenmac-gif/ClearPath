package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ClearPathHeader
import com.example.ui.components.VoiceMicButton
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.viewmodel.DecisionViewModel

enum class ReminderOption(val label: String, val description: String, val icon: ImageVector) {
    LATER_TODAY("Later today", "In about 4 hours", Icons.Default.Schedule),
    TOMORROW("Tomorrow", "In 24 hours", Icons.Default.Today),
    THIS_WEEK("This week", "In 3 days", Icons.Default.DateRange),
    CUSTOM("In 1 week", "7 days from now", Icons.Default.Alarm),
    NO_REMINDER("No reminder", "I will revisit on my own pace", Icons.Default.NotificationsOff)
}

@Composable
fun DecideLaterSetupScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onDecisionSaved: () -> Unit,
    onStartNow: () -> Unit
) {
    var decisionText by remember { mutableStateOf("") }
    var contextNotes by remember { mutableStateOf("") }
    var selectedReminder by remember { mutableStateOf(ReminderOption.TOMORROW) }
    var isSaving by remember { mutableStateOf(false) }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("decide_later_setup_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "Decide later",
                subtitle = "Take your time and revisit with clarity",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Calm reassurance banner
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSandLight),
                    border = BorderStroke(1.dp, WarmSand),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "You don't have to decide right now.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Save this decision and ClearPath will keep your thoughts safe. Come back when you feel rested and ready.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CharcoalMuted,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Decision Input
                Text(
                    text = "What's on your mind?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Type or speak the decision weighing on you.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Voice input card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VoiceMicButton(
                            onSpokenText = { spoken ->
                                decisionText = if (decisionText.isBlank()) spoken else "$decisionText $spoken"
                            },
                            sizeDp = 48,
                            promptMessage = "Speak your decision question..."
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Speak your decision",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal
                                )
                            )
                            Text(
                                text = "e.g., \"Should I accept this internship?\"",
                                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Text Field for Decision
                OutlinedTextField(
                    value = decisionText,
                    onValueChange = { decisionText = it },
                    placeholder = { Text("e.g., Should I accept this internship?") },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamWhite,
                        unfocusedContainerColor = CreamWhite,
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_later_input_text")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Context / Notes
                Text(
                    text = "Quick notes or options (optional)",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = contextNotes,
                    onValueChange = { contextNotes = it },
                    placeholder = { Text("Any deadlines, options, or initial thoughts...") },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamWhite,
                        unfocusedContainerColor = CreamWhite,
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("decide_later_notes_text")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Section 2: Revisit reminder options
                Text(
                    text = "When would you like to revisit it?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "We will keep it ready for you whenever you return.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReminderOption.values().forEach { opt ->
                        val isSelected = selectedReminder == opt
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) SoftSageLight else CreamWhite
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) SoftSage else CharcoalBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedReminder = opt }
                                .testTag("reminder_opt_${opt.name}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SoftSage else WarmSandLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = opt.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) CreamWhite else CharcoalMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = opt.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = DeepCharcoal
                                        )
                                    )
                                    Text(
                                        text = opt.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = SoftSageDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Actions
                Button(
                    onClick = {
                        if (decisionText.isNotBlank() && !isSaving) {
                            isSaving = true
                            val now = System.currentTimeMillis()
                            val reminderMillis = when (selectedReminder) {
                                ReminderOption.LATER_TODAY -> now + (4 * 3600000L)
                                ReminderOption.TOMORROW -> now + (24 * 3600000L)
                                ReminderOption.THIS_WEEK -> now + (3 * 86400000L)
                                ReminderOption.CUSTOM -> now + (7 * 86400000L)
                                ReminderOption.NO_REMINDER -> null
                            }

                            viewModel.saveNewDecideLater(
                                title = decisionText.trim(),
                                notes = contextNotes.trim(),
                                reminderMillis = reminderMillis,
                                speedMode = "DEEP",
                                onSaved = {
                                    onDecisionSaved()
                                }
                            )
                        }
                    },
                    enabled = decisionText.isNotBlank() && !isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_decide_later_button")
                ) {
                    Text(
                        text = "Save and decide later",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CreamWhite
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        if (decisionText.isNotBlank()) {
                            viewModel.startNewDecision("DEEP")
                            viewModel.updateRawInput(decisionText.trim())
                            onStartNow()
                        }
                    },
                    enabled = decisionText.isNotBlank(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepCharcoal),
                    border = BorderStroke(1.dp, CharcoalBorder),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_now_button")
                ) {
                    Text(
                        text = "Start answering questions now",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
