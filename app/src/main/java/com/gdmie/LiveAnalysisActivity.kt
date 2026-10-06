package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.MotionEvent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.gdmie.adapter.ContextState
import com.gdmie.adapter.CurrentState
import com.gdmie.adapter.GDMIESimpleDecision
import com.gdmie.adapter.GDMIESimpleDecisionAdapter
import com.gdmie.adapter.GDMIENormalization
import com.gdmie.adapter.GoalLevel
import com.gdmie.adapter.MomentumState
import com.gdmie.adapter.RiskLevel
import com.gdmie.adapter.TimingState
import com.gdmie.network.GDMIEEngineGateway
import com.gdmie.game.GDMIEGameProgress
import kotlin.concurrent.thread

private fun Int.gdmR(): Int = (this shr 16) and 0xFF
private fun Int.gdmG(): Int = (this shr 8) and 0xFF
private fun Int.gdmB(): Int = this and 0xFF

class LiveAnalysisActivity : Activity() {

    private val bg = Color.rgb(3, 7, 18)
    private val panel = Color.rgb(11, 19, 36)
    private val panel2 = Color.rgb(16, 27, 48)
    private val cyan = Color.rgb(0, 220, 255)
    private val purple = Color.rgb(145, 80, 255)
    private val gold = Color.rgb(255, 190, 70)
    private val green = Color.rgb(70, 230, 150)
    private val white = Color.WHITE
    private val muted = Color.rgb(150, 165, 190)
    private val red = Color.rgb(255, 100, 120)

    private lateinit var content: LinearLayout
    private var scenarioIndex = 0
    private var selectedChoice = -1
    private var sessionId = System.currentTimeMillis().toInt()

    private var scenario: Scenario = scenarios[0]
    private var firstDecision: GDMIESimpleDecision? = null
    private lateinit var currentMath: MathChallenge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scenarioIndex = (System.currentTimeMillis() % scenarios.size).toInt()
        scenario = scenarios[scenarioIndex]
        showScenario()
    }


    private fun showScenario() {
        selectedChoice = -1

        val palette = when (scenario.category) {
            "Changing Conditions" -> intArrayOf(cyan, Color.rgb(70, 95, 255), Color.rgb(255, 90, 175))
            "Project Simulation" -> intArrayOf(cyan, gold, purple)
            else -> intArrayOf(purple, cyan, gold)
        }

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(1, 4, 14))

        val world = HolographicWorldView(
            this,
            scenario.category,
            scenario.title,
            false,
            palette
        )

        root.addView(
            world,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // -------------------------------------------------
        // TOP HOLOGRAPHIC HUD
        // -------------------------------------------------
        val hud = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(14), dp(8))
            background = rounded(
                Color.argb(110, 2, 12, 30),
                16,
                Color.argb(170, palette[0].shr(), palette[0].shg(), palette[0].shb()),
                1
            )
        }

        hud.addView(
            tv("◈ GDMIE", 14f, cyan, true),
            LinearLayout.LayoutParams(0, dp(42), 1f)
        )

        hud.addView(
            tv(
                "LIVE ANALYSIS\nROUND 01  •  ${scenario.category.uppercase()}",
                8f,
                white,
                true
            ).apply {
                gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            },
            LinearLayout.LayoutParams(dp(210), dp(42))
        )

        root.addView(
            hud,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(60)
            ).apply {
                setMargins(dp(12), dp(12), dp(12), 0)
            }
        )

        // -------------------------------------------------
        // LIVE STATUS
        // -------------------------------------------------
        root.addView(
            tv(
                "●  ADAPTATION MODE  •  LIVE",
                9f,
                palette[0],
                true
            ).apply {
                letterSpacing = 0.12f
                setShadowLayer(dp(8).toFloat(), 0f, 0f, palette[0])
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                dp(30)
            ).apply {
                leftMargin = dp(18)
                topMargin = dp(82)
            }
        )

        // -------------------------------------------------
        // WORLD TITLE
        // -------------------------------------------------
        root.addView(
            tv(
                scenario.title.uppercase(),
                25f,
                white,
                true
            ).apply {
                setShadowLayer(dp(12).toFloat(), 0f, 0f, palette[0])
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(48)
            ).apply {
                leftMargin = dp(18)
                rightMargin = dp(18)
                topMargin = dp(112)
            }
        )

        // -------------------------------------------------
        // SITUATION FLOATING HUD
        // -------------------------------------------------
        val situation = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(10), dp(14), dp(10))
            background = GradientDrawable().apply {
                setColor(Color.argb(72, 5, 18, 40))
                cornerRadius = dp(12).toFloat()
                setStroke(
                    dp(1),
                    Color.argb(
                        145,
                        palette[0].shr(),
                        palette[0].shg(),
                        palette[0].shb()
                    )
                )
            }

            addView(
                tv("SITUATION", 8f, palette[0], true).apply {
                    letterSpacing = 0.16f
                }
            )

            addView(
                tv(
                    scenario.scene,
                    12f,
                    white,
                    false
                ).apply {
                    setPadding(0, dp(5), 0, 0)
                }
            )
        }

        root.addView(
            situation,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(112)
            ).apply {
                leftMargin = dp(18)
                rightMargin = dp(18)
                topMargin = dp(160)
            }
        )

        // -------------------------------------------------
        // QUESTION INSIDE WORLD
        // -------------------------------------------------
        root.addView(
            tv(
                dynamicQuestionFor(scenario),
                20f,
                white,
                true
            ).apply {
                gravity = Gravity.CENTER
                setShadowLayer(dp(10).toFloat(), 0f, 0f, palette[0])
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            ).apply {
                leftMargin = dp(14)
                rightMargin = dp(14)
                topMargin = dp(278)
            }
        )

        // -------------------------------------------------
        // LIVE MATH SIGNAL
        // -------------------------------------------------
        val mathLabel = tv(
            "LIVE MATH  •  SELECT A DECISION",
            8f,
            palette[1],
            true
        ).apply {
            gravity = Gravity.CENTER
            letterSpacing = 0.10f
        }

        val mathExpression = tv(
            "Choose A / B / C to calculate",
            17f,
            white,
            true
        ).apply {
            gravity = Gravity.CENTER
            setShadowLayer(
                dp(8).toFloat(),
                0f,
                0f,
                palette[1]
            )
            setPadding(0, dp(2), 0, 0)
        }

        val mathCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(5), dp(12), dp(5))
            background = GradientDrawable().apply {
                setColor(Color.argb(82, 8, 20, 42))
                cornerRadius = dp(12).toFloat()
                setStroke(
                    dp(1),
                    Color.argb(
                        170,
                        palette[1].shr(),
                        palette[1].shg(),
                        palette[1].shb()
                    )
                )
            }

            addView(mathLabel)
            addView(mathExpression)
        }

        root.addView(
            mathCard,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            ).apply {
                leftMargin = dp(24)
                rightMargin = dp(24)
                topMargin = dp(326)
            }
        )

        // -------------------------------------------------
        // HOLOGRAPHIC A / B / C PLATFORMS
        // -------------------------------------------------
        val choiceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        scenario.choices.forEachIndexed { index, choice ->
            val choiceColor = when (index) {
                0 -> cyan
                1 -> gold
                else -> purple
            }

            val holo = HolographicChoiceView(
                this,
                ('A'.code + index).toChar().toString(),
                choice.text,
                when (index) {
                    0 -> "DIRECT"
                    1 -> "VERIFY"
                    else -> "WAIT"
                },
                choiceColor
            )

            holo.setOnClickListener {
                GDMIEAudioManager.playUiClick(this@LiveAnalysisActivity)
                selectedChoice = index

                val presentValue =
                    GDMIENormalization.currentState(choice.current)

                val targetValue =
                    GDMIENormalization.goalLevel(scenario.goal)

                currentMath = MathChallenge.fromGDMIE(
                    presentValue = presentValue,
                    targetValue = targetValue
                )

                mathLabel.text =
                    "LIVE MATH  •  ${currentMath.operationLabel}"

                mathExpression.text =
                    currentMath.expression

                refreshHolographicChoices(choiceRow)
            }

            choiceRow.addView(
                holo,
                LinearLayout.LayoutParams(
                    0,
                    dp(128),
                    1f
                ).apply {
                    setMargins(dp(4), 0, dp(4), 0)
                }
            )
        }

        root.addView(
            choiceRow,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(138)
            ).apply {
                leftMargin = dp(10)
                rightMargin = dp(10)
                topMargin = dp(386)
            }
        )

        // -------------------------------------------------
        // HOLOGRAPHIC LOCK CONTROL
        // -------------------------------------------------
        val lock = HolographicLockView(this, palette[0])

        lock.setOnClickListener {
            GDMIEAudioManager.playSfx(
                this@LiveAnalysisActivity,
                R.raw.gdmie_audio_decision
            )
            if (selectedChoice >= 0) {
                lock.isEnabled = false
                lock.animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .alpha(0.25f)
                    .setDuration(180L)
                    .withEndAction {
                        runRoundOne()
                    }
                    .start()
            }
        }

        root.addView(
            lock,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                leftMargin = dp(42)
                rightMargin = dp(42)
                topMargin = dp(538)
            }
        )

        // -------------------------------------------------
        // BOTTOM WORLD HUD
        // -------------------------------------------------
        root.addView(
            tv(
                "◈ CHOOSE  •  LOCK  •  ADAPT",
                9f,
                muted,
                true
            ).apply {
                gravity = Gravity.CENTER
                letterSpacing = 0.10f
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            ).apply {
                leftMargin = dp(20)
                rightMargin = dp(20)
                topMargin = dp(610)
            }
        )

        setContentView(root)
    }

    private fun Int.shr(): Int = (this shr 16) and 0xFF
    private fun Int.shg(): Int = (this shr 8) and 0xFF
    private fun Int.shb(): Int = this and 0xFF

    private fun runRoundOne() {
        val choice = scenario.choices[selectedChoice]

        val decision = GDMIESimpleDecision(
            decisionText = choice.text,
            currentSituation = choice.current,
            goalOutcome = scenario.goal,
            context = choice.context,
            momentum = choice.momentum,
            risk = choice.risk,
            timing = choice.timing,
            )

        firstDecision = decision

        showLoading(
            "🧠 GDMIE ANALYSIS",
            "Processing your decision..."
        )

        thread {
            try {
                val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)
                val result = kotlinx.coroutines.runBlocking { GDMIEEngineGateway.calculate(input) }

                runOnUiThread {
                    showTwist(result.decision)
                }
            } catch (_: Exception) {
                runOnUiThread {
                    showTwist("The situation changed. Adapt to the new information.")
                }
            }
        }
    }


    private fun showTwist(engineDecision: String) {
        selectedChoice = -1

        val palette = when (scenario.category) {
            "Changing Conditions" -> intArrayOf(Color.rgb(255, 90, 175), cyan, purple)
            "Project Simulation" -> intArrayOf(gold, cyan, purple)
            else -> intArrayOf(purple, gold, cyan)
        }

        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(1, 3, 12))

        val world = HolographicWorldView(
            this,
            scenario.category,
            scenario.title,
            true,
            palette
        )

        root.addView(
            world,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // New-world HUD
        val hud = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(8), dp(14), dp(8))
            background = rounded(
                Color.argb(110, 10, 5, 30),
                16,
                Color.argb(
                    180,
                    palette[0].shr(),
                    palette[0].shg(),
                    palette[0].shb()
                ),
                1
            )
        }

        hud.addView(
            tv("◈ GDMIE", 14f, cyan, true),
            LinearLayout.LayoutParams(0, dp(42), 1f)
        )

        hud.addView(
            tv(
                "LIVE ANALYSIS\nROUND 02  •  ADAPT",
                8f,
                white,
                true
            ).apply {
                gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
            },
            LinearLayout.LayoutParams(dp(190), dp(42))
        )

        root.addView(
            hud,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(60)
            ).apply {
                setMargins(dp(12), dp(12), dp(12), 0)
            }
        )

        root.addView(
            tv(
                "⚡  NEW INFORMATION",
                18f,
                gold,
                true
            ).apply {
                setShadowLayer(dp(12).toFloat(), 0f, 0f, gold)
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(40)
            ).apply {
                leftMargin = dp(18)
                topMargin = dp(88)
            }
        )

        root.addView(
            tv(
                "THE WORLD CHANGED",
                25f,
                white,
                true
            ),
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(42)
            ).apply {
                leftMargin = dp(18)
                topMargin = dp(126)
            }
        )

        // Twist information floating in the world
        val twistPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = GradientDrawable().apply {
                setColor(Color.argb(78, 20, 8, 40))
                cornerRadius = dp(14).toFloat()
                setStroke(
                    dp(1),
                    Color.argb(
                        180,
                        palette[0].shr(),
                        palette[0].shg(),
                        palette[0].shb()
                    )
                )
            }

            addView(
                tv("LIVE EVENT UPDATE", 8f, palette[0], true).apply {
                    letterSpacing = 0.14f
                }
            )

            addView(
                tv(
                    scenario.twist,
                    14f,
                    white,
                    true
                ).apply {
                    setPadding(0, dp(6), 0, 0)
                }
            )

            addView(
                tv(
                    "FIRST DECISION: LOCKED  •  ADAPTATION REQUIRED",
                    8f,
                    muted,
                    true
                ).apply {
                    setPadding(0, dp(8), 0, 0)
                    letterSpacing = 0.06f
                }
            )
        }

        root.addView(
            twistPanel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(148)
            ).apply {
                leftMargin = dp(18)
                rightMargin = dp(18)
                topMargin = dp(178)
            }
        )

        root.addView(
            tv(
                dynamicAdaptQuestionFor(scenario),
                19f,
                white,
                true
            ).apply {
                gravity = Gravity.CENTER
                setShadowLayer(dp(9).toFloat(), 0f, 0f, palette[0])
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(48)
            ).apply {
                leftMargin = dp(15)
                rightMargin = dp(15)
                topMargin = dp(338)
            }
        )

        // Adaptation holographic platforms
        val choiceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        scenario.adaptChoices.forEachIndexed { index, choice ->
            val choiceColor = when (index) {
                0 -> cyan
                1 -> gold
                else -> purple
            }

            val holo = HolographicChoiceView(
                this,
                ('A'.code + index).toChar().toString(),
                choice.text,
                when (index) {
                    0 -> "COMMIT"
                    1 -> "REASSESS"
                    else -> "HOLD"
                },
                choiceColor
            )

            holo.setOnClickListener {
                GDMIEAudioManager.playUiClick(this@LiveAnalysisActivity)
                selectedChoice = index
                refreshHolographicChoices(choiceRow)
            }

            choiceRow.addView(
                holo,
                LinearLayout.LayoutParams(
                    0,
                    dp(128),
                    1f
                ).apply {
                    setMargins(dp(4), 0, dp(4), 0)
                }
            )
        }

        root.addView(
            choiceRow,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(138)
            ).apply {
                leftMargin = dp(10)
                rightMargin = dp(10)
                topMargin = dp(392)
            }
        )

        val lock = HolographicLockView(this, palette[0], "LOCK ADAPTATION")

        lock.setOnClickListener {
            GDMIEAudioManager.playSfx(
                this@LiveAnalysisActivity,
                R.raw.gdmie_audio_decision
            )
            if (selectedChoice >= 0) {
                lock.isEnabled = false
                lock.animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .alpha(0.25f)
                    .setDuration(180L)
                    .withEndAction {
                        runFinalAnalysis(engineDecision)
                    }
                    .start()
            }
        }

        root.addView(
            lock,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                leftMargin = dp(42)
                rightMargin = dp(42)
                topMargin = dp(548)
            }
        )

        root.addView(
            tv(
                "◈ INFORMATION CHANGED  •  YOUR RESPONSE MUST CHANGE",
                8f,
                muted,
                true
            ).apply {
                gravity = Gravity.CENTER
            },
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(28)
            ).apply {
                leftMargin = dp(18)
                rightMargin = dp(18)
                topMargin = dp(620)
            }
        )

        setContentView(root)
    }

    private fun refreshHolographicChoices(row: LinearLayout) {
        for (i in 0 until row.childCount) {
            val child = row.getChildAt(i)
            if (child is HolographicChoiceView) {
                child.setSelectedState(i == selectedChoice)
            }
        }
    }

    private fun runFinalAnalysis(engineDecision: String) {
        val base = firstDecision ?: return
        val choice = scenario.adaptChoices[selectedChoice]

        val adapted = base.copy(
            decisionText = choice.text,
            currentSituation = choice.current,
            context = choice.context,
            momentum = choice.momentum,
            risk = choice.risk,
            timing = choice.timing,
            )

        showLoading(
            "🧠 FINAL ANALYSIS",
            "Comparing your adaptation with the changing situation..."
        )

        thread {
            try {
                val input = GDMIESimpleDecisionAdapter.toGDMInput(adapted)
                val result = kotlinx.coroutines.runBlocking { GDMIEEngineGateway.calculate(input) }

                runOnUiThread {
                    showFinal(
                    result.decision,
                    result.confidence
                )
                }
            } catch (_: Exception) {
                runOnUiThread {
                    showFinal(engineDecision)
                }
            }
        }
    }

    private fun signalLabel(confidence: Double): String {
        val percent = (confidence * 100.0).coerceIn(0.0, 100.0)
        return when {
            percent < 50.0 -> "LOW SIGNAL"
            percent < 75.0 -> "MID SIGNAL"
            else -> "HIGH SIGNAL"
        }
    }

    private fun signalXp(confidence: Double): Int {
        val percent = (confidence * 100.0).coerceIn(0.0, 100.0)
        return when {
            percent < 50.0 -> 5
            percent < 75.0 -> 10
            else -> 15
        }
    }

    private fun derivedSignalPercent(
        confidence: Double,
        choiceIndex: Int
    ): Int {
        val confidenceBase = when {
            confidence < 0.55 -> 32.5
            confidence < 0.68 -> 47.5
            confidence < 0.83 -> 62.5
            else -> 77.0
        }

        val cycle = kotlin.math.abs(sessionId % 51)
        val variation = cycle - 25

        val choiceShift = when (choiceIndex) {
            0 -> 8
            1 -> 0
            2 -> -8
            else -> 0
        }

        val scenarioShift = when (scenarioIndex % 3) {
            0 -> 4
            1 -> 0
            else -> -4
        }

        return (
            confidenceBase +
                variation +
                choiceShift +
                scenarioShift
            ).coerceIn(10.0, 99.0).toInt()
    }

    private fun signalXpFromPercent(percent: Int): Int {
        return when {
            percent < 20 -> 5
            percent < 35 -> 7
            percent < 50 -> 9
            percent < 60 -> 11
            percent < 70 -> 13
            percent < 80 -> 15
            percent < 90 -> 18
            else -> 20
        }
    }

    private fun showFinal(
        resultText: String,
        confidence: Double = 0.5
    ) {
        content.removeAllViews()

        // Small self-contained cinematic Fluid Light visual.
        val fluidLight = android.widget.FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        val fluidParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
            dp(150)
        ).apply {
            setMargins(0, 0, 0, dp(2))
        }

        val orb = android.widget.TextView(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.argb(105, 0, 220, 255))
            }
            alpha = 0.30f
        }

        fluidLight.addView(
            orb,
            android.widget.FrameLayout.LayoutParams(
                dp(68),
                dp(68),
                android.view.Gravity.CENTER
            )
        )

        val innerRing = android.widget.TextView(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.TRANSPARENT)
                setStroke(
                    dp(2),
                    android.graphics.Color.argb(170, 0, 220, 255)
                )
            }
            alpha = 0.72f
        }

        fluidLight.addView(
            innerRing,
            android.widget.FrameLayout.LayoutParams(
                dp(104),
                dp(104),
                android.view.Gravity.CENTER
            )
        )

        val outerRing = android.widget.TextView(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(android.graphics.Color.TRANSPARENT)
                setStroke(
                    dp(1),
                    android.graphics.Color.argb(105, 170, 90, 255)
                )
            }
            alpha = 0.52f
        }

        fluidLight.addView(
            outerRing,
            android.widget.FrameLayout.LayoutParams(
                dp(138),
                dp(138),
                android.view.Gravity.CENTER
            )
        )

        // Flowing light beam.
        val beam = android.widget.TextView(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = dp(8).toFloat()
                setColor(android.graphics.Color.argb(70, 0, 220, 255))
            }
            alpha = 0.0f
        }

        fluidLight.addView(
            beam,
            android.widget.FrameLayout.LayoutParams(
                dp(110),
                dp(4),
                android.view.Gravity.CENTER
            )
        )

        android.animation.ObjectAnimator.ofFloat(
            beam,
            "translationX",
            -dp(85).toFloat(),
            dp(85).toFloat()
        ).apply {
            duration = 2100L
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.REVERSE
            interpolator =
                android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }

        android.animation.ObjectAnimator.ofFloat(
            beam,
            "alpha",
            0.0f,
            0.65f,
            0.0f
        ).apply {
            duration = 2100L
            repeatCount = android.animation.ValueAnimator.INFINITE
            interpolator =
                android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }

        // Breathing energy core.
        android.animation.ObjectAnimator.ofFloat(
            orb,
            "scaleX",
            0.78f,
            1.18f
        ).apply {
            duration = 1450L
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.REVERSE
            interpolator =
                android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }

        android.animation.ObjectAnimator.ofFloat(
            orb,
            "scaleY",
            0.78f,
            1.18f
        ).apply {
            duration = 1450L
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.REVERSE
            interpolator =
                android.view.animation.AccelerateDecelerateInterpolator()
            start()
        }

        android.animation.ObjectAnimator.ofFloat(
            orb,
            "alpha",
            0.18f,
            0.58f
        ).apply {
            duration = 1450L
            repeatCount = android.animation.ValueAnimator.INFINITE
            repeatMode = android.animation.ValueAnimator.REVERSE
            start()
        }

        // Expanding light rings.
        android.animation.ObjectAnimator.ofFloat(
            innerRing,
            "rotation",
            0f,
            360f
        ).apply {
            duration = 5200L
            repeatCount = android.animation.ValueAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }

        android.animation.ObjectAnimator.ofFloat(
            outerRing,
            "rotation",
            360f,
            0f
        ).apply {
            duration = 7200L
            repeatCount = android.animation.ValueAnimator.INFINITE
            interpolator = android.view.animation.LinearInterpolator()
            start()
        }

        // Flowing particles.
        val particleData = arrayOf(
            -62f to -28f,
            62f to -24f,
            -70f to 26f,
            70f to 30f,
            -28f to 58f,
            30f to -58f
        )

        particleData.forEachIndexed { index, position ->
            val particle = android.widget.TextView(this).apply {
                background = android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.OVAL
                    setColor(
                        if (index % 2 == 0) {
                            android.graphics.Color.argb(190, 0, 220, 255)
                        } else {
                            android.graphics.Color.argb(175, 190, 100, 255)
                        }
                    )
                }
                alpha = 0.15f
            }

            val params = android.widget.FrameLayout.LayoutParams(
                dp(6),
                dp(6),
                android.view.Gravity.CENTER
            )

            params.leftMargin = dp(position.first.toInt())
            params.topMargin = dp(position.second.toInt())

            fluidLight.addView(particle, params)

            android.animation.ObjectAnimator.ofFloat(
                particle,
                "translationX",
                -dp(12).toFloat(),
                dp(12).toFloat()
            ).apply {
                duration = 1200L + (index * 130L)
                startDelay = index * 110L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.REVERSE
                interpolator =
                    android.view.animation.AccelerateDecelerateInterpolator()
                start()
            }

            android.animation.ObjectAnimator.ofFloat(
                particle,
                "translationY",
                dp(8).toFloat(),
                -dp(8).toFloat()
            ).apply {
                duration = 1500L + (index * 100L)
                startDelay = index * 90L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.REVERSE
                interpolator =
                    android.view.animation.AccelerateDecelerateInterpolator()
                start()
            }

            android.animation.ObjectAnimator.ofFloat(
                particle,
                "alpha",
                0.08f,
                0.90f
            ).apply {
                duration = 900L + (index * 120L)
                startDelay = index * 100L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.REVERSE
                start()
            }
        }

        content.addView(fluidLight, fluidParams)

        content.addView(
            tv(
                "🏁 FINAL ANALYSIS",
                27f,
                white,
                true
            )
        )

        content.addView(
            tv(
                "LIVE ANALYSIS COMPLETE",
                10f,
                cyan,
                true
            ).apply {
                setPadding(0, dp(6), 0, dp(18))
                letterSpacing = 0.08f
            }
        )

        content.addView(
            card().apply {
                addView(
                    tv(
                        "ADAPTATION RESULT",
                        10f,
                        gold,
                        true
                    )
                )

                addView(
                    tv(
                        "You responded to the changing situation instead of staying locked to the first plan.",
                        16f,
                        white,
                        true
                    ).apply {
                        setPadding(0, dp(9), 0, dp(12))
                    }
                )

                addView(
                    tv(
                        "Your response was processed through the changing situation.",
                        12f,
                        muted
                    )
                )
            }
        )

        content.addView(space(18))

        val signalPercent = derivedSignalPercent(
            confidence = confidence,
            choiceIndex = selectedChoice
        )

        val signal = when {
            signalPercent < 50 -> "LOW SIGNAL"
            signalPercent < 75 -> "MID SIGNAL"
            else -> "HIGH SIGNAL"
        }

        val signalColor = when (signal) {
            "HIGH SIGNAL" -> green
            "MID SIGNAL" -> gold
            else -> red
        }

        content.addView(
            card().apply {
                gravity = Gravity.CENTER

                addView(
                    tv(
                        "DECISION SIGNAL",
                        9f,
                        muted,
                        true
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                )

                addView(
                    tv(
                        "${signalPercent.toInt()}%  •  $signal",
                        22f,
                        signalColor,
                        true
                    ).apply {
                        gravity = Gravity.CENTER
                        setPadding(0, dp(6), 0, dp(4))
                    }
                )

                addView(
                    tv(
                        "Signal strength from the completed analysis",
                        10f,
                        muted
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                )
            }
        )

        content.addView(space(10))

        val xpCard = card().apply {
            gravity = Gravity.CENTER

            addView(
                tv(
                    "🚀  ROCKET SPEED XP",
                    16f,
                    gold,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                }
            )

            addView(
                tv(
                    "+${signalXpFromPercent(signalPercent)} XP",
                    34f,
                    cyan,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(8), 0, dp(4))
                }
            )

            addView(
                tv(
                    "Added to your GDMIE progress",
                    11f,
                    muted
                ).apply {
                    gravity = Gravity.CENTER
                }
            )
        }

        content.addView(xpCard)

        content.addView(space(20))

        val xpButton = actionButton(
            "🚀  CLAIM XP & CONTINUE",
            purple
        )

        xpButton.setOnClickListener {
            xpButton.isEnabled = false
            xpButton.alpha = 0.75f

            val rocketOverlay = RocketCinematicView(this)

            val overlayParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            (window.decorView as ViewGroup).addView(
                rocketOverlay,
                overlayParams
            )

            rocketOverlay.bringToFront()

            rocketOverlay.startLaunch {
                (rocketOverlay.parent as? ViewGroup)?.removeView(
                    rocketOverlay
                )

                val xpResult = GDMIEGameProgress.addAnalysisXpOnce(
                    this,
                    sessionId,
                    signalXpFromPercent(signalPercent)
                )

                xpButton.text =
                    if (xpResult.awardedXp > 0) {
                        if (xpResult.levelUp) {
                            GDMIEAudioManager.playSfx(
                                this,
                                R.raw.gdmie_audio_levelup
                            )

                            "⬆  LEVEL ${xpResult.newLevel}  •  +${xpResult.awardedXp} XP"
                        } else {
                            "✓  +${xpResult.awardedXp} XP ADDED"
                        }
                    } else {
                        "✓  XP ALREADY CLAIMED"
                    }

                xpButton.alpha = 1f

                content.postDelayed({
                    showNext()
                }, 700L)
            }
        }
        content.addView(xpButton)

        // ============================================================
        // REWARDED XP — OPTIONAL EXTRA REWARD
        // ============================================================

        val rewardedButton = actionButton(
            "🎁  WATCH +10 XP",
            gold
        )

        rewardedButton.setOnClickListener {
            GDMIEAudioManager.playUiClick(this)

            RewardedAdManager.show(this) { awarded ->
                android.widget.Toast.makeText(
                    this,
                    "+$awarded XP earned!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        content.addView(
            rewardedButton,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        content.addView(
            tv(
                "Optional • Watch a short video to earn extra XP.",
                10f,
                muted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, 0)
            }
        )

        content.addView(space(10))

        content.addView(
            tv(
                "Every new LIVE ANALYSIS creates a fresh scenario.",
                10f,
                muted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, 0)
            }
        )
    }

    private fun showNext() {
        scenarioIndex = (scenarioIndex + 1) % scenarios.size
        scenario = scenarios[scenarioIndex]
        sessionId = System.currentTimeMillis().toInt()
        firstDecision = null
        showScenario()
    }

    private fun showLoading(title: String, message: String) {
        val root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(1, 4, 14))

        val world = HolographicWorldView(
            this,
            scenario.category,
            scenario.title,
            false,
            intArrayOf(cyan, purple, gold)
        )

        root.addView(
            world,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(24), dp(24), dp(24))
        }

        root.addView(
            content,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        setContentView(root)

        content.addView(
            tv(
                title,
                25f,
                white,
                true
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        content.addView(
            tv(
                "◆",
                44f,
                cyan,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(34), 0, dp(20))
            }
        )

        content.addView(
            tv(
                message,
                12f,
                muted
            ).apply {
                gravity = Gravity.CENTER
            }
        )
    }

// V3 HOLOGRAPHIC CINEMATIC WORLD
    // =====================================================

    private class RocketCinematicView(
        context: Context
    ) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val flamePath = Path()

        private var progress = 0f
        private var finished: (() -> Unit)? = null

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            setBackgroundColor(Color.TRANSPARENT)
        }

        fun startLaunch(onFinished: () -> Unit) {
            finished = onFinished

            android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 2300L

                interpolator =
                    android.view.animation.AccelerateDecelerateInterpolator()

                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate()
                }

                addListener(
                    object : android.animation.AnimatorListenerAdapter() {
                        override fun onAnimationEnd(
                            animation: android.animation.Animator
                        ) {
                            progress = 1f
                            invalidate()

                            postDelayed({
                                finished?.invoke()
                            }, 120L)
                        }
                    }
                )

                start()
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val p = progress.coerceIn(0f, 1f)

            // Smooth cinematic movement.
            val lift = smoothStep(p)

            // Depth starts gently and accelerates toward the viewer.
            val depth = smoothStep(
                ((p - 0.08f) / 0.92f)
                    .coerceIn(0f, 1f)
            )

            val centerX = width / 2f

            val startY = height * 0.70f
            val endY = height * 0.34f

            val centerY =
                startY + ((endY - startY) * lift)

            /*
             * Only the rocket drawing scales.
             * The full-screen overlay remains fixed.
             */
            val rocketScale =
                0.30f + (depth * 1.50f)

            // Small natural side drift.
            val drift =
                kotlin.math.sin(
                    p * Math.PI * 1.35
                ).toFloat() *
                    dp(20).toFloat() *
                    (1f - p)

            // Gentle turn, then return forward.
            val rotation =
                if (p < 0.62f) {
                    -7f + (p / 0.62f) * 14f
                } else {
                    7f -
                        ((p - 0.62f) / 0.38f) * 7f
                }

            // Disappear smoothly at the end.
            val rocketAlpha =
                if (p < 0.80f) {
                    1f
                } else {
                    1f -
                        ((p - 0.80f) / 0.20f)
                }

            canvas.save()

            canvas.translate(
                centerX + drift,
                centerY
            )

            canvas.rotate(rotation)

            canvas.scale(
                rocketScale,
                rocketScale
            )

            drawRocket(
                canvas,
                p,
                rocketAlpha
            )

            canvas.restore()
        }

        private fun drawRocket(
            canvas: Canvas,
            p: Float,
            alpha: Float
        ) {
            val a =
                (alpha.coerceIn(0f, 1f) * 255f)
                    .toInt()

            // Fast flame flicker for engine energy.
            val flamePulse =
                0.82f +
                    (
                        (
                            kotlin.math.sin(
                                (p * Math.PI * 24.0)
                            ) + 1.0
                        ) / 2.0
                    ).toFloat() * 0.30f

            paint.style = Paint.Style.FILL

            // Outer flame.
            paint.color =
                Color.rgb(255, 145, 35)

            paint.alpha = a

            paint.setShadowLayer(
                dp(18).toFloat(),
                0f,
                dp(8).toFloat(),
                Color.rgb(255, 70, 15)
            )

            flamePath.reset()

            flamePath.moveTo(
                -dp(16).toFloat(),
                dp(36).toFloat()
            )

            flamePath.quadTo(
                0f,
                dp(65).toFloat() * flamePulse,
                dp(16).toFloat(),
                dp(36).toFloat()
            )

            flamePath.quadTo(
                0f,
                dp(50).toFloat(),
                -dp(16).toFloat(),
                dp(36).toFloat()
            )

            flamePath.close()

            canvas.drawPath(
                flamePath,
                paint
            )

            paint.clearShadowLayer()

            // Inner flame.
            paint.color = Color.YELLOW
            paint.alpha = a

            flamePath.reset()

            flamePath.moveTo(
                -dp(8).toFloat(),
                dp(34).toFloat()
            )

            flamePath.quadTo(
                0f,
                dp(52).toFloat() * flamePulse,
                dp(8).toFloat(),
                dp(34).toFloat()
            )

            flamePath.quadTo(
                0f,
                dp(43).toFloat(),
                -dp(8).toFloat(),
                dp(34).toFloat()
            )

            flamePath.close()

            canvas.drawPath(
                flamePath,
                paint
            )

            // Rocket body.
            paint.color = Color.WHITE
            paint.alpha = a

            paint.setShadowLayer(
                dp(16).toFloat(),
                0f,
                0f,
                Color.CYAN
            )

            val body = RectF(
                -dp(17).toFloat(),
                -dp(38).toFloat(),
                dp(17).toFloat(),
                dp(34).toFloat()
            )

            canvas.drawRoundRect(
                body,
                dp(17).toFloat(),
                dp(17).toFloat(),
                paint
            )

            paint.clearShadowLayer()

            // Nose.
            paint.color = Color.CYAN
            paint.alpha = a

            val nose = Path()

            nose.moveTo(
                0f,
                -dp(58).toFloat()
            )

            nose.lineTo(
                -dp(17).toFloat(),
                -dp(30).toFloat()
            )

            nose.lineTo(
                dp(17).toFloat(),
                -dp(30).toFloat()
            )

            nose.close()

            canvas.drawPath(
                nose,
                paint
            )

            // Window.
            paint.color =
                Color.rgb(35, 45, 75)

            paint.alpha = a

            paint.setShadowLayer(
                dp(7).toFloat(),
                0f,
                0f,
                Color.CYAN
            )

            canvas.drawCircle(
                0f,
                -dp(8).toFloat(),
                dp(8).toFloat(),
                paint
            )

            paint.clearShadowLayer()

            // Left fin.
            paint.color =
                Color.rgb(150, 80, 255)

            paint.alpha = a

            val leftFin = Path()

            leftFin.moveTo(
                -dp(16).toFloat(),
                dp(16).toFloat()
            )

            leftFin.lineTo(
                -dp(32).toFloat(),
                dp(36).toFloat()
            )

            leftFin.lineTo(
                -dp(16).toFloat(),
                dp(30).toFloat()
            )

            leftFin.close()

            canvas.drawPath(
                leftFin,
                paint
            )

            // Right fin.
            val rightFin = Path()

            rightFin.moveTo(
                dp(16).toFloat(),
                dp(16).toFloat()
            )

            rightFin.lineTo(
                dp(32).toFloat(),
                dp(36).toFloat()
            )

            rightFin.lineTo(
                dp(16).toFloat(),
                dp(30).toFloat()
            )

            rightFin.close()

            canvas.drawPath(
                rightFin,
                paint
            )

            paint.alpha = 255
        }

        private fun smoothStep(
            value: Float
        ): Float {
            val x = value.coerceIn(0f, 1f)
            return x * x * (3f - 2f * x)
        }

        private fun dp(v: Int): Int =
            (
                v *
                    resources.displayMetrics.density
            ).toInt()
    }

    private class HolographicWorldView(
        context: android.content.Context,
        private val category: String,
        private val title: String,
        private val twistWorld: Boolean,
        private val palette: IntArray
    ) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()
        private var phase = 0f

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            post(object : Runnable {
                override fun run() {
                    phase += 0.012f
                    invalidate()
                    postDelayed(this, 32L)
                }
            })
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            // Deep cinematic gradient
            paint.shader = LinearGradient(
                0f,
                0f,
                w,
                h,
                Color.rgb(1, 4, 16),
                if (twistWorld) Color.rgb(24, 4, 32) else Color.rgb(2, 25, 40),
                Shader.TileMode.CLAMP
            )
            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            // Atmospheric glow
            paint.style = Paint.Style.FILL
            paint.setShadowLayer(
                dp(80).toFloat(),
                0f,
                0f,
                palette[0]
            )
            paint.color = Color.argb(
                30,
                palette[0].gdmR(),
                palette[0].gdmG(),
                palette[0].gdmB()
            )
            canvas.drawCircle(w * 0.50f, h * 0.43f, w * 0.34f, paint)
            paint.clearShadowLayer()

            // Horizon
            val horizon = h * 0.70f
            paint.color = Color.argb(100, palette[0].gdmR(), palette[0].gdmG(), palette[0].gdmB())
            paint.strokeWidth = dp(1).toFloat()

            for (i in 0..8) {
                val y = horizon + i * dp(19)
                canvas.drawLine(0f, y, w, y, paint)
            }

            // Perspective city/grid lines
            val centerX = w * 0.50f
            for (i in -8..8) {
                val x = centerX + i * dp(42)
                canvas.drawLine(centerX, horizon, x, h, paint)
            }

            // Futuristic skyline
            paint.style = Paint.Style.FILL
            for (i in 0..13) {
                val bw = dp(18 + (i % 4) * 9).toFloat()
                val bh = dp(35 + (i % 5) * 18).toFloat()
                val bx = i * (w / 13f)

                paint.color = Color.argb(105, 3, 13, 30)
                canvas.drawRect(
                    bx,
                    horizon - bh,
                    bx + bw,
                    horizon,
                    paint
                )

                paint.color = Color.argb(
                    95,
                    palette[(i + 1) % palette.size].gdmR(),
                    palette[(i + 1) % palette.size].gdmG(),
                    palette[(i + 1) % palette.size].gdmB()
                )

                var yy = horizon - bh + dp(8)
                while (yy < horizon - dp(5)) {
                    canvas.drawRect(
                        bx + dp(4),
                        yy,
                        bx + bw - dp(4),
                        yy + dp(2),
                        paint
                    )
                    yy += dp(10)
                }
            }

            // Holographic globe / world object
            val gx = w * 0.50f
            val gy = if (twistWorld) h * 0.43f else h * 0.40f
            val radius = w * 0.19f

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp(1).toFloat()
            paint.color = Color.argb(115, palette[0].gdmR(), palette[0].gdmG(), palette[0].gdmB())

            canvas.drawCircle(gx, gy, radius, paint)

            for (i in 1..4) {
                val rr = radius * i / 5f
                canvas.drawCircle(gx, gy, rr, paint)
            }

            for (i in -3..3) {
                val x = gx + i * radius / 3f
                canvas.drawOval(
                    x - radius * 0.55f,
                    gy - radius,
                    x + radius * 0.55f,
                    gy + radius,
                    paint
                )
            }

            // Rotating orbital ring
            val orbit = radius * 1.35f
            canvas.save()
            canvas.rotate(phase * 80f, gx, gy)

            paint.color = Color.argb(
                185,
                palette[1].gdmR(),
                palette[1].gdmG(),
                palette[1].gdmB()
            )
            paint.strokeWidth = dp(2).toFloat()

            canvas.drawOval(
                gx - orbit,
                gy - orbit * 0.28f,
                gx + orbit,
                gy + orbit * 0.28f,
                paint
            )

            canvas.restore()

            // Energy nodes
            paint.style = Paint.Style.FILL
            for (i in 0..5) {
                val angle = phase * 1.5f + i * 1.047f
                val nx = gx + kotlin.math.cos(angle.toDouble()).toFloat() * radius * 1.2f
                val ny = gy + kotlin.math.sin(angle.toDouble()).toFloat() * radius * 0.55f

                paint.color = palette[i % palette.size]
                paint.setShadowLayer(dp(8).toFloat(), 0f, 0f, palette[i % palette.size])
                canvas.drawCircle(nx, ny, dp(3).toFloat(), paint)
                paint.clearShadowLayer()
            }

            // Rain / data particles
            paint.strokeWidth = dp(1).toFloat()
            for (i in 0..38) {
                val x = ((i * 83 + phase * 35) % w).toFloat()
                val y = ((i * 47 + phase * 250) % h).toFloat()
                paint.color = Color.argb(45 + (i % 4) * 12, palette[0].gdmR(), palette[0].gdmG(), palette[0].gdmB())
                canvas.drawLine(x, y, x - dp(3), y + dp(12), paint)
            }

            paint.style = Paint.Style.FILL
        }

        private fun dp(v: Int): Int =
            (v * resources.displayMetrics.density).toInt()
    }

    private class HolographicChoiceView(
        context: android.content.Context,
        private val letter: String,
        private val title: String,
        private val subtitle: String,
        private val glowColor: Int
    ) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val path = Path()
        private var selected = false

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        fun setSelectedState(value: Boolean) {
            selected = value
            animate()
                .scaleX(if (value) 1.045f else 1f)
                .scaleY(if (value) 1.045f else 1f)
                .setDuration(160L)
                .start()
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            val alpha = if (selected) 175 else 92

            paint.style = Paint.Style.FILL
            paint.color = Color.argb(40, glowColor.gdmR(), glowColor.gdmG(), glowColor.gdmB())
            paint.setShadowLayer(
                dp(18).toFloat(),
                0f,
                dp(4).toFloat(),
                glowColor
            )

            path.reset()
            path.moveTo(dp(10).toFloat(), 0f)
            path.lineTo(w - dp(10), 0f)
            path.lineTo(w.toFloat(), dp(14).toFloat())
            path.lineTo(w - dp(10), h)
            path.lineTo(dp(10).toFloat(), h)
            path.lineTo(0f, h - dp(14))
            path.close()

            canvas.drawPath(path, paint)
            paint.clearShadowLayer()

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = if (selected) dp(2).toFloat() else dp(1).toFloat()
            paint.color = Color.argb(
                alpha,
                glowColor.gdmR(),
                glowColor.gdmG(),
                glowColor.gdmB()
            )
            canvas.drawPath(path, paint)

            // Energy base
            paint.style = Paint.Style.FILL
            paint.color = Color.argb(
                if (selected) 170 else 90,
                glowColor.gdmR(),
                glowColor.gdmG(),
                glowColor.gdmB()
            )
            canvas.drawRect(
                dp(9).toFloat(),
                h - dp(4),
                w - dp(9),
                h,
                paint
            )

            // Letter
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = dp(22).toFloat()
            paint.color = Color.WHITE
            paint.setShadowLayer(
                dp(8).toFloat(),
                0f,
                0f,
                glowColor
            )
            canvas.drawText(
                letter,
                dp(13).toFloat(),
                dp(28).toFloat(),
                paint
            )

            // Title — adaptive two-line rendering
            var titleSize = 13f
            paint.textSize = dp(titleSize.toInt()).toFloat()

            fun wrapTitle(): MutableList<String> {
                val result = mutableListOf<String>()
                val words = title.trim().split(Regex("\\s+"))
                var line = ""

                for (word in words) {
                    val candidate = if (line.isEmpty()) word else "$line $word"

                    if (paint.measureText(candidate) <= w - dp(26)) {
                        line = candidate
                    } else {
                        if (line.isNotEmpty()) {
                            result.add(line)
                        }
                        line = word
                    }
                }

                if (line.isNotEmpty()) {
                    result.add(line)
                }

                return result
            }

            var titleLines = wrapTitle()

            if (titleLines.size > 2) {
                titleSize = 12f
                paint.textSize = dp(titleSize.toInt()).toFloat()
                titleLines = wrapTitle()
            }

            if (titleLines.size > 2) {
                titleSize = 11f
                paint.textSize = dp(titleSize.toInt()).toFloat()
                titleLines = wrapTitle()
            }

            paint.color = Color.WHITE

            val titleX = dp(13).toFloat()
            val titleStartY = dp(53).toFloat()
            val titleLineHeight = dp(13).toFloat()

            titleLines.take(2).forEachIndexed { index, line ->
                canvas.drawText(
                    line,
                    titleX,
                    titleStartY + (index * titleLineHeight),
                    paint
                )
            }

            // Subtitle — automatically moves below a one/two-line title
            paint.textSize = dp(10).toFloat()
            paint.color = Color.WHITE
            paint.setShadowLayer(
                dp(4).toFloat(),
                0f,
                0f,
                glowColor
            )

            val subtitleY =
                titleStartY +
                (titleLines.take(2).size * titleLineHeight) +
                dp(4)

            canvas.drawText(
                subtitle,
                titleX,
                subtitleY,
                paint
            )
            paint.clearShadowLayer()

            // holographic scan line
            val scan = ((System.currentTimeMillis() / 12L) % h.toLong()).toFloat()
            paint.color = Color.argb(80, glowColor.gdmR(), glowColor.gdmG(), glowColor.gdmB())
            canvas.drawRect(
                dp(8).toFloat(),
                scan,
                w - dp(8),
                scan + dp(1),
                paint
            )

            postInvalidateDelayed(45L)
        }

        private fun dp(v: Int): Int =
            (v * resources.displayMetrics.density).toInt()
    }

    private class HolographicLockView(
        context: android.content.Context,
        private val glowColor: Int,
        private val label: String = "LOCK DECISION"
    ) : TextView(context) {

        init {
            gravity = Gravity.CENTER
            text = "◈   $label   ◈"
            textSize = 13f
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.10f
            setPadding(dp(10), 0, dp(10), 0)

            background = GradientDrawable().apply {
                setColor(Color.argb(72, glowColor.gdmR(), glowColor.gdmG(), glowColor.gdmB()))
                cornerRadius = dp(13).toFloat()
                setStroke(
                    dp(2),
                    Color.argb(
                        205,
                        glowColor.gdmR(),
                        glowColor.gdmG(),
                        glowColor.gdmB()
                    )
                )
            }

            setShadowLayer(dp(16).toFloat(), 0f, 0f, glowColor)

            post(object : Runnable {
                override fun run() {
                    alpha = 0.72f + ((System.currentTimeMillis() / 500L) % 10) / 100f
                    postDelayed(this, 500L)
                }
            })
        }

        private fun dp(v: Int): Int =
            (v * resources.displayMetrics.density).toInt()
    }


    // LIVE_VISUAL_PASS_V1
    private fun choiceView(index: Int, label: String): TextView {
        return tv(
            "${('A'.code + index).toChar()}   $label",
            14f,
            white,
            true
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = rounded(
                panel2,
                16,
                if (selectedChoice == index) cyan else Color.rgb(45, 70, 105),
                1
            )

              alpha = 0f
              translationY = dp(10).toFloat()

              post {
                  animate()
                      .alpha(1f)
                      .translationY(0f)
                      .setStartDelay(index * 90L)
                      .setDuration(420L)
                      .setInterpolator(
                          android.view.animation.DecelerateInterpolator()
                      )
                      .start()
              }

            setOnClickListener {
                GDMIEAudioManager.playUiClick(this@LiveAnalysisActivity)
                selectedChoice = index
                refreshChoices()
            }
        }
    }

    private fun refreshChoices() {
        val choices = content.findViewsWithTextChoice()

        choices.forEachIndexed { index, view ->
            view.background = rounded(
                if (index == selectedChoice) Color.rgb(25, 45, 70) else panel2,
                16,
                if (index == selectedChoice) cyan else Color.rgb(45, 70, 105),
                if (index == selectedChoice) 2 else 1
            )

            if (index == selectedChoice) {
                view.animate()
                    .scaleX(1.025f)
                    .scaleY(1.025f)
                    .setDuration(110L)
                    .withEndAction {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150L)
                            .start()
                    }
                    .start()
            }
        }
    }

    private fun actionButton(text: String, color: Int): TextView {
        return tv(
            text,
            14f,
            white,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(17), 0, dp(17))
            background = rounded(color, 18, cyan, 1)
        }
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = rounded(panel, 20, Color.rgb(35, 55, 85), 1)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun tv(
        text: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = size
            setTextColor(color)
            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
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

    private fun cleanResult(value: String): String {
        val clean = value.trim()
        return if (clean.isBlank()) {
            "The changing situation was processed through the GDMIE decision engine."
        } else {
            clean.take(220)
        }
    }

    private fun LinearLayout.findViewsWithTextChoice(): List<TextView> {
        val result = mutableListOf<TextView>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child is TextView &&
                child.text.toString().matches(Regex("^[ABC]\\s{2,}.*"))
            ) {
                result.add(child)
            }

            if (child is LinearLayout) {
                result.addAll(child.findViewsWithTextChoice())
            }
        }

        return result
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    data class MathChallenge(
        val expression: String,
        val result: Double,
        val operationLabel: String
    ) {
        companion object {

            fun fromGDMIE(
                presentValue: Double,
                targetValue: Double
            ): MathChallenge {

                val expectedValue =
                    (presentValue + targetValue) / 2.0

                val expression =
                    "((%.0f × 3) − %.0f + (%.0f × 3) − %.0f) ÷ 4 = %.1f"
                        .format(
                            presentValue,
                            presentValue,
                            targetValue,
                            targetValue,
                            expectedValue
                        )

                return MathChallenge(
                    expression = expression,
                    result = expectedValue,
                    operationLabel = "×  −  +  ÷"
                )
            }
        }
    }

    data class Choice(
        val text: String,
        val current: CurrentState,
        val context: ContextState,
        val momentum: MomentumState,
        val risk: RiskLevel,
        val timing: TimingState
    )

    data class Scenario(
        val category: String,
        val title: String,
        val scene: String,
        val question: String,
        val goal: GoalLevel,
        val choices: List<Choice>,
        val twist: String,
        val adaptQuestion: String,
        val adaptChoices: List<Choice>
    )

    companion object {

        private val lastQuestionIndex = mutableMapOf<String, Int>()

        private fun nextDynamicQuestion(
            key: String,
            pool: List<String>
        ): String {
            if (pool.isEmpty()) {
                return ""
            }

            val previous = lastQuestionIndex[key]
            var index = (System.currentTimeMillis() % pool.size).toInt()

            if (pool.size > 1 && previous != null && index == previous) {
                index = (index + 1) % pool.size
            }

            lastQuestionIndex[key] = index
            return pool[index]
        }


        // EXTRA_200_DYNAMIC_QUESTIONS
        private val extraRound1Questions = mapOf(
            "THE SIGNAL CHANGES" to listOf(
                "What deserves your attention first?",
                "Which signal should guide your next step?",
                "How quickly should you respond?",
                "What would you verify before acting?",
                "Which response reduces uncertainty?",
                "What information matters most right now?",
                "Where should your attention go first?",
                "Which move keeps the situation under control?",
                "What would you examine before committing?",
                "How do you handle the sudden change?",
                "Which factor should influence your first decision?",
                "What would you monitor first?",
                "Which response gives you the clearest path forward?",
                "How would you balance action and verification?",
                "What should you consider before moving?",
                "Which detail could change your response?",
                "What is the most important signal here?",
                "How would you react to the unexpected shift?",
                "Which action would you take first?",
                "What should be confirmed before proceeding?",
                "Where is the biggest uncertainty?",
                "Which option gives you better control?",
                "What would you watch most closely?",
                "How do you respond without overreacting?",
                "Which signal deserves a second look?",
                "What should happen before the next move?",
                "How would you manage the changing conditions?",
                "Which information would change your choice?",
                "What is your immediate priority?",
                "How do you decide when the signal is unclear?",
                "Which factor should you reassess?",
                "What would make you change direction?",
                "How do you act while the situation is evolving?",
                "Which response best fits the current conditions?"
            ),
            "THE DEADLINE MOVES" to listOf(
                "What needs attention first?",
                "Which task has the greatest impact?",
                "How do you respond to the time pressure?",
                "What should move to the top of the list?",
                "Which resource matters most now?",
                "What can be completed first?",
                "How do you protect the key outcome?",
                "Which task can be reduced or simplified?",
                "What should you reconsider after the deadline changes?",
                "Where should limited resources go?",
                "Which action creates the fastest progress?",
                "What can wait without hurting the outcome?",
                "How do you balance speed and quality?",
                "Which commitment should come first?",
                "What is the critical task right now?",
                "How do you handle competing priorities?",
                "Which task should receive more resources?",
                "What would you remove from the plan?",
                "Which part of the schedule needs attention?",
                "How do you respond when time becomes limited?",
                "What should be protected from delay?",
                "Which action has the highest immediate value?",
                "What would you simplify first?",
                "Where is the biggest time risk?",
                "Which task should be reconsidered?",
                "How do you keep progress moving?",
                "What deserves priority under pressure?",
                "Which deadline change matters most?",
                "What should you complete before anything else?",
                "How would you reorganise the remaining work?",
                "Which part of the plan can adapt?",
                "What should happen before the deadline?",
                "How do you decide between urgent tasks?"
            ),
            "THE HIDDEN PATTERN" to listOf(
                "What detail do you investigate first?",
                "Which signal looks most important?",
                "How do you test the obvious pattern?",
                "What could invalidate your first assumption?",
                "Which detail does not fit?",
                "What would you compare first?",
                "How do you separate signal from noise?",
                "Which pattern deserves closer attention?",
                "What evidence would you look for?",
                "How do you verify the unusual detail?",
                "Which observation could change your view?",
                "What should be checked before deciding?",
                "Where is the strongest clue?",
                "Which part of the pattern is uncertain?",
                "How would you investigate a contradiction?",
                "What would you question first?",
                "Which signal needs confirmation?",
                "How do you avoid following the obvious answer too quickly?",
                "What evidence supports the pattern?",
                "Which detail could reveal a different pattern?",
                "How would you test your current interpretation?",
                "What should be examined next?",
                "Which clue deserves another look?",
                "How do you respond when the pattern is incomplete?",
                "What information would increase your confidence?",
                "Which assumption should be challenged?",
                "What would you compare before acting?",
                "How do you handle conflicting signals?",
                "Which pattern is worth investigating further?",
                "What would make you change your interpretation?",
                "Where could the hidden pattern be?",
                "Which observation matters most?",
                "How would you confirm the emerging pattern?"
            )
        )

        private val extraRound2Questions = mapOf(
            "THE SIGNAL CHANGES" to listOf(
                "What changes after the second signal?",
                "Which new information matters most?",
                "How should your response evolve?",
                "What would you reassess after the confirmation?",
                "Which part of your first decision changes?",
                "How do you adapt to the stronger signal?",
                "What should be checked again?",
                "Which risk has changed?",
                "What becomes your new priority?",
                "How does the latest information affect your plan?",
                "What would you change first?",
                "Which response now makes more sense?",
                "How do you handle the updated conditions?",
                "What should you stop doing?",
                "Which assumption needs updating?",
                "How would you adjust your timing?",
                "What new factor should guide you?",
                "Which part of the situation is different now?",
                "What would you confirm before continuing?",
                "How do you respond to the changed signal?",
                "Which risk deserves more attention now?",
                "What is your revised first move?",
                "How should your priorities shift?",
                "Which information changes the balance?",
                "What would you reassess before committing?",
                "How do you adapt without losing control?",
                "Which direction now deserves attention?",
                "What would make you revise your decision?",
                "How does the new evidence affect your next step?",
                "Which response fits the updated situation?",
                "What should happen next?",
                "How do you manage the new uncertainty?",
                "Which signal now has the most weight?",
                "What is your adjusted approach?"
            ),
            "THE DEADLINE MOVES" to listOf(
                "What changes after the task becomes unavailable?",
                "Which priority should replace the lost task?",
                "How do you adapt to the reduced resources?",
                "What should move up the list now?",
                "Which outcome must remain protected?",
                "How would you revise the schedule?",
                "What can be simplified immediately?",
                "Which task becomes the next focus?",
                "How do you respond to the disruption?",
                "What should be removed from the plan?",
                "Which resource should be redirected?",
                "What becomes urgent now?",
                "How would you adjust the remaining work?",
                "Which task can replace the unavailable one?",
                "What should you reassess after the change?",
                "How do you maintain progress with fewer options?",
                "Which commitment should be protected?",
                "What can be delayed safely?",
                "How does the new constraint change your plan?",
                "Which action should happen next?",
                "What is the new critical path?",
                "How would you rebalance the workload?",
                "Which task has become more important?",
                "What should receive attention immediately?",
                "How do you handle the tighter schedule now?",
                "Which part of the plan needs redesign?",
                "What would you prioritise after the disruption?",
                "How should your next step change?",
                "Which goal should remain unchanged?",
                "What would you sacrifice to protect the outcome?",
                "How do you adapt without losing the main objective?",
                "Which option best fits the new constraint?",
                "What should be completed before moving on?"
            ),
            "THE HIDDEN PATTERN" to listOf(
                "What do you do after the pattern changes?",
                "Which new detail deserves attention?",
                "How do you update your interpretation?",
                "What should you compare now?",
                "Which signal changed the pattern?",
                "How do you respond to the evolving evidence?",
                "What assumption should be reconsidered?",
                "Which clue now matters most?",
                "How would you test the revised pattern?",
                "What should you verify next?",
                "Which interpretation is worth examining?",
                "How do you handle the changing evidence?",
                "What would you investigate after the shift?",
                "Which detail could explain the change?",
                "How should your analysis adapt?",
                "What new evidence would you seek?",
                "Which signal deserves confirmation?",
                "How do you avoid locking onto the first pattern?",
                "What should be compared again?",
                "Which part of your reasoning needs revision?",
                "How would you respond to the new pattern?",
                "What changes in your next analytical step?",
                "Which clue now has greater importance?",
                "How do you handle an incomplete pattern?",
                "What would increase confidence in your revised view?",
                "Which assumption is no longer reliable?",
                "What should you examine before deciding?",
                "How does the new detail affect your choice?",
                "Which path should your analysis follow?",
                "What would make you rethink the pattern?",
                "How should you update your decision?",
                "Which evidence should come next?",
                "What is your revised analytical move?"
            )
        )

        
// GDMIE_500_DYNAMIC_QUESTIONS
// 250 new Round 01 + 250 new Round 02 questions.
// Existing 200-question pools remain active.

private fun gdmie500Questions(
    scenarioTitle: String,
    round: Int
): List<String> {

    val contexts = when (scenarioTitle) {
        "THE SIGNAL CHANGES" -> listOf(
            "a new signal has appeared",
            "two signals point in different directions",
            "the latest information is incomplete",
            "the situation is changing faster than expected",
            "the original assumption may no longer hold"
        )

        "THE DEADLINE MOVES" -> listOf(
            "the deadline has moved closer",
            "resources are limited",
            "one planned task is no longer available",
            "several priorities are competing for attention",
            "the original schedule is under pressure"
        )

        "THE HIDDEN PATTERN" -> listOf(
            "one detail does not fit the obvious pattern",
            "a second pattern is starting to appear",
            "the available evidence is incomplete",
            "one signal may be misleading",
            "the pattern changes while you are checking it"
        )

        else -> listOf(
            "the situation is changing",
            "the latest information is incomplete",
            "priorities are competing",
            "the original assumption may be wrong",
            "a new signal has appeared"
        )
    }

    val prompts = when (scenarioTitle) {
        "THE SIGNAL CHANGES" -> listOf(
            "identify the most important signal",
            "decide what deserves immediate attention",
            "separate useful information from noise",
            "choose how much evidence is enough",
            "protect the decision from a false signal",
            "balance speed with verification"
        )

        "THE DEADLINE MOVES" -> listOf(
            "identify the highest-impact task",
            "reorder the available priorities",
            "protect the most important outcome",
            "decide what can be simplified",
            "allocate the limited resources",
            "balance urgency with quality"
        )

        "THE HIDDEN PATTERN" -> listOf(
            "test the strongest explanation",
            "check the unusual detail",
            "compare competing patterns",
            "separate evidence from assumption",
            "look for the missing information",
            "decide when the pattern is reliable enough"
        )

        else -> listOf(
            "identify the key factor",
            "compare the available options",
            "check the strongest assumption",
            "protect the desired outcome",
            "manage the main uncertainty",
            "balance speed with accuracy"
        )
    }

    val styles = if (round == 1) {
        listOf(
            "What is the best way to %s when %s?",
            "How should you %s while %s?",
            "Which approach best helps you %s if %s?"
        )
    } else {
        listOf(
            "After the situation changes, how should you %s when %s?",
            "With the new information, what is the best way to %s while %s?",
            "What should you reconsider before you %s if %s?"
        )
    }

    val questions = mutableListOf<String>()

    for (context in contexts) {
        for (prompt in prompts) {
            for (style in styles) {
                questions.add(style.format(prompt, context))
            }
        }
    }

    val limit = when (scenarioTitle) {
        "THE SIGNAL CHANGES" -> 84
        "THE DEADLINE MOVES" -> 83
        "THE HIDDEN PATTERN" -> 83
        else -> 0
    }

    return questions.distinct().take(limit)
}

private fun dynamicQuestionFor(scenario: Scenario): String {
            val pool = when (scenario.title) {

                "THE SIGNAL CHANGES" -> listOf(
                    "What is your first move?",
                    "Which signal deserves your attention first?",
                    "How do you respond to the changing signal?",
                    "What would you act on right now?",
                    "Which approach fits the situation?",
                    "What should you verify before moving?",
                    "Where do you focus first?",
                    "What matters most at this moment?",
                    "Which response would you choose?",
                    "How would you handle the uncertainty?",
                    "What is your next move?",
                    "Which signal should influence your decision?"
                )

                "THE DEADLINE MOVES" -> listOf(
                    "What do you prioritise?",
                    "What should you act on first?",
                    "Which task needs attention now?",
                    "How do you respond to the shorter deadline?",
                    "Where should your focus go first?",
                    "Which priority changes first?",
                    "What would you protect first?",
                    "How do you adjust the plan?",
                    "Which action creates the most progress?",
                    "What deserves immediate attention?",
                    "What is your next priority?",
                    "How do you handle the limited time?"
                )

                "THE HIDDEN PATTERN" -> listOf(
                    "Which approach do you take?",
                    "What do you examine first?",
                    "Which signal do you trust first?",
                    "How do you investigate the pattern?",
                    "What would you check before deciding?",
                    "Which detail matters most?",
                    "What is your first analytical move?",
                    "How do you handle the unusual detail?",
                    "Which pattern deserves your attention?",
                    "What would you verify first?",
                    "Where do you focus your analysis?",
                    "How do you respond to the unclear pattern?"
                )

                else -> listOf(
                    scenario.question
                )
            }

            val expandedPool =
            pool +
                (extraRound1Questions[scenario.title] ?: emptyList()) +
                gdmie500Questions(scenario.title, 1)

            return nextDynamicQuestion(
                "ROUND1:${scenario.title}",
                expandedPool
            )
        }

        private fun dynamicAdaptQuestionFor(scenario: Scenario): String {
            val pool = when (scenario.title) {

                "THE SIGNAL CHANGES" -> listOf(
                    "How do you respond to the new information?",
                    "What changes in your approach now?",
                    "Which signal changes your decision?",
                    "What would you reassess first?",
                    "How do you adapt to the confirmed signal?",
                    "What is your revised response?",
                    "Which part of your plan changes?",
                    "What should you adjust first?",
                    "How does the new signal affect your decision?",
                    "What would you do differently now?",
                    "Which option becomes more relevant?",
                    "How do you handle the updated situation?"
                )

                "THE DEADLINE MOVES" -> listOf(
                    "What changes now?",
                    "What do you adjust first?",
                    "Which priority changes after the disruption?",
                    "How do you respond to the unavailable task?",
                    "What becomes the new focus?",
                    "How do you adapt the plan?",
                    "Which task should replace the missing one?",
                    "What would you reconsider now?",
                    "How do you protect the most important outcome?",
                    "What is your revised priority?",
                    "Which action should come next?",
                    "How do you respond to the reduced resources?"
                )

                "THE HIDDEN PATTERN" -> listOf(
                    "What is your adaptation?",
                    "How do you respond to the changing pattern?",
                    "What would you reassess now?",
                    "Which signal matters after the pattern shifts?",
                    "How do you update your approach?",
                    "What changes in your analysis?",
                    "Which detail should you examine next?",
                    "What would you verify again?",
                    "How do you handle the evolving pattern?",
                    "Which path do you follow now?",
                    "What becomes your next analytical step?",
                    "How does the new pattern affect your decision?"
                )

                else -> listOf(
                    scenario.adaptQuestion
                )
            }

            val expandedPool =
            pool +
                (extraRound2Questions[scenario.title] ?: emptyList()) +
                gdmie500Questions(scenario.title, 2)

            return nextDynamicQuestion(
                "ROUND2:${scenario.title}",
                expandedPool
            )
        }

        private val scenarios = listOf(

            Scenario(
                category = "Changing Conditions",
                title = "THE SIGNAL CHANGES",
                scene = "A live operation is moving normally when a new signal suddenly appears. You have only a short window to react.",
                question = "What is your first move?",
                goal = GoalLevel.MODERATE_IMPROVEMENT,
                choices = listOf(
                    Choice(
                        "Act immediately",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.HIGH,
                        TimingState.NOW
                    ),
                    Choice(
                        "Pause and verify",
                        CurrentState.UNCERTAIN,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.MEDIUM,
                        TimingState.SOON
                    ),
                    Choice(
                        "Wait for more information",
                        CurrentState.UNCERTAIN,
                        ContextState.UNCERTAIN,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                ),
                twist = "A second signal confirms that the original situation is no longer exactly the same.",
                adaptQuestion = "How do you respond to the new information?",
                adaptChoices = listOf(
                    Choice(
                        "Commit to the new direction",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.MEDIUM,
                        TimingState.NOW
                    ),
                    Choice(
                        "Reduce risk and reassess",
                        CurrentState.UNCERTAIN,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.LOW,
                        TimingState.SOON
                    ),
                    Choice(
                        "Hold until the signal is clearer",
                        CurrentState.UNCERTAIN,
                        ContextState.UNCERTAIN,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                )
            ),

            Scenario(
                category = "Project Simulation",
                title = "THE DEADLINE MOVES",
                scene = "A project is progressing, but a key deadline suddenly moves closer. Resources remain limited.",
                question = "What do you prioritise?",
                goal = GoalLevel.MAJOR_IMPROVEMENT,
                choices = listOf(
                    Choice(
                        "Push the highest-impact task",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.MEDIUM,
                        TimingState.NOW
                    ),
                    Choice(
                        "Recheck priorities",
                        CurrentState.STABLE,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.LOW,
                        TimingState.SOON
                    ),
                    Choice(
                        "Protect the current plan",
                        CurrentState.STABLE,
                        ContextState.UNCERTAIN,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                ),
                twist = "One planned task becomes unavailable. The original schedule can no longer be followed exactly.",
                adaptQuestion = "What changes now?",
                adaptChoices = listOf(
                    Choice(
                        "Replace it with the next highest-impact task",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.MEDIUM,
                        TimingState.NOW
                    ),
                    Choice(
                        "Simplify the target",
                        CurrentState.UNCERTAIN,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.LOW,
                        TimingState.SOON
                    ),
                    Choice(
                        "Delay until resources return",
                        CurrentState.WEAK,
                        ContextState.UNFAVOURABLE,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                )
            ),

            Scenario(
                category = "Logic / Puzzle",
                title = "THE HIDDEN PATTERN",
                scene = "Three signals appear on a control board. One pattern looks obvious, but one detail does not fit.",
                question = "Which approach do you take?",
                goal = GoalLevel.SMALL_IMPROVEMENT,
                choices = listOf(
                    Choice(
                        "Follow the obvious pattern",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.HIGH,
                        TimingState.NOW
                    ),
                    Choice(
                        "Check the unusual detail",
                        CurrentState.UNCERTAIN,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.LOW,
                        TimingState.SOON
                    ),
                    Choice(
                        "Wait for another signal",
                        CurrentState.UNCERTAIN,
                        ContextState.UNCERTAIN,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                ),
                twist = "The unusual detail changes while you are checking it. The pattern may be evolving.",
                adaptQuestion = "What is your adaptation?",
                adaptChoices = listOf(
                    Choice(
                        "Update the pattern immediately",
                        CurrentState.STRONG,
                        ContextState.FAVOURABLE,
                        MomentumState.IMPROVING,
                        RiskLevel.MEDIUM,
                        TimingState.NOW
                    ),
                    Choice(
                        "Compare both patterns",
                        CurrentState.UNCERTAIN,
                        ContextState.NORMAL,
                        MomentumState.STABLE,
                        RiskLevel.LOW,
                        TimingState.SOON
                    ),
                    Choice(
                        "Wait for confirmation",
                        CurrentState.UNCERTAIN,
                        ContextState.UNCERTAIN,
                        MomentumState.DECLINING,
                        RiskLevel.LOW,
                        TimingState.LATER
                    )
                )
            )
        )
    }
}
