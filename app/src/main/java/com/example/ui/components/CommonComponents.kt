package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
import com.example.ui.theme.SeriousTerracotta
import com.example.ui.theme.SeriousTerracottaBg
import com.example.ui.theme.SoftClay
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight

@Composable
fun ClearPathHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null
) {
    Surface(
        color = WarmIvory,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("nav_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = DeepCharcoal
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                if (actions != null) {
                    actions()
                }
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CharcoalMuted,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(
                        start = if (onBack != null) 48.dp else 0.dp,
                        top = 2.dp
                    )
                )
            }
        }
    }
}

@Composable
fun VoiceMicButton(
    onSpokenText: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentText: String = "",
    sizeDp: Int = 56,
    promptMessage: String = "Speak naturally about your decision...",
    testTag: String = "voice_mic_button"
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }
    var initialTextSnapshot by remember { mutableStateOf("") }

    // Pulse animation when listening is active
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Fallback Intent Launcher (for devices without native SpeechRecognizer service)
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenList = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = spokenList?.firstOrNull()
            if (!recognizedText.isNullOrBlank()) {
                val combined = if (initialTextSnapshot.isBlank()) {
                    recognizedText
                } else {
                    "${initialTextSnapshot.trimEnd()} $recognizedText"
                }
                onSpokenText(combined)
            }
        }
    }

    // SpeechRecognizer instance managed by lifecycle
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (_: Throwable) {}
        }
    }

    fun startListeningInternal() {
        val hasService = try {
            SpeechRecognizer.isRecognitionAvailable(context)
        } catch (_: Throwable) {
            false
        }

        initialTextSnapshot = currentText

        if (hasService) {
            try {
                try {
                    speechRecognizer?.destroy()
                } catch (_: Throwable) {}

                speechRecognizer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    SpeechRecognizer.isOnDeviceRecognitionAvailable(context)) {
                    SpeechRecognizer.createOnDeviceSpeechRecognizer(context)
                } else {
                    SpeechRecognizer.createSpeechRecognizer(context)
                }

                speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                    }

                    override fun onBeginningOfSpeech() {
                        isListening = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> {
                                Toast.makeText(context, "No speech heard. Please speak clearly into the microphone.", Toast.LENGTH_SHORT).show()
                            }
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                                // Natural timeout when user pauses
                            }
                            SpeechRecognizer.ERROR_AUDIO -> {
                                Toast.makeText(context, "Audio recording issue. Please check microphone.", Toast.LENGTH_SHORT).show()
                            }
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                                Toast.makeText(context, "Microphone permission is required.", Toast.LENGTH_SHORT).show()
                            }
                            SpeechRecognizer.ERROR_NETWORK,
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                            SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> {
                                tryFallbackIntent(context, speechLauncher, promptMessage)
                            }
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                                try {
                                    speechRecognizer?.cancel()
                                } catch (_: Throwable) {}
                            }
                            else -> {
                                tryFallbackIntent(context, speechLauncher, promptMessage)
                            }
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            val combined = if (initialTextSnapshot.isBlank()) {
                                text
                            } else {
                                "${initialTextSnapshot.trimEnd()} $text"
                            }
                            onSpokenText(combined)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            val combined = if (initialTextSnapshot.isBlank()) {
                                text
                            } else {
                                "${initialTextSnapshot.trimEnd()} $text"
                            }
                            onSpokenText(combined)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, promptMessage)
                    putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                }

                speechRecognizer?.startListening(intent)
                isListening = true
            } catch (_: Throwable) {
                // If native recognition fails to start, fallback to Intent
                tryFallbackIntent(context, speechLauncher, promptMessage)
            }
        } else {
            tryFallbackIntent(context, speechLauncher, promptMessage)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListeningInternal()
        } else {
            isListening = false
            Toast.makeText(
                context,
                "Microphone permission is required to speak your decision.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val buttonBg = if (isListening) SeriousTerracottaBg else SoftSageLight
    val borderColor = if (isListening) SeriousTerracotta else SoftSage
    val iconColor = if (isListening) SeriousTerracotta else SoftSageDark

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizeDp.dp)
            .scale(if (isListening) pulseScale else 1.0f)
            .clip(CircleShape)
            .background(buttonBg)
            .border(if (isListening) 2.dp else 1.5.dp, borderColor, CircleShape)
            .clickable {
                if (isListening) {
                    try {
                        speechRecognizer?.stopListening()
                    } catch (_: Throwable) {}
                    isListening = false
                } else {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        startListeningInternal()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
            contentDescription = if (isListening) "Listening... Tap to stop" else "Tap to speak",
            tint = iconColor,
            modifier = Modifier.size((sizeDp * 0.45).dp)
        )
    }
}

private fun tryFallbackIntent(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Intent>,
    prompt: String
) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
    }
    try {
        launcher.launch(intent)
    } catch (_: Exception) {
        Toast.makeText(
            context,
            "Speech recognition is not supported or available on this device. You can type directly.",
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun RecoverabilitySelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        RecoverabilityOptionCard(
            title = "Yes",
            description = "I could recover.",
            indicatorColor = RecoverableGreen,
            bgColor = RecoverableGreenBg,
            isSelected = selected == "Yes",
            onClick = { onSelect("Yes") },
            testTag = "recoverability_yes"
        )
        RecoverabilityOptionCard(
            title = "Maybe",
            description = "It would be difficult, but possible.",
            indicatorColor = MaybeOchre,
            bgColor = MaybeOchreBg,
            isSelected = selected == "Maybe",
            onClick = { onSelect("Maybe") },
            testTag = "recoverability_maybe"
        )
        RecoverabilityOptionCard(
            title = "No",
            description = "It would seriously affect me.",
            indicatorColor = SeriousTerracotta,
            bgColor = SeriousTerracottaBg,
            isSelected = selected == "No",
            onClick = { onSelect("No") },
            testTag = "recoverability_no"
        )
    }
}

@Composable
fun RecoverabilityOptionCard(
    title: String,
    description: String,
    indicatorColor: Color,
    bgColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) bgColor else CreamWhite
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) indicatorColor else CharcoalBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(indicatorColor)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DeepCharcoal
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CharcoalMuted
                    )
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = indicatorColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ImpactSelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf(
        Pair("Low", SoftSageDark),
        Pair("Medium", MaybeOchre),
        Pair("High", SeriousTerracotta)
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        levels.forEach { (level, color) ->
            val isSelected = selected.equals(level, ignoreCase = true) ||
                    (selected.isBlank() && level == "Medium")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) color.copy(alpha = 0.12f) else CreamWhite,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) color else CharcoalBorder
                ),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(level) }
                    .testTag("impact_$level")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = level,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) color else DeepCharcoal
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LikelihoodSelector(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = listOf("Very unlikely", "Unlikely", "Possible", "Likely", "Very likely")
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        levels.forEach { level ->
            val isSelected = selected.equals(level, ignoreCase = true)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) SoftSageLight else CreamWhite,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) SoftSage else CharcoalBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelect(level) }
                    .testTag("likelihood_$level")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = level,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) SoftSageDark else DeepCharcoal
                        )
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SoftSage,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SoftSage else WarmSandLight,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isSelected) SoftSageDark else CharcoalBorder
        ),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag("priority_chip_$text")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = CreamWhite,
                    modifier = Modifier
                        .size(14.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isSelected) CreamWhite else DeepCharcoal,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun ConfidenceRatingBar(
    confidence: Int,
    onConfidenceChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        (1..5).forEach { starIndex ->
            val isFilled = starIndex <= confidence
            Icon(
                imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "Rating $starIndex of 5",
                tint = if (isFilled) MaybeOchre else CharcoalBorder,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onConfidenceChanged(starIndex) }
                    .testTag("star_rating_$starIndex")
            )
        }
    }
}

@Composable
fun RecoverabilityBadge(
    recoverability: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (recoverability) {
        "Yes" -> Triple(RecoverableGreenBg, RecoverableGreen, "Recoverable")
        "Maybe" -> Triple(MaybeOchreBg, MaybeOchre, "Difficult but possible")
        else -> Triple(SeriousTerracottaBg, SeriousTerracotta, "Serious consequence")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SafetyNetCheckboxRow(
    text: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isChecked) SoftSageLight else CreamWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SoftSage,
                    checkmarkColor = CreamWhite,
                    uncheckedColor = CharcoalMuted
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = DeepCharcoal,
                    fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
