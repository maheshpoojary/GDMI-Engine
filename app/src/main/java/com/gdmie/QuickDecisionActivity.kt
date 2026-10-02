package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import android.widget.TextView
import com.gdmie.adapter.ContextState
import com.gdmie.adapter.CurrentState
import com.gdmie.adapter.GDMIESimpleDecision
import com.gdmie.adapter.GDMIESimpleDecisionAdapter
import com.gdmie.adapter.GoalLevel
import com.gdmie.adapter.MomentumState
import com.gdmie.adapter.RiskLevel
import com.gdmie.adapter.TimingState
import com.gdmie.network.GDMIEEngineGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuickDecisionActivity : Activity() {

    private val bg = Color.rgb(4, 10, 22)
    private val card = Color.rgb(12, 22, 40)
    private val field = Color.rgb(18, 32, 55)
    private val cyan = Color.rgb(0, 220, 255)
    private val purple = Color.rgb(150, 80, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 160, 185)

    private lateinit var decisionInput: EditText

    private var situation = CurrentState.UNCERTAIN
    private var goal = GoalLevel.MODERATE_IMPROVEMENT
    private var timing = TimingState.NOW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        buildScreen()
    }

    private fun buildScreen() {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(24), dp(22), dp(30))
        }

        content.addView(
            TextView(this).apply {
                text = "⚡ FAST DECISION"
                textSize = 28f
                setTextColor(white)
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
        )

        content.addView(
            TextView(this).apply {
                text = "Make a decision. Get a clearer view in a few simple steps."
                textSize = 15f
                setTextColor(muted)
                setPadding(0, dp(8), 0, dp(22))
            }
        )

        val decisionCard = card()

        addTitle(decisionCard, "💭 WHAT ARE YOU DECIDING?", cyan)

        decisionInput = EditText(this).apply {
            hint = "Example: Should I take this new job?"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 16f
            gravity = Gravity.TOP
            minLines = 4
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(field, 16, Color.rgb(45, 75, 115), 1)
        }

        decisionCard.addView(
            decisionInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(130)
            )
        )

        val chipScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            setPadding(0, dp(10), 0, 0)
        }

        val chipRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val smartChoices = listOf(
            "💰 Investment" to "Should I invest in this opportunity?",
            "💼 Job Change" to "Should I take this new job?",
            "🛒 Purchase" to "Should I buy this product?",
            "🚗 Vehicle" to "Should I buy this vehicle?",
            "🏠 Home" to "Should I buy or move into this home?",
            "📱 Technology" to "Should I buy or upgrade this technology?",
            "🎯 Personal Goal" to "Should I pursue this personal goal?"
        )

        smartChoices.forEach { (label, example) ->
            val chip = TextView(this).apply {
                text = label
                textSize = 13f
                setTextColor(white)
                gravity = Gravity.CENTER
                setPadding(dp(14), dp(10), dp(14), dp(10))
                background = rounded(
                    field,
                    20,
                    Color.rgb(45, 75, 115),
                    1
                )
                setOnClickListener {
                    decisionInput.setText(example)
                    decisionInput.setSelection(decisionInput.text.length)
                }
            }

            chipRow.addView(
                chip,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    dp(42)
                ).apply {
                    setMargins(0, 0, dp(8), 0)
                }
            )
        }

        chipScroll.addView(chipRow)

        decisionCard.addView(chipScroll)

        content.addView(decisionCard)
        content.addView(space(16))

        prepareFastQuestions()

        val situationCard = card()
        addTitle(situationCard, "01  ${fastQuestion(fastSituationBases, fastSituationContexts, fastQuestionIndex)}", cyan)

        addOptions(
            situationCard,
            listOf(
                "Not going well" to CurrentState.WEAK,
                "I'm not sure" to CurrentState.UNCERTAIN,
                "It's okay" to CurrentState.STABLE,
                "Going well" to CurrentState.STRONG
            )
        ) { value ->
            situation = value as CurrentState
        }

        content.addView(situationCard)
        content.addView(space(16))

        val goalCard = card()
        addTitle(goalCard, "02  ${fastQuestion(fastGoalBases, fastGoalContexts, fastQuestionIndex)}", purple)

        addOptions(
            goalCard,
            listOf(
                "Small improvement" to GoalLevel.SMALL_IMPROVEMENT,
                "Better outcome" to GoalLevel.MODERATE_IMPROVEMENT,
                "Major improvement" to GoalLevel.MAJOR_IMPROVEMENT,
                "Long-term change" to GoalLevel.LONG_TERM_TRANSFORMATION
            )
        ) { value ->
            goal = value as GoalLevel
        }

        content.addView(goalCard)
        content.addView(space(16))

        val timingCard = card()
        addTitle(timingCard, "03  ${fastQuestion(fastTimingBases, fastTimingContexts, fastQuestionIndex)}", cyan)

        addOptions(
            timingCard,
            listOf(
                "Now" to TimingState.NOW,
                "Soon" to TimingState.SOON,
                "Later" to TimingState.LATER
            )
        ) { value ->
            timing = value as TimingState
        }

        content.addView(timingCard)
        content.addView(space(22))

        val runButton = Button(this).apply {
            text = "🧠 THINK WITH GDMIE"
            textSize = 14f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
            isAllCaps = false
            background = rounded(purple, 18, cyan, 1)
            elevation = dp(5).toFloat()

            setOnClickListener {
                GDMIEAudioManager.playSfx(
                    this@QuickDecisionActivity,
                    R.raw.gdmie_audio_decision
                )
                runQuickDecision()
            }
        }

        content.addView(
            runButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        content.addView(
            TextView(this).apply {
                text = "GDMIE supports your thinking. You make the final decision."
                textSize = 12f
                setTextColor(muted)
                gravity = Gravity.CENTER
                setPadding(dp(10), dp(16), dp(10), 0)
            }
        )

        scroll.addView(content)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)
    }


    // FAST_200_DYNAMIC_QUESTIONS
    // 200 unique questions for each FAST DECISION section.
    // One shared index keeps the three questions synchronized per session.

    private var fastQuestionIndex = 0

    private val fastSituationBases = listOf(
        "What best describes the situation right now?",
        "What is changing around you?",
        "How stable is the current situation?",
        "What needs your attention first?",
        "What is the strongest signal you can see?",
        "What part of the situation is uncertain?",
        "What is working well right now?",
        "Where is the main difficulty?",
        "What has changed most recently?",
        "What should you understand before acting?",
        "What is the current situation telling you?",
        "Which part of the situation needs closer attention?",
        "What is the most important thing happening now?",
        "What should you verify before moving forward?",
        "What is currently under the most pressure?",
        "What information matters most right now?",
        "What is the clearest sign of the current situation?",
        "Where do you see the biggest uncertainty?",
        "What should you assess before making your move?",
        "What deserves your attention before anything else?"
    )

    private val fastSituationContexts = listOf(
        "with the information available now",
        "before the next change occurs",
        "while the situation is still developing",
        "before committing to an action",
        "while keeping the bigger picture in mind",
        "before deciding what to prioritize",
        "while the current conditions remain uncertain",
        "before taking the next step",
        "while balancing speed and understanding",
        "before making a firm decision"
    )

    private val fastGoalBases = listOf(
        "What outcome matters most to you?",
        "What are you trying to improve?",
        "What would success look like here?",
        "What result are you aiming for?",
        "What should improve first?",
        "What is the most useful outcome?",
        "What would make this decision worthwhile?",
        "What change do you want to create?",
        "What should the final result achieve?",
        "What matters most when choosing your direction?",
        "What would you like to accomplish?",
        "Which outcome should receive the most attention?",
        "What result would move you forward?",
        "What would be a meaningful improvement?",
        "What should your decision ultimately support?",
        "What is the main result you want?",
        "What would make the situation better?",
        "What should your next move help achieve?",
        "Which result best matches your priority?",
        "What outcome should guide your decision?"
    )

    private val fastGoalContexts = listOf(
        "given your current situation",
        "without losing sight of what matters",
        "while keeping the decision practical",
        "with the available options",
        "while protecting the most important priority",
        "before the situation changes again",
        "while considering the effort required",
        "without creating unnecessary risk",
        "while keeping your longer-term direction in mind",
        "before choosing the final approach"
    )

    private val fastTimingBases = listOf(
        "When does this decision matter most?",
        "How quickly does this decision matter?",
        "When should the result start to matter?",
        "How urgent is the decision?",
        "When should you expect this to have an effect?",
        "What timing best fits this decision?",
        "When is the decision most relevant?",
        "How soon does action become important?",
        "When should you focus on the outcome?",
        "What time horizon matters here?",
        "When should you make the decision count?",
        "How much time do you have for this decision?",
        "When does waiting become less useful?",
        "When should the next step happen?",
        "What timing should guide your choice?",
        "When does this require your attention?",
        "How soon should the decision begin to matter?",
        "When is the right time horizon?",
        "What timing best matches the situation?",
        "When should you act on the decision?"
    )

    private val fastTimingContexts = listOf(
        "based on the current situation",
        "given the outcome you want",
        "before conditions change",
        "while you still have useful options",
        "considering the current pressure",
        "before more uncertainty appears",
        "while the opportunity remains available",
        "depending on how quickly the situation develops",
        "while protecting the desired outcome",
        "before deciding whether to wait"
    )

    private fun fastQuestion(
        bases: List<String>,
        contexts: List<String>,
        index: Int
    ): String {
        val base = bases[index % bases.size]
        val context = contexts[(index / bases.size) % contexts.size]
        return "$base $context."
    }

    private fun prepareFastQuestions() {
        var index = (System.currentTimeMillis() % 200L).toInt()

        if (index == fastQuestionIndex) {
            index = (index + 1) % 200
        }

        fastQuestionIndex = index
    }

    private fun runQuickDecision() {

        val decisionText = decisionInput.text.toString().trim()

        if (decisionText.isBlank()) {
            decisionInput.error = "Tell GDMIE what you are deciding."
            decisionInput.requestFocus()
            return
        }

        showThinkingScreen()

        val context = when (situation) {
            CurrentState.WEAK -> ContextState.UNFAVOURABLE
            CurrentState.UNCERTAIN -> ContextState.UNCERTAIN
            CurrentState.STABLE -> ContextState.NORMAL
            CurrentState.STRONG -> ContextState.FAVOURABLE
        }

        val momentum = when (situation) {
            CurrentState.WEAK -> MomentumState.DECLINING
            CurrentState.UNCERTAIN -> MomentumState.STABLE
            CurrentState.STABLE -> MomentumState.STABLE
            CurrentState.STRONG -> MomentumState.IMPROVING
        }

        val risk = when (goal) {
            GoalLevel.SMALL_IMPROVEMENT -> RiskLevel.LOW
            GoalLevel.MODERATE_IMPROVEMENT -> RiskLevel.MEDIUM
            GoalLevel.MAJOR_IMPROVEMENT -> RiskLevel.HIGH
            GoalLevel.LONG_TERM_TRANSFORMATION -> RiskLevel.MEDIUM
        }

        val simpleDecision = GDMIESimpleDecision(
            decisionText = decisionText,
            currentSituation = situation,
            goalOutcome = goal,
            context = context,
            momentum = momentum,
            risk = risk,
            timing = timing
        )

        val input = GDMIESimpleDecisionAdapter.toGDMInput(simpleDecision)

        CoroutineScope(Dispatchers.Main).launch {

            val result = GDMIEEngineGateway.calculate(input)

            val intent = Intent(
                this@QuickDecisionActivity,
                DecisionOutputActivity::class.java
            ).apply {
                putExtra("analysisId", System.currentTimeMillis())
                putExtra("source_mode", "FAST")
                putExtra("presentValue", result.presentValue.toFloat())
                putExtra("expectedValue", result.expectedValue.toFloat())
                putExtra("marketValue", result.marketValue.toFloat())
                putExtra("gap", result.gap)
                putExtra("momentum", result.momentum.toFloat())
                putExtra("risk", result.risk.toFloat())
                putExtra("edge", result.edge)
                putExtra("confidence", result.confidence.toFloat())
                putExtra("decision", result.decision)
                putExtra("explanation", result.explanation)
                putExtra("decisionText", decisionText)
            }

            startActivity(intent)
            finish()
        }
    }

    private fun showThinkingScreen() {

        val thinking = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(bg)
            setPadding(dp(30), dp(30), dp(30), dp(30))
        }

        thinking.addView(
            TextView(this).apply {
                text = "🧠"
                textSize = 52f
                gravity = Gravity.CENTER
            }
        )

        thinking.addView(
            TextView(this).apply {
                text = "GDMIE THINKING"
                textSize = 25f
                setTextColor(cyan)
                setTypeface(null, android.graphics.Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, dp(18), 0, dp(8))
            }
        )

        thinking.addView(
            TextView(this).apply {
                text = "Understanding your situation, goal and timing..."
                textSize = 14f
                setTextColor(muted)
                gravity = Gravity.CENTER
            }
        )

        setContentView(thinking)
    }

    private fun addOptions(
        parent: LinearLayout,
        options: List<Pair<String, Any>>,
        onSelect: (Any) -> Unit
    ) {

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        options.forEach { option ->

            val button = Button(this).apply {
                text = option.first
                textSize = 14f
                setTextColor(white)
                isAllCaps = false
                background = rounded(field, 15, Color.rgb(45, 65, 95), 1)
                setPadding(dp(8), 0, dp(8), 0)

                setOnClickListener {
                    GDMIEAudioManager.playUiClick(
                        this@QuickDecisionActivity
                    )
                    onSelect(option.second)
                    selectButton(this, container)
                }
            }

            container.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(52)
                ).apply {
                    bottomMargin = dp(8)
                }
            )
        }

        parent.addView(container)
    }

    private fun selectButton(selected: Button, parent: LinearLayout) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child is Button) {
                child.alpha = if (child == selected) 1f else 0.55f
            }
        }
        selected.alpha = 1f
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = rounded(card, 20, Color.rgb(38, 60, 90), 1)
            elevation = dp(3).toFloat()
        }
    }

    private fun addTitle(parent: LinearLayout, text: String, color: Int) {
        parent.addView(
            TextView(this).apply {
                this.text = text
                textSize = 13f
                setTextColor(color)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, dp(14))
            }
        )
    }

    private fun space(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        }
    }

    private fun rounded(
        fill: Int,
        radius: Int,
        stroke: Int,
        strokeWidth: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(strokeWidth), stroke)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
