package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class QuickDecisionFullActivity : Activity() {
    // QUICK_1200_DYNAMIC_QUESTIONS
    // 6 sections × 20 bases × 10 contexts = 1,200 unique questions.
    private var quickQuestionIndex = 0

    private val quickSituationBases = arrayOf(
        "How is the situation right now",
        "What is your current position",
        "Where do things stand at the moment",
        "What best describes the situation today",
        "How would you describe your present situation",
        "What is happening with this decision right now",
        "How stable is the situation at present",
        "What state is this decision currently in",
        "How are things developing right now",
        "What is the current condition you are facing",
        "How clear is the situation at this point",
        "What is the situation looking like today",
        "Where are you starting from with this decision",
        "How would you describe where things are now",
        "What is your present situation telling you",
        "How is this decision positioned right now",
        "What is the current state of affairs",
        "How are things standing before you decide",
        "What is the situation like as you begin",
        "How would you summarise the situation right now"
    )

    private val quickSituationContexts = arrayOf(
        "before you make this choice",
        "as you look at the decision",
        "based on what you know today",
        "with the information available",
        "before taking the next step",
        "at this stage of the decision",
        "given the circumstances around you",
        "as you assess your starting point",
        "before anything changes",
        "from your current perspective"
    )

    private val quickGoalBases = arrayOf(
        "What outcome do you want",
        "What are you trying to achieve",
        "What result would you like",
        "Where do you want this decision to lead",
        "What would a better result look like",
        "What is the main result you are aiming for",
        "What change do you want from this decision",
        "What would you like to improve",
        "What destination are you aiming toward",
        "What result matters most to you",
        "What would make this decision successful",
        "What are you hoping to accomplish",
        "What should this decision help you achieve",
        "What improvement are you looking for",
        "What result would give you the most value",
        "What would you like to see happen",
        "What is the outcome you care about most",
        "What should happen if this decision works",
        "What direction do you want the result to take",
        "What would you consider a meaningful outcome"
    )

    private val quickGoalContexts = arrayOf(
        "from this decision",
        "over the next stage",
        "given your current situation",
        "before moving forward",
        "with the result you have in mind",
        "based on what matters to you",
        "as you consider your next step",
        "within the time you have",
        "while keeping the bigger picture in mind",
        "before you commit to a direction"
    )

    private val quickContextBases = arrayOf(
        "What is affecting this decision",
        "What factors are shaping this choice",
        "What is influencing your decision",
        "What should you consider before deciding",
        "What is changing the way you see this choice",
        "What outside factors matter here",
        "What information is influencing your thinking",
        "What circumstances are important to this decision",
        "What is creating pressure around this choice",
        "What conditions should you keep in mind",
        "What details could affect your choice",
        "What is shaping the situation around you",
        "What should not be overlooked here",
        "What surrounding factors could change the outcome",
        "What matters beyond the obvious choice",
        "What current conditions are influencing you",
        "What should you weigh before moving ahead",
        "What part of the situation needs more attention",
        "What is most relevant to this decision",
        "What context could change your next move"
    )

    private val quickContextContexts = arrayOf(
        "before you choose an option",
        "as you assess the situation",
        "with the current information",
        "before taking action",
        "while comparing your choices",
        "given everything happening around you",
        "as you think through the decision",
        "before committing to a direction",
        "while considering the possible outcome",
        "at this stage of the decision"
    )

    private val quickMomentumBases = arrayOf(
        "How is the situation changing",
        "Is the situation moving in a better direction",
        "What direction is the situation taking",
        "How is progress developing",
        "What is the current momentum telling you",
        "Are things improving or slowing down",
        "How quickly is the situation changing",
        "What trend are you seeing right now",
        "Is the current direction becoming stronger",
        "How is progress behaving at this point",
        "What pattern is developing over time",
        "How are things moving compared with before",
        "Is the situation gaining or losing momentum",
        "What direction does recent progress suggest",
        "How consistent is the current progress",
        "What is changing most noticeably right now",
        "Is the situation becoming more stable",
        "How would you describe the current trend",
        "What does the recent movement suggest",
        "How is the decision environment evolving"
    )

    private val quickMomentumContexts = arrayOf(
        "as you prepare to decide",
        "compared with where you started",
        "based on recent changes",
        "over the latest stage",
        "with the current trend in mind",
        "before making your next move",
        "given what has changed recently",
        "as you look ahead",
        "from the progress you can see",
        "at the moment you need to decide"
    )

    private val quickRiskBases = arrayOf(
        "How much risk are you comfortable with",
        "How much uncertainty can you accept",
        "What level of risk feels acceptable",
        "How cautious do you want to be",
        "How much downside can you tolerate",
        "What risk level fits this decision",
        "How strongly do you want to protect against a setback",
        "How much uncertainty are you willing to take",
        "What balance between safety and opportunity feels right",
        "How much exposure are you comfortable taking",
        "How carefully do you want to approach this choice",
        "What amount of risk can you reasonably handle",
        "How much room for error do you have",
        "How important is limiting downside here",
        "How aggressive or cautious should this decision be",
        "What level of uncertainty works for you",
        "How much potential loss can you accept",
        "How much protection do you want before acting",
        "What risk boundary makes sense for you",
        "How comfortable are you with an uncertain outcome"
    )

    private val quickRiskContexts = arrayOf(
        "before you commit",
        "given the possible outcomes",
        "with your current situation",
        "as you compare the choices",
        "before taking the next step",
        "while protecting your desired outcome",
        "based on what could go wrong",
        "as you weigh opportunity against caution",
        "with the information you have now",
        "before deciding how far to go"
    )

    private val quickTimingBases = arrayOf(
        "When do you need to decide",
        "How soon does this decision matter",
        "When should you act on this choice",
        "What timing fits this decision",
        "How urgent is the decision",
        "When would action matter most",
        "What is the right time to decide",
        "How much time do you have before acting",
        "When does waiting become less useful",
        "When should the next step happen",
        "How quickly does this need your attention",
        "What time horizon are you working with",
        "When would you need a clear direction",
        "How soon must you choose a path",
        "When is this decision most relevant",
        "What timing pressure exists here",
        "When would delaying affect the outcome",
        "How much time should you allow before deciding",
        "What point in time matters for this choice",
        "When do you expect to act on the decision"
    )

    private val quickTimingContexts = arrayOf(
        "based on what you know now",
        "given the situation today",
        "before the opportunity changes",
        "with your current goal in mind",
        "as you consider the next step",
        "while the current conditions remain",
        "before more information arrives",
        "as you balance speed and caution",
        "with the outcome you want",
        "before the situation moves again"
    )

    private fun quickQuestion(
        bases: Array<String>,
        contexts: Array<String>
    ): String {
        val baseIndex = quickQuestionIndex % bases.size
        val contextIndex = quickQuestionIndex / bases.size
        return "${bases[baseIndex]} ${contexts[contextIndex]}?"
    }

    private fun prepareQuickQuestions() {
        var index = (System.currentTimeMillis() % 200L).toInt()

        if (index == quickQuestionIndex) {
            index = (index + 1) % 200
        }

        quickQuestionIndex = index
    }



    private val choices = mutableMapOf<String, Int>()
    private val choiceViews = mutableMapOf<String, MutableList<TextView>>()
    private lateinit var decisionInput: EditText

    private val bg = Color.rgb(4, 8, 18)
    private val panel = Color.rgb(12, 20, 34)
    private val panel2 = Color.rgb(17, 28, 46)
    private val cyan = Color.rgb(0, 220, 255)
    private val blue = Color.rgb(55, 145, 255)
    private val purple = Color.rgb(145, 80, 255)
    private val gold = Color.rgb(245, 190, 70)
    private val green = Color.rgb(45, 215, 135)
    private val red = Color.rgb(245, 85, 95)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 160, 185)
    private val dim = Color.rgb(82, 100, 125)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareQuickQuestions()

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(32))
        }

        scroll.addView(content)

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val heroTitle = TextView(this).apply {
            text = "🧠 QUICK DECISION"
            textSize = 29f
            setTextColor(white)
            setTypeface(null, Typeface.BOLD)
            setShadowLayer(dp(12).toFloat(), 0f, 0f, cyan)
        }

        topRow.addView(
            heroTitle,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val version = TextView(this).apply {
            text = "GDMIE"
            textSize = 11f
            setTextColor(cyan)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedStroke(Color.TRANSPARENT, cyan, 10)
            setPadding(dp(10), dp(5), dp(10), dp(5))
        }

        topRow.addView(version)

        content.addView(
            topRow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val intro = TextView(this).apply {
            text = "Make a structured decision with deeper context."
            textSize = 15f
            setTextColor(muted)
            setPadding(0, dp(7), 0, dp(18))
        }

        content.addView(intro)

        val decisionCard = glassCard()

        addCardTitle(
            decisionCard,
            "YOUR DECISION",
            "What are you deciding?",
            cyan
        )

        val helper = TextView(this).apply {
            text = "Describe your decision in your own words."
            textSize = 13f
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(8))
        }

        decisionCard.addView(helper)

        decisionInput = EditText(this).apply {
            hint = "Your Decision"
            setHintTextColor(dim)
            setTextColor(white)
            textSize = 16f
            gravity = Gravity.TOP or Gravity.START
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 3
            maxLines = 5
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = rounded(panel2, 14)
        }

        val cosmosPrefill =
            intent.getStringExtra("cosmos_question").orEmpty()

        if (cosmosPrefill.isNotBlank()) {
            decisionInput.setText(cosmosPrefill)
            decisionInput.setSelection(decisionInput.text.length)
        }

        decisionCard.addView(
            decisionInput,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(96)
            )
        )

        content.addView(
            decisionCard,
            marginParams(0, 0, 0, 14)
        )

        addChoiceSection(
            content,
            "01",
            "CURRENT SITUATION",
            quickQuestion(quickSituationBases, quickSituationContexts),
            "currentSituation",
            arrayOf("Weak", "Uncertain", "Stable", "Strong"),
            blue
        )

        addChoiceSection(
            content,
            "02",
            "GOAL / OUTCOME",
            quickQuestion(quickGoalBases, quickGoalContexts),
            "goalOutcome",
            arrayOf(
                "Small Improvement",
                "Moderate Improvement",
                "Major Improvement",
                "Long-Term Change"
            ),
            purple
        )

        addChoiceSection(
            content,
            "03",
            "CONTEXT",
            quickQuestion(quickContextBases, quickContextContexts),
            "context",
            arrayOf(
                "Favourable",
                "Normal",
                "Uncertain",
                "Unfavourable"
            ),
            cyan
        )

        addChoiceSection(
            content,
            "04",
            "MOMENTUM",
            quickQuestion(quickMomentumBases, quickMomentumContexts),
            "momentum",
            arrayOf(
                "Improving",
                "Stable",
                "Declining"
            ),
            green
        )

        addChoiceSection(
            content,
            "05",
            "RISK",
            quickQuestion(quickRiskBases, quickRiskContexts),
            "risk",
            arrayOf(
                "Low",
                "Medium",
                "High"
            ),
            red
        )

        addChoiceSection(
            content,
            "06",
            "TIMING",
            quickQuestion(quickTimingBases, quickTimingContexts),
            "timing",
            arrayOf(
                "Now",
                "Soon",
                "Later"
            ),
            gold
        )

        val runButton = TextView(this).apply {
            text = "🧠  RUN QUICK DECISION"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(cyan, 18)
            elevation = dp(8).toFloat()
            setPadding(0, dp(4), 0, dp(4))
            isClickable = true
            isFocusable = true

            setOnClickListener {
                calculateAndReturn()
            }
        }

        content.addView(
            runButton,
            marginParams(
                0,
                12,
                0,
                18,
                dp(60)
            )
        )

        val footer = TextView(this).apply {
            text = "GDMIE supports your thinking. You make the final decision."
            textSize = 12f
            setTextColor(dim)
            gravity = Gravity.CENTER
            setPadding(dp(10), 0, dp(10), dp(10))
        }

        content.addView(footer)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun addChoiceSection(
        parent: LinearLayout,
        number: String,
        title: String,
        question: String,
        key: String,
        items: Array<String>,
        accent: Int
    ) {
        val card = glassCard()

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val numberView = TextView(this).apply {
            text = number
            textSize = 11f
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedStroke(Color.TRANSPARENT, accent, 10)
            setPadding(dp(9), dp(5), dp(9), dp(5))
        }

        header.addView(
            numberView,
            LinearLayout.LayoutParams(dp(42), dp(32))
        )

        val titleView = TextView(this).apply {
            text = title
            textSize = 13f
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
            setPadding(dp(10), 0, 0, 0)
        }

        header.addView(
            titleView,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        card.addView(header)

        val questionView = TextView(this).apply {
            text = question
            textSize = 14f
            setTextColor(muted)
            setPadding(dp(2), dp(9), dp(2), dp(10))
        }

        card.addView(questionView)

        val grid = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        card.addView(grid)

        val views = mutableListOf<TextView>()
        choiceViews[key] = views
        choices[key] = -1

        if (items.size <= 3) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            items.forEachIndexed { index, item ->
                val option = createOption(item, key, index, accent)

                val lp = LinearLayout.LayoutParams(
                    0,
                    dp(50),
                    1f
                )

                if (index > 0) {
                    lp.leftMargin = dp(6)
                }

                row.addView(option, lp)
                views.add(option)
            }

            grid.addView(row)
        } else {
            var index = 0

            while (index < items.size) {
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                }

                val first = createOption(
                    items[index],
                    key,
                    index,
                    accent
                )

                row.addView(
                    first,
                    LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1f
                    )
                )

                views.add(first)

                if (index + 1 < items.size) {
                    val second = createOption(
                        items[index + 1],
                        key,
                        index + 1,
                        accent
                    )

                    val secondLp = LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1f
                    )

                    secondLp.leftMargin = dp(6)

                    row.addView(second, secondLp)
                    views.add(second)
                }

                grid.addView(
                    row,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(50)
                    ).apply {
                        if (index > 0) {
                            topMargin = dp(6)
                        }
                    }
                )

                index += 2
            }
        }

        parent.addView(
            card,
            marginParams(0, 0, 0, 12)
        )
    }

    private fun createOption(
        text: String,
        key: String,
        index: Int,
        accent: Int
    ): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 13f
            setTextColor(white)
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            background = roundedStroke(
                panel2,
                Color.rgb(45, 60, 82),
                13
            )
            elevation = dp(2).toFloat()
            isClickable = true
            isFocusable = true

            setOnClickListener {
                choices[key] = index

                GDMIEAudioManager.playUiClick(this@QuickDecisionFullActivity)

                val list = choiceViews[key] ?: return@setOnClickListener

                list.forEachIndexed { i, view ->
                    if (i == index) {
                        view.background = rounded(accent, 13)
                        view.setTextColor(bg)
                    } else {
                        view.background = roundedStroke(
                            panel2,
                            Color.rgb(45, 60, 82),
                            13
                        )
                        view.setTextColor(white)
                    }
                }
            }
        }
    }

    private fun calculateAndReturn() {

        val decisionText =
            decisionInput.text?.toString()?.trim().orEmpty()

        if (decisionText.isBlank()) {
            decisionInput.requestFocus()

            Toast.makeText(
                this,
                "Please describe what you are deciding.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (!validateChoices()) {
            return
        }

        val currentSituation =
            when (choices["currentSituation"] ?: -1) {
                0 -> CurrentState.WEAK
                1 -> CurrentState.UNCERTAIN
                2 -> CurrentState.STABLE
                else -> CurrentState.STRONG
            }

        val goalOutcome =
            when (choices["goalOutcome"] ?: -1) {
                0 -> GoalLevel.SMALL_IMPROVEMENT
                1 -> GoalLevel.MODERATE_IMPROVEMENT
                2 -> GoalLevel.MAJOR_IMPROVEMENT
                else -> GoalLevel.LONG_TERM_TRANSFORMATION
            }

        val context =
            when (choices["context"] ?: -1) {
                0 -> ContextState.FAVOURABLE
                1 -> ContextState.NORMAL
                2 -> ContextState.UNCERTAIN
                else -> ContextState.UNFAVOURABLE
            }

        val momentum =
            when (choices["momentum"] ?: -1) {
                0 -> MomentumState.IMPROVING
                1 -> MomentumState.STABLE
                else -> MomentumState.DECLINING
            }

        val risk =
            when (choices["risk"] ?: -1) {
                0 -> RiskLevel.LOW
                1 -> RiskLevel.MEDIUM
                else -> RiskLevel.HIGH
            }

        val timing =
            when (choices["timing"] ?: -1) {
                0 -> TimingState.NOW
                1 -> TimingState.SOON
                else -> TimingState.LATER
            }

        val decision = GDMIESimpleDecision(
            decisionText = decisionText,
            currentSituation = currentSituation,
            goalOutcome = goalOutcome,
            context = context,
            momentum = momentum,
            risk = risk,
            timing = timing
        )

        val input =
            GDMIESimpleDecisionAdapter.toGDMInput(decision)

        CoroutineScope(Dispatchers.IO).launch {

            val result =
                GDMIEEngineGateway.calculate(input)

            runOnUiThread {

                getSharedPreferences(
                    "GDMIE_HOME",
                    MODE_PRIVATE
                )
                    .edit()
                    .putString(
                        "edge",
                        "%.2f".format(result.edge)
                    )
                    .putString(
                        "momentum",
                        "%.2f".format(result.momentum)
                    )
                    .putString(
                        "risk",
                        "%.2f".format(result.risk)
                    )
                    .putString(
                        "confidence",
                        "%.0f%%".format(result.confidence * 100)
                    )
                    .apply()

                val intent = Intent(
                    this@QuickDecisionFullActivity,
                    DecisionOutputActivity::class.java
                )

                intent.putExtra(
                    "presentValue",
                    result.presentValue
                )
                intent.putExtra(
                    "expectedValue",
                    result.expectedValue
                )
                intent.putExtra(
                    "marketValue",
                    result.marketValue
                )
                intent.putExtra(
                    "gap",
                    result.gap
                )
                intent.putExtra(
                    "momentum",
                    result.momentum
                )
                intent.putExtra(
                    "risk",
                    result.risk
                )
                intent.putExtra(
                    "edge",
                    result.edge
                )
                intent.putExtra(
                    "confidence",
                    result.confidence.toFloat()
                )
                intent.putExtra(
                    "decision",
                    result.decision
                )
                intent.putExtra(
                    "explanation",
                    result.explanation
                )
                intent.putExtra(
                    "decisionText",
                    decisionText
                )
                intent.putExtra(
                    "source_mode",
                    "QUICK"
                )
                intent.putExtra(
                    "analysisId",
                    System.currentTimeMillis()
                )

                startActivity(intent)
            }
        }
    }

    private fun validateChoices(): Boolean {

        val keys = arrayOf(
            "currentSituation",
            "goalOutcome",
            "context",
            "momentum",
            "risk",
            "timing"
        )

        for (key in keys) {

            if ((choices[key] ?: -1) < 0) {

                val message = when (key) {
                    "currentSituation" ->
                        "Please select your current situation."

                    "goalOutcome" ->
                        "Please select your goal / outcome."

                    "context" ->
                        "Please select the context."

                    "momentum" ->
                        "Please select the current momentum."

                    "risk" ->
                        "Please select your risk comfort."

                    "timing" ->
                        "Please select when you need to decide."

                    else ->
                        "Please complete all sections."
                }

                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_SHORT
                ).show()

                return false
            }
        }

        return true
    }

    private fun glassCard(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(16),
                dp(15),
                dp(16),
                dp(16)
            )
            background = roundedStroke(
                panel,
                Color.rgb(28, 48, 72),
                20
            )
            elevation = dp(3).toFloat()
        }
    }

    private fun addCardTitle(
        card: LinearLayout,
        title: String,
        question: String,
        accent: Int
    ) {
        val titleView = TextView(this).apply {
            text = title
            textSize = 12f
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
        }

        card.addView(titleView)

        val questionView = TextView(this).apply {
            text = question
            textSize = 19f
            setTextColor(white)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, dp(6), 0, dp(3))
        }

        card.addView(questionView)
    }

    private fun rounded(
        color: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun roundedStroke(
        fill: Int,
        stroke: Int,
        radius: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fill)
            setStroke(dp(1), stroke)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun marginParams(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        ).apply {
            this.leftMargin = dp(left)
            this.topMargin = dp(top)
            this.rightMargin = dp(right)
            this.bottomMargin = dp(bottom)
        }
    }

    private fun dp(value: Int): Int {
        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }
}
