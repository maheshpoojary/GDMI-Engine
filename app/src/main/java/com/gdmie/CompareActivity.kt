package com.gdmie

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

class CompareActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val card = Color.rgb(14, 23, 39)
    private val blue = Color.rgb(45, 155, 255)
    private val cyan = Color.rgb(0, 220, 255)
    private val green = Color.rgb(55, 225, 135)
    private val red = Color.rgb(255, 90, 100)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun panel(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(card)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 82))
            }
        }
    }

    private fun space(height: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(height))
        }

    private fun input(
        parent: LinearLayout,
        title: String,
        hint: String,
        defaultValue: String = "0"
    ): EditText {

        parent.addView(text(title, 12f, muted, true))

        val field = EditText(this).apply {
            this.hint = hint
            setText(defaultValue)
            textSize = 15f
            setTextColor(white)
            setHintTextColor(Color.rgb(85, 105, 130))
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
            setPadding(0, dp(4), 0, 0)
        }

        parent.addView(
            field,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(46)
            )
        )

        return field
    }

    private fun nameInput(
        parent: LinearLayout,
        title: String,
        hint: String
    ): EditText {

        parent.addView(text(title, 12f, muted, true))

        val field = EditText(this).apply {
            this.hint = hint
            textSize = 15f
            setTextColor(white)
            setHintTextColor(Color.rgb(85, 105, 130))
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(0, dp(4), 0, 0)
        }

        parent.addView(
            field,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(46)
            )
        )

        return field
    }

    private fun number(field: EditText): Double =
        field.text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun resultLine(
        parent: LinearLayout,
        label: String,
        a: TextView,
        b: TextView
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(7), 0, dp(7))
        }

        row.addView(
            text(label, 12f, muted, true),
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            a,
            LinearLayout.LayoutParams(
                dp(82),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        row.addView(
            text("vs", 10f, muted).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(
                dp(30),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        row.addView(
            b,
            LinearLayout.LayoutParams(
                dp(82),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        parent.addView(row)
    }

    private fun valueText(value: String): TextView =
        text(value, 13f, white, true).apply {
            gravity = Gravity.CENTER
        }

    private fun runEngine(
        name: String,
        present: Double,
        expected: Double,
        target: Double,
        recent: Double,
        immediate: Double,
        advantage: Double,
        market: Double,
        odds: Double,
        movement: Double,
        timing: Double,
        risk: Double
    ): GDMResult {

        return GDMEngine.calculate(
            GDMInput(
                presentValue = present,
                expectedValue = expected,
                targetValue = target,
                recentMomentum = recent,
                immediateMomentum = immediate,
                twoMinMarketAdvantage = advantage,
                exactMarketLine = market,
                odds = odds,
                oddsMovement = movement,
                timingFactor = timing,
                riskFactor = risk
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(30))
        }

        root.addView(text("COMPARE", 30f, white, true))

        root.addView(
            text(
                "Compare two decision profiles using the GDMIE engine.",
                13f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // ASSET A
        // ---------------------------------------------------------

        root.addView(text("ASSET A", 11f, cyan, true))
        root.addView(space(8))

        val panelA = panel()

        val nameA = nameInput(
            panelA,
            "Asset Name",
            "Enter stock / asset"
        )

        panelA.addView(space(8))

        val presentA = input(panelA, "Present Value", "0")
        val expectedA = input(panelA, "Expected Value", "0")
        val targetA = input(panelA, "Target Value", "0")
        val recentA = input(panelA, "Recent Momentum", "0")
        val immediateA = input(panelA, "Immediate Momentum", "0")
        val advantageA = input(panelA, "2-Min Market Advantage", "0")
        val marketA = input(panelA, "Exact Market Line", "0")
        val oddsA = input(panelA, "Odds", "0")
        val movementA = input(panelA, "Odds Movement", "0")
        val timingA = input(panelA, "Timing Factor", "0")
        val riskA = input(panelA, "Risk Factor", "0")

        root.addView(
            panelA,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(18))

        // ---------------------------------------------------------
        // ASSET B
        // ---------------------------------------------------------

        root.addView(text("ASSET B", 11f, cyan, true))
        root.addView(space(8))

        val panelB = panel()

        val nameB = nameInput(
            panelB,
            "Asset Name",
            "Enter stock / asset"
        )

        panelB.addView(space(8))

        val presentB = input(panelB, "Present Value", "0")
        val expectedB = input(panelB, "Expected Value", "0")
        val targetB = input(panelB, "Target Value", "0")
        val recentB = input(panelB, "Recent Momentum", "0")
        val immediateB = input(panelB, "Immediate Momentum", "0")
        val advantageB = input(panelB, "2-Min Market Advantage", "0")
        val marketB = input(panelB, "Exact Market Line", "0")
        val oddsB = input(panelB, "Odds", "0")
        val movementB = input(panelB, "Odds Movement", "0")
        val timingB = input(panelB, "Timing Factor", "0")
        val riskB = input(panelB, "Risk Factor", "0")

        root.addView(
            panelB,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // RESULT PANEL
        // ---------------------------------------------------------

        root.addView(text("GDMIE COMPARISON", 11f, cyan, true))
        root.addView(space(8))

        val resultPanel = panel()

        val resultTitle = text(
            "WAITING FOR ANALYSIS",
            17f,
            white,
            true
        )

        resultPanel.addView(resultTitle)

        resultPanel.addView(space(12))

        val resultRows = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        resultPanel.addView(resultRows)

        val decision = text(
            "Enter both profiles and run comparison.",
            13f,
            muted,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        }

        resultPanel.addView(decision)

        root.addView(
            resultPanel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(18))

        // ---------------------------------------------------------
        // RUN
        // ---------------------------------------------------------

        val analyze = Button(this).apply {
            text = "RUN COMPARISON"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            background = GradientDrawable().apply {
                setColor(blue)
                cornerRadius = dp(16).toFloat()
            }

            setOnClickListener {

                val resultA = runEngine(
                    nameA.text.toString(),
                    number(presentA),
                    number(expectedA),
                    number(targetA),
                    number(recentA),
                    number(immediateA),
                    number(advantageA),
                    number(marketA),
                    number(oddsA),
                    number(movementA),
                    number(timingA),
                    number(riskA)
                )

                val resultB = runEngine(
                    nameB.text.toString(),
                    number(presentB),
                    number(expectedB),
                    number(targetB),
                    number(recentB),
                    number(immediateB),
                    number(advantageB),
                    number(marketB),
                    number(oddsB),
                    number(movementB),
                    number(timingB),
                    number(riskB)
                )

                resultRows.removeAllViews()

                resultTitle.text = "SIDE-BY-SIDE ANALYSIS"

                resultLine(
                    resultRows,
                    "Present Value",
                    valueText("%.2f".format(resultA.presentValue)),
                    valueText("%.2f".format(resultB.presentValue))
                )

                resultLine(
                    resultRows,
                    "Expected Value",
                    valueText("%.2f".format(resultA.expectedValue)),
                    valueText("%.2f".format(resultB.expectedValue))
                )

                resultLine(
                    resultRows,
                    "Market Value",
                    valueText("%.2f".format(resultA.marketValue)),
                    valueText("%.2f".format(resultB.marketValue))
                )

                resultLine(
                    resultRows,
                    "Gap",
                    valueText("%.2f".format(resultA.gap)),
                    valueText("%.2f".format(resultB.gap))
                )

                resultLine(
                    resultRows,
                    "Momentum",
                    valueText("%.2f".format(resultA.momentum)),
                    valueText("%.2f".format(resultB.momentum))
                )

                resultLine(
                    resultRows,
                    "Risk",
                    valueText("%.2f".format(resultA.risk)),
                    valueText("%.2f".format(resultB.risk))
                )

                resultLine(
                    resultRows,
                    "Reverse Edge",
                    valueText("%.2f".format(resultA.edge)),
                    valueText("%.2f".format(resultB.edge))
                )

                resultLine(
                    resultRows,
                    "Confidence",
                    valueText(
                        "%.0f%%".format(resultA.confidence * 100)
                    ),
                    valueText(
                        "%.0f%%".format(resultB.confidence * 100)
                    )
                )

                val edgeDifference = kotlin.math.abs(resultA.edge - resultB.edge)
                val momentumDifference = kotlin.math.abs(resultA.momentum - resultB.momentum)
                val riskDifference = kotlin.math.abs(resultA.risk - resultB.risk)

                decision.text =
                    "COMPARISON INSIGHT\n" +
                    "Reverse Edge Difference: %.2f\n".format(edgeDifference) +
                    "Momentum Difference: %.2f\n".format(momentumDifference) +
                    "Risk Difference: %.2f".format(riskDifference)

                decision.setTextColor(white)

                Toast.makeText(
                    this@CompareActivity,
                    "GDMIE comparison calculated",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        root.addView(
            analyze,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        root.addView(space(18))

        root.addView(
            text(
                "GDMIE • PRESENT • EXPECTED • TARGET • GAP • MOVEMENT • RISK • TIMING",
                9f,
                Color.rgb(70, 95, 120)
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }
}
