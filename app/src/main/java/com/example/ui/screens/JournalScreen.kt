package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import com.example.data.DecisionEntity
import com.example.ui.components.ClearPathHeader
import com.example.ui.components.RecoverabilityBadge
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
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
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.viewmodel.DecisionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit,
    onSelectDecision: (DecisionEntity) -> Unit,
    onStartNewDecision: () -> Unit
) {
    val decisions by viewModel.allDecisions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Decided", "In Progress", "Decide Later"

    val filteredDecisions = decisions.filter { item ->
        val matchesSearch = item.title.contains(searchQuery, ignoreCase = true) ||
                item.finalChoice.contains(searchQuery, ignoreCase = true) ||
                item.mainConcerns.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Decided" -> item.isDecided || item.status == "Decided"
            "In Progress" -> (!item.isDecided && item.status != "Decide Later" && item.speedMode != "LATER") || item.status == "In Progress"
            "Decide Later" -> item.status == "Decide Later" || item.speedMode == "LATER"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        containerColor = WarmIvory,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onStartNewDecision,
                containerColor = SoftSage,
                contentColor = CreamWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_new_decision")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Decision")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("journal_screen")
        ) {
            ClearPathHeader(
                title = "Decision journal",
                subtitle = "${decisions.size} decisions recorded",
                onBack = onBack
            )

            // Search Bar & Filters
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search decisions...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = CharcoalMuted)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CreamWhite,
                        unfocusedContainerColor = CreamWhite,
                        focusedBorderColor = SoftSage,
                        unfocusedBorderColor = CharcoalBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("journal_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("All", "Decided", "In Progress", "Decide Later").forEach { tab ->
                        val isSelected = selectedFilter == tab
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) SoftSage else WarmSandLight,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedFilter = tab }
                                .testTag("filter_tab_$tab")
                        ) {
                            Text(
                                text = tab,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) CreamWhite else DeepCharcoal,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredDecisions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = null,
                            tint = CharcoalMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No decisions match this filter.",
                            style = MaterialTheme.typography.bodyLarge.copy(color = CharcoalMuted)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredDecisions, key = { it.id }) { item ->
                        DecisionJournalCard(
                            decision = item,
                            onClick = { onSelectDecision(item) }
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
fun DecisionJournalCard(
    decision: DecisionEntity,
    onClick: () -> Unit
) {
    val dateStr = remember(decision.createdAt) {
        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        sdf.format(Date(decision.createdAt))
    }

    val (modeColor, modeBg) = when (decision.speedMode) {
        "QUICK" -> MaybeOchre to MaybeOchreBg
        "RAPID" -> SoftSageDark to SoftSageLight
        "DEEP" -> MutedBlueDark to MutedBlueLight
        else -> SoftClayDark to SoftClayLight
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("journal_card_${decision.id}")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = modeBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = decision.speedMode,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = modeColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Status Badge
                    val statusColor = when (decision.status) {
                        "Decided" -> RecoverableGreen to RecoverableGreenBg
                        "Reviewed" -> SoftSage to SoftSageLight
                        "Decide Later" -> MaybeOchre to MaybeOchreBg
                        else -> CharcoalMuted to WarmSandLight
                    }
                    Surface(
                        color = statusColor.second,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = decision.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor.first,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (decision.isHighStakes) {
                        Surface(
                            color = SoftClayLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "High Stakes",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SoftClayDark,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (decision.reversibility.isNotBlank()) {
                        Surface(
                            color = WarmSandLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = decision.reversibility,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = DeepCharcoal,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = decision.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    fontSize = 17.sp
                )
            )

            if (decision.finalChoice.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = RecoverableGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Chosen: ${decision.finalChoice}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DeepCharcoal,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // ClearPath Reflection Preview (Requirement 6)
            if (decision.clearPathReflection.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = WarmSandLight.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val snippet = remember(decision.clearPathReflection) {
                        val lines = decision.clearPathReflection.lines()
                            .filter { it.isNotBlank() && !it.startsWith("THE ") && !it.startsWith("WHAT ") && !it.startsWith("YOUR ") && !it.startsWith("REOPEN") }
                        lines.take(2).joinToString(" ").take(140)
                    }
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Reflection: ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftSageDark
                            )
                        )
                        Text(
                            text = snippet.ifBlank { "Recorded why you made this choice." },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp
                            ),
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Confidence Stars
                Row(verticalAlignment = Alignment.CenterVertically) {
                    (1..decision.confidenceLevel).forEach { _ ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaybeOchre,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Review Status indicator
                if (decision.reviewOutcome != null) {
                    Surface(
                        color = RecoverableGreenBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Reviewed: ${decision.reviewOutcome}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RecoverableGreen,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (decision.isDecided) {
                    Text(
                        text = "Review outcome →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SoftSageDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}
