package com.gdmie

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.gdmie.audio.GDMIEAudioManager
import com.gdmie.game.GDMIEGameProgress
import kotlin.math.abs

class DecisionOutputActivity : Activity() {

    private val bg = Color.rgb(4, 8, 18)
    private val panel = Color.rgb(12, 20, 34)
    private val panel2 = Color.rgb(17, 28, 46)
    private val cyan = Color.rgb(0, 220, 255)
    private val blue = Color.rgb(55, 145, 255)
    private val purple = Color.rgb(145, 80, 255)
    private val green = Color.rgb(45, 215, 135)
    private val red = Color.rgb(245, 85, 95)
    private val gold = Color.rgb(245, 190, 70)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 160, 185)
    private val dim = Color.rgb(82, 100, 125)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val confidence =
            intent.getFloatExtra("confidence", 0f)

        val momentum =
            intent.getFloatExtra("momentum", 50f)

        val risk =
            intent.getFloatExtra("risk", 50f)

        val decision =
            intent.getStringExtra("decision")
                .orEmpty()

        val decisionText =
            intent.getStringExtra("decisionText")
                .orEmpty()

        val analysisId =
            intent.getLongExtra("analysisId", 0L)

        // Internal values are intentionally read only for
        // safe interpretation. They are NEVER displayed.
        val edge =
            intent.getFloatExtra("edge", 0f)
        // ---------------------------------------------------------
        // SAVE ANALYSIS TO DECISION HISTORY
        // ---------------------------------------------------------
        val historyPrefs =
            getSharedPreferences("GDMIE_HISTORY", MODE_PRIVATE)

        val historyCount =
            historyPrefs.getInt("count", 0)

        val analysisIndex =
            historyCount + 1

        historyPrefs.edit()
            .putInt("count", analysisIndex)
            .putInt("last_index", analysisIndex)
            .putString(
                "analysis_id_$analysisIndex",
                analysisId.toString()
            )
            .putString(
                "time_$analysisIndex",
                java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())
            )
            .putString(
                "decision_$analysisIndex",
                decision
            )
            .putString(
                "edge_$analysisIndex",
                "%.2f".format(edge)
            )
            .putString(
                "confidence_$analysisIndex",
                "%.0f%%".format(confidence * 100)
            )
            .putString(
                "momentum_$analysisIndex",
                "%.2f".format(momentum)
            )
            .putString(
                "risk_$analysisIndex",
                "%.2f".format(risk)
            )
            .putString(
                "gap_$analysisIndex",
                "%.2f".format(
                    intent.getDoubleExtra("gap", 0.0)
                )
            )
            .putString(
                "expected_$analysisIndex",
                "%.2f".format(
                    intent.getDoubleExtra("expectedValue", 0.0)
                )
            )
            .putString(
                "decision_text_$analysisIndex",
                decisionText
            )
            .apply()

        // 🧠 ANALYSIS — reveal audio
        GDMIEAudioManager.playSfx(
            this,
            R.raw.gdmie_audio_analysis
        )

        // Analysis XP: +5 once for this analysis.
        if (intent.getStringExtra("source_mode").orEmpty() != "FAST") {
            val xpResult = GDMIEGameProgress.addAnalysisXpOnce(
                this,
                analysisIndex,
                5
            )

            // ⚡ XP reward sound — only when XP is actually awarded
            if (xpResult.awardedXp > 0) {
                GDMIEAudioManager.playSfx(
                    this,
                    R.raw.gdmie_audio_xp
                )
            }
        }


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
            setPadding(
                dp(20),
                dp(20),
                dp(20),
                dp(32)
            )
        }

        scroll.addView(content)

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        val header = TextView(this).apply {
            text = "LIVE SIGNAL"
            textSize = 29f
            setTextColor(white)
            setTypeface(null, Typeface.BOLD)
            setShadowLayer(
                dp(12).toFloat(),
                0f,
                0f,
                cyan
            )
        }

        content.addView(header)

        val sub = TextView(this).apply {
            text = "A clearer view of your current situation."
            textSize = 14f
            setTextColor(muted)
            setPadding(
                0,
                dp(6),
                0,
                dp(18)
            )
        }

        content.addView(sub)

        // ---------------------------------------------------------
        // SIGNAL
        // ---------------------------------------------------------

        val signalCard = glassCard()

        addSmallTitle(
            signalCard,
            "SIGNAL",
            cyan
        )

        val signalText =
            safeSignal(decision, edge)

        val signalColor = when (signalText) {
            "POSITIVE SIGNAL" -> green
            "CAUTION SIGNAL" -> red
            else -> gold
        }

        val signal = TextView(this).apply {
            text = signalText
            textSize = 25f
            setTypeface(null, Typeface.BOLD)
            setTextColor(signalColor)
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            paint.setShadowLayer(dp(10).toFloat(), 0f, 0f, signalColor)
            setPadding(
                0,
                dp(8),
                0,
                dp(2)
            )
        }

        signalCard.addView(signal)

        ObjectAnimator.ofFloat(signal, "alpha", 0.72f, 1f).apply {
            duration = 900L
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
            start()
        }

        val signalHint = TextView(this).apply {
            text = "This is a decision signal, not a command."
            textSize = 12f
            setTextColor(dim)
        }

        signalCard.addView(signalHint)

        content.addView(
            signalCard,
            marginParams(0, 0, 0, 12)
        )

        // ---------------------------------------------------------
        // CONFIDENCE
        // ---------------------------------------------------------

        val confidenceCard = glassCard()

        addSmallTitle(
            confidenceCard,
            "CONFIDENCE",
            purple
        )

        val confidenceValue =
            (confidence * 100f)
                .coerceIn(0f, 100f)

        val confidenceView = TextView(this).apply {
            text = "%.0f%%".format(
                confidenceValue
            )
            textSize = 32f
            setTextColor(white)
            setTypeface(null, Typeface.BOLD)
            setPadding(
                0,
                dp(6),
                0,
                dp(4)
            )
        }

        confidenceCard.addView(confidenceView)

        val confidenceHint = TextView(this).apply {
            text = confidenceDescription(
                confidenceValue
            )
            textSize = 13f
            setTextColor(muted)
        }

        confidenceCard.addView(confidenceHint)

        content.addView(
            confidenceCard,
            marginParams(0, 0, 0, 12)
        )

        // ---------------------------------------------------------
        // CURRENT SITUATION
        // ---------------------------------------------------------

        val situationCard = glassCard()

        addSmallTitle(
            situationCard,
            "CURRENT SITUATION",
            blue
        )

        val situationSummary =
            buildSituationSummary(
                momentum,
                risk
            )

        situationCard.addView(
            bodyText(situationSummary)
        )

        content.addView(
            situationCard,
            marginParams(0, 0, 0, 12)
        )

        // ---------------------------------------------------------
        // KEY FACTORS
        // ---------------------------------------------------------

        val factorsCard = glassCard()

        addSmallTitle(
            factorsCard,
            "KEY FACTORS",
            cyan
        )

        factorsCard.addView(
            factorRow(
                "Current situation",
                situationLevel(momentum),
                blue
            )
        )

        factorsCard.addView(
            factorRow(
                "Recent change",
                momentumLevel(momentum),
                green
            )
        )

        factorsCard.addView(
            factorRow(
                "Risk level",
                riskLevel(risk),
                riskColor(risk)
            )
        )

        factorsCard.addView(
            factorRow(
                "Decision confidence",
                confidenceDescription(
                    confidenceValue
                ),
                purple
            )
        )

        content.addView(
            factorsCard,
            marginParams(0, 0, 0, 12)
        )

        // ---------------------------------------------------------
        // WHY THIS DECISION?
        // ---------------------------------------------------------

        val whyCard = glassCard()

        addSmallTitle(
            whyCard,
            "WHY THIS DECISION?",
            gold
        )

        whyCard.addView(
            bodyText(
                buildWhyText(
                    momentum,
                    risk,
                    confidenceValue
                )
            )
        )

        content.addView(
            whyCard,
            marginParams(0, 0, 0, 12)
        )

        // ---------------------------------------------------------
        // SUGGESTED NEXT STEP
        // ---------------------------------------------------------

        val nextCard = glassCard()

        addSmallTitle(
            nextCard,
            "SUGGESTED NEXT STEP",
            green
        )

        nextCard.addView(
            bodyText(
                buildNextStep(
                    momentum,
                    risk,
                    confidenceValue
                )
            )
        )

        content.addView(
            nextCard,
            marginParams(0, 0, 0, 14)
        )

        // ---------------------------------------------------------
        // DECISION TEXT
        // ---------------------------------------------------------

        if (decisionText.isNotBlank()) {

            val yourDecisionCard = glassCard()

            addSmallTitle(
                yourDecisionCard,
                "YOUR DECISION",
                blue
            )

            yourDecisionCard.addView(
                bodyText(
                    decisionText
                )
            )

            content.addView(
                yourDecisionCard,
                marginParams(0, 0, 0, 14)
            )
        }

        // ---------------------------------------------------------
        // CHECK RESULT
        // ---------------------------------------------------------

        // ---------------------------------------------------------
        // ANALYSIS XP
        // ---------------------------------------------------------
        if (intent.getStringExtra("source_mode").orEmpty() != "FAST") {
            val xpCard = glassCard()
            addSmallTitle(
                xpCard,
                "XP EARNED",
                green
            )
            xpCard.addView(
                bodyText("+5 XP")
            )
            content.addView(
                xpCard,
                marginParams(0, 0, 0, 14)
            )
        }

        val checkButton = TextView(this).apply {
            text = "✓  CHECK RESULT"
            textSize = 16f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(
                cyan,
                18
            )
            elevation = dp(7).toFloat()
            setPadding(
                0,
                dp(4),
                0,
                dp(4)
            )

            setOnClickListener {
                GDMIEAudioManager.playUiClick(
                    this@DecisionOutputActivity
                )

                val prefs =
                    getSharedPreferences(
                        "GDMIE_CHECK",
                        MODE_PRIVATE
                    )

                val historyPrefs =
                    getSharedPreferences(
                        "GDMIE_HISTORY",
                        MODE_PRIVATE
                    )

                val index =
                    historyPrefs.getInt(
                        "last_index",
                        0
                    )

                prefs.edit()
                    .putInt(
                        "pending_index",
                        index
                    )
                    .apply()

                val checkIntent = Intent(
                                this@DecisionOutputActivity,
                                OutcomeCheckActivity::class.java
                            ).apply {
                                putExtra(
                                    "source_mode",
                                    intent.getStringExtra("source_mode").orEmpty()
                                )
                                putExtra(
                                    "fast_signal",
                                    signalText
                                )
                                putExtra(
                                    "analysis_index",
                                    index
                                )
                            }

                            startActivity(checkIntent)
            }
        }

        content.addView(
            checkButton,
            marginParams(
                0,
                0,
                0,
                12,
                dp(58)
            )
        )

        // ---------------------------------------------------------
        // REWARDED XP
        // ---------------------------------------------------------

        val rewardedCard = glassCard()

        val rewardedTitle = TextView(this).apply {
            text = "🎁  WATCH & EARN"
            textSize = 13f
            setTextColor(gold)
            setTypeface(null, Typeface.BOLD)
        }

        rewardedCard.addView(rewardedTitle)

        rewardedCard.addView(
            bodyText("Complete a short video • Earn +10 XP")
        )

        val rewardedButton = TextView(this).apply {
            text = "WATCH  +10 XP"
            textSize = 13f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 15)
            elevation = dp(5).toFloat()
            setPadding(
                dp(12),
                dp(4),
                dp(12),
                dp(4)
            )

            setOnClickListener {
                GDMIEAudioManager.playUiClick(
                    this@DecisionOutputActivity
                )

                RewardedAdManager.show(this@DecisionOutputActivity) { awarded ->
                    Toast.makeText(
                        this@DecisionOutputActivity,
                        "+$awarded XP earned!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        rewardedCard.addView(
            rewardedButton,
            marginParams(
                0,
                10,
                0,
                0,
                dp(46)
            )
        )

        content.addView(
            rewardedCard,
            marginParams(0, 0, 0, 14)
        )

        // ---------------------------------------------------------
        // HOME
        // ---------------------------------------------------------

        val homeButton = TextView(this).apply {
            text = "BACK TO HOME"
            textSize = 14f
            setTextColor(cyan)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = roundedStroke(
                Color.TRANSPARENT,
                cyan,
                16
            )
            setPadding(
                0,
                dp(4),
                0,
                dp(4)
            )

            setOnClickListener {
                GDMIEAudioManager.playUiClick(
                    this@DecisionOutputActivity
                )
                val homeIntent =
                    Intent(
                        this@DecisionOutputActivity,
                        MainActivity::class.java
                    ).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                startActivity(homeIntent)
                finish()
            }
        }

        content.addView(
            homeButton,
            marginParams(
                0,
                0,
                0,
                18,
                dp(54)
            )
        )

        val footer = TextView(this).apply {
            text = "GDMIE supports your thinking. You make the final decision."
            textSize = 12f
            setTextColor(dim)
            gravity = Gravity.CENTER
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
    // SAFE SIGNAL
    // =============================================================

    private fun safeSignal(
        decision: String,
        edge: Float
    ): String {

        val upper = decision.uppercase()

        return when {
            upper.contains("POSITIVE") ->
                "POSITIVE SIGNAL"

            upper.contains("NEGATIVE") ->
                "CAUTION SIGNAL"

            edge > 0.5f ->
                "POSITIVE SIGNAL"

            edge < -0.5f ->
                "CAUTION SIGNAL"

            else ->
                "NEUTRAL SIGNAL"
        }
    }

    // =============================================================
    // HUMAN EXPLANATION
    // =============================================================

    private fun buildSituationSummary(
        momentum: Float,
        risk: Float
    ): String {

        val movement = momentumLevel(momentum)
        val riskText = riskLevel(risk)

        return "The situation is showing $movement movement with a $riskText level of risk. Review the latest information before taking action."
    }

    private fun buildWhyText(
        momentum: Float,
        risk: Float,
        confidence: Float
    ): String {

        return when {
            risk >= 70f ->
                "The current situation carries a higher level of risk. A careful review of the available information may be useful before acting."

            momentum >= 70f && confidence >= 70f ->
                "The situation is showing strong positive movement and the available information gives a clearer signal."

            momentum >= 55f ->
                "The situation is showing steady movement. Watching for the next meaningful change may improve your understanding."

            momentum < 40f ->
                "Recent movement appears weaker. It may be useful to gather more information before making a major decision."

            else ->
                "The situation is mixed. Consider the latest information, your goal, and your comfort with risk."
        }
    }

    private fun buildNextStep(
        momentum: Float,
        risk: Float,
        confidence: Float
    ): String {

        return when {
            risk >= 70f ->
                "Review the important risks and confirm the information you are relying on before acting."

            confidence < 50f ->
                "Gather the missing information and review the decision again before taking action."

            momentum < 40f ->
                "Watch for a meaningful change and reassess the situation before making a major move."

            else ->
                "Consider the key factors, compare your available options, and choose the next step that fits your goal."
        }
    }

    // =============================================================
    // FACTOR HELPERS
    // =============================================================

    private fun situationLevel(
        momentum: Float
    ): String {
        return when {
            momentum >= 70f -> "Strong"
            momentum >= 55f -> "Stable"
            momentum >= 40f -> "Uncertain"
            else -> "Weak"
        }
    }

    private fun momentumLevel(
        momentum: Float
    ): String {
        return when {
            momentum >= 70f -> "Improving"
            momentum >= 45f -> "Stable"
            else -> "Declining"
        }
    }

    private fun riskLevel(
        risk: Float
    ): String {
        return when {
            risk >= 70f -> "High"
            risk >= 40f -> "Medium"
            else -> "Low"
        }
    }

    private fun riskColor(
        risk: Float
    ): Int {
        return when {
            risk >= 70f -> red
            risk >= 40f -> gold
            else -> green
        }
    }

    private fun confidenceDescription(
        confidence: Float
    ): String {
        return when {
            confidence >= 80f -> "Very clear"
            confidence >= 65f -> "Clear"
            confidence >= 50f -> "Moderate"
            else -> "Limited"
        }
    }

    // =============================================================
    // UI
    // =============================================================

    private fun addSmallTitle(
        parent: LinearLayout,
        text: String,
        color: Int
    ) {
        val view = TextView(this).apply {
            this.text = text
            textSize = 12f
            setTextColor(color)
            setTypeface(null, Typeface.BOLD)
        }

        parent.addView(view)
    }

    private fun bodyText(
        text: String
    ): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(muted)
            setPadding(
                0,
                dp(9),
                0,
                dp(2)
            )
            setLineSpacing(
                dp(3).toFloat(),
                1f
            )
        }
    }

    private fun factorRow(
        title: String,
        value: String,
        accent: Int
    ): LinearLayout {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(
                0,
                dp(9),
                0,
                dp(9)
            )
        }

        val titleView = TextView(this).apply {
            text = title
            textSize = 14f
            setTextColor(muted)
        }

        row.addView(
            titleView,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val valueView = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(accent)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.END
        }

        row.addView(valueView)

        return row
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
            setStroke(
                dp(1),
                stroke
            )
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun marginParams(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        height: Int =
            ViewGroup.LayoutParams.WRAP_CONTENT
    ): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            height
        ).apply {
            leftMargin = dp(left)
            topMargin = dp(top)
            rightMargin = dp(right)
            bottomMargin = dp(bottom)
        }
    }

    private fun dp(
        value: Int
    ): Int {
        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }
}
