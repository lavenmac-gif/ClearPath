package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecisionPatternItem
import com.example.ui.components.ClearPathHeader
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MaybeOchre
import com.example.ui.theme.MaybeOchreBg
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.RecoverableGreen
import com.example.ui.theme.RecoverableGreenBg
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftClayLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.viewmodel.DecisionViewModel

@Composable
fun PatternsScreen(
    viewModel: DecisionViewModel,
    onBack: () -> Unit
) {
    val patterns by viewModel.decisionPatterns.collectAsState()
    val scrollState = rememberScrollState()

    Surface(
        color = WarmIvory,
        modifier = Modifier
            .fillMaxSize()
            .testTag("patterns_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ClearPathHeader(
                title = "Decision patterns",
                subtitle = "Gentle observations from your choices",
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header Note
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SoftSageLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CreamWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = SoftSageDark,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Patterns noticed over time",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = DeepCharcoal
                                )
                            )
                            Text(
                                text = "These observations are neither praise nor criticism — just mirrors to help you understand your style.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = DeepCharcoal,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                patterns.forEach { pattern ->
                    PatternItemCard(pattern = pattern)
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
fun PatternItemCard(pattern: DecisionPatternItem) {
    val (catIcon, catColor, catBg) = when (pattern.category) {
        "Risk Perception" -> Triple(Icons.Default.Shield, MutedBlue, MutedBlueLight)
        "Confidence Habit" -> Triple(Icons.Default.CheckCircle, SoftSage, SoftSageLight)
        "Priority Alignment" -> Triple(Icons.Default.Lightbulb, MaybeOchre, MaybeOchreBg)
        else -> Triple(Icons.Default.AutoAwesome, SoftClay, SoftClayLight)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = catBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = catIcon,
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pattern.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = catColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pattern.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = CharcoalMuted,
                    lineHeight = 22.sp
                )
            )
        }
    }
}
