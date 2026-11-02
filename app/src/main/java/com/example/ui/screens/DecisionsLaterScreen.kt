package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueDark
import com.example.ui.theme.MutedBlueLight
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

@Composable
fun DecisionsLaterScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onContinueDecision: (DecisionEntity) -> Unit,
    onAddNewDecideLater: () -> Unit
) {
    val decisionsLater by viewModel.decideLaterDecisions.collectAsState()

    Scaffold(
        containerColor = WarmIvory,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewDecideLater,
                containerColor = SoftSage,
                contentColor = CreamWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_decide_later")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add decision to revisit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("decisions_later_screen")
        ) {
            ClearPathHeader(
                title = "Decisions later",
                subtitle = "${decisionsLater.size} decisions waiting for you",
                onBack = onBack
            )

            // Reassurance Card
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = WarmSandLight,
                    border = BorderStroke(1.dp, WarmSand),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = SoftSageDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "You don't have to decide right now. Continue any decision when you have clarity.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (decisionsLater.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(WarmSandLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = CharcoalMuted,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No decisions waiting",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "When a choice feels overwhelming, saving it for later gives your mind room to settle.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = CharcoalMuted,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = onAddNewDecideLater,
                            colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("empty_add_decision_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = CreamWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add a decision to revisit", color = CreamWhite)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(decisionsLater, key = { it.id }) { decision ->
                        DecideLaterCard(
                            decision = decision,
                            onContinue = { onContinueDecision(decision) },
                            onDelete = { viewModel.deleteDecision(decision) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DecideLaterCard(
    decision: DecisionEntity,
    onContinue: () -> Unit,
    onDelete: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }
    val isDue = decision.reminderDateMillis != null && decision.reminderDateMillis <= now

    val reminderLabel = remember(decision.reminderDateMillis) {
        val millis = decision.reminderDateMillis
        if (millis == null) {
            "No reminder"
        } else {
            val diffHours = (millis - now) / 3600000L
            when {
                diffHours in 0..5 -> "Revisit later today"
                diffHours in 6..28 -> "Revisit tomorrow"
                diffHours in 29..72 -> "Revisit this week"
                diffHours < 0 -> "Waiting for you"
                else -> {
                    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                    "Revisit on " + sdf.format(Date(millis))
                }
            }
        }
    }

    val (statusLabel, statusColor, statusBg) = when {
        decision.isDecided -> Triple("Decided", SoftSageDark, SoftSageLight)
        decision.status == "Ready to Decide" -> Triple("Ready to decide", SoftSageDark, SoftSageLight)
        decision.savedStep > 1 || decision.options.isNotEmpty() -> Triple("In progress", MutedBlueDark, MutedBlueLight)
        else -> Triple("Decide later", SoftClayDark, SoftClayLight)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = BorderStroke(1.dp, if (isDue) MaybeOchre else CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("decide_later_card_${decision.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header row with Status and Reminder
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Reminder text / icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDue) Icons.Default.NotificationsActive else Icons.Default.Alarm,
                        contentDescription = null,
                        tint = if (isDue) MaybeOchre else CharcoalMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reminderLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isDue) MaybeOchre else CharcoalMuted,
                            fontWeight = if (isDue) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Decision Question / Title
            Text(
                text = decision.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                )
            )

            // Optional context note
            if (decision.situationContext.isNotBlank() && decision.situationContext != decision.title) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = decision.situationContext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        lineHeight = 18.sp
                    ),
                    maxLines = 2
                )
            }

            // Options summary if entered
            if (decision.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    decision.options.take(2).forEach { opt ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = WarmSandLight,
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.labelSmall.copy(color = DeepCharcoal),
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // Progress / Step Info
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (decision.savedStep > 1) "Saved at Step ${decision.savedStep} of 12" else "Ready to start",
                    style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = CharcoalMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: "Continue decision" button
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSage),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("continue_decision_${decision.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Continue decision",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CreamWhite
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CreamWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
