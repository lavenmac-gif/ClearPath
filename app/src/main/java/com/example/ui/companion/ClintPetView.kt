package com.example.ui.companion

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Clint (c:) — Animated Decision Pet & Companion
 * Directly inspired by the reference motion video (00:16 - 00:19):
 * Minimalist, elegant typographic/vector face `c:` that blinks, breathes,
 * winks, and reacts playfully to touch with warm reassurance.
 */
@Composable
fun ClintPetView(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    emotion: CompanionEmotion = CompanionEmotion.IDLE,
    isInteractive: Boolean = true,
    backgroundColor: Color = CreamWhite,
    faceColor: Color = DarkForestSage,
    borderColor: Color = WarmSand,
    showContainer: Boolean = true,
    onPetTapped: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isWinking by remember { mutableStateOf(false) }
    var isBouncing by remember { mutableStateOf(false) }

    // Breathing / floating subtle animation
    val infiniteTransition = rememberInfiniteTransition(label = "clint_breathing")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "clint_float"
    )

    // Eye blink scale animatable
    val eyeBlinkScale = remember { Animatable(1f) }
    val bounceScale = remember { Animatable(1f) }
    val headTilt = remember { Animatable(0f) }

    // Natural blinking loop
    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2800, 5200))
            // Blink quickly down and up
            eyeBlinkScale.animateTo(0.1f, animationSpec = tween(70, easing = LinearEasing))
            eyeBlinkScale.animateTo(1f, animationSpec = tween(90, easing = FastOutSlowInEasing))
        }
    }

    val boxModifier = if (showContainer) {
        modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.32f))
            .background(backgroundColor)
            .border(1.2.dp, borderColor, RoundedCornerShape(size * 0.32f))
    } else {
        modifier.size(size)
    }

    Box(
        modifier = boxModifier
            .testTag("clint_pet_companion")
            .then(
                if (isInteractive) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = SoftSage)
                    ) {
                        coroutineScope.launch {
                            isBouncing = true
                            isWinking = true
                            // Spring jump
                            launch {
                                bounceScale.animateTo(1.22f, spring(dampingRatio = 0.45f, stiffness = 600f))
                                bounceScale.animateTo(1.0f, spring(dampingRatio = 0.6f, stiffness = 400f))
                            }
                            // Head tilt
                            launch {
                                headTilt.animateTo(8f, spring(dampingRatio = 0.5f, stiffness = 500f))
                                headTilt.animateTo(-6f, spring(dampingRatio = 0.5f, stiffness = 500f))
                                headTilt.animateTo(0f, spring(dampingRatio = 0.7f, stiffness = 400f))
                            }
                            delay(450)
                            isWinking = false
                            isBouncing = false
                        }
                        onPetTapped()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size * 0.75f)
                .scale(bounceScale.value)
                .rotate(headTilt.value)
        ) {
            val canvasW = this.size.width
            val canvasH = this.size.height

            val centerX = canvasW / 2f
            val centerY = (canvasH / 2f) + floatOffset

            // The minimalist `c:` face geometry
            // 1. The `c` smile curve: an arc on the left side, opening towards the right
            val smileStrokeWidth = (canvasW * 0.12f).coerceAtLeast(3f)
            val smileRadius = canvasW * 0.28f
            val smileRect = Size(smileRadius * 2f, smileRadius * 2f)
            val smileTopLeft = Offset(
                x = centerX - smileRadius - (canvasW * 0.08f),
                y = centerY - smileRadius
            )

            // Draw the gentle arc (the 'c')
            // Sweep from 45 deg to 315 deg (a smooth 270 deg open circle)
            drawArc(
                color = faceColor,
                startAngle = 45f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = smileTopLeft,
                size = smileRect,
                style = Stroke(width = smileStrokeWidth, cap = StrokeCap.Round)
            )

            // 2. The `:` eyes on the right of the `c`
            val eyeRadius = (canvasW * 0.065f).coerceAtLeast(2.5f)
            val eyeX = centerX + (canvasW * 0.18f)
            val eyeTopY = centerY - (canvasH * 0.16f)
            val eyeBottomY = centerY + (canvasH * 0.16f)

            // Top Eye (or winking crescent)
            val currentScale = eyeBlinkScale.value
            if (isWinking) {
                // Happy curved wink arc for top eye
                drawArc(
                    color = faceColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(eyeX - eyeRadius, eyeTopY - eyeRadius),
                    size = Size(eyeRadius * 2f, eyeRadius * 1.5f),
                    style = Stroke(width = smileStrokeWidth * 0.75f, cap = StrokeCap.Round)
                )
            } else {
                drawOval(
                    color = faceColor,
                    topLeft = Offset(eyeX - eyeRadius, eyeTopY - (eyeRadius * currentScale)),
                    size = Size(eyeRadius * 2f, eyeRadius * 2f * currentScale)
                )
            }

            // Bottom Eye
            drawOval(
                color = faceColor,
                topLeft = Offset(eyeX - eyeRadius, eyeBottomY - (eyeRadius * currentScale)),
                size = Size(eyeRadius * 2f, eyeRadius * 2f * currentScale)
            )

            // Soft friendly blush cheeks when happy or tapped
            if (emotion == CompanionEmotion.HAPPY || isBouncing) {
                val blushRadius = canvasW * 0.09f
                drawCircle(
                    color = SoftSage.copy(alpha = 0.25f),
                    radius = blushRadius,
                    center = Offset(centerX + (canvasW * 0.12f), centerY)
                )
            }
        }
    }
}
