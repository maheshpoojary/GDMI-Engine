package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.LinearGradient
import android.graphics.Shader
import android.view.MotionEvent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
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
                scenario.question,
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
                topMargin = dp(338)
            }
        )

        // -------------------------------------------------
        // HOLOGRAPHIC LOCK CONTROL
        // -------------------------------------------------
        val lock = HolographicLockView(this, palette[0])

        lock.setOnClickListener {
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
                topMargin = dp(490)
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
                topMargin = dp(562)
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
            timing = choice.timing
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
                scenario.adaptQuestion,
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
            timing = choice.timing
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
                    showFinal(result.decision)
                }
            } catch (_: Exception) {
                runOnUiThread {
                    showFinal(engineDecision)
                }
            }
        }
    }

    private fun showFinal(resultText: String) {
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

        content.addView(
            card().apply {
                gravity = Gravity.CENTER

                addView(
                    tv(
                        "🚀 ROCKET SPEED XP",
                        16f,
                        gold,
                        true
                    ).apply {
                        gravity = Gravity.CENTER
                    }
                )

                addView(
                    tv(
                        "+20 XP",
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
        )

        content.addView(space(20))

        val xpButton = actionButton(
            "🚀  CLAIM XP & CONTINUE",
            purple
        )

        xpButton.setOnClickListener {
            xpButton.isEnabled = false

            val awarded = GDMIEGameProgress.addAnalysisXpOnce(
                this,
                sessionId,
                20
            )

            xpButton.text =
                if (awarded > 0) {
                    "✓  +$awarded XP ADDED"
                } else {
                    "✓  XP ALREADY CLAIMED"
                }

            xpButton.alpha = 0.75f

            content.postDelayed({
                showNext()
            }, 900)
        }

        content.addView(xpButton)

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
        content.removeAllViews()

        content.gravity = Gravity.CENTER_HORIZONTAL

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


    // =====================================================
    // V3 HOLOGRAPHIC CINEMATIC WORLD
    // =====================================================

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
            paint.textSize = dp(18).toFloat()
            paint.color = glowColor
            canvas.drawText(
                letter,
                dp(13).toFloat(),
                dp(28).toFloat(),
                paint
            )

            // Title
            paint.textSize = dp(11).toFloat()
            paint.color = Color.WHITE
            canvas.drawText(
                title.take(19),
                dp(13).toFloat(),
                dp(53).toFloat(),
                paint
            )

            // Subtitle
            paint.textSize = dp(7).toFloat()
            paint.color = Color.argb(185, 185, 205, 225)
            canvas.drawText(
                subtitle,
                dp(13).toFloat(),
                dp(73).toFloat(),
                paint
            )

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
