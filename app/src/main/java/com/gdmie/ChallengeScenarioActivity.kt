package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.gdmie.audio.GDMIEAudioManager
import com.gdmie.game.GDMIEGameProgress


class ChallengeScenarioActivity : Activity() {
    private var currentDailyScenarioTheme: String = "GENERAL"
    private var currentDailyChallengeId: String = ""


    private val bg = Color.rgb(3, 6, 14)
    private val surface = Color.rgb(10, 18, 35)
    private val surface2 = Color.rgb(16, 27, 52)
    private val cyan = Color.rgb(35, 205, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val gold = Color.rgb(255, 205, 70)
    private val green = Color.rgb(45, 225, 135)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val dim = Color.rgb(90, 115, 145)
    private val border = Color.rgb(35, 75, 115)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun rounded(
        color: Int,
        radius: Int = 20,
        strokeColor: Int = border
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), strokeColor)
        }
    }

    private fun tv(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        includeFontPadding = false
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val mode = intent.getStringExtra("challenge_mode") ?: "DAILY"

        val title = when (mode) {
            "TIME_ATTACK" -> "⏱ TIME ATTACK"
            "QUICK" -> "⚡ QUICK CHALLENGE"
            else -> "🏆 DAILY CHALLENGE"
        }

        val xp = when (mode) {
            "TIME_ATTACK" -> "+20 XP"
            "QUICK" -> "+10 XP"
            else -> "+30 XP"
        }

        // DAILY CHALLENGE uses the new Decision Arena flow.
        if (mode == "DAILY") {
            if (intent.getBooleanExtra("next_challenge", false)) {
                val prefs = getSharedPreferences("GDMIE_DAILY_SCENARIO", MODE_PRIVATE)
                val currentIndex = prefs.getInt("rotation_index", 0)
                prefs.edit()
                    .putInt("rotation_index", currentIndex + 1)
                    .apply()
            }

            showDailyDecisionArena()
            return
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE CHALLENGE", 11f, cyan, true).apply {
                letterSpacing = 0.12f
            }
        )

        content.addView(
            tv(title, 29f, white, true).apply {
                setPadding(0, dp(5), 0, dp(5))
            }
        )

        content.addView(
            tv(
                "Think clearly. Make your decision.\nThen check what happened.",
                13f,
                muted
            ).apply {
                setLineSpacing(2f, 1f)
                setPadding(0, dp(5), 0, dp(20))
            }
        )

        val xpCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(surface, 18, gold)
        }

        xpCard.addView(
            tv("REWARD", 10f, gold, true),
            LinearLayout.LayoutParams(0, dp(40), 1f)
        )

        xpCard.addView(
            tv(xp, 18f, gold, true).apply {
                gravity = Gravity.CENTER
            }
        )

        content.addView(
            xpCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(70)
            ).apply {
                bottomMargin = dp(16)
            }
        )

        val currentLevel = GDMIEGameProgress.getLevel(this)
        val gameScenario = getGameScenario(mode, currentLevel)

        val scenario = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            background = rounded(surface2, 22, cyan)
        }

        scenario.addView(
            tv(
                "🎯  ${gameScenario.category.uppercase()}",
                11f,
                cyan,
                true
            )
        )

        scenario.addView(
            tv(
                gameScenario.title,
                20f,
                white,
                true
            ).apply {
                setPadding(0, dp(8), 0, dp(8))
            }
        )

        scenario.addView(
            tv(
                gameScenario.situation,
                15f,
                white,
                true
            ).apply {
                setLineSpacing(3f, 1f)
            }
        )

        val signalRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(14), 0, 0)
        }

        fun signalChip(text: String, color: Int): TextView {
            return TextView(this).apply {
                this.text = text
                textSize = 11f
                setTextColor(white)
                setPadding(dp(9), dp(8), dp(9), dp(8))
                background = rounded(color, 14, color)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dp(42),
                    1f
                ).apply {
                    marginEnd = dp(6)
                }
            }
        }

        signalRow.addView(signalChip(gameScenario.signal1, cyan))
        signalRow.addView(signalChip(gameScenario.signal2, purple))
        signalRow.addView(signalChip(gameScenario.signal3, gold))

        scenario.addView(signalRow)

        content.addView(
            scenario,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        )

        val decisionCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = rounded(surface, 20, purple)
        }

        decisionCard.addView(
            tv("YOUR DECISION", 11f, purple, true)
        )

        decisionCard.addView(
            tv(
                "What would you do?",
                19f,
                white,
                true
            ).apply {
                setPadding(0, dp(7), 0, dp(12))
            }
        )

        var selectedDecision = ""

        fun option(
            id: Int,
            title: String,
            description: String
        ): TextView {
            return TextView(this).apply {
                text = "$title\n$description"
                textSize = 15f
                setTextColor(white)
                setTypeface(null, Typeface.BOLD)
                setPadding(dp(16), dp(14), dp(16), dp(14))
                background = rounded(surface2, 16, border)
                isClickable = true

                setOnClickListener {
                    GDMIEAudioManager.playUiClick(this@ChallengeScenarioActivity)
                    selectedDecision = title

                    for (i in 0 until decisionCard.childCount) {
                        val child = decisionCard.getChildAt(i)
                        if (child is TextView && child.tag == "DECISION_OPTION") {
                            child.background = rounded(surface2, 16, border)
                        }
                    }

                    background = rounded(cyan, 16, cyan)
                    setTextColor(bg)
                }

                tag = "DECISION_OPTION"

                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(10)
                }
            }
        }

        decisionCard.addView(
            option(
                1001,
                "A • ${gameScenario.optionA}",
                "Commit to this path based on the strongest signal available."
            )
        )

        decisionCard.addView(
            option(
                1002,
                "B • ${gameScenario.optionB}",
                "Hold this position and use the conflicting signal before committing."
            )
        )

        decisionCard.addView(
            option(
                1003,
                "C • ${gameScenario.optionC}",
                "Choose this route to balance opportunity, timing and risk."
            )
        )

        content.addView(
            decisionCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(16)
            }
        )

        content.addView(
            tv(
                "GDMIE supports your thinking.\nYou make the final decision.",
                11f,
                dim
            ).apply {
                gravity = Gravity.CENTER
                setPadding(dp(10), 0, dp(10), 0)
            }
        )

        val lockButton = TextView(this).apply {
            text = "🔒  LOCK MY DECISION"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {
                if (selectedDecision.isBlank()) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose A, B or C first.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@ChallengeScenarioActivity,
                    R.raw.gdmie_audio_decision
                )

                showQuickRethinkStage(
                    selectedDecision,
                    mode,
                    gameScenario
                )
            }
        }

        content.addView(
            lockButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                topMargin = dp(16)
                bottomMargin = dp(18)
            }
        )

        scroll.addView(content)

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

    private data class GameScenario(
        val id: String,
        val category: String,
        val title: String,
        val situation: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val signal1: String,
        val signal2: String,
        val signal3: String
    )

    private fun getGameScenario(mode: String, level: Int): GameScenario {
        val pool = listOf(
            GameScenario(
                "signal_shift",
                "SIGNAL SHIFT",
                "THE SIGNAL JUST CHANGED",
                "You are close to an important goal. A new signal suddenly changes the situation.\n\nYour original plan still looks possible, but the latest information could make it weaker.",
                "A • Act on the new signal",
                "B • Hold the original plan",
                "C • Test the change before committing",
                "NEW SIGNAL • HIGH",
                "TIME • LIMITED",
                "RISK • MEDIUM"
            ),
            GameScenario(
                "hidden_risk",
                "HIDDEN RISK",
                "THE OBVIOUS CHOICE HAS A CATCH",
                "The fastest option looks attractive because it gives you an immediate advantage.\n\nBut one important downside is not fully visible yet.",
                "A • Take the fast advantage",
                "B • Pause and expose the hidden risk",
                "C • Take a smaller controlled step",
                "ADVANTAGE • HIGH",
                "UNCERTAINTY • HIGH",
                "CONTROL • LIMITED"
            ),
            GameScenario(
                "momentum",
                "MOMENTUM",
                "MOMENTUM IS BUILDING",
                "Recent results are moving strongly in one direction.\n\nThe momentum may continue, but acting only because things are moving quickly can create a bad decision.",
                "A • Follow the momentum now",
                "B • Wait for confirmation",
                "C • Use momentum with a safety limit",
                "MOMENTUM • STRONG",
                "CONFIRMATION • LOW",
                "RISK • MEDIUM"
            ),
            GameScenario(
                "timing",
                "TIMING",
                "THE WINDOW IS CLOSING",
                "A useful opportunity is available right now.\n\nWaiting could improve your information, but the opportunity may become unavailable.",
                "A • Commit before the window closes",
                "B • Wait for stronger evidence",
                "C • Make a limited commitment",
                "TIME WINDOW • SHORT",
                "EVIDENCE • PARTIAL",
                "UPSIDE • HIGH"
            ),
            GameScenario(
                "conflict",
                "CONFLICT",
                "TWO SIGNALS DISAGREE",
                "One signal says move forward. Another signal warns that the current conditions are becoming weaker.\n\nYou must decide which information deserves more weight.",
                "A • Trust the positive signal",
                "B • Trust the warning signal",
                "C • Reduce exposure until signals agree",
                "SIGNAL A • POSITIVE",
                "SIGNAL B • WARNING",
                "CONFLICT • HIGH"
            ),
            GameScenario(
                "resource",
                "RESOURCE PRESSURE",
                "YOU CANNOT PROTECT EVERYTHING",
                "Your main goal is still achievable, but your available time and resources are shrinking.\n\nProtecting one priority may weaken another.",
                "A • Push for the main goal",
                "B • Protect resources first",
                "C • Redesign the plan around both",
                "GOAL • HIGH",
                "RESOURCES • LOW",
                "TRADE-OFF • HIGH"
            ),
            GameScenario(
                "reversal",
                "REVERSAL",
                "THE PLAN STARTED TO FAIL",
                "Your original decision produced an unexpected negative signal.\n\nYou can continue, reverse direction, or change only the risky part.",
                "A • Continue and recover",
                "B • Reverse immediately",
                "C • Change only the weak part",
                "RESULT • WEAK",
                "RECOVERY • POSSIBLE",
                "REVERSAL COST • MEDIUM"
            ),
            GameScenario(
                "opportunity",
                "OPPORTUNITY",
                "A BETTER OPTION APPEARS",
                "You already have a workable plan when a new option appears.\n\nThe new option has higher potential but also greater uncertainty.",
                "A • Switch to the new opportunity",
                "B • Protect the working plan",
                "C • Run a controlled test",
                "UPSIDE • HIGH",
                "CERTAINTY • LOW",
                "SWITCH COST • MEDIUM"
            )
        )

        val prefs = getSharedPreferences("GDMIE_GAME_SCENARIO", MODE_PRIVATE)
        val index = prefs.getInt("rotation_$mode", 0)
        prefs.edit()
            .putInt("rotation_$mode", index + 1)
            .apply()

        return pool[(index + (level - 1).coerceAtLeast(0)) % pool.size]
    }

    private data class DailyScenario(
        val situation: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val pressure: String,
        val theme: String = "GENERAL"
    )

    private fun getDailyScenario(level: Int): DailyScenario {
        val pool = when {
            level <= 1 -> listOf(
                DailyScenario(
                    "You have an important goal to complete today.\n\n" +
                    "You have limited time and resources, and a new opportunity has appeared. " +
                    "Acting now could create a useful result, but waiting could reduce the risk.\n\n" +
                    "What path would you choose?",
                    "Take the opportunity and move immediately.",
                    "Pause, gather more information and reduce uncertainty.",
                    "Look for a lower-risk path toward the same goal.",
                    "LOW PRESSURE",
                    "OPPORTUNITY"
                )
            )

            level == 2 -> listOf(
                DailyScenario(
                    "A useful opportunity has appeared, but some important information is still missing.\n\n" +
                    "Acting now may give you an early advantage. Waiting may give you better information, " +
                    "but the opportunity could become smaller.\n\n" +
                    "What path would you choose?",
                    "Act early and accept some uncertainty.",
                    "Wait until the missing information becomes clearer.",
                    "Change the approach to reduce the uncertainty.",
                    "CONTROLLED PRESSURE",
                    "UNCERTAINTY"
                ),
                DailyScenario(
                    "You are making progress toward a goal when an unexpected shortcut appears.\n\n" +
                    "The shortcut could save time, but it has not been tested. " +
                    "The safer route is slower but more familiar.\n\n" +
                    "What path would you choose?",
                    "Use the shortcut and move faster.",
                    "Stay with the known route and reassess later.",
                    "Find another route that balances speed and safety.",
                    "CONTROLLED PRESSURE",
                    "SHORTCUT"
                )
            )

            level == 3 -> listOf(
                DailyScenario(
                    "You have limited time, incomplete information and a valuable opportunity.\n\n" +
                    "A fast decision may improve the outcome, but one wrong assumption could create a costly setback. " +
                    "A slower decision reduces uncertainty but may lose the opportunity.\n\n" +
                    "What path would you choose?",
                    "Act now and manage the uncertainty while moving.",
                    "Pause and collect the most important missing information.",
                    "Redesign the approach to protect against the main downside.",
                    "MEDIUM PRESSURE",
                    "OPPORTUNITY"
                ),
                DailyScenario(
                    "Two priorities are competing: reaching the goal quickly and protecting your available resources.\n\n" +
                    "Choosing one strongly may weaken the other. There is no completely risk-free option.\n\n" +
                    "What path would you choose?",
                    "Prioritize speed and accept a controlled trade-off.",
                    "Slow down and protect resources first.",
                    "Create a third approach that balances both priorities.",
                    "MEDIUM PRESSURE",
                    "CONFLICT"
                )
            )

            level == 4 -> listOf(
                DailyScenario(
                    "A high-value opportunity is available, but time is limited and some information is uncertain.\n\n" +
                    "A fast decision could create a strong result, but a wrong assumption may create a meaningful setback.\n\n" +
                    "What path would you choose?",
                    "Commit now and actively manage the main risks.",
                    "Delay until the most important uncertainty is resolved.",
                    "Test a smaller version before committing fully.",
                    "HIGH PRESSURE",
                    "TIMING"
                ),
                DailyScenario(
                    "Your current plan is working, but a new opportunity could improve the result.\n\n" +
                    "Changing direction creates transition risk, while staying with the current plan may limit the upside.\n\n" +
                    "What path would you choose?",
                    "Switch direction and manage the transition risk.",
                    "Stay with the current plan and protect predictability.",
                    "Run a limited test before changing direction.",
                    "HIGH PRESSURE",
                    "TRANSITION"
                )
            )

            level == 5 -> listOf(
                DailyScenario(
                    "You must make a decision while three factors are moving at once: time pressure, resource limits and changing information.\n\n" +
                    "The best-looking option now may become weaker if one key assumption changes.\n\n" +
                    "What path would you choose?",
                    "Act now with a clear risk-control plan.",
                    "Wait for the highest-impact information before committing.",
                    "Create a flexible path that can adapt if conditions change.",
                    "EXTREME PRESSURE",
                    "ADAPTIVE"
                ),
                DailyScenario(
                    "A major goal is close, but reaching it requires accepting one significant trade-off.\n\n" +
                    "Protecting one priority may weaken another, and there is limited time to recover from a poor choice.\n\n" +
                    "What path would you choose?",
                    "Prioritize the main goal and accept a controlled trade-off.",
                    "Protect resources and reduce the chance of a major setback.",
                    "Redesign the plan to preserve both priorities as much as possible.",
                    "EXTREME PRESSURE",
                    "TRADE_OFF"
                )
            )

            else -> listOf(
                DailyScenario(
                    "A critical decision has to be made under multiple simultaneous constraints.\n\n" +
                    "Time is limited, information is incomplete, resources are constrained and every option carries a different downside.\n\n" +
                    "Your decision must remain useful even if your main assumption proves wrong.\n\n" +
                    "What path would you choose?",
                    "Commit now with active risk controls and contingency planning.",
                    "Delay until the highest-impact uncertainty is resolved.",
                    "Build an adaptive strategy that can change as new information arrives.",
                    "MAX PRESSURE",
                    "CONSTRAINT"
                ),
                DailyScenario(
                    "A high-impact opportunity conflicts with an already successful plan.\n\n" +
                    "Changing course could create substantial upside, but it could also damage what is already working.\n\n" +
                    "There is no risk-free option and the cost of reversal is significant.\n\n" +
                    "What path would you choose?",
                    "Change course and actively manage the transition.",
                    "Protect the existing plan and reject unnecessary disruption.",
                    "Run a controlled parallel test before making the major transition.",
                    "MAX PRESSURE",
                    "TRANSITION"
                )
            )
        }

        val prefs = getSharedPreferences("GDMIE_DAILY_SCENARIO", MODE_PRIVATE)
        val rotationIndex = prefs.getInt("rotation_index", 0)

        return pool[rotationIndex % pool.size]
    }

    private data class DailyPresentation(
        val situation: String,
        val question: String,
        val optionATitle: String,
        val optionBTitle: String,
        val optionCTitle: String,
        val optionA: String,
        val optionB: String,
        val optionC: String,
        val priorityQuestion: String,
        val speedLabel: String,
        val outcomeLabel: String,
        val riskLabel: String
    )


    private class DailyPressureLineView(
        context: android.content.Context,
        private val lineColor: Int,
        private val bgColor: Int
    ) : android.view.View(context) {

        private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = lineColor
            style = Paint.Style.STROKE
            strokeWidth = 4f
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            setShadowLayer(12f, 0f, 0f, lineColor)
        }

        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(35, 255, 255, 255)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        private val points = floatArrayOf(
            0.02f, 0.30f,
            0.14f, 0.48f,
            0.27f, 0.37f,
            0.40f, 0.62f,
            0.53f, 0.46f,
            0.66f, 0.73f,
            0.79f, 0.57f,
            0.91f, 0.78f,
            0.98f, 0.68f
        )

        private var progress = 0f

        init {
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            setBackgroundColor(bgColor)
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val left = 8f
            val right = width - 8f
            val top = 12f
            val bottom = height - 12f
            val chartWidth = right - left
            val chartHeight = bottom - top

            // subtle grid
            for (i in 1..3) {
                val y = top + chartHeight * (i / 4f)
                canvas.drawLine(left, y, right, y, gridPaint)
            }

            val path = Path()
            val visible = points.size / 2
            val totalSegments = visible - 1
            val drawSegments = totalSegments * progress

            var previousX = 0f
            var previousY = 0f

            for (i in 0 until visible) {
                val x = left + points[i * 2] * chartWidth
                val y = top + (1f - points[i * 2 + 1]) * chartHeight

                if (i == 0) {
                    path.moveTo(x, y)
                    previousX = x
                    previousY = y
                    continue
                }

                val segmentStart = i - 1
                val segmentProgress =
                    (drawSegments - segmentStart).coerceIn(0f, 1f)

                if (segmentProgress <= 0f) break

                val targetX = previousX + (x - previousX) * segmentProgress
                val targetY = previousY + (y - previousY) * segmentProgress

                path.lineTo(targetX, targetY)

                if (segmentProgress >= 1f) {
                    previousX = x
                    previousY = y
                } else {
                    break
                }
            }

            canvas.drawPath(path, linePaint)
        }

        fun startAnimation() {
            android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1500L
                interpolator = android.view.animation.DecelerateInterpolator()
                addUpdateListener {
                    progress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }
    }

    private fun buildDailyPresentation(
        scenario: DailyScenario,
        variant: Int
    ): DailyPresentation {
        val cleanSituation = scenario.situation
            .replace(
                Regex("\\n\\nWhat path would you choose\\?$"),
                ""
            )
            .trim()

        val questions = listOf(
            "What would you do next?",
            "Which move makes the most sense here?",
            "How would you handle this situation?",
            "Which direction would you take?",
            "What would be your next move?",
            "How would you approach this decision?",
            "Which path fits the situation best?",
            "What would you choose under these conditions?"
        )

        val priorityQuestions = listOf(
            "What matters most before you commit?",
            "Which factor should guide your decision?",
            "What deserves the most attention here?",
            "Which consideration should come first?",
            "What should carry the most weight in your thinking?",
            "Which factor could change your decision most?"
        )

        val titleSets = listOf(
            Triple("A  •  MOVE NOW", "B  •  PAUSE & CHECK", "C  •  FIND ANOTHER WAY"),
            Triple("A  •  ACT EARLY", "B  •  WAIT FOR CLARITY", "C  •  ADAPT THE PLAN"),
            Triple("A  •  TAKE THE OPENING", "B  •  REDUCE UNCERTAINTY", "C  •  BUILD A SAFER ROUTE"),
            Triple("A  •  COMMIT", "B  •  REASSESS", "C  •  TEST AN ALTERNATIVE"),
            Triple("A  •  MOVE FORWARD", "B  •  HOLD POSITION", "C  •  CHANGE DIRECTION"),
            Triple("A  •  SEIZE THE MOMENT", "B  •  GATHER MORE SIGNALS", "C  •  BALANCE BOTH SIDES")
        )

        val speedWords = listOf(
            "⚡ Speed",
            "⏱ Timing",
            "🚀 Momentum",
            "⚡ Quick action"
        )

        val outcomeWords = listOf(
            "🎯 Outcome",
            "🏆 Result",
            "🎯 Goal",
            "📈 Upside"
        )

        val riskWords = listOf(
            "🛡 Risk",
            "⚠️ Exposure",
            "🛡 Safety",
            "🔎 Uncertainty"
        )

        val titles = titleSets[variant % titleSets.size]

        return DailyPresentation(
            situation = cleanSituation,
            question = questions[variant % questions.size],
            optionATitle = titles.first,
            optionBTitle = titles.second,
            optionCTitle = titles.third,
            optionA = scenario.optionA,
            optionB = scenario.optionB,
            optionC = scenario.optionC,
            priorityQuestion =
                priorityQuestions[variant % priorityQuestions.size],
            speedLabel =
                "${speedWords[variant % speedWords.size]} • ${scenario.pressure}",
            outcomeLabel =
                "${outcomeWords[variant % outcomeWords.size]} • ${scenario.pressure}",
            riskLabel =
                "${riskWords[variant % riskWords.size]} • ${scenario.pressure}"
        )
    }

    private fun dailyImageRes(theme: String, presentationIndex: Int): Int {
        val variant = presentationIndex % 3
        return when (theme) {
            "OPPORTUNITY" -> when (variant) {
                1 -> R.drawable.daily_opportunity_2
                2 -> R.drawable.daily_opportunity_3
                else -> R.drawable.daily_opportunity_1
            }
            "UNCERTAINTY" -> when (variant) {
                1 -> R.drawable.daily_uncertainty_2
                2 -> R.drawable.daily_uncertainty_3
                else -> R.drawable.daily_uncertainty_1
            }
            "SHORTCUT" -> when (variant) {
                1 -> R.drawable.daily_shortcut_2
                2 -> R.drawable.daily_shortcut_3
                else -> R.drawable.daily_shortcut_1
            }
            "CONFLICT" -> when (variant) {
                1 -> R.drawable.daily_conflict_2
                2 -> R.drawable.daily_conflict_3
                else -> R.drawable.daily_conflict_1
            }
            "TIMING" -> when (variant) {
                1 -> R.drawable.daily_timing_2
                2 -> R.drawable.daily_timing_3
                else -> R.drawable.daily_timing_1
            }
            "TRANSITION" -> when (variant) {
                1 -> R.drawable.daily_transition_2
                2 -> R.drawable.daily_transition_3
                else -> R.drawable.daily_transition_1
            }
            "ADAPTIVE" -> when (variant) {
                1 -> R.drawable.daily_adaptive_2
                2 -> R.drawable.daily_adaptive_3
                else -> R.drawable.daily_adaptive_1
            }
            "TRADE_OFF" -> when (variant) {
                1 -> R.drawable.daily_trade_off_2
                2 -> R.drawable.daily_trade_off_3
                else -> R.drawable.daily_trade_off_1
            }
            "CONSTRAINT" -> when (variant) {
                1 -> R.drawable.daily_constraint_2
                2 -> R.drawable.daily_constraint_3
                else -> R.drawable.daily_constraint_1
            }
            else -> R.drawable.daily_opportunity_1
        }
    }

    private fun showDailyDecisionArena() {

        val currentLevel = GDMIEGameProgress.getLevel(this)
        val scenario = getDailyScenario(currentLevel)

        currentDailyScenarioTheme = scenario.theme
        currentDailyChallengeId =
            "DAILY_${scenario.theme}_${System.currentTimeMillis()}"

        val presentationPrefs =
            getSharedPreferences("GDMIE_DAILY_SCENARIO", MODE_PRIVATE)

        val presentationIndex =
            presentationPrefs.getInt("presentation_index", 0)

        presentationPrefs.edit()
            .putInt("presentation_index", presentationIndex + 1)
            .apply()
val presentation =
            buildDailyPresentation(scenario, presentationIndex)


        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DAILY ARENA", 11f, cyan, true).apply {
                letterSpacing = 0.14f
            }
        )

        content.addView(
            tv("🏆 DAILY CHALLENGE", 29f, white, true).apply {
                setPadding(0, dp(6), 0, dp(4))
            }
        )

        content.addView(
            tv(
                "One decision. Three paths. Choose carefully.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(18))
            }
        )

        // Reward + streak
        val reward = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(surface, 18, gold)
        }

        reward.addView(
            tv("TODAY'S REWARD", 10f, gold, true),
            LinearLayout.LayoutParams(0, dp(42), 1f)
        )

        reward.addView(
            tv("+30 XP  🔥", 18f, gold, true)
        )

        content.addView(
            reward,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(70)
            ).apply {
                bottomMargin = dp(16)
            }
        )

        // Situation
        val situation = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            background = rounded(surface2, 22, cyan)
        }

        situation.addView(
            tv("🔥 TODAY'S SITUATION", 11f, cyan, true)
        )

        val dailyImage = ImageView(this).apply {
            setImageResource(dailyImageRes(scenario.theme, presentationIndex))
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = rounded(surface, 18, cyan)
            adjustViewBounds = true
            contentDescription = "Context visual for ${scenario.theme.lowercase()}"
        }

        situation.addView(
            dailyImage,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(150)
            ).apply {
                bottomMargin = dp(12)
            }
        )

        situation.addView(
            tv(
                presentation.situation + "\n\n" + presentation.question,
                15f,
                white,
                true
            ).apply {
                setLineSpacing(3f, 1f)
                setPadding(0, dp(4), 0, 0)
            }
        )

        content.addView(
            situation,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(14)
            }
        )

        // Animated decision-pressure visual.
        val pressureCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = rounded(surface2, 20, cyan)
        }

        pressureCard.addView(
            tv("◉ LIVE DECISION PRESSURE", 10f, cyan, true).apply {
                letterSpacing = 0.08f
            }
        )

        pressureCard.addView(
            tv("Momentum is moving as you evaluate the situation.", 11f, muted)
                .apply {
                    setPadding(0, dp(5), 0, dp(8))
                }
        )

        val pressureLine = DailyPressureLineView(
            this,
            cyan,
            surface2
        )

        pressureCard.addView(
            pressureLine,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
            ).apply {
                bottomMargin = dp(5)
            }
        )

        pressureCard.addView(
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL

                addView(
                    tv("MOMENTUM", 9f, muted, true),
                    LinearLayout.LayoutParams(0, dp(24), 1f)
                )

                addView(
                    tv("LIVE", 9f, cyan, true).apply {
                        gravity = Gravity.RIGHT
                    },
                    LinearLayout.LayoutParams(0, dp(24), 1f)
                )
            }
        )

        content.addView(
            pressureCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        pressureLine.post {
            pressureLine.startAnimation()
        }

        content.addView(
            tv("CHOOSE YOUR PATH", 11f, purple, true).apply {
                letterSpacing = 0.08f
                setPadding(0, 0, 0, dp(10))
            }
        )

        val choiceGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    GDMIEAudioManager.playUiClick(this@ChallengeScenarioActivity)
                }
            }
        }

        fun choice(
            id: Int,
            title: String,
            description: String
        ): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = "$title\n$description"
                textSize = 14f
                setTextColor(white)
                buttonTintList = android.content.res.ColorStateList.valueOf(cyan)
                setPadding(dp(14), dp(12), dp(10), dp(12))
                background = rounded(surface, 17, border)
                layoutParams = RadioGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(72)
                ).apply {
                    bottomMargin = dp(10)
                }
            }
        }

        val optionA = choice(
            1001,
            presentation.optionATitle,
            scenario.optionA
        )

        val optionB = choice(
            1002,
            presentation.optionBTitle,
            scenario.optionB
        )

        val optionC = choice(
            1003,
            presentation.optionCTitle,
            scenario.optionC
        )

        choiceGroup.addView(optionA)
        choiceGroup.addView(optionB)
        choiceGroup.addView(optionC)

        content.addView(
            choiceGroup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        // Priority
        val priorityCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
            background = rounded(surface, 20, purple)
        }

        priorityCard.addView(
            tv("🧠 BEFORE YOU LOCK", 11f, purple, true)
        )

        priorityCard.addView(
            tv(
                presentation.priorityQuestion,
                17f,
                white,
                true
            ).apply {
                setPadding(0, dp(7), 0, dp(12))
            }
        )

        val priorityGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
        }

        fun priority(
            id: Int,
            textValue: String
        ): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = textValue
                textSize = 14f
                setTextColor(white)
                buttonTintList = android.content.res.ColorStateList.valueOf(purple)
                setPadding(dp(8), dp(7), 0, dp(7))
            }
        }

        priorityGroup.addView(
            priority(
                2001,
                presentation.speedLabel
            )
        )
        priorityGroup.addView(
            priority(
                2002,
                presentation.outcomeLabel
            )
        )
        priorityGroup.addView(
            priority(
                2003,
                presentation.riskLabel
            )
        )

        priorityCard.addView(priorityGroup)

        content.addView(
            priorityCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        val lockButton = TextView(this).apply {
            text = "🔒  LOCK MY DECISION"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {

                val selectedPath = choiceGroup.checkedRadioButtonId
                val selectedPriority = priorityGroup.checkedRadioButtonId

                if (selectedPath == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose your decision path first.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (selectedPriority == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose what matters most.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val selectedPathButton =
                    choiceGroup.findViewById<RadioButton>(selectedPath)

                val selectedPriorityButton =
                    priorityGroup.findViewById<RadioButton>(selectedPriority)

                if (selectedPathButton == null || selectedPriorityButton == null) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Unable to read the selected choice. Please choose again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val path = selectedPathButton.text.toString()
                val priority = selectedPriorityButton.text.toString()

                showRethinkStage(path, priority)
            }
        }

        content.addView(
            lockButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv(
                "GDMIE supports your thinking.\nYou make the final decision.",
                11f,
                dim
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(content)

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



    private fun showQuickRethinkStage(
        path: String,
        mode: String,
        scenario: GameScenario
    ) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DECISION JOURNEY", 10f, cyan, true).apply {
                letterSpacing = 0.14f
            }
        )

        content.addView(
            tv("🔄 RETHINK", 29f, white, true).apply {
                setPadding(0, dp(7), 0, dp(5))
            }
        )

        content.addView(
            tv(
                "Before you make the final lock, take one final look.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(20))
            }
        )

        val reviewCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            background = rounded(surface2, 22, purple)
        }

        reviewCard.addView(
            tv("🧠 YOUR CURRENT DECISION", 11f, purple, true)
        )

        reviewCard.addView(
            tv(path, 17f, white, true).apply {
                setPadding(0, dp(10), 0, dp(8))
            }
        )

        reviewCard.addView(
            tv(scenario.title, 12f, muted)
        )

        content.addView(
            reviewCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv(
                "Would you still make this decision?",
                18f,
                white,
                true
            ).apply {
                setPadding(0, 0, 0, dp(14))
            }
        )

        val rethinkGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    GDMIEAudioManager.playUiClick(this@ChallengeScenarioActivity)
                }
            }
        }

        fun rethinkOption(id: Int, label: String): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = label
                textSize = 15f
                setTextColor(white)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(cyan)
                setPadding(dp(10), dp(10), 0, dp(10))
                background = rounded(surface, 16, border)
            }
        }

        rethinkGroup.addView(
            rethinkOption(
                4001,
                "KEEP — Same decision"
            )
        )

        rethinkGroup.addView(
            rethinkOption(
                4002,
                "CHANGE — Choose differently"
            )
        )

        content.addView(
            rethinkGroup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(20)
            }
        )

        val finalLock = TextView(this).apply {
            text = "FINAL LOCK"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {
                val rethinkId = rethinkGroup.checkedRadioButtonId

                if (rethinkId == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose KEEP or CHANGE before locking.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@ChallengeScenarioActivity,
                    R.raw.gdmie_audio_decision
                )

                if (rethinkId == 4002) {
                    showQuickChangedDecisionStage(
                        path,
                        mode,
                        scenario
                    )
                    return@setOnClickListener
                }

                launchQuickGameOutcome(
                    mode,
                    path,
                    scenario
                )
            }
        }

        content.addView(
            finalLock,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv(
                "Final lock starts the GDMIE analysis.",
                10f,
                dim
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(content)

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

    private fun showQuickChangedDecisionStage(
        oldPath: String,
        mode: String,
        scenario: GameScenario
    ) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DECISION JOURNEY", 10f, cyan, true).apply {
                letterSpacing = 0.14f
            }
        )

        content.addView(
            tv("🔁 CHANGE DECISION", 29f, white, true).apply {
                setPadding(0, dp(7), 0, dp(5))
            }
        )

        content.addView(
            tv(
                "Your rethink changed your direction. Choose the final path.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(20))
            }
        )

        content.addView(
            tv(
                "ORIGINAL: $oldPath",
                12f,
                dim,
                true
            ).apply {
                setPadding(0, 0, 0, dp(14))
            }
        )

        val choiceGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    GDMIEAudioManager.playUiClick(
                        this@ChallengeScenarioActivity
                    )
                }
            }
        }

        fun changedOption(id: Int, label: String): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = label
                textSize = 15f
                setTextColor(white)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(cyan)
                setPadding(dp(10), dp(10), 0, dp(10))
                background = rounded(surface, 16, border)
            }
        }

        choiceGroup.addView(
            changedOption(
                5001,
                "A • ${scenario.optionA}"
            )
        )

        choiceGroup.addView(
            changedOption(
                5002,
                "B • ${scenario.optionB}"
            )
        )

        choiceGroup.addView(
            changedOption(
                5003,
                "C • ${scenario.optionC}"
            )
        )

        content.addView(
            choiceGroup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(20)
            }
        )

        val finalLock = TextView(this).apply {
            text = "FINAL LOCK"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {
                val selectedId = choiceGroup.checkedRadioButtonId

                if (selectedId == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose A, B or C before the final lock.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val selectedButton =
                    choiceGroup.findViewById<RadioButton>(selectedId)

                if (selectedButton == null) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Unable to read the selected decision. Please choose again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@ChallengeScenarioActivity,
                    R.raw.gdmie_audio_decision
                )

                val finalDecision = selectedButton.text.toString()

                launchQuickGameOutcome(
                    mode,
                    finalDecision,
                    scenario
                )
            }
        }

        content.addView(
            finalLock,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            )
        )

        scroll.addView(content)

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

    private fun launchQuickGameOutcome(
        mode: String,
        decision: String,
        scenario: GameScenario
    ) {
        val challengeId =
            "${mode}_${scenario.id}_${System.currentTimeMillis()}"

        val intent = android.content.Intent(
            this@ChallengeScenarioActivity,
            GameOutcomeActivity::class.java
        ).apply {
            putExtra("challenge_mode", mode)
            putExtra("challenge_decision", decision)
            putExtra("challenge_id", challengeId)
            putExtra("scenario_id", scenario.id)
            putExtra("scenario_title", scenario.title)
            putExtra("scenario_category", scenario.category)
            putExtra("decision_locked", true)
        }

        startActivity(intent)
    }

    private fun showRethinkStage(
        path: String,
        priority: String
    ) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DECISION JOURNEY", 10f, cyan, true).apply {
                letterSpacing = 0.14f
            }
        )

        content.addView(
            tv("🔄 RETHINK", 29f, white, true).apply {
                setPadding(0, dp(7), 0, dp(5))
            }
        )

        content.addView(
            tv(
                "Before you lock, take one final look.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(20))
            }
        )

        val reviewCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            background = rounded(surface2, 22, purple)
        }

        reviewCard.addView(
            tv("🧠 YOUR CURRENT DECISION", 11f, purple, true)
        )

        reviewCard.addView(
            tv(path, 17f, white, true).apply {
                setPadding(0, dp(10), 0, dp(8))
            }
        )

        reviewCard.addView(
            tv("Priority: $priority", 12f, muted)
        )

        content.addView(
            reviewCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv("Would you still make this decision?", 18f, white, true).apply {
                setPadding(0, 0, 0, dp(14))
            }
        )

        val rethinkGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    GDMIEAudioManager.playUiClick(
                        this@ChallengeScenarioActivity
                    )
                }
            }
        }

        fun rethinkOption(id: Int, label: String): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = label
                textSize = 15f
                setTextColor(white)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(cyan)
                setPadding(dp(10), dp(10), 0, dp(10))
                background = rounded(surface, 16, border)
            }
        }

        rethinkGroup.addView(
            rethinkOption(
                3001,
                "KEEP — Same decision"
            )
        )

        rethinkGroup.addView(
            rethinkOption(
                3002,
                "CHANGE — Choose differently"
            )
        )

        content.addView(
            rethinkGroup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(20)
            }
        )

        val finalLock = TextView(this).apply {
            text = "FINAL LOCK"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {
                val rethinkId = rethinkGroup.checkedRadioButtonId

                if (rethinkId == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose KEEP or CHANGE before locking.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val selectedRethinkButton =
                    rethinkGroup.findViewById<RadioButton>(rethinkId)

                if (selectedRethinkButton == null) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Unable to read the selected rethink choice. Please choose again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@ChallengeScenarioActivity,
                    R.raw.gdmie_audio_decision
                )

                val rethink = selectedRethinkButton.text.toString()

                if (rethinkId == 3002) {
                    showChangedDecisionStage(path, priority)
                    return@setOnClickListener
                }

                val finalIntent = android.content.Intent(
                    this@ChallengeScenarioActivity,
                    DailyRevealActivity::class.java
                )

                finalIntent.putExtra("challenge_mode", "DAILY")
                finalIntent.putExtra("scenario_theme", currentDailyScenarioTheme)
                finalIntent.putExtra("challenge_id", currentDailyChallengeId)
                finalIntent.putExtra("challenge_decision", path)
                finalIntent.putExtra("challenge_priority", priority)
                finalIntent.putExtra("challenge_rethink", rethink)
                finalIntent.putExtra("decision_locked", true)

                startActivity(finalIntent)
            }
        }

        content.addView(
            finalLock,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv(
                "Lock = final decision • XP is awarded only after completion.",
                10f,
                dim
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(content)

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


    private fun showChangedDecisionStage(
        oldPath: String,
        priority: String
    ) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DECISION JOURNEY", 10f, cyan, true)
        )

        content.addView(
            tv("🔄 CHOOSE AGAIN", 28f, white, true).apply {
                setPadding(0, dp(8), 0, dp(5))
            }
        )

        content.addView(
            tv(
                "You chose CHANGE. Reconsider the situation and select a new path.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(18))
            }
        )

        val oldCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = rounded(surface2, 18, border)
        }

        oldCard.addView(
            tv("PREVIOUS DECISION", 10f, dim, true)
        )

        oldCard.addView(
            tv(oldPath, 15f, white, true).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        content.addView(
            oldCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv("Choose your new decision:", 17f, white, true).apply {
                setPadding(0, 0, 0, dp(12))
            }
        )

        val newGroup = RadioGroup(this).apply {
            orientation = RadioGroup.VERTICAL
            setOnCheckedChangeListener { _, checkedId ->
                if (checkedId != -1) {
                    GDMIEAudioManager.playUiClick(
                        this@ChallengeScenarioActivity
                    )
                }
            }
        }

        fun newOption(id: Int, label: String): RadioButton {
            return RadioButton(this).apply {
                this.id = id
                text = label
                textSize = 15f
                setTextColor(white)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(cyan)
                setPadding(dp(10), dp(10), 0, dp(10))
                background = rounded(surface, 16, border)
            }
        }

        newGroup.addView(
            newOption(4001, "A • ACT NOW")
        )

        newGroup.addView(
            newOption(4002, "B • WAIT & REASSESS")
        )

        newGroup.addView(
            newOption(4003, "C • FIND AN ALTERNATIVE")
        )

        content.addView(
            newGroup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(20)
            }
        )

        val finalLock = TextView(this).apply {
            text = "🔒 FINAL LOCK"
            textSize = 17f
            setTextColor(bg)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(gold, 18, gold)
            elevation = dp(7).toFloat()
            isClickable = true

            setOnClickListener {
                val selected = newGroup.checkedRadioButtonId

                if (selected == -1) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Choose a new decision before locking.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val selectedNewPathButton =
                    newGroup.findViewById<RadioButton>(selected)

                if (selectedNewPathButton == null) {
                    Toast.makeText(
                        this@ChallengeScenarioActivity,
                        "Unable to read the selected decision. Please choose again.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                GDMIEAudioManager.playSfx(
                    this@ChallengeScenarioActivity,
                    R.raw.gdmie_audio_decision
                )

                val newPath = selectedNewPathButton.text.toString()

                val finalIntent = android.content.Intent(
                    this@ChallengeScenarioActivity,
                    DailyRevealActivity::class.java
                )

                finalIntent.putExtra("challenge_mode", "DAILY")
                finalIntent.putExtra("scenario_theme", currentDailyScenarioTheme)
                finalIntent.putExtra("challenge_id", currentDailyChallengeId)
                finalIntent.putExtra("challenge_decision", newPath)
                finalIntent.putExtra("challenge_priority", priority)
                finalIntent.putExtra(
                    "challenge_rethink",
                    "CHANGE — $oldPath → $newPath"
                )
                finalIntent.putExtra("decision_locked", true)

                startActivity(finalIntent)
            }
        }

        content.addView(
            finalLock,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            )
        )

        scroll.addView(content)

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

}
