package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeneratedAnalysis
import com.example.data.RiskItem
import com.example.ui.components.RiskMapSection
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight

@Composable
fun DecisionSummaryScreen(
    decisionTitle: String,
    options: List<String>,
    reversibility: String,
    biggestUpside: String,
    biggestConcern: String,
    risks: List<RiskItem>,
    whatToVerify: List<String>,
    nextSmallStep: String,
    onProceedToDecide: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    analysis: GeneratedAnalysis? = null
) {
    val scrollState = rememberScrollState()

    Surface(
        color = WarmIvory,
        modifier = modifier
            .fillMaxSize()
            .testTag("decision_summary_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            DecisionSummaryContent(
                decisionTitle = decisionTitle,
                options = options,
                reversibility = reversibility,
                biggestUpside = biggestUpside,
                biggestConcern = biggestConcern,
                risks = risks,
                whatToVerify = whatToVerify,
                nextSmallStep = nextSmallStep,
                onProceedToDecide = onProceedToDecide,
                onBack = onBack,
                analysis = analysis
            )
        }
    }
}

@Composable
fun DecisionSummaryContent(
    decisionTitle: String,
    options: List<String>,
    reversibility: String,
    biggestUpside: String,
    biggestConcern: String,
    risks: List<RiskItem>,
    whatToVerify: List<String>,
    nextSmallStep: String,
    onProceedToDecide: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    analysis: GeneratedAnalysis? = null
) {
    // Identify key risk
    val keyRisk = risks.maxByOrNull {
        when (it.riskCategory) {
            "Needs attention" -> 4
            "Important" -> 3
            "Worth checking" -> 2
            else -> 1
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SoftSageLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        tint = SoftSageDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Decision Summary",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "Your one-page briefing before making your choice",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Decision Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "DECISION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = SoftSageDark,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = decisionTitle.ifBlank { "Should I proceed with this decision?" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 19.sp,
                            lineHeight = 25.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Options list
                    Text(
                        text = "OPTIONS UNDER CONSIDERATION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalMuted,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    options.filter { it.isNotBlank() }.forEachIndexed { idx, opt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SoftSageDark)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = DeepCharcoal,
                                    fontSize = 14.5.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Reversibility badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarmSandLight.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Reversibility",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = DeepCharcoal,
                                fontSize = 13.sp
                            )
                        )
                        Surface(
                            color = CreamWhite,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, CharcoalBorder)
                        ) {
                            Text(
                                text = reversibility,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Insert strengthened analysis blocks when available
            if (analysis != null) {
                // The Central Trade-Off
                if (analysis.centralTradeOff.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CreamWhite),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, SoftSageDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("summary_central_tradeoff")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SoftSageDark,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "WHAT THIS FUNDAMENTALLY COMES DOWN TO",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SoftSageDark,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = analysis.centralTradeOff,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ClearPath's Recommendation & Decision Readiness
                if (analysis.recommendation.recommendationTitle.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftSageLight.copy(alpha = 0.5f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SoftSageDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("summary_recommendation")
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
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
                                        fontSize = 11.sp,
                                        letterSpacing = 0.8.sp
                                    )
                                )
                                Surface(
                                    color = SoftSageDark,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = analysis.decisionReadiness,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = CreamWhite,
                                            fontSize = 10.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = analysis.recommendation.recommendationTitle,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DeepCharcoal,
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp
                                )
                            )
                            if (analysis.recommendation.whyPoints.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Why:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalMuted
                                    )
                                )
                                analysis.recommendation.whyPoints.forEach { pt ->
                                    Row(modifier = Modifier.padding(top = 3.dp), verticalAlignment = Alignment.Top) {
                                        Text(text = "• ", style = MaterialTheme.typography.bodySmall.copy(color = SoftSageDark, fontWeight = FontWeight.Bold))
                                        Text(text = pt, style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 18.sp))
                                    }
                                }
                            }
                            if (analysis.recommendation.whatCouldChangeThis.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "What could change this:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalMuted
                                    )
                                )
                                analysis.recommendation.whatCouldChangeThis.forEach { ch ->
                                    Row(modifier = Modifier.padding(top = 2.dp), verticalAlignment = Alignment.Top) {
                                        Text(text = "• ", style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted))
                                        Text(text = ch, style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted, lineHeight = 17.sp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // What is Actually Driving Hesitation
                if (analysis.hesitationDriver.isNotBlank()) {
                    SummarySectionCard(
                        title = "What appears to be holding you back",
                        icon = Icons.Default.Psychology,
                        accentColor = SoftClay
                    ) {
                        Text(
                            text = analysis.hesitationDriver,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = DeepCharcoal,
                                lineHeight = 20.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Non-Obvious Insights
                if (analysis.nonObviousInsights.isNotEmpty()) {
                    SummarySectionCard(
                        title = "Non-obvious insights",
                        icon = Icons.Default.Lightbulb,
                        accentColor = DarkForestSage
                    ) {
                        analysis.nonObviousInsights.forEach { ins ->
                            Row(modifier = Modifier.padding(vertical = 3.dp), verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SoftSageDark,
                                    modifier = Modifier.size(15.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ins,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = DeepCharcoal,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Priority Alignment
                if (analysis.priorityComparisons.isNotEmpty()) {
                    SummarySectionCard(
                        title = "How options match what matters",
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        accentColor = SoftSageDark
                    ) {
                        analysis.priorityComparisons.forEach { comp ->
                            Column(modifier = Modifier.padding(vertical = 3.dp)) {
                                Text(
                                    text = "${comp.priority}: favors ${comp.favoredOption}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SoftSageDark
                                    )
                                )
                                Text(
                                    text = comp.comparisonReason,
                                    style = MaterialTheme.typography.bodySmall.copy(color = DeepCharcoal, lineHeight = 17.sp)
                                )
                            }
                        }
                        if (analysis.conflictingPriorities.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Conflicting priorities: ${analysis.conflictingPriorities}",
                                style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Upside vs Concern Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Upside
                SummaryPointCard(
                    title = "Biggest upside",
                    content = biggestUpside.ifBlank { "Meaningful progress and learning opportunity" },
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    accentColor = SoftSageDark,
                    bgColor = SoftSageLight.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )

                // Concern
                SummaryPointCard(
                    title = "Biggest concern",
                    content = biggestConcern.ifBlank { "Navigating unexpected friction or time balance" },
                    icon = Icons.Default.WarningAmber,
                    accentColor = SoftClay,
                    bgColor = SoftClayLight.copy(alpha = 0.45f),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Risk Highlight
            if (keyRisk != null) {
                SummarySectionCard(
                    title = "Key risk to keep in mind",
                    icon = Icons.Default.CompassCalibration,
                    accentColor = SoftClay
                ) {
                    Text(
                        text = keyRisk.description.ifBlank { keyRisk.worstRealisticOutcome },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 14.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Why it matters: ${keyRisk.whyItMattersExplanation}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 12.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Information Sufficiency & What to verify
            val effectiveVerifyList = if (analysis?.infoSufficiency?.isInformationProblem == true &&
                analysis.infoSufficiency.actionableVerifications.isNotEmpty()) {
                analysis.infoSufficiency.actionableVerifications
            } else {
                whatToVerify.take(3)
            }

            if (analysis?.infoSufficiency?.isInformationProblem == false) {
                SummarySectionCard(
                    title = "Information sufficiency",
                    icon = Icons.Default.Info,
                    accentColor = SoftSageDark
                ) {
                    Text(
                        text = analysis.infoSufficiency.message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCharcoal,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (effectiveVerifyList.isNotEmpty()) {
                SummarySectionCard(
                    title = "What you need to check before deciding",
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    accentColor = DarkForestSage
                ) {
                    effectiveVerifyList.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = SoftSageDark,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCharcoal,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Next Small Step
            if (nextSmallStep.isNotBlank()) {
                SummarySectionCard(
                    title = "Next small step",
                    icon = Icons.Default.Lightbulb,
                    accentColor = SoftSageDark
                ) {
                    Text(
                        text = nextSmallStep,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = DeepCharcoal,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Calm Assurance Notice
            Surface(
                color = WarmSandLight.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ClearPath does not tell you what to decide. You have evaluated the upsides, contained the risks, and clarified what matters. The choice is yours.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = onProceedToDecide,
                colors = ButtonDefaults.buttonColors(containerColor = SoftSageDark),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("summary_proceed_button")
            ) {
                Text(
                    text = "Make My Decision",
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

            if (onBack != null) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onBack,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("summary_back_button")
                ) {
                    Text(
                        text = "Review Previous Steps",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = DeepCharcoal,
                            fontSize = 14.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

@Composable
private fun SummaryPointCard(
    title: String,
    content: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    bgColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(13.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = CharcoalMuted,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = DeepCharcoal,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            )
        }
    }
}

@Composable
private fun SummarySectionCard(
    title: String,
    icon: ImageVector,
    accentColor: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 13.5.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
