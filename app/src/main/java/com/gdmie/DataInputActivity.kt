package com.gdmie

import com.gdmie.audio.GDMIEAudioManager
import android.app.Activity
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

class DataInputActivity : Activity() {

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

        // ---------------------------------------------------------
        // HERO
        // ---------------------------------------------------------

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val heroTitle = TextView(this).apply {
            text = "RUN GDM ENGINE"
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
            text = "Turn your situation into a clearer decision."
            textSize = 15f
            setTextColor(muted)
            setPadding(0, dp(7), 0, dp(18))
        }

        content.addView(intro)

        // ---------------------------------------------------------
        // DECISION INTRO CARD
        // ---------------------------------------------------------

        val decisionCard = glassCard()

        addCardTitle(
            decisionCard,
            "YOUR DECISION",
            "What are you deciding?",
            cyan
        )

        val helper = TextView(this).apply {
            text = "Describe your situation in your own words."
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

        // ---------------------------------------------------------
        // 01 CURRENT SITUATION
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "01",
            "CURRENT SITUATION",
            "How is the situation right now?",
            "currentSituation",
            arrayOf("Weak", "Uncertain", "Stable", "Strong"),
            blue
        )

        // ---------------------------------------------------------
        // 02 GOAL / OUTCOME
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "02",
            "GOAL / OUTCOME",
            "What outcome do you want?",
            "goalOutcome",
            arrayOf(
                "Small Improvement",
                "Moderate Improvement",
                "Major Improvement",
                "Long-Term Change"
            ),
            purple
        )

        // ---------------------------------------------------------
        // 03 CONTEXT
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "03",
            "CONTEXT",
            "What is affecting this decision?",
            "context",
            arrayOf(
                "Favourable",
                "Normal",
                "Uncertain",
                "Unfavourable"
            ),
            cyan
        )

        // ---------------------------------------------------------
        // 04 MOMENTUM
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "04",
            "MOMENTUM",
            "How is the situation changing?",
            "momentum",
            arrayOf(
                "Improving",
                "Stable",
                "Declining"
            ),
            green
        )

        // ---------------------------------------------------------
        // 05 RISK
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "05",
            "RISK",
            "How much risk are you comfortable with?",
            "risk",
            arrayOf(
                "Low",
                "Medium",
                "High"
            ),
            red
        )

        // ---------------------------------------------------------
        // 06 TIMING
        // ---------------------------------------------------------

        addChoiceSection(
            content,
            "06",
            "TIMING",
            "When do you need to decide?",
            "timing",
            arrayOf(
                "Now",
                "Soon",
                "Later"
            ),
            gold
        )

        // ---------------------------------------------------------
        // ENGINE CTA
        // ---------------------------------------------------------

        val runButton = TextView(this).apply {
            text = "⚡  RUN GDM ENGINE"
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
            text = "GDMIE helps you understand the situation. You make the final decision."
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

    // =============================================================
    // CHOICE SECTION
    // =============================================================

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
            LinearLayout.LayoutParams(
                dp(42),
                dp(32)
            )
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

        val rows = mutableListOf<LinearLayout>()

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
            rows.add(row)
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

                rows.add(row)
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

                GDMIEAudioManager.playUiClick(this@DataInputActivity)

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

    // =============================================================
    // ENGINE
    // =============================================================

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

        // 🎯 DECISION — tension audio
        GDMIEAudioManager.playSfx(
            this,
            R.raw.gdmie_audio_decision
        )

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
                        "%.0f%%".format(
                            result.confidence * 100
                        )
                    )
                    .apply()

                val intent = android.content.Intent(
                    this@DataInputActivity,
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

    // =============================================================
    // UI HELPERS
    // =============================================================

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
