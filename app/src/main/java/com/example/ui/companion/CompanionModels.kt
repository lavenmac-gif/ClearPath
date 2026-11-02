package com.example.ui.companion

/**
 * Clint (c:) — ClearPath's minimal, offline decision companion.
 * Inspired by the minimalist animated identity in the reference video:
 * A warm organic arc smile with expressive dot eyes that blinks, breathes,
 * consoling and encouraging the user whenever they feel low, overwhelmed, or stuck.
 */

enum class CompanionEmotion {
    IDLE,
    BLINK,
    HAPPY,
    COMFORTING,
    BREATHING,
    WINK
}

enum class FeelingState(val displayName: String, val shortBadge: String) {
    OVERWHELMED("Overwhelmed", "Too much input"),
    AFRAID_WRONG_CHOICE("Afraid to pick wrong", "Fear of regret"),
    EXHAUSTED("Mentally drained", "Need rest"),
    OVERTHINKING("Overthinking", "Looping thoughts"),
    PARALYZED("Paralyzed / Stuck", "Cannot start"),
    GENERAL_RESET("Need a breath", "Quick reset")
}

data class CompanionWisdom(
    val id: Int,
    val quote: String,
    val author: String,
    val feelingState: FeelingState,
    val consolation: String,
    val practicalAnchor: String
)

object OfflineCompanionRepository {

    val quotes: List<CompanionWisdom> = listOf(
        // OVERWHELMED
        CompanionWisdom(
            id = 1,
            quote = "You don't have to figure out your whole life today. Just the very next honest step.",
            author = "ClearPath Wisdom",
            feelingState = FeelingState.OVERWHELMED,
            consolation = "Feeling overwhelmed usually means you are asking today to carry too many tomorrows. Put the rest down for an hour.",
            practicalAnchor = "Pick just one small thing you can touch or decide right now. The rest can wait."
        ),
        CompanionWisdom(
            id = 2,
            quote = "Simplicity is the ultimate sophistication.",
            author = "Leonardo da Vinci",
            feelingState = FeelingState.OVERWHELMED,
            consolation = "When there are too many variables, the answer isn't more thinking—it's subtracting what doesn't truly matter.",
            practicalAnchor = "What is the single priority that makes everything else secondary?"
        ),
        CompanionWisdom(
            id = 3,
            quote = "Almost everything will work again if you unplug it for a few minutes, including you.",
            author = "Anne Lamott",
            feelingState = FeelingState.OVERWHELMED,
            consolation = "Your brain is not failing; it is simply full. A 10-minute break changes the biology of decision-making.",
            practicalAnchor = "Stand up, drink a glass of water, and give yourself permission to be undecided for 15 minutes."
        ),
        CompanionWisdom(
            id = 4,
            quote = "It is not a daily increase, but a daily decrease. Hack away at the unessential.",
            author = "Bruce Lee",
            feelingState = FeelingState.OVERWHELMED,
            consolation = "You don't need more options or advice. You need permission to ignore 80% of the noise.",
            practicalAnchor = "Cross off any option or detail that is merely nice-to-have."
        ),

        // AFRAID TO PICK WRONG
        CompanionWisdom(
            id = 5,
            quote = "In any moment of decision, the best thing you can do is the right thing, the next best thing is the wrong thing, and the worst thing you can do is nothing.",
            author = "Theodore Roosevelt",
            feelingState = FeelingState.AFRAID_WRONG_CHOICE,
            consolation = "A choice that proves imperfect gives you data. Remaining frozen only gives you chronic anxiety.",
            practicalAnchor = "Most decisions are two-way doors. If it doesn't work, you can turn around and adjust."
        ),
        CompanionWisdom(
            id = 6,
            quote = "You do not have to be good. You only have to let the soft animal of your body love what it loves.",
            author = "Mary Oliver",
            feelingState = FeelingState.AFRAID_WRONG_CHOICE,
            consolation = "There is no cosmic exam you are failing. There are only paths, each with its own terrain.",
            practicalAnchor = "Notice which option gives your chest a slight feeling of relief."
        ),
        CompanionWisdom(
            id = 7,
            quote = "We suffer more often in imagination than in reality.",
            author = "Seneca",
            feelingState = FeelingState.AFRAID_WRONG_CHOICE,
            consolation = "The catastrophe you are rehearsing in your mind is almost never what actually happens. And even if friction occurs, you adapt.",
            practicalAnchor = "Ask yourself: what is the worst realistic outcome, and could I recover from it in 3 months? (The answer is almost always yes)."
        ),
        CompanionWisdom(
            id = 8,
            quote = "Doubt is not a pleasant condition, but certainty is an absurd one.",
            author = "Voltaire",
            feelingState = FeelingState.AFRAID_WRONG_CHOICE,
            consolation = "Waiting for 100% certainty is just fear wearing a mask of prudence. 70% certainty is plenty to act.",
            practicalAnchor = "Accept that no choice is frictionless. Pick the set of problems you are willing to solve."
        ),

        // EXHAUSTED / LOW ENERGY
        CompanionWisdom(
            id = 9,
            quote = "Never make a permanent decision based on a temporary exhaustion.",
            author = "ClearPath Wisdom",
            feelingState = FeelingState.EXHAUSTED,
            consolation = "When you are tired, every option looks like a mountain. Rest first; the problem will shrink by morning.",
            practicalAnchor = "Close this screen, sleep or rest, and review this with morning eyes."
        ),
        CompanionWisdom(
            id = 10,
            quote = "Rest is not idleness, and to lie sometimes on the grass under trees... is by no means a waste of time.",
            author = "John Lubbock",
            feelingState = FeelingState.EXHAUSTED,
            consolation = "Your subconscious continues integrating information while you rest. Stepping back is active progress.",
            practicalAnchor = "Set a 'Decide Later' reminder for 24 hours from now and give yourself rest today."
        ),
        CompanionWisdom(
            id = 11,
            quote = "Be gentle with yourself. You are doing the best you can with the clarity you currently have.",
            author = "ClearPath Companion",
            feelingState = FeelingState.EXHAUSTED,
            consolation = "You have navigated every difficult crossroads in your life so far. You will navigate this one too.",
            practicalAnchor = "Place a hand over your chest, breathe out deeply, and soften your shoulders."
        ),

        // OVERTHINKING / LOOPING THOUGHTS
        CompanionWisdom(
            id = 12,
            quote = "Action cures fear. Inaction creates terror.",
            author = "Douglas Horton",
            feelingState = FeelingState.OVERTHINKING,
            consolation = "Thinking will not solve problems that require movement. A small test will teach you more than a month of ruminating.",
            practicalAnchor = "Design a 48-hour micro-experiment to test one assumption without full commitment."
        ),
        CompanionWisdom(
            id = 13,
            quote = "Rule your mind or it will rule you.",
            author = "Horace",
            feelingState = FeelingState.OVERTHINKING,
            consolation = "Your thoughts are just suggestions, not prophecies. You don't have to attend every argument your mind throws at you.",
            practicalAnchor = "Separate the cold facts from your narrative assumptions."
        ),
        CompanionWisdom(
            id = 14,
            quote = "The greatest weapon against stress is our ability to choose one thought over another.",
            author = "William James",
            feelingState = FeelingState.OVERTHINKING,
            consolation = "When thoughts loop, they aren't looking for answers—they are looking for reassurance. You can choose to step out of the loop.",
            practicalAnchor = "Write the two options down on paper and walk away for 30 minutes."
        ),

        // PARALYZED / CANNOT START
        CompanionWisdom(
            id = 15,
            quote = "The journey of a thousand miles begins with a single step.",
            author = "Lao Tzu",
            feelingState = FeelingState.PARALYZED,
            consolation = "You don't need to commit to the entire summit today. You only need to lace up your shoes.",
            practicalAnchor = "What is a 5-minute action you can take right now toward either path?"
        ),
        CompanionWisdom(
            id = 16,
            quote = "Start where you are. Use what you have. Do what you can.",
            author = "Arthur Ashe",
            feelingState = FeelingState.PARALYZED,
            consolation = "Perfect conditions never arrive. Clarity comes from engagement, not from contemplation.",
            practicalAnchor = "Flip a coin. While it's in the air, notice what you secretly hope it lands on."
        ),

        // GENERAL RESET / BREATH
        CompanionWisdom(
            id = 17,
            quote = "Smile, breathe and go slowly.",
            author = "Thích Nhất Hạnh",
            feelingState = FeelingState.GENERAL_RESET,
            consolation = "There is no rush in this exact breath. You are safe right now.",
            practicalAnchor = "Inhale for 4 seconds, hold for 4, exhale for 4. Feel your feet on the floor."
        ),
        CompanionWisdom(
            id = 18,
            quote = "Peace is the result of retraining your mind to process life as it is, rather than as you think it should be.",
            author = "Wayne Dyer",
            feelingState = FeelingState.GENERAL_RESET,
            consolation = "Whatever choice you make, you will bring yourself to it. Trust your future self to adapt.",
            practicalAnchor = "You are resilient enough to handle either outcome."
        )
    )

    fun getWisdomForFeeling(state: FeelingState): CompanionWisdom {
        val matches = quotes.filter { it.feelingState == state }
        return matches.randomOrNull() ?: quotes.first()
    }

    fun getRandomDailyWisdom(): CompanionWisdom {
        return quotes.random()
    }
}
