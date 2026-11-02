package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ClearPathReflectionScreen
import com.example.ui.screens.DecideLaterSetupScreen
import com.example.ui.screens.DecisionDetailScreen
import com.example.ui.screens.DecisionGuidedFlowScreen
import com.example.ui.screens.DecisionsLaterScreen
import com.example.ui.screens.FutureReviewScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.PatternsScreen
import com.example.ui.screens.QuickDecisionScreen
import com.example.ui.screens.SpeedSelectionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DecisionViewModel

class MainActivity : ComponentActivity() {

  private val decisionViewModel: DecisionViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ClearPathApp(viewModel = decisionViewModel)
      }
    }
  }
}

@Composable
fun ClearPathApp(viewModel: DecisionViewModel) {
  val navController = rememberNavController()
  val selectedDecision by viewModel.selectedDecision.collectAsState()
  val decideLaterDecisions by viewModel.decideLaterDecisions.collectAsState()
  val isOnline by viewModel.isOnline.collectAsState()
  val syncStatusText by viewModel.syncStatusText.collectAsState()

  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = "home",
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      composable("home") {
        HomeScreen(
          decisionsLaterCount = decideLaterDecisions.size,
          isOnline = isOnline,
          syncStatusText = syncStatusText,
          onStartDecision = {
            viewModel.startNewDecision()
            navController.navigate("guided_flow")
          },
          onViewJournal = {
            navController.navigate("journal")
          },
          onViewDecisionsLater = {
            navController.navigate("decisions_later")
          },
          onDecideLaterSetup = {
            navController.navigate("decide_later_setup")
          },
          onViewPatterns = {
            navController.navigate("patterns")
          },
          onViewAbout = {
            navController.navigate("about")
          },
          onQuickStartWithVoice = { spokenText ->
            viewModel.startNewDecisionWithInput(spokenText)
            navController.navigate("guided_flow")
          },
          onQuickDecision = {
            viewModel.resetQuickDecision()
            navController.navigate("quick_decision")
          }
        )
      }

      composable("speed_selection") {
        SpeedSelectionScreen(
          onBack = { navController.popBackStack() },
          onSelectMode = { mode ->
            if (mode == "QUICK") {
              viewModel.resetQuickDecision()
              navController.navigate("quick_decision")
            } else if (mode == "LATER") {
              navController.navigate("decide_later_setup")
            } else {
              viewModel.startNewDecision(mode)
              navController.navigate("guided_flow")
            }
          }
        )
      }

      composable("quick_decision") {
        QuickDecisionScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() },
          onComplete = {
            navController.navigate("reflection") {
              popUpTo("home")
            }
          }
        )
      }

      composable("decide_later_setup") {
        DecideLaterSetupScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() },
          onDecisionSaved = {
            navController.navigate("decisions_later") {
              popUpTo("home")
            }
          },
          onStartNow = {
            navController.navigate("guided_flow") {
              popUpTo("home")
            }
          }
        )
      }

      composable("decisions_later") {
        DecisionsLaterScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() },
          onContinueDecision = { decision ->
            viewModel.resumeDecision(decision) {
              navController.navigate("guided_flow")
            }
          },
          onAddNewDecideLater = {
            navController.navigate("decide_later_setup")
          }
        )
      }

      composable("guided_flow") {
        DecisionGuidedFlowScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() },
          onFinishDecision = { decisionId, isCompleted ->
            if (isCompleted) {
              navController.navigate("reflection") {
                popUpTo("home")
              }
            } else {
              navController.navigate("decisions_later") {
                popUpTo("home")
              }
            }
          }
        )
      }

      composable("reflection") {
        selectedDecision?.let { decision ->
          ClearPathReflectionScreen(
            decision = decision,
            onSaveEditedReflection = { editedText ->
              viewModel.updateDecisionReflection(decision.id, editedText)
            },
            onReturnHome = {
              navController.navigate("home") {
                popUpTo("home") { inclusive = true }
              }
            },
            onViewJournal = {
              navController.navigate("journal") {
                popUpTo("home")
              }
            }
          )
        } ?: run {
          navController.navigate("journal") {
            popUpTo("home")
          }
        }
      }

      composable("journal") {
        JournalScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() },
          onSelectDecision = { decision ->
            viewModel.selectDecision(decision)
            navController.navigate("decision_detail")
          },
          onStartNewDecision = {
            navController.navigate("speed_selection")
          }
        )
      }

      composable("decision_detail") {
        selectedDecision?.let { decision ->
          DecisionDetailScreen(
            decision = decision,
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onReviewDecision = {
              navController.navigate("future_review")
            },
            onContinueDecision = {
              viewModel.resumeDecision(decision) {
                navController.navigate("guided_flow")
              }
            }
          )
        } ?: run {
          navController.popBackStack()
        }
      }

      composable("future_review") {
        selectedDecision?.let { decision ->
          FutureReviewScreen(
            decision = decision,
            viewModel = viewModel,
            onBack = { navController.popBackStack() },
            onReviewCompleted = {
              navController.popBackStack()
            }
          )
        } ?: run {
          navController.popBackStack()
        }
      }

      composable("patterns") {
        PatternsScreen(
          viewModel = viewModel,
          onBack = { navController.popBackStack() }
        )
      }

      composable("about") {
        val context = LocalContext.current
        var exportDialogText by remember { mutableStateOf<String?>(null) }
        var showDeleteConfirm by remember { mutableStateOf(false) }

        AboutScreen(
          onBack = { navController.popBackStack() },
          onStartDecision = { mode ->
            if (mode == "QUICK") {
              navController.navigate("quick_decision")
            } else {
              viewModel.startNewDecision(mode)
              navController.navigate("guided_flow")
            }
          },
          onExportData = {
            viewModel.exportAllDecisionsJson { json ->
              exportDialogText = json
            }
          },
          onClearData = {
            showDeleteConfirm = true
          }
        )

        exportDialogText?.let { json ->
          AlertDialog(
            onDismissRequest = { exportDialogText = null },
            title = { Text("Export Decisions (JSON)") },
            text = {
              Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                  text = "Your decisions formatted as structured JSON for backup or export:",
                  style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                SelectionContainer {
                  Text(
                    text = json,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                  )
                }
              }
            },
            confirmButton = {
              Row {
                TextButton(onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("ClearPath JSON Export", json))
                  Toast.makeText(context, "Export copied to clipboard", Toast.LENGTH_SHORT).show()
                }) {
                  Text("Copy JSON")
                }
                TextButton(onClick = { exportDialogText = null }) {
                  Text("Close")
                }
              }
            }
          )
        }

        if (showDeleteConfirm) {
          AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Local Device Data?") },
            text = {
              Text("This will permanently remove all decisions stored locally on this device. This action cannot be undone.")
            },
            confirmButton = {
              TextButton(onClick = {
                viewModel.clearAllLocalData {
                  showDeleteConfirm = false
                }
              }) {
                Text("Delete All", color = androidx.compose.ui.graphics.Color.Red)
              }
            },
            dismissButton = {
              TextButton(onClick = { showDeleteConfirm = false }) {
                Text("Cancel")
              }
            }
          )
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

