package com.example.ui.companion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.MutedBlue
import com.example.ui.theme.MutedBlueLight
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import kotlinx.coroutines.delay

/**
 * Clint's Sanctuary Modal
 * A calm, offline space where Clint consoles the user whenever they feel low,
 * anxious, or overwhelmed by a decision. Includes grounded quotes, comforting reassurance,
 * and an interactive 4-4-4 box breathing guide.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClintSanctuarySheet(
    onDismiss: () -> Unit,
    initialFeeling: FeelingState = FeelingState.OVERWHELMED
) {
    var selectedFeeling by remember { mutableStateOf(initialFeeling) }
    var currentWisdom by remember {
        mutableStateOf(OfflineCompanionRepository.getWisdomForFeeling(initialFeeling))
    }
    var isBreathingMode by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 28.dp, bottomEnd = 28.dp),
            color = WarmIvory,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, CharcoalBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("clint_sanctuary_sheet")
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top row: Companion Title + Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = SoftSageLight,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = SoftSageDark,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clint • Decision Companion",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DeepCharcoal,
                                fontSize = 16.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_clint_sanctuary")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CharcoalMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clint Pet Character Display
                ClintPetView(
                    size = 96.dp,
                    emotion = if (isBreathingMode) CompanionEmotion.BREATHING else CompanionEmotion.HAPPY,
                    isInteractive = true,
                    backgroundColor = CreamWhite,
                    faceColor = DarkForestSage,
                    borderColor = WarmSand,
                    onPetTapped = {
                        // Tapping Clint shuffles a new comforting thought!
                        currentWisdom = OfflineCompanionRepository.getWisdomForFeeling(selectedFeeling)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap Clint anytime to refresh",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CharcoalMuted,
                        fontSize = 11.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (isBreathingMode) {
                    // Box Breathing Exercise
                    BoxBreathingExerciseView(
                        onExit = { isBreathingMode = false }
                    )
                } else {
                    // Feeling Selector Pills
                    Text(
                        text = "How does this decision feel right now?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 14.5.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FeelingState.entries.forEach { feeling ->
                            val isSelected = feeling == selectedFeeling
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) DarkForestSage else CreamWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) DarkForestSage else CharcoalBorder
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedFeeling = feeling
                                        currentWisdom = OfflineCompanionRepository.getWisdomForFeeling(feeling)
                                    }
                            ) {
                                Text(
                                    text = feeling.displayName,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) CreamWhite else DeepCharcoal,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        fontSize = 12.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Consolation & Quote Card
                    AnimatedContent(
                        targetState = currentWisdom,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, delayMillis = 60)) +
                                    slideInVertically(animationSpec = tween(220)) { it / 6 })
                                .togetherWith(fadeOut(animationSpec = tween(180)) + slideOutVertically { -it / 6 })
                        },
                        label = "wisdom_card_transition"
                    ) { wisdom ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CreamWhite),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, CharcoalBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                // Consolation badge
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = SoftSageLight
                                    ) {
                                        Text(
                                            text = wisdom.feelingState.shortBadge,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = SoftSageDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = "100% Offline",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CharcoalMuted,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Empathetic consolidation
                                Text(
                                    text = wisdom.consolation,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = DeepCharcoal,
                                        lineHeight = 22.sp,
                                        fontSize = 14.5.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Quote highlight box
                                Surface(
                                    color = WarmSandLight,
                                    shape = RoundedCornerShape(14.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WarmSand)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "“${wisdom.quote}”",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = DeepCharcoal,
                                                fontStyle = FontStyle.Italic,
                                                lineHeight = 19.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "— ${wisdom.author}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CharcoalMuted,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Practical Anchor
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "Gentle step:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DarkForestSage,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = wisdom.practicalAnchor,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DeepCharcoal,
                                            fontSize = 12.5.sp,
                                            lineHeight = 17.sp
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons: "Another thought" + "Take 1-minute breath"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                currentWisdom = OfflineCompanionRepository.getWisdomForFeeling(selectedFeeling)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CreamWhite,
                                contentColor = DeepCharcoal
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("clint_another_thought_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = DeepCharcoal,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Another quote",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                        }

                        Button(
                            onClick = { isBreathingMode = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SoftSageDark,
                                contentColor = CreamWhite
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("clint_breathing_exercise_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = CreamWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Take a breath",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "I feel calmer, let's continue",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CharcoalMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.5.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Calming 4-4-4 Box Breathing Guide with animated expanding circle
 */
@Composable
fun BoxBreathingExerciseView(
    onExit: () -> Unit
) {
    var phaseIndex by remember { mutableIntStateOf(0) }
    val phases = listOf(
        "Inhale slowly..." to 4,
        "Hold gently..." to 4,
        "Exhale smoothly..." to 4,
        "Rest..." to 4
    )

    LaunchedEffect(Unit) {
        while (true) {
            for (i in phases.indices) {
                phaseIndex = i
                delay(4000)
            }
        }
    }

    val (currentInstruction, _) = phases[phaseIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_breath")
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_scale"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CharcoalBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Box Breathing with Clint",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepCharcoal
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Steadies heart rate and settles decision anxiety",
                style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Breathing Circle
            Box(
                modifier = Modifier
                    .size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(breathScale)
                        .clip(CircleShape)
                        .background(SoftSageLight.copy(alpha = 0.6f))
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DarkForestSage),
                    contentAlignment = Alignment.Center
                ) {
                    ClintPetView(
                        size = 56.dp,
                        emotion = CompanionEmotion.BREATHING,
                        isInteractive = false,
                        showContainer = false,
                        faceColor = CreamWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = currentInstruction,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = SoftSageDark,
                    fontSize = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MutedBlueLight,
                    contentColor = MutedBlue
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Done breathing",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
