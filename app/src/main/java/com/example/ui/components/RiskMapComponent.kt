package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RiskItem
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
fun RiskMapSection(
    risks: List<RiskItem>,
    modifier: Modifier = Modifier,
    testTag: String = "risk_map_section"
) {
    if (risks.isEmpty()) return

    // Find the most critical risk:
    // Priority order: "Needs attention" > "Important" > "Worth checking" > "Low concern"
    val sortedRisks = risks.sortedWith(
        compareByDescending<RiskItem> {
            when (it.riskCategory) {
                "Needs attention" -> 4
                "Important" -> 3
                "Worth checking" -> 2
                else -> 1
            }
        }
    )
    val mostImportantRisk = sortedRisks.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SoftSageLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CompassCalibration,
                    contentDescription = null,
                    tint = SoftSageDark,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Your Risk Map",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "Summarizing downsides to see which deserve your attention",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // "Most important risk" Callout Card
        if (mostImportantRisk != null) {
            Surface(
                color = SoftClayLight.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftClay.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("most_important_risk_callout")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Most important risk",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftClay,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        RiskCategoryBadge(category = mostImportantRisk.riskCategory)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = mostImportantRisk.description.ifBlank { mostImportantRisk.worstRealisticOutcome },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 15.sp,
                            lineHeight = 21.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Why it matters
                    Text(
                        text = "Why it matters",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalMuted,
                            fontSize = 11.5.sp
                        )
                    )
                    Text(
                        text = mostImportantRisk.whyItMattersExplanation,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DeepCharcoal,
                            fontSize = 13.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This downside deserves attention. A simple safety net or checking question can give you peace of mind.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // List of all risks with calm visual tags
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            risks.forEachIndexed { index, risk ->
                RiskMapItemCard(
                    risk = risk,
                    testTag = "risk_map_item_$index"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Risk is not the same thing as a bad decision. Every worthwhile decision carries risk.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = CharcoalMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun RiskMapItemCard(
    risk: RiskItem,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (risk.optionTitle.isNotBlank()) {
                        Text(
                            text = risk.optionTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = SoftSageDark,
                                fontSize = 11.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = risk.description.ifBlank { risk.worstRealisticOutcome },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = DeepCharcoal,
                            fontSize = 14.5.sp,
                            lineHeight = 20.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                RiskCategoryBadge(category = risk.riskCategory)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Three calm metrics pills: Likelihood | Impact | Recoverability
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RiskMetricChip(
                    label = "Likelihood",
                    value = risk.likelihood,
                    modifier = Modifier.weight(1f)
                )
                RiskMetricChip(
                    label = "Impact",
                    value = risk.impact,
                    modifier = Modifier.weight(1f)
                )
                RiskMetricChip(
                    label = "Recoverability",
                    value = risk.normalizedRecoverability,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RiskMetricChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = WarmIvory,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, CharcoalBorder.copy(alpha = 0.7f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CharcoalMuted,
                    fontSize = 10.5.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal,
                    fontSize = 12.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
fun RiskCategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, border) = when (category) {
        "Needs attention" -> Triple(SoftClayLight, SoftClay, SoftClay.copy(alpha = 0.5f))
        "Important" -> Triple(MaybeOchreBg, MaybeOchre, MaybeOchre.copy(alpha = 0.5f))
        "Worth checking" -> Triple(SoftSageLight, SoftSageDark, SoftSage.copy(alpha = 0.5f))
        else -> Triple(WarmSandLight, DeepCharcoal, WarmSand)
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, border),
        modifier = modifier
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
