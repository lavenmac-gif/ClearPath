package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [DecisionEntity::class, SyncQueueItem::class], version = 10, exportSchema = false)
abstract class ClearPathDatabase : RoomDatabase() {
    abstract fun decisionDao(): DecisionDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: ClearPathDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClearPathDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClearPathDatabase::class.java,
                    "clearpath_database"
                )
                .fallbackToDestructiveMigration(true)
                .addCallback(ClearPathDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class ClearPathDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDecisions(database.decisionDao())
                    }
                }
            }
        }

        suspend fun populateInitialDecisions(dao: DecisionDao) {
            val sample1 = DecisionEntity(
                title = "Should I accept this internship?",
                situationContext = "I have two internship offers. One pays more at a large financial firm, but the other at an early product studio gives me much better hands-on experience in modern tech and mentorship.",
                speedMode = "DEEP",
                reversibility = "Somewhat difficult",
                options = listOf("Higher-paying financial firm", "Product studio with better learning"),
                mainConcerns = "Short-term financial buffer vs accelerated skill growth and mentorship",
                biggestUpside = "Accelerated skill growth in modern Kotlin and hands-on senior engineering mentorship",
                biggestConcern = "Lower stipend leaving a tighter living budget during the summer",
                risks = listOf(
                    RiskItem(
                        optionTitle = "Higher-paying financial firm",
                        description = "I might spend the summer doing repetitive legacy maintenance instead of engineering growth.",
                        likelihood = "Likely",
                        impact = "High",
                        worstRealisticOutcome = "I feel uninspired for 3 months and learn little to build my portfolio with.",
                        recoverability = "Medium"
                    ),
                    RiskItem(
                        optionTitle = "Product studio with better learning",
                        description = "The lower compensation might stretch my summer living budget tighter.",
                        likelihood = "Possible",
                        impact = "Medium",
                        worstRealisticOutcome = "I have to cook more at home and cut leisure spending for 12 weeks.",
                        recoverability = "Easy"
                    ),
                    RiskItem(
                        optionTitle = "Product studio with better learning",
                        description = "Less formal onboarding structure than a corporate enterprise.",
                        likelihood = "Possible",
                        impact = "Medium",
                        worstRealisticOutcome = "Having to ask more questions and be proactive in the first 2 weeks.",
                        recoverability = "Easy"
                    )
                ),
                priorities = listOf("Learning", "Career growth", "Happiness"),
                safetyNets = listOf(
                    SafetyNetAction(
                        text = "Talk to a former intern at the product studio about daily responsibilities",
                        isChecked = true
                    ),
                    SafetyNetAction(
                        text = "Build a 12-week summer living budget spreadsheet",
                        isChecked = true
                    ),
                    SafetyNetAction(
                        text = "Ask the studio manager if there is mentorship on modern Android architecture",
                        isChecked = true
                    ),
                    SafetyNetAction(
                        text = "Negotiate a flexible remote Friday option",
                        isChecked = false
                    )
                ),
                aiAnalysis = "Your responses suggest that the fear of making the 'wrong' choice may be larger than the actual consequences you identified. Both downsides appear entirely recoverable.",
                holdingBackInsight = "Your fear of regretting the lower stipend is overshadowing the clear alignment with your top priorities of learning and career growth.",
                whatSeemsManageable = "A tighter 12-week budget is clearly manageable and temporary with minor lifestyle adjustments.",
                whatDeservesChecking = "Clarifying mentorship expectations with the studio lead will resolve the last remaining question.",
                finalChoice = "Product studio with better learning",
                reasoning = "The hands-on learning and mentorship directly serve where I want my career to be next year, and the financial risk is small and manageable.",
                confidenceLevel = 4,
                userNotes = "Accepted the offer after speaking with their senior engineer. Feeling much calmer!",
                whatIfChoiceFuture = "6 months from now, I will have shipped real features to users, updated my portfolio with modern Kotlin code, and built strong mentor references.",
                whatIfOtherFuture = "6 months from now, I might have more savings in my bank account, but would likely still wonder why I didn't take the challenge.",
                clearPathReflection = "THE DECISION\nChoosing between an enterprise firm and an early product studio internship.\n\nTHE REAL TENSION\nYou weren't choosing between money and poverty. You were choosing between short-term financial ease and steep skill acquisition.\n\nWHAT MATTERED\nHands-on engineering experience, senior mentorship, and career trajectory.\n\nWHAT YOU LEARNED\nThe stipend difference is manageable for 12 weeks and fully recoverable.\n\nYOUR DECISION\nYou chose the Product Studio internship with active mentorship.\n\nWHAT REMAINS UNCERTAIN\nDaily workload intensity during product sprints.\n\nYOUR SAFETY NET\nA 12-week budget spreadsheet and speaking with previous studio interns.\n\nREOPEN IF\nAssigned projects change away from mobile engineering or mentorship is unavailable.",
                isDecided = true,
                status = "Reviewed",
                savedStep = 12,
                reviewOutcome = "Better than expected",
                reviewReflection = "The mentorship exceeded my expectations. The budget was tight for the first month, but the portfolio project helped me land early interviews for full-time roles.",
                reviewedAt = System.currentTimeMillis() - 86400000L * 14,
                createdAt = System.currentTimeMillis() - 86400000L * 45
            )

            val sample2 = DecisionEntity(
                title = "Should I study abroad next semester?",
                situationContext = "Considering a study exchange program in Scandinavia. Worried about feeling isolated or falling behind on core credits.",
                speedMode = "RAPID",
                options = listOf("Go on exchange program", "Stay on home campus"),
                mainConcerns = "Credit transfer and stepping outside my comfort zone",
                risks = listOf(
                    RiskItem(
                        optionTitle = "Go on exchange program",
                        description = "One of the credits might not transfer as a direct elective equivalent.",
                        likelihood = "Possible",
                        worstRealisticOutcome = "I might need to take one extra elective course during the summer session.",
                        recoverability = "Yes"
                    )
                ),
                priorities = listOf("Learning", "Independence", "Happiness"),
                safetyNets = listOf(
                    SafetyNetAction(
                        text = "Get written pre-approval from the academic advisor for all syllabus units",
                        isChecked = true
                    ),
                    SafetyNetAction(
                        text = "Join the international student WhatsApp group before flying",
                        isChecked = true
                    )
                ),
                aiAnalysis = "You're leaning toward going on exchange. The academic risk is manageable with pre-approval, while staying on campus sacrifices your top priority of independence.",
                holdingBackInsight = "General travel anxiety masquerading as credit transfer concerns.",
                whatSeemsManageable = "Taking an online summer elective if a unit fails to transfer.",
                whatDeservesChecking = "Advisor sign-off on course syllabi.",
                finalChoice = "Go on exchange program",
                reasoning = "This is a rare window in my life to experience another culture, and academic credit rules can be verified before departure.",
                confidenceLevel = 5,
                userNotes = "Submitted passport and university application forms.",
                whatIfChoiceFuture = "Exploring a new country, becoming confident navigating unfamiliar places, and making lifelong international friends.",
                whatIfOtherFuture = "Comfortable routine at home, but looking at exchange photos with lingering FOMO.",
                isDecided = true,
                status = "Reviewed",
                savedStep = 12,
                reviewOutcome = "Much better than expected",
                reviewReflection = "Pre-clearing courses with the advisor worked like a charm. Truly glad I looked at the worst-case scenario objectively.",
                reviewedAt = System.currentTimeMillis() - 86400000L * 5,
                createdAt = System.currentTimeMillis() - 86400000L * 60
            )

            val sample3 = DecisionEntity(
                title = "Should I accept this internship?",
                situationContext = "Evaluating whether to join a fast-paced product studio or a stable financial corporation.",
                speedMode = "DEEP",
                reversibility = "Partially reversible",
                options = listOf("Higher-paying enterprise offer", "Product studio with mentorship"),
                mainConcerns = "Lower starting stipend vs accelerated technical skill growth",
                biggestUpside = "Working on production Android features alongside senior mentors",
                biggestConcern = "Tight summer living expenses",
                stuckReason = "TRADE_OFF_GAP",
                isDecided = false,
                status = "Decide Later",
                savedStep = 3,
                reminderDateMillis = System.currentTimeMillis() + 86400000L,
                createdAt = System.currentTimeMillis() - 3600000L * 4
            )

            dao.insertDecision(sample1)
            dao.insertDecision(sample2)
            dao.insertDecision(sample3)
        }
    }
}
