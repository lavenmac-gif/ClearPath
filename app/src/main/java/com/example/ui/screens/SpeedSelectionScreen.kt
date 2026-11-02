package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.companion.ClintPetView
import com.example.ui.companion.ClintSanctuarySheet
import com.example.ui.companion.CompanionEmotion
import com.example.ui.companion.FeelingState
import com.example.ui.components.ClearPathHeader
import com.example.ui.motion.ArchedDeckCarousel
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
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight

@Composable
fun SpeedSelectionScreen(
    onBack: () -> Unit,
    onSelectMode: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    var isClintSheetOpen by remember { mutableStateOf(false) }

    if (isClintSheetOpen) {
        ClintSanctuarySheet(
            initialFeeling = FeelingState.OVERTHINKING,
            onDismiss = { isClintSheetOpen = false }
        )
    }

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("speed_selection_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "Decision pace",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "How much time do you have?",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Swipe the deck to pick the pace that fits your moment.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CharcoalMuted,
                        fontSize = 13.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Arched Deck Carousel for selecting pace (Video 00:00 - 00:08)
                val paceCards = remember {
                    listOf(
                        DeckCardItem(
                            id = "QUICK",
                            title = "⚡ Quick",
                            subtitle = "3 questions to test downside, recoverability, and safety nets.",
                            badge = "10 seconds",
                            icon = Icons.Default.Bolt,
                            accentColor = MaybeOchre,
                            accentBg = MaybeOchreBg,
                            testTag = "mode_quick",
                            onClick = { onSelectMode("QUICK") }
                        ),
                        DeckCardItem(
                            id = "RAPID",
                            title = "🌿 Rapid",
                            subtitle = "5–7 focused questions balancing priorities and realistic downsides.",
                            badge = "~1 minute",
                            icon = Icons.Default.Spa,
                            accentColor = SoftSage,
                            accentBg = SoftSageLight,
                            testTag = "mode_rapid",
                            onClick = { onSelectMode("RAPID") }
                        ),
                        DeckCardItem(
                            id = "DEEP",
                            title = "🧭 Deep",
                            subtitle = "Full structured trade-offs, non-obvious insights, and synthesis.",
                            badge = "10+ minutes",
                            icon = Icons.Default.Explore,
                            accentColor = MutedBlue,
                            accentBg = MutedBlueLight,
                            testTag = "mode_deep",
                            onClick = { onSelectMode("DEEP") }
                        ),
                        DeckCardItem(
                            id = "LATER",
                            title = "📅 Decide Later",
                            subtitle = "Capture thoughts now, set a gentle reminder, revisit rested.",
                            badge = "Save & Park",
                            icon = Icons.Default.CalendarMonth,
                            accentColor = SoftClay,
                            accentBg = SoftClayLight,
                            testTag = "mode_later",
                            onClick = { onSelectMode("LATER") }
                        )
                    )
                }

                ArchedDeckCarousel(
                    items = paceCards,
                    initialIndex = 1,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Clint Companion Gentle Tip Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, CharcoalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { isClintSheetOpen = true }
                        .testTag("speed_clint_tip_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClintPetView(
                            size = 46.dp,
                            emotion = CompanionEmotion.HAPPY,
                            isInteractive = false,
                            backgroundColor = SoftSageLight,
                            faceColor = DarkForestSage,
                            showContainer = true
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Clint's rule of thumb",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForestSage,
                                    fontSize = 11.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "If you can easily undo it in 48 hours, choose Quick. Don't spend days analyzing a two-way door.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCharcoal,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
