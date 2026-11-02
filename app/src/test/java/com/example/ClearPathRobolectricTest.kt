package com.example

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.example.ai.DecisionAnalysisEngine
import com.example.data.DecisionEntity
import com.example.data.JsonAdapters
import com.example.data.RiskItem
import com.example.data.SafetyNetAction
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QuickDecisionScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DecisionViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ClearPathRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testJsonAdaptersSerialization() {
        val risks = listOf(
            RiskItem(
                optionTitle = "Option A",
                description = "May take longer",
                likelihood = "Possible",
                worstRealisticOutcome = "Adjustment friction",
                recoverability = "Yes"
            )
        )
        val json = JsonAdapters.riskListToJson(risks)
        val deserialized = JsonAdapters.riskListFromJson(json)
        assertEquals(1, deserialized.size)
        assertEquals("Option A", deserialized[0].optionTitle)
        assertEquals("Yes", deserialized[0].recoverability)

        val nets = listOf(
            SafetyNetAction(text = "Talk to team", isChecked = true)
        )
        val netJson = JsonAdapters.safetyNetListToJson(nets)
        val deserializedNets = JsonAdapters.safetyNetListFromJson(netJson)
        assertEquals(1, deserializedNets.size)
        assertTrue(deserializedNets[0].isChecked)
    }

    @Test
    fun testDecisionAnalysisEngineFallback() = runBlocking {
        val understanding = DecisionAnalysisEngine.extractUnderstanding("Should I accept the job offer or stay at current company?")
        assertNotNull(understanding.decision)
        assertTrue(understanding.decision.contains("Should I", ignoreCase = true) || understanding.decision.contains("job", ignoreCase = true))

        val downsides = DecisionAnalysisEngine.suggestDownsides("Job offer", "New role")
        assertEquals(3, downsides.size)

        val safetyNets = DecisionAnalysisEngine.suggestSafetyNets("Job offer", "New role", emptyList())
        assertEquals(4, safetyNets.size)
    }

    @Test
    fun testClearPath2CoreModels() {
        val facts = listOf(
            com.example.data.StructuredFactItem(text = "Job pays 120k", category = "KNOWN"),
            com.example.data.StructuredFactItem(text = "Culture will be toxic", category = "ASSUMPTION")
        )
        val json = JsonAdapters.factListToJson(facts)
        val deserialized = JsonAdapters.factListFromJson(json)
        assertEquals(2, deserialized.size)
        assertEquals("KNOWN", deserialized[0].category)
        assertEquals("ASSUMPTION", deserialized[1].category)

        val concerns = listOf(
            com.example.data.ConcernFilterItem(text = "What if commute is bad", resolutionImpact = "YES", status = "ACTIVE")
        )
        val concernJson = JsonAdapters.concernFilterListToJson(concerns)
        val deserializedConcerns = JsonAdapters.concernFilterListFromJson(concernJson)
        assertEquals(1, deserializedConcerns.size)
        assertEquals("ACTIVE", deserializedConcerns[0].status)
    }

    @Test
    fun testAdaptiveDepthClassification() {
        val quickDepth = DecisionAnalysisEngine.classifyAdaptiveDepth(
            reversibility = com.example.data.ReversibilityLevels.EASY,
            stuckCategory = com.example.data.StuckStateCatalog.QUICK_DECISION_NEEDED,
            isHighStakes = false,
            input = "Which shirt should I wear today"
        )
        assertEquals("QUICK", quickDepth)

        val deepDepth = DecisionAnalysisEngine.classifyAdaptiveDepth(
            reversibility = com.example.data.ReversibilityLevels.DIFFICULT,
            stuckCategory = com.example.data.StuckStateCatalog.AFRAID_OF_CHOOSING_WRONG,
            isHighStakes = true,
            input = "Should I quit my job and relocate abroad"
        )
        assertEquals("DEEP", deepDepth)
    }

    @Test
    fun testHomeScreenRendersCleanly() {
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(
                    onStartDecision = {},
                    onViewJournal = {},
                    onViewPatterns = {},
                    onViewAbout = {},
                    onQuickStartWithVoice = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("What are you trying to decide?").assertIsDisplayed()
        composeTestRule.onNodeWithText("You don't need to have all the answers. Just a safe space to think it through.").assertIsDisplayed()
        composeTestRule.onNodeWithTag("start_decision_button").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("about_clearpath_card").assertExists()
    }

    @Test
    fun testAboutScreenRendersCleanly() {
        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.AboutScreen(
                    onBack = {},
                    onStartDecision = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("about_clearpath_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("ClearPath does not make decisions for you.").assertExists()
    }

    @Test
    fun testWhyWeAskCardToggles() {
        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.components.WhyWeAskCard(
                    questionContext = "Can this be undone?",
                    explanation = "Reversible decisions need little analysis."
                )
            }
        }

        composeTestRule.onNodeWithTag("why_we_ask_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("Why we ask this").assertIsDisplayed()
    }

    @Test
    fun testDecisionSummaryContentRenders() {
        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.DecisionSummaryContent(
                    decisionTitle = "Join early-stage startup or stay at corporate role",
                    options = listOf("Join Startup", "Stay Corporate"),
                    reversibility = "Partially reversible",
                    biggestUpside = "High learning and equity",
                    biggestConcern = "Financial volatility",
                    risks = listOf(
                        RiskItem(
                            optionTitle = "Join Startup",
                            description = "Runway cut short",
                            likelihood = "Possible",
                            worstRealisticOutcome = "Need to find new job in 12 months",
                            recoverability = "Yes"
                        )
                    ),
                    whatToVerify = listOf("Check personal 6-month savings runway"),
                    nextSmallStep = "Review offer contract",
                    onProceedToDecide = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Decision Summary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Join early-stage startup or stay at corporate role").assertIsDisplayed()
        composeTestRule.onNodeWithTag("summary_proceed_button").assertExists()
    }

    @Test
    fun testDecisionPatternsDerivation() {
        val dummyDecisions = listOf(
            DecisionEntity(
                title = "Career choice",
                options = listOf("Option A", "Option B"),
                priorities = listOf("Learning", "Growth"),
                risks = listOf(
                    RiskItem(
                        optionTitle = "Option A",
                        description = "Adjustment friction",
                        likelihood = "Low",
                        worstRealisticOutcome = "Temporary learning curve",
                        recoverability = "Yes"
                    )
                ),
                finalChoice = "Option A",
                isDecided = true,
                reviewOutcome = "Much better than expected"
            )
        )
        val patterns = DecisionAnalysisEngine.derivePatterns(dummyDecisions)
        assertTrue(patterns.isNotEmpty())
        assertTrue(patterns.any { it.category == "Risk Perception" || it.category == "Learning" })
    }

    @Test
    fun testDecideLaterEntityDefaults() {
        val entity = DecisionEntity(
            title = "Should I accept this internship?",
            speedMode = "LATER",
            status = "Decide Later",
            savedStep = 3,
            reminderDateMillis = System.currentTimeMillis() + 86400000L
        )
        assertEquals("Decide Later", entity.status)
        assertEquals(3, entity.savedStep)
        assertNotNull(entity.reminderDateMillis)
        assertEquals("Should I accept this internship?", entity.title)
    }

    @Test
    fun testHomeScreenShowsDecisionsLaterButton() {
        composeTestRule.setContent {
            MyApplicationTheme {
                HomeScreen(
                    onStartDecision = {},
                    onViewJournal = {},
                    onViewPatterns = {},
                    onViewAbout = {},
                    onQuickStartWithVoice = {},
                    decisionsLaterCount = 2,
                    onViewDecisionsLater = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("view_decisions_later_button").assertExists()
        composeTestRule.onNodeWithText("Decisions later (2)").assertExists()
    }

    @Test
    fun testQuickModeFullTypingAndNavigationFlow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DecisionViewModel(app)
        viewModel.resetQuickDecision()

        composeTestRule.setContent {
            MyApplicationTheme {
                QuickDecisionScreen(
                    viewModel = viewModel,
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        // A & B: Tap Question 1 input and type a sentence
        composeTestRule.onNodeWithTag("quick_decision_input").performTextInput("Should I accept the job offer?")
        // C: Confirm text is immediately visible
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains("Should I accept the job offer?")
        assertEquals("Should I accept the job offer?", viewModel.quickState.value.question1Decision)

        // D & E: Delete some text and add more text
        composeTestRule.onNodeWithTag("quick_decision_input").performTextClearance()
        composeTestRule.onNodeWithTag("quick_decision_input").performTextInput("Should I accept the new senior job offer?")
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains("Should I accept the new senior job offer?")
        assertEquals("Should I accept the new senior job offer?", viewModel.quickState.value.question1Decision)

        // Type Question 2
        composeTestRule.onNodeWithTag("quick_downside_input").performTextInput("Longer daily commute")
        composeTestRule.onNodeWithTag("quick_downside_input").assertTextContains("Longer daily commute")
        assertEquals("Longer daily commute", viewModel.quickState.value.question2WorstDownside)

        // F: Press Next
        composeTestRule.onNodeWithTag("quick_continue_to_q3").performScrollTo().performClick()

        // G: Confirm the correct answer is passed to the next step
        composeTestRule.onNodeWithText("Could you handle it if it happened?").assertExists()
        composeTestRule.onNodeWithText("The downside you named: \"Longer daily commute\"").assertExists()

        // H & I: Go back and confirm the previous answers are still there
        composeTestRule.onNodeWithTag("nav_back_button").performClick()
        composeTestRule.onNodeWithTag("quick_decision_input").assertExists()
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains("Should I accept the new senior job offer?")
        composeTestRule.onNodeWithTag("quick_downside_input").assertTextContains("Longer daily commute")
    }

    @Test
    fun testQuickModeVoiceInputAndManualEditFlow() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DecisionViewModel(app)
        viewModel.resetQuickDecision()

        composeTestRule.setContent {
            MyApplicationTheme {
                QuickDecisionScreen(
                    viewModel = viewModel,
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        // J & K: Test voice input updating the single source of truth state
        viewModel.updateQuickQuestion1("Move to Seattle")
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains("Move to Seattle")

        // L: Edit the recognized speech manually
        composeTestRule.onNodeWithTag("quick_decision_input").performTextClearance()
        composeTestRule.onNodeWithTag("quick_decision_input").performTextInput("Move to Seattle this winter")
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains("Move to Seattle this winter")
        assertEquals("Move to Seattle this winter", viewModel.quickState.value.question1Decision)

        // Provide downside via voice/state
        viewModel.updateQuickQuestion2("High cost of living")
        composeTestRule.onNodeWithTag("quick_downside_input").assertTextContains("High cost of living")

        // M & N: Press Next and confirm the edited answer is used
        composeTestRule.onNodeWithTag("quick_continue_to_q3").performScrollTo().performClick()
        composeTestRule.onNodeWithText("The downside you named: \"High cost of living\"").assertExists()
    }

    @Test
    fun testQuickModeEmptyInputValidation() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DecisionViewModel(app)
        viewModel.resetQuickDecision()

        composeTestRule.setContent {
            MyApplicationTheme {
                QuickDecisionScreen(
                    viewModel = viewModel,
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        // Attempting to proceed with empty inputs
        composeTestRule.onNodeWithTag("quick_continue_to_q3").performScrollTo().performClick()

        // Must show error messages and not proceed
        composeTestRule.onNodeWithTag("quick_q1_error").assertExists()
        composeTestRule.onNodeWithTag("quick_q2_error").assertExists()
        // Still on Question 1 & 2
        composeTestRule.onNodeWithText("What are you deciding?").assertExists()
    }

    @Test
    fun testQuickModeLongInputAndPaste() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DecisionViewModel(app)
        viewModel.resetQuickDecision()

        composeTestRule.setContent {
            MyApplicationTheme {
                QuickDecisionScreen(
                    viewModel = viewModel,
                    onBack = {},
                    onComplete = {}
                )
            }
        }

        val longText = "Should I accept an offer to lead the engineering department at a rapidly expanding biotech company that requires relocating across the country, selling our current home, finding a new school for the children, and taking a temporary salary cut in exchange for significant equity stake that vests over four years?"
        composeTestRule.onNodeWithTag("quick_decision_input").performTextInput(longText)
        composeTestRule.onNodeWithTag("quick_decision_input").assertTextContains(longText)
        assertEquals(longText, viewModel.quickState.value.question1Decision)
    }

    @Test
    fun testOfflineDecisionCreationAndLocalPersistence() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = com.example.data.ClearPathDatabase.getDatabase(app, this)
        val repository = com.example.data.DecisionRepository(db.decisionDao(), db.syncQueueDao())

        // 1. Verify offline deterministic extraction produces structured decision
        val offlineUnderstanding = DecisionAnalysisEngine.extractUnderstanding("Should I buy this used car or lease a new one?")
        assertNotNull(offlineUnderstanding.decision)
        assertTrue(offlineUnderstanding.decision.contains("car", ignoreCase = true) || offlineUnderstanding.decision.contains("Should I", ignoreCase = true))

        // 2. Persist to local Room database
        val newDecision = DecisionEntity(
            title = offlineUnderstanding.decision,
            options = listOf(offlineUnderstanding.optionA, offlineUnderstanding.optionB),
            reversibility = offlineUnderstanding.reversibility,
            status = "In Progress",
            structuredFacts = offlineUnderstanding.facts
        )
        val insertedId = repository.insert(newDecision)
        assertTrue(insertedId > 0)

        // 3. Confirm retrieval from Room
        val loaded = repository.getDecisionOnce(insertedId)
        assertNotNull(loaded)
        assertEquals(offlineUnderstanding.decision, loaded?.title)

        // 4. Confirm JSON export includes the local decision
        val exportedJson = repository.exportAllDecisionsAsJson()
        assertTrue(exportedJson.contains("ClearPath"))
        assertTrue(exportedJson.contains(offlineUnderstanding.decision.take(15)))

        // 5. Test clean data wipe
        repository.deleteAllData()
        val allAfterWipe = repository.getAllDecisionsOnce()
        assertEquals(0, allAfterWipe.size)
    }

    @Test
    fun testOfflineSyncQueueHandling() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = com.example.data.ClearPathDatabase.getDatabase(app, this)
        val repository = com.example.data.DecisionRepository(db.decisionDao(), db.syncQueueDao())

        val decision = DecisionEntity(title = "Local offline test decision")
        val id = repository.insert(decision)

        // Sync queue should have enqueued the CREATE operation
        val pendingCount = db.syncQueueDao().getPendingItems().size
        assertTrue(pendingCount >= 1)

        // Process sync queue when online
        val syncSuccess = repository.syncPendingQueue(isOnline = true)
        assertTrue(syncSuccess)

        // Completed items cleared
        val remainingPending = db.syncQueueDao().getPendingItems().size
        assertEquals(0, remainingPending)
    }

    @Test
    fun testClearPathReflectionGenerationAndStructure() {
        val reflection = DecisionAnalysisEngine.generateDeterministicReflection(
            decisionTitle = "Accept new job offer or stay at current company",
            stuckReason = "Balancing immediate job security against long-term career growth",
            priorities = listOf("Personal growth", "Work-life balance"),
            whatLearned = "The downside was manageable with a 6-month buffer",
            finalChoice = "Accept the new role",
            remainingUncertainty = "Day-to-day team dynamics remain to be seen",
            safetyNets = listOf("Maintain friendly ties with current manager"),
            reopenConditions = listOf("Offer terms change before start date")
        )

        assertTrue(reflection.contains("THE DECISION"))
        assertTrue(reflection.contains("THE REAL TENSION"))
        assertTrue(reflection.contains("WHAT MATTERED"))
        assertTrue(reflection.contains("WHAT YOU LEARNED"))
        assertTrue(reflection.contains("YOUR DECISION"))
        assertTrue(reflection.contains("WHAT REMAINS UNCERTAIN"))
        assertTrue(reflection.contains("YOUR SAFETY NET"))
        assertTrue(reflection.contains("REOPEN IF"))
        assertTrue(reflection.contains("Accept the new role"))
    }

    @Test
    fun testClearPathReflectionScreenRendersCompletionMoment() {
        var savedReflectionText: String? = null
        val testDecision = DecisionEntity(
            id = 42L,
            title = "Accept new position",
            finalChoice = "Accept new position",
            clearPathReflection = """
                THE DECISION
                Accept new position

                THE REAL TENSION
                Fear of leaving comfort zone

                WHAT MATTERED
                Growth and learning

                WHAT YOU LEARNED
                The financial safety net was sufficient

                YOUR DECISION
                Accept new position

                WHAT REMAINS UNCERTAIN
                New company pace

                YOUR SAFETY NET
                • Emergency savings buffer

                REOPEN IF
                • Compensation package reduced
            """.trimIndent()
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.ClearPathReflectionScreen(
                    decision = testDecision,
                    onSaveEditedReflection = { savedReflectionText = it },
                    onReturnHome = {},
                    onViewJournal = {}
                )
            }
        }

        // 1. Completion Moment
        composeTestRule.onNodeWithTag("completion_moment_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("You don't need to solve this again unless something meaningful changes.").assertIsDisplayed()

        // 2. Reflection card & 8 sections
        composeTestRule.onNodeWithText("Your ClearPath Reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("THE REAL TENSION").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Fear of leaving comfort zone").performScrollTo().assertIsDisplayed()

        // 3. User editing affordance
        composeTestRule.onNodeWithTag("edit_reflection_button").performClick()
        composeTestRule.onNodeWithTag("edit_reflection_input").assertIsDisplayed()
    }

    @Test
    fun testFutureReviewPreservesOriginalThinkingAndCalibratesConfidence() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DecisionViewModel(app)
        val testDecision = DecisionEntity(
            id = 99L,
            title = "Career move to startup",
            finalChoice = "Joined startup",
            confidenceLevel = 4,
            confidencePercent = 80,
            clearPathReflection = """
                THE DECISION
                Career move to startup

                THE REAL TENSION
                Trading corporate stability for accelerated learning

                YOUR DECISION
                Joined startup
            """.trimIndent(),
            isDecided = true
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.screens.FutureReviewScreen(
                    decision = testDecision,
                    viewModel = viewModel,
                    onBack = {},
                    onReviewCompleted = {}
                )
            }
        }

        // Section 7: WHAT I THOUGHT THEN is displayed
        composeTestRule.onNodeWithText("WHAT I THOUGHT THEN").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your original reflection").assertIsDisplayed()
        composeTestRule.onNodeWithText("Joined startup").assertExists()

        // Section 8: Confidence calibration
        composeTestRule.onNodeWithText("Confidence at the time: 80%").performScrollTo().assertIsDisplayed()

        // Section 9: Process vs Outcome
        composeTestRule.onNodeWithText("Process vs. Outcome").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Yes, I stand by the decision process").performScrollTo().assertIsDisplayed()

        // Outcome options
        composeTestRule.onNodeWithText("Better than expected").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("About as expected").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("submit_review_button").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun testDecisionAnalysisEngineDeepAnalysis() = runBlocking {
        val analysis = DecisionAnalysisEngine.generateAnalysis(
            decision = "Should I accept the new senior role at Acme Corp or stay at Beta Inc?",
            options = listOf("Accept Acme Corp offer", "Stay at Beta Inc"),
            risks = listOf(
                RiskItem(
                    optionTitle = "Accept Acme Corp offer",
                    description = "Culture might be intense and workload overwhelming",
                    likelihood = "Possible",
                    worstRealisticOutcome = "Need to adapt for 6 months with extra hours",
                    recoverability = "Yes"
                )
            ),
            priorities = listOf("Growth & Career advancement", "Stability & Predictable hours"),
            stuckCategory = "Afraid of choosing the wrong option",
            context = "I have 6 months savings as a buffer.",
            structuredFacts = listOf(
                com.example.data.StructuredFactItem(text = "Compensation is 25% higher at Acme", category = "KNOWN"),
                com.example.data.StructuredFactItem(text = "Manager will be demanding", category = "ASSUMPTION")
            ),
            reversibility = com.example.data.ReversibilityLevels.SOMEWHAT,
            safetyNets = listOf(
                SafetyNetAction(text = "Keep in touch with old teammates", isChecked = true),
                SafetyNetAction(text = "Keep 6 months emergency savings intact", isChecked = true)
            )
        )

        // Verify central trade-off is identified
        assertNotNull(analysis.centralTradeOff)
        assertTrue(analysis.centralTradeOff.isNotBlank())

        // Verify hesitation driver explanation
        assertNotNull(analysis.hesitationDriver)
        assertTrue(analysis.hesitationDriver.isNotBlank())
        assertTrue(analysis.hesitationCategory.isNotBlank())

        // Verify non-obvious insights generated
        assertTrue(analysis.nonObviousInsights.isNotEmpty())

        // Verify priority comparison without fake arbitrary scores
        assertTrue(analysis.priorityComparisons.isNotEmpty())

        // Verify qualitative risk analysis
        assertTrue(analysis.analyzedRisks.isNotEmpty())
        val risk = analysis.analyzedRisks.first()
        assertTrue(risk.likelihood in listOf("unlikely", "possible", "plausible", "likely", "Unknown"))

        // Verify information sufficiency assessment
        assertNotNull(analysis.infoSufficiency)
        assertTrue(analysis.infoSufficiency.message.isNotBlank())

        // Verify recommendation and decision readiness
        assertNotNull(analysis.recommendation)
        assertTrue(analysis.recommendation.recommendationTitle.isNotBlank())
        assertTrue(analysis.recommendation.whyPoints.isNotEmpty())
        assertTrue(analysis.recommendation.userAutonomyNotice.contains("final decision", ignoreCase = true) || analysis.recommendation.userAutonomyNotice.contains("ClearPath's read", ignoreCase = true))
        assertTrue(analysis.decisionReadiness in listOf(
            "READY TO DECIDE",
            "NEEDS A LITTLE MORE INFORMATION",
            "NEEDS A SMALL EXPERIMENT",
            "NOT CLEAR ENOUGH YET"
        ))
    }

    @Test
    fun testDecisionAnalysisOverwhelmedInterests() = runBlocking {
        val analysis = DecisionAnalysisEngine.generateAnalysis(
            decision = "I want to learn maths, cooking, PCB design and fashion illustration. I want to understand everything immediately. None are connected. I feel useless because I cannot decide where to start.",
            options = listOf("Learn advanced mathematics", "Learn cooking", "PCB circuit design", "Fashion illustration"),
            risks = emptyList(),
            priorities = listOf("Mastery", "Creativity", "Curiosity"),
            stuckCategory = "Too many options / Overwhelmed",
            context = "Overwhelmed by wanting to pursue everything at once."
        )

        // Verify detection of overwhelmed interests
        assertTrue(analysis.isOverwhelmedByManyInterests)
        assertNotNull(analysis.sequencingAdvice)
        assertTrue(analysis.sequencingAdvice?.isNotBlank() == true)
        val advice = analysis.sequencingAdvice.orEmpty()
        assertTrue(
            advice.contains("sequencing", ignoreCase = true) ||
            analysis.centralTradeOff.contains("sequence", ignoreCase = true) ||
            analysis.hesitationDriver.contains("simultaneously", ignoreCase = true)
        )
        // Verify recommendation is a small experiment or sequencing
        assertTrue(
            analysis.decisionReadiness == "NEEDS A SMALL EXPERIMENT" ||
            analysis.decisionReadiness == "READY TO DECIDE"
        )
    }

    @Test
    fun testClintCompanionOfflineWisdom() {
        val allWisdom = com.example.ui.companion.OfflineCompanionRepository.quotes
        assertTrue(allWisdom.isNotEmpty())
        assertTrue(allWisdom.size >= 15)

        // Verify all feeling states are supported offline
        com.example.ui.companion.FeelingState.entries.forEach { feeling ->
            val wisdom = com.example.ui.companion.OfflineCompanionRepository.getWisdomForFeeling(feeling)
            assertNotNull(wisdom)
            assertTrue(wisdom.quote.isNotBlank())
            assertTrue(wisdom.author.isNotBlank())
            assertTrue(wisdom.consolation.isNotBlank())
            assertTrue(wisdom.practicalAnchor.isNotBlank())
        }
    }

    @Test
    fun testClintPetViewAndSanctuarySheet() {
        composeTestRule.setContent {
            MyApplicationTheme {
                com.example.ui.companion.ClintPetView(
                    size = 72.dp,
                    emotion = com.example.ui.companion.CompanionEmotion.HAPPY
                )
            }
        }

        composeTestRule.onNodeWithTag("clint_pet_companion").assertIsDisplayed()
        composeTestRule.onNodeWithTag("clint_pet_companion").performClick()
    }

    @Test
    fun testSemanticDilemmaReframerOffline() {
        val userAnxiousInput = "i don't feel good today.i feel so so sad.i can't concentrate on anything tommorows exam and club meating are stressing me. how do i tell them i can't come?what if they misunderstand me?what if they isolate me?"

        val crystallized = com.example.ai.DecisionAnalysisEngine.crystallizeDilemma(userAnxiousInput)

        assertNotNull(crystallized)
        assertTrue("Dilemma should be marked as reframed", crystallized.isDilemmaReframed)
        assertTrue(
            "Pinned decision should focus on conveying absence due to the exam",
            crystallized.pinnedDecision.contains("convey", ignoreCase = true) &&
                    crystallized.pinnedDecision.contains("absence", ignoreCase = true) &&
                    crystallized.pinnedDecision.contains("exam", ignoreCase = true)
        )
        assertTrue(
            "Should discuss why exam is genuinely important",
            crystallized.contentDiscussion.contains("exam", ignoreCase = true) &&
                    crystallized.contentDiscussion.contains("important", ignoreCase = true)
        )
        assertTrue(
            "Should address fear of misunderstanding/isolation",
            crystallized.fearAddressed.contains("misunderstand", ignoreCase = true) ||
                    crystallized.fearAddressed.contains("isolate", ignoreCase = true)
        )
        assertTrue("Should provide at least 3 polite scripts", crystallized.politeScripts.size >= 3)
        crystallized.politeScripts.forEach { script ->
            assertTrue("Script should be polite and non-empty", script.isNotBlank())
        }

        // Test through extractUnderstanding offline fallback as well
        val extracted = kotlinx.coroutines.runBlocking {
            com.example.ai.DecisionAnalysisEngine.extractUnderstanding(userAnxiousInput)
        }
        assertEquals(crystallized.pinnedDecision, extracted.decision)
        assertEquals(crystallized.optionA, extracted.optionA)
        assertEquals(crystallized.optionB, extracted.optionB)
        assertEquals(crystallized.politeScripts, extracted.politeScripts)
        assertEquals(crystallized.contentDiscussion, extracted.contentDiscussion)
    }
}
