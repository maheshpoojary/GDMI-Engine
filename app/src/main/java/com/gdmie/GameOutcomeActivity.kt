package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.gdmie.game.GDMIEGameProgress
import kotlin.math.abs

class GameOutcomeActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val card = Color.rgb(11, 18, 31)
    private val cyan = Color.rgb(0, 220, 255)
    private val purple = Color.rgb(165, 90, 255)
    private val gold = Color.rgb(255, 200, 55)
    private val green = Color.rgb(55, 225, 135)
    private val red = Color.rgb(255, 90, 100)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int) =
        (v * resources.displayMetrics.density).toInt()

    private fun tv(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun box(color: Int, stroke: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(18).toFloat()
            setStroke(dp(1), stroke)
        }

    private fun calculateGameAnalysis(
        scenarioId: String,
        decision: String
    ): GDMResult {

        val base = when (scenarioId) {
            "signal_shift" ->
                doubleArrayOf(72.0, 68.0, 76.0, 58.0, 64.0, 24.0, 66.0, 8.0, 62.0, 34.0)
            "hidden_risk" ->
                doubleArrayOf(64.0, 60.0, 70.0, 46.0, 42.0, 10.0, 62.0, -4.0, 48.0, 58.0)
            "momentum" ->
                doubleArrayOf(70.0, 74.0, 78.0, 76.0, 72.0, 30.0, 68.0, 10.0, 70.0, 38.0)
            "timing" ->
                doubleArrayOf(76.0, 70.0, 82.0, 60.0, 68.0, 36.0, 72.0, 6.0, 82.0, 42.0)
            "conflict" ->
                doubleArrayOf(62.0, 64.0, 70.0, 48.0, 45.0, 4.0, 64.0, -2.0, 52.0, 55.0)
            "resource" ->
                doubleArrayOf(68.0, 62.0, 74.0, 52.0, 48.0, 8.0, 60.0, 0.0, 58.0, 62.0)
            "reversal" ->
                doubleArrayOf(54.0, 50.0, 60.0, 38.0, 34.0, -8.0, 58.0, -6.0, 64.0, 68.0)
            "opportunity" ->
                doubleArrayOf(74.0, 72.0, 84.0, 62.0, 58.0, 28.0, 70.0, 7.0, 66.0, 44.0)
            else ->
                doubleArrayOf(60.0, 60.0, 65.0, 50.0, 50.0, 10.0, 60.0, 0.0, 55.0, 45.0)
        }

        var present = base[0]
        var expected = base[1]
        var target = base[2]
        var recent = base[3]
        var immediate = base[4]
        var twoMin = base[5]
        val marketLine = base[6]
        val odds = base[7]
        var timing = base[8]
        var risk = base[9]

        when {
            decision.startsWith("A •") -> {
                present += 5
                immediate += 8
                twoMin += 10
                timing += 5
                risk += 8
            }
            decision.startsWith("B •") -> {
                present -= 2
                expected += 5
                recent += 3
                immediate -= 4
                twoMin -= 4
                timing -= 6
                risk -= 8
            }
            decision.startsWith("C •") -> {
                expected += 3
                target += 4
                immediate += 2
                twoMin += 5
                timing += 2
                risk -= 5
            }
        }

        return GDMEngine.calculate(
            GDMInput(
                presentValue = present,
                expectedValue = expected,
                targetValue = target,
                recentMomentum = recent,
                immediateMomentum = immediate,
                twoMinMarketAdvantage = twoMin,
                exactMarketLine = marketLine,
                oddsMovement = odds,
                timingFactor = timing,
                riskFactor = risk
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val mode = intent.getStringExtra("challenge_mode") ?: "QUICK"
        val decision = intent.getStringExtra("challenge_decision") ?: "Decision locked"
        val scenarioId = intent.getStringExtra("scenario_id") ?: "signal_shift"
        val challengeId =
            intent.getStringExtra("challenge_id")
                ?: "${mode}_${scenarioId}_${System.currentTimeMillis()}"

        val result = calculateGameAnalysis(scenarioId, decision)

        val confidence =
            (result.confidence * 100).toInt().coerceIn(0, 100)

        val validity =
            (50.0 + result.edge * 2.5 + (confidence - 50) * 0.20)
                .coerceIn(5.0, 95.0)
                .toInt()

        val contradiction = 100 - validity

        val scroll = android.widget.ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(22), dp(18), dp(30))
        }

        root.addView(
            tv(
                if (mode == "TIME_ATTACK")
                    "⏱  GDMIE ANALYSIS"
                else
                    "🧠  GDMIE ANALYSIS",
                28f,
                cyan,
                true
            )
        )

        root.addView(
            tv(
                "Your decision is locked. Now see what the engine found.",
                14f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, dp(18))
            }
        )

        val decisionCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = box(card, purple)
        }

        decisionCard.addView(
            tv("YOUR DECISION", 12f, purple, true)
        )

        decisionCard.addView(
            tv(decision, 18f, white, true).apply {
                setPadding(0, dp(8), 0, 0)
            }
        )

        root.addView(
            decisionCard,
            LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dp(16)
            }
        )

        val analysisCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = box(card, cyan)
        }

        analysisCard.addView(
            tv("GDMIE ENGINE RESULT", 17f, cyan, true)
        )

        analysisCard.addView(
            tv(
                "\n${validity}%  EVIDENCE SUPPORT\n" +
                "$contradiction%  CONTRADICTING\n\n" +
                "ENGINE SIGNAL: ${result.decision}\n" +
                "CONFIDENCE: $confidence%\n" +
                "EDGE: %.2f".format(result.edge),
                16f,
                white,
                true
            )
        )

        val insightVariant = (System.currentTimeMillis() % 3).toInt()

        val userInsight = when {
            result.edge > 5.0 -> {
                when (insightVariant) {
                    0 -> """
RESULT
The current signals are supporting this decision.

INSIGHT
The available evidence is aligning with the direction you selected, giving the decision a stronger foundation.

WHAT TO NOTICE
Watch whether the supporting conditions and momentum continue to hold.

NEXT THINKING STEP
Before moving forward, check that the key conditions behind this signal are still present.
""".trimIndent()

                    1 -> """
RESULT
The decision is receiving a positive signal.

INSIGHT
Several of the current signals are working in the same direction, which strengthens the case for your choice.

WHAT TO NOTICE
Pay attention to whether that support remains consistent as the situation develops.

NEXT THINKING STEP
Move forward by checking the most important condition that could change this signal.
""".trimIndent()

                    else -> """
RESULT
The current analysis is leaning in favor of your decision.

INSIGHT
The evidence is showing a useful level of alignment, giving this direction more support than resistance right now.

WHAT TO NOTICE
Keep an eye on the factor carrying the strongest influence on the current signal.

NEXT THINKING STEP
Confirm that the situation still matches the conditions that supported your decision.
""".trimIndent()
                }
            }

            result.edge < -5.0 -> {
                when (insightVariant) {
                    0 -> """
RESULT
The current signals are leaning against this decision.

INSIGHT
There is not enough supporting evidence to make the decision comfortable to move forward with right now.

WHAT TO NOTICE
Pay attention to the factors creating the strongest resistance.

NEXT THINKING STEP
Reassess what would need to change before this decision becomes more convincing.
""".trimIndent()

                    1 -> """
RESULT
The decision is receiving a caution signal.

INSIGHT
The current evidence is weaker than the concerns surrounding the decision, leaving important uncertainty unresolved.

WHAT TO NOTICE
Look closely at the risk factors that could have the biggest impact.

NEXT THINKING STEP
Pause and identify whether the situation has changed enough to justify continuing.
""".trimIndent()

                    else -> """
RESULT
The current analysis is pushing back on this direction.

INSIGHT
Some support is present, but it is not strong enough to outweigh the signals working against the decision.

WHAT TO NOTICE
Watch for any meaningful change in the conditions creating resistance.

NEXT THINKING STEP
Consider what new evidence would be needed before committing further.
""".trimIndent()
                }
            }

            else -> {
                when (insightVariant) {
                    0 -> """
RESULT
The decision is sitting in an uncertain zone.

INSIGHT
Some signals support your direction, while others are pulling the analysis the other way.

WHAT TO NOTICE
The balance could shift if one of the key conditions changes.

NEXT THINKING STEP
Identify which factor matters most before deciding whether to continue or rethink.
""".trimIndent()

                    1 -> """
RESULT
The current signal is not clearly settled.

INSIGHT
There are meaningful reasons on both sides, so the available evidence does not point strongly in one direction yet.

WHAT TO NOTICE
Watch for the next change that could strengthen either side of the decision.

NEXT THINKING STEP
Recheck the situation before turning uncertainty into commitment.
""".trimIndent()

                    else -> """
RESULT
The analysis is showing a mixed signal.

INSIGHT
The decision has some supporting evidence, but enough uncertainty remains to keep the picture open.

WHAT TO NOTICE
Focus on the condition that could most quickly change the balance.

NEXT THINKING STEP
Take another look at the situation before making the next move.
""".trimIndent()
                }
            }
        }

        analysisCard.addView(
            tv(
                "\n$userInsight",
                13f,
                muted
            )
        )

        root.addView(
            analysisCard,
            LinearLayout.LayoutParams(-1, -2).apply {
                bottomMargin = dp(20)
            }
        )

        root.addView(
            tv("🎯  ACTUAL OUTCOME", 19f, gold, true).apply {
                setPadding(0, 0, 0, dp(10))
            }
        )

        val outcomeGroup = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val outcomeValues = arrayOf(
            "Successful",
            "Not Successful",
            "Mixed / Neutral"
        )

        var selectedOutcome = ""

        fun outcomeButton(label: String, value: String): TextView =
            tv(label, 16f, white, true).apply {
                gravity = Gravity.CENTER
                setPadding(dp(12), dp(16), dp(12), dp(16))
                background = box(card, gold)

                setOnClickListener {
                    GDMIEAudioManager.playUiClick(
                        this@GameOutcomeActivity
                    )

                    selectedOutcome = value

                    for (i in 0 until outcomeGroup.childCount) {
                        val child = outcomeGroup.getChildAt(i)
                        child.background = box(card, gold)
                    }

                    background = box(Color.rgb(65, 52, 15), gold)
                }
            }

        outcomeValues.forEachIndexed { index, label ->
            outcomeGroup.addView(
                outcomeButton(
                    label,
                    when (index) {
                        0 -> "WIN"
                        1 -> "LOSS"
                        else -> "NEUTRAL"
                    }
                ),
                LinearLayout.LayoutParams(-1, dp(58)).apply {
                    bottomMargin = dp(10)
                }
            )
        }

        root.addView(outcomeGroup)

        val review = tv(
            "🪞  DECISION MIRROR",
            17f,
            green,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(17), 0, dp(17))
            background = box(green, green)

            setOnClickListener {
                if (selectedOutcome.isBlank()) {
                    Toast.makeText(
                        this@GameOutcomeActivity,
                        "Choose the actual outcome first.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@GameOutcomeActivity,
                    R.raw.gdmie_audio_analysis
                )

                val outcomeText = when (selectedOutcome) {
                    "WIN" -> "Successful"
                    "LOSS" -> "Not Successful"
                    else -> "Mixed / Neutral"
                }

                val reflection = when {
                    validity >= 75 ->
                        "Strong evidence supported this decision at decision time."
                    validity >= 55 ->
                        "The decision had partial support, with some uncertainty."
                    else ->
                        "Several available signals challenged this decision."
                }

                AlertDialog.Builder(this@GameOutcomeActivity)
                    .setTitle("🪞 DECISION MIRROR")
                    .setMessage(
                        "YOUR DECISION\n$decision\n\n" +
                        "GDMIE SUPPORT\n" +
                        "$validity% supported • $contradiction% contradicting\n\n" +
                        "ACTUAL OUTCOME\n$outcomeText\n\n" +
                        "GDMIE REFLECTION\n$reflection"
                    )
                    .setPositiveButton("🏆 COMPLETE") { _, _ ->

                        val analysisId = challengeId.hashCode()

                        val earnedXp =
                            when (mode) {
                                "DAILY" -> {
                                    GDMIEGameProgress.addOutcomeXpOnce(
                                        this@GameOutcomeActivity,
                                        analysisId,
                                        30
                                    )
                                }
                                "TIME_ATTACK" -> {
                                    GDMIEGameProgress.addOutcomeXpOnce(
                                        this@GameOutcomeActivity,
                                        analysisId,
                                        20
                                    )
                                }
                                else -> {
                                    GDMIEGameProgress.addOutcomeXpOnce(
                                        this@GameOutcomeActivity,
                                        analysisId,
                                        10
                                    )
                                }
                            }
                                                                                                    if (mode == "DAILY" && earnedXp > 0) {
                            // Mark Daily Challenge completion and update streak.
                            // XP was already awarded above, so use 0 here.
                            GDMIEGameProgress.completeDailyChallenge(
                                this@GameOutcomeActivity,
                                0
                            )

                            val priority =
                                intent.getStringExtra("challenge_priority")
                                    ?: "Not specified"

                            val streak =
                                GDMIEGameProgress.getStreak(
                                    this@GameOutcomeActivity
                                )

                            GDMIEGameProgress.saveDailyLearning(
                                this@GameOutcomeActivity,
                                decision,
                                priority,
                                outcomeText,
                                earnedXp,
                                streak
                            )
                        }

                        val level =
                            GDMIEGameProgress.getLevel(this@GameOutcomeActivity)

                        AlertDialog.Builder(this@GameOutcomeActivity)
                            .setTitle("🏆 DECISION COMPLETE")
                            .setMessage(
                                "Outcome : $outcomeText\n\n" +
                                "+$earnedXp XP\n" +
                                "LEVEL $level\n\n" +
                                "Your decision has been recorded."
                            )
                            .setNeutralButton("🎁 WATCH +10 XP") { _, _ ->
                                RewardedAdManager.show(
                                    this@GameOutcomeActivity
                                ) { awarded ->
                                    Toast.makeText(
                                        this@GameOutcomeActivity,
                                        "+$awarded XP earned!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .setPositiveButton("NEXT CHALLENGE") { _, _ ->

                                val next = Intent(
                                    this@GameOutcomeActivity,
                                    ChallengeScenarioActivity::class.java
                                ).apply {
                                    putExtra("challenge_mode", mode)
                                    putExtra("next_challenge", true)
                                }

                                startActivity(next)
                                finish()
                            }
                            .setNegativeButton("HOME") { _, _ ->
                                val home = Intent(
                                    this@GameOutcomeActivity,
                                    MainActivity::class.java
                                )
                                startActivity(home)
                                finish()
                            }
                            .setCancelable(false)
                            .show()
                    }
                    .setNegativeButton("BACK", null)
                    .show()
            }
        }

        root.addView(
            review,
            LinearLayout.LayoutParams(-1, dp(60)).apply {
                topMargin = dp(16)
            }
        )

        root.addView(
            tv(
                "\nGDMIE supports your thinking.\nYou make the final decision.",
                12f,
                muted
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }
}
