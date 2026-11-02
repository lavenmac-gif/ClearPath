package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DecisionEntity
import com.example.ui.theme.CharcoalBorder
import com.example.ui.theme.CharcoalMuted
import com.example.ui.theme.CreamWhite
import com.example.ui.theme.DarkForestSage
import com.example.ui.theme.DeepCharcoal
import com.example.ui.theme.NewsreaderFontFamily
import com.example.ui.theme.SoftSage
import com.example.ui.theme.SoftSageDark
import com.example.ui.theme.SoftSageLight
import com.example.ui.theme.WarmIvory
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight

@Composable
fun ClearPathReflectionScreen(
    decision: DecisionEntity,
    onSaveEditedReflection: (String) -> Unit,
    onReturnHome: () -> Unit,
    onViewJournal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }
    var reflectionText by remember(decision.clearPathReflection) {
        mutableStateOf(decision.clearPathReflection.ifBlank {
            // Fallback generation if empty
            com.example.ai.DecisionAnalysisEngine.generateDeterministicReflection(
                decisionTitle = decision.title,
                stuckReason = decision.mainConcerns.ifBlank { decision.situationContext },
                priorities = decision.priorities,
                whatLearned = decision.whatSeemsManageable.ifBlank { decision.holdingBackInsight },
                finalChoice = decision.finalChoice,
                remainingUncertainty = decision.whatDeservesChecking,
                safetyNets = decision.safetyNets.map { it.text },
                reopenConditions = decision.reopenConditions
            )
        })
    }

    Surface(
        color = WarmIvory,
        modifier = modifier
            .fillMaxSize()
            .testTag("reflection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Completion Moment (Relief, clarity, closure)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = SoftSageLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, SoftSage),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("completion_moment_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(DarkForestSage),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CreamWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Decision made.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = DarkForestSage,
                                fontSize = 18.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You don't need to solve this again unless something meaningful changes.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = DeepCharcoal,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            fontSize = 14.5.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. Heading
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your ClearPath Reflection",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = DeepCharcoal,
                            fontSize = 22.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "A quiet record of why you chose this path.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 13.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ClearPath Reflection", reflectionText))
                            Toast.makeText(context, "Reflection copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("copy_reflection_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy reflection",
                            tint = CharcoalMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    TextButton(
                        onClick = {
                            if (isEditing) {
                                onSaveEditedReflection(reflectionText)
                                isEditing = false
                            } else {
                                isEditing = true
                            }
                        },
                        modifier = Modifier.testTag("edit_reflection_button")
                    ) {
                        Text(
                            text = if (isEditing) "Done" else "Edit",
                            color = SoftSageDark,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. ClearPath Reflection Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CreamWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("clearpath_reflection_card")
            ) {
                Column(modifier = Modifier.padding(22.dp)) {
                    if (isEditing) {
                        Text(
                            text = "Editing your reflection:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SoftSageDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reflectionText,
                            onValueChange = { reflectionText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_reflection_input"),
                            minLines = 14,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WarmIvory,
                                unfocusedContainerColor = WarmIvory,
                                focusedBorderColor = SoftSage,
                                unfocusedBorderColor = CharcoalBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onSaveEditedReflection(reflectionText)
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkForestSage),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("save_reflection_button")
                        ) {
                            Text("Save edited reflection", color = CreamWhite)
                        }
                    } else {
                        // Render formatted sections
                        ReflectionFormattedSections(rawText = reflectionText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 4. Grounding final thought
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Decision made.",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = CharcoalMuted,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "“Whatever happens next, you now know why you chose it.”",
                    fontFamily = NewsreaderFontFamily,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = DeepCharcoal,
                        textAlign = TextAlign.Center,
                        fontSize = 16.5.sp,
                        lineHeight = 24.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Actions: Return home & View in Journal
            Button(
                onClick = onViewJournal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkForestSage,
                    contentColor = CreamWhite
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("view_in_journal_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View in Journal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onReturnHome,
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("return_home_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Return home",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = DeepCharcoal,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ReflectionFormattedSections(rawText: String) {
    val sections = remember(rawText) {
        parseReflectionBlocks(rawText)
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        sections.forEach { (header, content) ->
            Column {
                Text(
                    text = header,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SoftSageDark,
                        fontSize = 11.sp,
                        letterSpacing = 0.6.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = DeepCharcoal,
                        fontSize = 14.5.sp,
                        lineHeight = 22.sp
                    )
                )
            }
        }
    }
}

private fun parseReflectionBlocks(text: String): List<Pair<String, String>> {
    val knownHeaders = listOf(
        "THE DECISION",
        "THE REAL TENSION",
        "WHAT MATTERED",
        "WHAT YOU LEARNED",
        "YOUR DECISION",
        "WHAT REMAINS UNCERTAIN",
        "YOUR SAFETY NET",
        "REOPEN IF"
    )

    val result = mutableListOf<Pair<String, String>>()
    val lines = text.lines()
    var currentHeader: String? = null
    val currentBody = StringBuilder()

    for (line in lines) {
        val trimmed = line.trim()
        val matchingHeader = knownHeaders.find { it.equals(trimmed, ignoreCase = true) }
        if (matchingHeader != null) {
            if (currentHeader != null) {
                result.add(currentHeader to currentBody.toString().trim())
                currentBody.clear()
            }
            currentHeader = matchingHeader
        } else {
            if (currentHeader != null) {
                if (currentBody.isNotEmpty()) currentBody.append("\n")
                currentBody.append(line)
            } else if (trimmed.isNotBlank()) {
                currentBody.append(line)
            }
        }
    }

    if (currentHeader != null) {
        result.add(currentHeader to currentBody.toString().trim())
    } else if (text.isNotBlank()) {
        result.add("SUMMARY" to text.trim())
    }

    return result
}
