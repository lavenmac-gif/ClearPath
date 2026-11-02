package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.companion.ClintPetView
import com.example.ui.companion.ClintSanctuarySheet
import com.example.ui.companion.CompanionEmotion
import com.example.ui.companion.FeelingState
import com.example.ui.companion.OfflineCompanionRepository
import com.example.ui.components.VoiceMicButton
import com.example.ui.motion.ArchedDeckCarousel
import com.example.ui.motion.ClintCompanionDockCard
import com.example.ui.motion.DeckCardItem
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.MutedPillBg
import com.example.ui.theme.MutedPillBorder
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight

@Composable
fun HomeScreen(
    onStartDecision: () -> Unit,
    onViewJournal: () -> Unit,
    onViewPatterns: () -> Unit,
    onViewAbout: () -> Unit = {},
    onQuickStartWithVoice: (String) -> Unit,
    onQuickDecision: () -> Unit = {},
    decisionsLaterCount: Int = 0,
    onViewDecisionsLater: () -> Unit = {},
    onDecideLaterSetup: () -> Unit = {},
    isOnline: Boolean = true,
    syncStatusText: String = "",
    activeDecision: com.example.data.DecisionEntity? = null,
    onResumeActiveDecision: (com.example.data.DecisionEntity) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var isClintSanctuaryOpen by remember { mutableStateOf(false) }
    var initialSanctuaryFeeling by remember { mutableStateOf(FeelingState.OVERWHELMED) }
    var decisionInputText by remember { mutableStateOf("") }
    val dailyWisdom = remember { OfflineCompanionRepository.getRandomDailyWisdom() }

    // Sanctuary modal when user taps Clint
    if (isClintSanctuaryOpen) {
        ClintSanctuarySheet(
            initialFeeling = initialSanctuaryFeeling,
            onDismiss = { isClintSanctuaryOpen = false }
        )
    }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkForestSage),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_logo),
                            contentDescription = "ClearPath Logo",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ClearPath",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 18.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Clint quick companion button in header
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = SoftSageLight,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                initialSanctuaryFeeling = FeelingState.GENERAL_RESET
                                isClintSanctuaryOpen = true
                            }
                            .testTag("header_clint_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ClintPetView(
                                size = 22.dp,
                                emotion = CompanionEmotion.HAPPY,
                                isInteractive = false,
                                showContainer = false,
                                faceColor = DarkForestSage
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "c:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftSageDark,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onViewAbout,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("about_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About ClearPath",
                            tint = DeepCharcoal
                        )
                    }
                    IconButton(
                        onClick = onViewJournal,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("journal_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = DeepCharcoal
                        )
                    }
                }
            }

            // Offline Status Banner
            if (!isOnline) {
                Surface(
                    color = WarmSandLight,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("offline_status_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(CharcoalMuted)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Offline — all decisions & Clint work fully locally",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = DeepCharcoal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Main Copy
            Text(
                text = "What are you trying to decide?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "You don't need to have all the answers. Just a safe space to think it through.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CharcoalMuted,
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Clint Companion Dock Card (Consolation & Encouragement when low)
            ClintCompanionDockCard(
                wisdom = dailyWisdom,
                onOpenSanctuary = {
                    initialSanctuaryFeeling = FeelingState.OVERWHELMED
                    isClintSanctuaryOpen = true
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Reference Motion Template 1: Arched Deck Carousel
            // Replaces static stacked walls of text with an interactive curved card deck!
            val deckCards = remember(decisionsLaterCount) {
                listOf(
                    DeckCardItem(
                        id = "untangle",
                        title = "Untangle Dilemma",
                        subtitle = "Step-by-step clarity to separate noise from what actually matters.",
                        badge = "Guided Flow",
                        icon = Icons.Default.Explore,
                        accentColor = DarkForestSage,
                        accentBg = SoftSageLight,
                        testTag = "deck_card_untangle",
                        onClick = onStartDecision
                    ),
                    DeckCardItem(
                        id = "quick",
                        title = "Quick Clarity",
                        subtitle = "Downside, recoverability, and safety nets in 10 seconds.",
                        badge = "10s Check",
                        icon = Icons.Default.Bolt,
                        accentColor = MaybeOchre,
                        accentBg = MaybeOchreBg,
                        testTag = "deck_card_quick",
                        onClick = onQuickDecision
                    ),
                    DeckCardItem(
                        id = "companion",
                        title = "Clint's Sanctuary",
                        subtitle = "Take a breath, get soothing perspective, or settle looping doubts.",
                        badge = "Feeling Low?",
                        icon = Icons.Default.Favorite,
                        accentColor = SoftSageDark,
                        accentBg = SoftSageLight,
                        testTag = "deck_card_sanctuary",
                        onClick = {
                            initialSanctuaryFeeling = FeelingState.OVERWHELMED
                            isClintSanctuaryOpen = true
                        }
                    ),
                    DeckCardItem(
                        id = "later",
                        title = if (decisionsLaterCount > 0) "Decide Later ($decisionsLaterCount)" else "Decide Later",
                        subtitle = "Safe holding area with timed gentle reminders for fresh eyes.",
                        badge = "Pause & Park",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = SoftClay,
                        accentBg = SoftClayLight,
                        testTag = "deck_card_later",
                        onClick = onViewDecisionsLater
                    ),
                    DeckCardItem(
                        id = "patterns",
                        title = "Decision Patterns",
                        subtitle = "Review how your past feared outcomes actually turned out.",
                        badge = "Self-Knowledge",
                        icon = Icons.Default.Psychology,
                        accentColor = MutedBlue,
                        accentBg = MutedBlueLight,
                        testTag = "deck_card_patterns",
                        onClick = onViewPatterns
                    )
                )
            }

            ArchedDeckCarousel(
                items = deckCards,
                initialIndex = 0,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Crisp Decision Input Card (Reduced text, clean microcopy)
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_decision_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Tell ClearPath what's on your mind",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = DeepCharcoal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type or speak freely — ClearPath crystallizes it into a clear decision.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 12.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = decisionInputText,
                        onValueChange = { decisionInputText = it },
                        placeholder = {
                            Text(
                                text = "e.g. Should I accept the new offer or stay? / Which project to pick first?",
                                style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted, fontSize = 14.sp)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("home_decision_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = DeepCharcoal,
                            unfocusedTextColor = DeepCharcoal,
                            focusedBorderColor = SoftSageDark,
                            unfocusedBorderColor = CharcoalBorder,
                            focusedContainerColor = WarmIvory,
                            unfocusedContainerColor = WarmIvory,
                            focusedPlaceholderColor = CharcoalMuted,
                            unfocusedPlaceholderColor = CharcoalMuted
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VoiceMicButton(
                                onSpokenText = { spoken ->
                                    if (spoken.isNotBlank()) {
                                        decisionInputText = spoken
                                        onQuickStartWithVoice(spoken)
                                    }
                                },
                                sizeDp = 36,
                                promptMessage = "Speak what's on your mind..."
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Speak instead",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCharcoal,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        if (decisionInputText.isNotBlank()) {
                            TextButton(
                                onClick = { decisionInputText = "" }
                            ) {
                                Text("Clear", color = CharcoalMuted, fontSize = 13.sp)
                            }
                        }
                    }

                    // Live Crystallized Decision Preview Card (Uses Editorial Sliced Motion Template)
                    if (decisionInputText.isNotBlank()) {
                        val crystallized = remember(decisionInputText) {
                            com.example.ai.DecisionAnalysisEngine.crystallizeDilemma(decisionInputText)
                        }

                        com.example.ui.motion.EditorialSlicedCard(
                            triggerKey = crystallized.pinnedDecision,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .testTag("home_crystallized_preview_card")
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SoftSageLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        onQuickStartWithVoice(decisionInputText)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = DarkForestSage
                                        ) {
                                            Text(
                                                text = if (crystallized.isDilemmaReframed) "✨ Pinned Decision" else "Target Decision",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = CreamWhite,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.5.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Tap to begin untangling",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SoftSageDark,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = crystallized.pinnedDecision,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = DeepCharcoal,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.5.sp
                                        )
                                    )

                                    if (crystallized.politeScripts.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "💬 Includes 3 polite message templates to communicate without worry",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SoftSageDark,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Action Button (Preserves testTag: start_decision_button)
            Button(
                onClick = {
                    if (decisionInputText.isNotBlank()) {
                        onQuickStartWithVoice(decisionInputText)
                    } else {
                        onStartDecision()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkForestSage,
                    contentColor = CreamWhite
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_decision_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (decisionInputText.isNotBlank()) "Untangle this decision" else "Start a decision",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Shortcut Row: Quick 10s pill + Voice Mic pill (Preserves testTags)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CreamWhite)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                        .clickable { onQuickDecision() }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                        .testTag("quick_decision_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = SoftSageDark,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Quick (10s)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(CreamWhite)
                        .border(1.dp, CharcoalBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("voice_talk_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        VoiceMicButton(
                            onSpokenText = onQuickStartWithVoice,
                            sizeDp = 34,
                            promptMessage = "Speak what's on your mind..."
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Talk it out",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal,
                                fontSize = 13.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Past Decisions & Decisions Later Buttons (Preserves testTags)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onViewJournal,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MutedPillBg,
                        contentColor = DeepCharcoal
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MutedPillBorder),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("view_journal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Past decisions",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp
                        )
                    )
                }

                Button(
                    onClick = onViewDecisionsLater,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CreamWhite,
                        contentColor = DeepCharcoal
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (decisionsLaterCount > 0) SoftSage else CharcoalBorder
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("view_decisions_later_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (decisionsLaterCount > 0) SoftSageDark else CharcoalMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (decisionsLaterCount > 0) "Decisions later ($decisionsLaterCount)" else "Decisions later",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Decision Patterns & About cards row (compact, clean, preserving testTags)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSandLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onViewPatterns() }
                        .testTag("decision_patterns_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MutedBlueLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MutedBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Patterns",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "Past accuracy",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CharcoalMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onViewAbout() }
                        .testTag("about_clearpath_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SoftSageLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SoftSageDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "About & Method",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "ClearPath philosophy",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CharcoalMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Concise ground philosophy quote
            Text(
                text = "Understand the trade-off • Settle uncertainty • You decide",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CharcoalMuted,
                    fontSize = 12.sp,
                    letterSpacing = 0.3.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
