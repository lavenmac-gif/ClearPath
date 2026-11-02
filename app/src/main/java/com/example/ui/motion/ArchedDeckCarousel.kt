package com.example.ui.motion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmSand
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * DeckCardItem representing a pathway or mode in the Arched Deck Carousel.
 */
data class DeckCardItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val accentColor: Color,
    val accentBg: Color,
    val testTag: String = "",
    val onClick: () -> Unit
)

/**
 * Arched Deck Card Carousel
 * Directly modeled after the reference motion template (video 00:00 - 00:08):
 * Interactive curved fan-out card deck where cards rotate along a circular arc,
 * featuring smooth physics-based dragging, spring snapping, and tactile elevation.
 */
@Composable
fun ArchedDeckCarousel(
    items: List<DeckCardItem>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    onCardSelected: (DeckCardItem) -> Unit = {}
) {
    if (items.isEmpty()) return

    var activeIndex by remember { mutableIntStateOf(initialIndex.coerceIn(0, items.size - 1)) }
    val dragOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(310.dp)
            .testTag("arched_deck_carousel")
            .pointerInput(items.size) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val currentFraction = dragOffset.value / 180f
                        val targetIndex = (activeIndex - currentFraction.roundToInt()).coerceIn(0, items.size - 1)
                        coroutineScope.launch {
                            dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioMediumBouncy))
                            activeIndex = targetIndex
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val newOffset = (dragOffset.value + dragAmount).coerceIn(-240f, 240f)
                            dragOffset.snapTo(newOffset)
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Base decorative subtle arc horizon line behind cards (matching the video's curved pedestal)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.92f)
                .height(48.dp)
                .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                .background(WarmSand.copy(alpha = 0.25f))
        )

        // Render each card along the arc
        items.forEachIndexed { index, card ->
            val offsetFromActive = (index - activeIndex) - (dragOffset.value / 180f)
            val absOffset = abs(offsetFromActive)

            // Only render cards reasonably close to the view
            if (absOffset <= 3.2f) {
                // Arc calculations
                val angleDeg = (offsetFromActive * 12.5f).coerceIn(-35f, 35f)
                val angleRad = (angleDeg * PI / 180f)

                // Radius of curvature
                val arcRadius = 450f
                val transX = (sin(angleRad) * arcRadius).toFloat()
                val transY = ((1f - cos(angleRad)) * arcRadius * 0.42f).toFloat()

                val cardScale = (1.02f - (absOffset * 0.08f)).coerceIn(0.82f, 1.05f)
                val cardAlpha = (1f - (absOffset * 0.22f)).coerceIn(0.45f, 1f)
                val cardZIndex = (10f - absOffset).coerceAtLeast(0f)

                Box(
                    modifier = Modifier
                        .zIndex(cardZIndex)
                        .offset { IntOffset(transX.roundToInt(), transY.roundToInt()) }
                        .graphicsLayer {
                            rotationZ = angleDeg
                            scaleX = cardScale
                            scaleY = cardScale
                            alpha = cardAlpha
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (index == activeIndex) {
                                card.onClick()
                                onCardSelected(card)
                            } else {
                                coroutineScope.launch {
                                    dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                    activeIndex = index
                                }
                            }
                        }
                ) {
                    DeckCardView(
                        item = card,
                        isCenter = absOffset < 0.45f
                    )
                }
            }
        }

        // Indicator dots at bottom (from video 00:09)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEachIndexed { i, _ ->
                val isSelected = i == activeIndex
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 18.dp else 6.dp, 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) DarkForestSage else CharcoalMuted.copy(alpha = 0.35f))
                        .clickable {
                            coroutineScope.launch {
                                dragOffset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                activeIndex = i
                            }
                        }
                )
            }
        }
    }
}

/**
 * Individual Card inside the Arched Deck
 */
@Composable
private fun DeckCardView(
    item: DeckCardItem,
    isCenter: Boolean
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CreamWhite),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isCenter) 1.8.dp else 1.dp,
            color = if (isCenter) item.accentColor else CharcoalBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isCenter) 10.dp else 3.dp
        ),
        modifier = Modifier
            .width(200.dp)
            .height(260.dp)
            .testTag(if (item.testTag.isNotBlank()) item.testTag else "deck_card_${item.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: Badge + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = item.accentBg
                ) {
                    Text(
                        text = item.badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = item.accentColor,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(item.accentBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Middle: Title & concise subtitle
            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        fontSize = 17.sp,
                        lineHeight = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = CharcoalMuted,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp
                    ),
                    maxLines = 3
                )
            }

            // Bottom CTA Pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isCenter) DarkForestSage else SoftSageLight,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCenter) "Open" else "Tap to choose",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (isCenter) CreamWhite else SoftSageDark,
                            fontSize = 12.sp
                        )
                    )
                    if (isCenter) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = CreamWhite,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
