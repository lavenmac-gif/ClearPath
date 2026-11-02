package com.example.ui.motion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.companion.ClintPetView
import com.example.ui.companion.CompanionEmotion
import com.example.ui.companion.CompanionWisdom
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import kotlinx.coroutines.delay

/**
 * Editorial Sliced Motion Card
 * Directly inspired by the reference motion video (00:09 - 00:15):
 * Modern high-design editorial reveal where content smoothly reveals through
 * staggered animated horizontal slats / slices.
 */
@Composable
fun EditorialSlicedCard(
    modifier: Modifier = Modifier,
    sliceCount: Int = 5,
    triggerKey: Any? = null,
    content: @Composable () -> Unit
) {
    val sliceProgress = remember(triggerKey) {
        List(sliceCount) { Animatable(0f) }
    }

    LaunchedEffect(triggerKey) {
        sliceProgress.forEachIndexed { index, anim ->
            delay(index * 40L)
            anim.animateTo(
                targetValue = 1f,
                animationSpec = tween(320, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(modifier = modifier) {
        content()

        // Sliced curtain overlay that slides away with staggered delay
        Column(modifier = Modifier.matchParentSize()) {
            sliceProgress.forEachIndexed { _, anim ->
                val fraction = 1f - anim.value
                if (fraction > 0.001f) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = fraction
                                translationX = (1f - anim.value) * 80f
                            }
                            .background(WarmSand.copy(alpha = 0.3f))
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * Clint Companion Dock Card (For Home and Flow screens)
 * Features animated Clint, a rotating calming offline quote,
 * and quick consolidation access when feeling low.
 */
@Composable
fun ClintCompanionDockCard(
    wisdom: CompanionWisdom,
    onOpenSanctuary: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, CharcoalBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onOpenSanctuary() }
            .testTag("clint_companion_dock_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Clint Pet Avatar
            ClintPetView(
                size = 54.dp,
                emotion = CompanionEmotion.HAPPY,
                isInteractive = false,
                backgroundColor = SoftSageLight,
                borderColor = SoftSage.copy(alpha = 0.3f),
                faceColor = DarkForestSage
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text column: Quote & Consolidation
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Clint",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkForestSage,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(CharcoalMuted.copy(alpha = 0.5f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Feeling low?",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SoftSageDark,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = SoftSageLight
                    ) {
                        Text(
                            text = "Tap for calm",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SoftSageDark,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "“${wisdom.quote}”",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = DeepCharcoal,
                        fontStyle = FontStyle.Italic,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    ),
                    maxLines = 2
                )
            }
        }
    }
}
