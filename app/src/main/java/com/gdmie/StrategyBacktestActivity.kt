package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*

class StrategyBacktestActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val panel = Color.rgb(11, 18, 31)
    private val cyan = Color.rgb(0, 220, 255)
    private val blue = Color.rgb(45, 155, 255)
    private val green = Color.rgb(55, 225, 135)
    private val red = Color.rgb(255, 90, 100)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(panel)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 80))
            }
        }

    private fun space(h: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(h))
        }

    private fun input(
        label: String,
        defaultValue: String
    ): EditText {
        val box = EditText(this).apply {
            setText(defaultValue)
            setTextColor(white)
            setTextSize(14f)
            setSingleLine(true)
            setHint(label)
            setHintTextColor(muted)
            setPadding(dp(12), 0, dp(12), 0)

            background = GradientDrawable().apply {
                setColor(Color.rgb(14, 23, 39))
                cornerRadius = dp(12).toFloat()
                setStroke(dp(1), Color.rgb(35, 65, 90))
            }
        }

        return box
    }

    private fun read(edit: EditText): Double =
        edit.text.toString().trim().toDoubleOrNull() ?: 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        root.addView(
            text("STRATEGY & BACKTEST", 27f, cyan, true)
        )

        root.addView(
            text(
                "Test GDMIE decision logic against structured input scenarios",
                13f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        root.addView(space(22))

        val setup = card()

        setup.addView(
            text("STRATEGY SETUP", 12f, blue, true)
        )

        setup.addView(space(12))

        val present = input("Present Value", "100")
        val expected = input("Expected Value", "100")
        val target = input("Target Value", "100")
        val recent = input("Recent Momentum", "0")
        val immediate = input("Immediate Momentum", "0")
        val advantage = input("2-Min Market Advantage", "0")
        val market = input("Exact Market Line", "100")
        val odds = input("Odds", "0")
        val movement = input("Odds Movement", "0")
        val timing = input("Timing Factor", "0")
        val risk = input("Risk Factor", "0")

        val fields = listOf(
            present to "PRESENT VALUE",
            expected to "EXPECTED VALUE",
            target to "TARGET VALUE",
            recent to "RECENT MOMENTUM",
            immediate to "IMMEDIATE MOMENTUM",
            advantage to "2-MIN MARKET ADVANTAGE",
            market to "EXACT MARKET LINE",
            odds to "ODDS",
            movement to "ODDS MOVEMENT",
            timing to "TIMING FACTOR",
            risk to "RISK FACTOR"
        )

        fields.forEachIndexed { index, pair ->
            setup.addView(
                text(pair.second, 10f, muted, true).apply {
                    setPadding(0, if (index == 0) 0 else dp(10), 0, dp(4))
                }
            )

            setup.addView(
                pair.first,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(48)
                )
            )
        }

        root.addView(
            setup,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(18))

        val runButton = Button(this).apply {
            text = "RUN BACKTEST"
            textSize = 13f
            setTextColor(white)
            background = GradientDrawable().apply {
                setColor(Color.rgb(20, 90, 125))
                cornerRadius = dp(14).toFloat()
            }
        }

        root.addView(
            runButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54)
            )
        )

        root.addView(space(10))

        val saveLoadRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val saveButton = Button(this).apply {
            text = "SAVE STRATEGY"
            textSize = 12f
            setTextColor(white)
            background = GradientDrawable().apply {
                setColor(Color.rgb(18, 75, 105))
                cornerRadius = dp(12).toFloat()
            }
        }

        val loadButton = Button(this).apply {
            text = "LOAD SAVED"
            textSize = 12f
            setTextColor(white)
            background = GradientDrawable().apply {
                setColor(Color.rgb(25, 55, 85))
                cornerRadius = dp(12).toFloat()
            }
        }

        saveLoadRow.addView(
            saveButton,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            ).apply {
                setMargins(0, 0, dp(5), 0)
            }
        )

        saveLoadRow.addView(
            loadButton,
            LinearLayout.LayoutParams(
                0,
                dp(50),
                1f
            ).apply {
                setMargins(dp(5), 0, 0, 0)
            }
        )

        root.addView(
            saveLoadRow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        val prefs = getSharedPreferences(
            "GDMIE_STRATEGY",
            MODE_PRIVATE
        )

        fun saveStrategy() {
            prefs.edit()
                .putString("present", present.text.toString())
                .putString("expected", expected.text.toString())
                .putString("target", target.text.toString())
                .putString("recent", recent.text.toString())
                .putString("immediate", immediate.text.toString())
                .putString("advantage", advantage.text.toString())
                .putString("market", market.text.toString())
                .putString("odds", odds.text.toString())
                .putString("movement", movement.text.toString())
                .putString("timing", timing.text.toString())
                .putString("risk", risk.text.toString())
                .apply()

            Toast.makeText(
                this,
                "Strategy saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

        fun loadStrategy() {
            present.setText(prefs.getString("present", present.text.toString()))
            expected.setText(prefs.getString("expected", expected.text.toString()))
            target.setText(prefs.getString("target", target.text.toString()))
            recent.setText(prefs.getString("recent", recent.text.toString()))
            immediate.setText(prefs.getString("immediate", immediate.text.toString()))
            advantage.setText(prefs.getString("advantage", advantage.text.toString()))
            market.setText(prefs.getString("market", market.text.toString()))
            odds.setText(prefs.getString("odds", odds.text.toString()))
            movement.setText(prefs.getString("movement", movement.text.toString()))
            timing.setText(prefs.getString("timing", timing.text.toString()))
            risk.setText(prefs.getString("risk", risk.text.toString()))

            Toast.makeText(
                this,
                "Saved strategy loaded",
                Toast.LENGTH_SHORT
            ).show()
        }

        saveButton.setOnClickListener {
            saveStrategy()
        }

        loadButton.setOnClickListener {
            loadStrategy()
        }

        if (prefs.contains("present")) {
            loadStrategy()
        }

        root.addView(space(18))

        val resultCard = card()
        resultCard.addView(
            text("BACKTEST RESULT", 12f, cyan, true)
        )

        val resultView = text(
            "Enter the scenario values and run GDMIE.",
            13f,
            muted
        ).apply {
            setPadding(0, dp(12), 0, 0)
            setLineSpacing(dp(3).toFloat(), 1f)
        }

        resultCard.addView(resultView)

        root.addView(
            resultCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        runButton.setOnClickListener {
            try {
                val result = GDMEngine.calculate(
                    GDMInput(
                        presentValue = read(present),
                        expectedValue = read(expected),
                        targetValue = read(target),
                        recentMomentum = read(recent),
                        immediateMomentum = read(immediate),
                        twoMinMarketAdvantage = read(advantage),
                        exactMarketLine = read(market),
                        odds = read(odds),
                        oddsMovement = read(movement),
                        timingFactor = read(timing),
                        riskFactor = read(risk)
                    )
                )

            val decisionColor = when (result.decision) {
                "POSITIVE EDGE" -> green
                "NEGATIVE EDGE" -> red
                else -> cyan
            }

            resultView.text = buildString {
                append("DECISION\n")
                append(result.decision)
                append("\n\n")

                append("REVERSE EDGE     %.2f\n".format(result.edge))
                append("CONFIDENCE       %.0f%%\n".format(result.confidence * 100))
                append("EXPECTED VALUE   %.2f\n".format(result.expectedValue))
                append("MARKET VALUE     %.2f\n".format(result.marketValue))
                append("GAP              %.2f\n".format(result.gap))
                append("MOMENTUM         %.2f\n".format(result.momentum))
                append("RISK             %.2f\n".format(result.risk))
            }

            resultView.setTextColor(decisionColor)

            // ---------------------------------------------------------
            // SAVE BACKTEST RESULT TO HISTORY
            // ---------------------------------------------------------

            val historyPrefs = getSharedPreferences(
                "GDMIE_BACKTEST_HISTORY",
                MODE_PRIVATE
            )

            val existing = historyPrefs.getStringSet(
                "records",
                emptySet()
            )?.toMutableSet() ?: mutableSetOf()

            val record = buildString {
                append("Decision: ${result.decision}\n")
                append("Reverse Edge: %.2f\n".format(result.edge))
                append("Confidence: %.0f%%\n".format(result.confidence * 100))
                append("Expected Value: %.2f\n".format(result.expectedValue))
                append("Market Value: %.2f\n".format(result.marketValue))
                append("Gap: %.2f\n".format(result.gap))
                append("Momentum: %.2f\n".format(result.momentum))
                append("Risk: %.2f".format(result.risk))
            }

            existing.add(record)

            historyPrefs.edit()
                .putStringSet("records", existing)
                .apply()

            Toast.makeText(
                this,
                "Backtest result saved to history",
                Toast.LENGTH_SHORT
            ).show()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this,
                    "Backtest error: ${e.javaClass.simpleName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        root.addView(space(20))

        root.addView(
            text(
                "GDMIE • STRATEGY & BACKTEST",
                9f,
                Color.rgb(70, 95, 120)
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }
}
