package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.example.R
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun AboutScreen(
    onBack: () -> Unit,
    onStartDecision: (String) -> Unit,
    onExportData: (() -> Unit)? = null,
    onClearData: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Surface(
        color = WarmIvory,
        modifier = modifier
            .fillMaxSize()
            .testTag("about_clearpath_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("about_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = DeepCharcoal
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "About ClearPath",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 18.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Hero Section
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_logo),
                                contentDescription = "ClearPath Logo",
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, SoftSage, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "OUR CORE PURPOSE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SoftSageDark,
                                        fontSize = 11.5.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Text(
                                    text = "ClearPath Decision Companion",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepCharcoal,
                                        fontSize = 15.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ClearPath does not make decisions for you.",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal,
                                fontSize = 20.sp,
                                lineHeight = 26.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "It helps you understand what is really happening in your decision so you can choose with confidence.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = DeepCharcoal,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Philosophy Section
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmSandLight.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Text(
                            text = "PHILOSOPHY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalMuted,
                                fontSize = 11.sp,
                                letterSpacing = 0.8.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"Good decisions are not made by eliminating all uncertainty. They are made by understanding the risks, knowing what you can handle, and taking small steps forward.\"",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = DeepCharcoal,
                                fontSize = 14.5.sp,
                                lineHeight = 21.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: How the Method Works (Visual Sequence)
                Text(
                    text = "How the Method Works",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "A simple 5-step sequence that brings order to noisy dilemmas",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                val sequenceSteps = listOf(
                    Triple(
                        "1. Understand the decision",
                        "Extract the real question, the context, and what's creating tension in your thoughts.",
                        Icons.Default.Psychology
                    ),
                    Triple(
                        "2. Map the options",
                        "Move beyond false binary traps (A vs B) to explore nuanced or third alternatives.",
                        Icons.Default.CompassCalibration
                    ),
                    Triple(
                        "3. Assess downsides & recoverability",
                        "Look at the worst realistic outcome, how likely it is, and whether you could recover.",
                        Icons.Default.Shield
                    ),
                    Triple(
                        "4. Build safety nets",
                        "Create practical, low-effort protections that make moving forward feel safe.",
                        Icons.Default.CheckCircle
                    ),
                    Triple(
                        "5. Choose or test with a small experiment",
                        "Instead of waiting for 100% certainty, test the waters with an easy trial run.",
                        Icons.Default.Lightbulb
                    )
                )

                sequenceSteps.forEachIndexed { idx, (stepTitle, stepDesc, icon) ->
                    MethodSequenceCard(
                        number = idx + 1,
                        title = stepTitle,
                        description = stepDesc,
                        icon = icon,
                        isLast = idx == sequenceSteps.size - 1
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: 5 Steps for First-Time Users
                Text(
                    text = "5 Steps for First-Time Users",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "A gentle guide to making your first decision in ClearPath",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                val beginnerGuide = listOf(
                    "Start with any thought or dilemma" to "You don't need a polished prompt. Speak or type your raw, messy thoughts naturally.",
                    "Let ClearPath organize it into clear options" to "We will untangle the dilemma, clarify what's at stake, and frame your choices clearly.",
                    "Look honestly at the worst realistic outcome" to "Separate vague, catastrophic imagination from what could actually happen in reality.",
                    "Ask yourself: 'Could I recover if this happened?'" to "Most choices are not fatal. Knowing you can recover removes the paralysis.",
                    "Make a small move instead of waiting for 100% certainty" to "Clarity comes from action, not endless rumination. Test it first."
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    beginnerGuide.forEachIndexed { index, (guideTitle, guideDetail) ->
                        FirstTimeGuideCard(
                            stepNumber = index + 1,
                            title = guideTitle,
                            detail = guideDetail
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: "How It Works" Core Concepts Visual
                Text(
                    text = "Core Principles",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "The mental models behind every tool in ClearPath",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Reversible vs Irreversible
                    ConceptCard(
                        title = "Reversible vs Irreversible Decisions",
                        subtitle = "Can this decision be undone?",
                        description = "Easily reversible choices (e.g. buying a book, trying an app, attending a trial class) need little analysis — make the choice quickly. Hard-to-reverse decisions (e.g. relocating, quitting a job, major commitments) deserve deep, structured thought.",
                        icon = Icons.Default.LockReset,
                        accentColor = SoftSageDark,
                        bgColor = SoftSageLight.copy(alpha = 0.5f)
                    )

                    // Downside Containment
                    ConceptCard(
                        title = "Downside Containment",
                        subtitle = "Capping the worst outcome",
                        description = "Instead of endlessly agonizing over making the 'perfect' choice, define the worst realistic outcome and build a safety net to contain it. Once the downside is bounded, moving forward becomes comfortable.",
                        icon = Icons.Default.Shield,
                        accentColor = MutedBlue,
                        bgColor = MutedBlueLight.copy(alpha = 0.5f)
                    )

                    // Real vs Imagined Risk
                    ConceptCard(
                        title = "Real vs Imagined Risk",
                        subtitle = "Anxiety amplifies consequences",
                        description = "Uncertainty triggers catastrophic thinking. ClearPath breaks anxiety down into concrete dimensions: How likely is it? What would the actual impact be? And could you bounce back?",
                        icon = Icons.Default.CompassCalibration,
                        accentColor = SoftClay,
                        bgColor = SoftClayLight.copy(alpha = 0.45f)
                    )

                    // Reducing Overthinking through Action
                    ConceptCard(
                        title = "Reducing Overthinking through Action",
                        subtitle = "Small experiments over endless debate",
                        description = "When you feel stuck between two paths, don't stay in your head. Run a 2-day low-stakes experiment, shadow a colleague, or ask a simple question. Experience provides clarity that thinking never can.",
                        icon = Icons.Default.Repeat,
                        accentColor = MaybeOchre,
                        bgColor = MaybeOchreBg.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Data Privacy & Offline Vault Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CreamWhite),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier.fillMaxWidth().testTag("offline_vault_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = DarkForestSage,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Local Storage & Data Privacy",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "• On-Device Storage: Your dilemmas, options, and reflections are stored locally on your device in a private SQLite database.\n• Offline-First: All decision stages, journaling, and reviews work completely offline without an internet connection.\n• AI Synthesis: When online and AI analysis is requested, your dilemma is transmitted securely via HTTPS to Google Gemini to structure your options. No text is used for model training or data brokering.\n• Data Retention & Control: Your data stays on your device as long as you choose. You can export a JSON copy or delete everything permanently at any time below.\n• No Third-Party Trackers: ClearPath includes no advertising SDKs, social trackers, or background telemetry.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = DeepCharcoal,
                                lineHeight = 19.sp,
                                fontSize = 12.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (onExportData != null) {
                                androidx.compose.material3.OutlinedButton(
                                    onClick = onExportData,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("export_json_button")
                                ) {
                                    Text("Export JSON", fontSize = 12.sp, color = DeepCharcoal)
                                }
                            }
                            if (onClearData != null) {
                                androidx.compose.material3.OutlinedButton(
                                    onClick = onClearData,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        contentColor = androidx.compose.ui.graphics.Color(0xFFBA1A1A)
                                    ),
                                    modifier = Modifier.weight(1f).testTag("clear_data_button")
                                ) {
                                    Text("Clear Data", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // CTA Button
                Button(
                    onClick = { onStartDecision("DEEP") },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSageDark),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("about_start_decision_button")
                ) {
                    Text(
                        text = "Try ClearPath with a Decision",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CreamWhite,
                            fontSize = 15.sp
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MethodSequenceCard(
    number: Int,
    title: String,
    description: String,
    icon: ImageVector,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(SoftSageDark),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = CreamWhite,
                        fontSize = 13.sp
                    )
                )
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(44.dp)
                        .background(CharcoalBorder)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    fontSize = 14.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CharcoalMuted,
                    fontSize = 12.5.sp,
                    lineHeight = 17.5.sp
                )
            )
        }
    }
}

@Composable
private fun FirstTimeGuideCard(
    stepNumber: Int,
    title: String,
    detail: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(WarmSandLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepNumber.toString(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepCharcoal,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 14.sp
                        )
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CharcoalMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 13.sp,
                            lineHeight = 18.5.sp
                        ),
                        modifier = Modifier.padding(start = 34.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConceptCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(17.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 14.5.sp
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CharcoalMuted,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = DeepCharcoal,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            )
        }
    }
}
