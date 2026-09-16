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

class AskGDMIEActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val card = Color.rgb(14, 23, 39)
    private val blue = Color.rgb(45, 155, 255)
    private val cyan = Color.rgb(0, 220, 255)
    private val green = Color.rgb(55, 225, 135)
    private val yellow = Color.rgb(255, 205, 70)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun panel(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(card)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 82))
            }
        }

    private fun space(h: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(h))
        }

    private fun input(
        parent: LinearLayout,
        title: String
    ): EditText {

        parent.addView(text(title, 12f, muted, true))

        val field = EditText(this).apply {
            hint = "Enter value"
            textSize = 15f
            setTextColor(white)
            setHintTextColor(Color.rgb(85, 105, 130))
            setSingleLine(true)
            inputType =
                InputType.TYPE_CLASS_NUMBER or
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

    private fun number(field: EditText): Double =
        field.text.toString().trim().toDoubleOrNull() ?: 0.0

    private fun resultLine(
        parent: LinearLayout,
        label: String,
        value: String
    ) {
        parent.addView(
            text(
                "$label: $value",
                13f,
                white,
                true
            ).apply {
                setPadding(0, dp(4), 0, dp(4))
            }
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

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        root.addView(
            text("ASK GDMIE", 30f, white, true)
        )

        root.addView(
            text(
                "Ask the intelligence engine for structured reasoning.",
                13f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // QUESTION
        // ---------------------------------------------------------

        root.addView(text("QUESTION", 11f, cyan, true))
        root.addView(space(8))

        val questionPanel = panel()

        questionPanel.addView(
            text(
                "What decision are you analyzing?",
                13f,
                muted,
                true
            )
        )

        val question = EditText(this).apply {
            hint = "Describe the decision or situation..."
            textSize = 16f
            setTextColor(white)
            setHintTextColor(Color.rgb(85, 105, 130))
            gravity = Gravity.TOP
            minLines = 4
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(0, dp(10), 0, dp(8))
        }

        questionPanel.addView(
            question,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(125)
            )
        )

        root.addView(
            questionPanel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // DECISION CATEGORY
        // ---------------------------------------------------------

        root.addView(text("DECISION CATEGORY", 11f, cyan, true))
        root.addView(space(8))

        val categoryPanel = panel()

        categoryPanel.addView(
            text(
                "What type of decision is this?",
                13f,
                muted,
                true
            )
        )

        val categorySpinner = Spinner(this).apply {
            val categories = arrayOf(
                "General",
                "Financial",
                "Market",
                "Strategy",
                "Life Decision"
            )

            adapter = ArrayAdapter(
                this@AskGDMIEActivity,
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
        }

        categoryPanel.addView(
            categorySpinner,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        root.addView(
            categoryPanel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // DECISION DATA
        // ---------------------------------------------------------

        root.addView(text("DECISION DATA", 11f, cyan, true))
        root.addView(space(8))

        val dataPanel = panel()

        val present = input(dataPanel, "Present Value")
        dataPanel.addView(space(7))

        val expected = input(dataPanel, "Expected Value")
        dataPanel.addView(space(7))

        val target = input(dataPanel, "Target Value")
        dataPanel.addView(space(7))

        val recent = input(dataPanel, "Recent Momentum")
        dataPanel.addView(space(7))

        val immediate = input(dataPanel, "Immediate Momentum")
        dataPanel.addView(space(7))

        val advantage = input(dataPanel, "2-Min Market Advantage")
        dataPanel.addView(space(7))

        val market = input(dataPanel, "Exact Market Line")
        dataPanel.addView(space(7))

        val odds = input(dataPanel, "Odds")
        dataPanel.addView(space(7))

        val movement = input(dataPanel, "Odds Movement")
        dataPanel.addView(space(7))

        val timing = input(dataPanel, "Timing Factor")
        dataPanel.addView(space(7))

        val risk = input(dataPanel, "Risk Factor")

        root.addView(
            dataPanel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // RUN ENGINE
        // ---------------------------------------------------------

        val run = Button(this).apply {
            text = "RUN GDM ENGINE"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            background = GradientDrawable().apply {
                setColor(blue)
                cornerRadius = dp(16).toFloat()
            }
        }

        root.addView(
            run,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        root.addView(space(20))

        // ---------------------------------------------------------
        // RESULT
        // ---------------------------------------------------------

        root.addView(text("GDMIE RESULT", 11f, cyan, true))
        root.addView(space(8))

        val resultPanel = panel()

        val resultTitle = text(
            "WAITING FOR ANALYSIS",
            18f,
            white,
            true
        )

        resultPanel.addView(resultTitle)
        resultPanel.addView(space(12))

        val resultDetails = text(
            "Enter the decision data and run the engine.",
            13f,
            muted
        )

        resultDetails.setLineSpacing(dp(3).toFloat(), 1f)

        resultPanel.addView(resultDetails)

        root.addView(
            resultPanel,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        // ---------------------------------------------------------
        // ENGINE ACTION
        // ---------------------------------------------------------

        run.setOnClickListener {

            val questionText =
                question.text.toString().trim()
                    .ifEmpty { "Not specified" }

            val category =
                categorySpinner.selectedItem.toString()

            val result = GDMEngine.calculate(
                GDMInput(
                    presentValue = number(present),
                    expectedValue = number(expected),
                    targetValue = number(target),
                    recentMomentum = number(recent),
                    immediateMomentum = number(immediate),
                    twoMinMarketAdvantage = number(advantage),
                    exactMarketLine = number(market),
                    odds = number(odds),
                    oddsMovement = number(movement),
                    timingFactor = number(timing),
                    riskFactor = number(risk)
                )
            )

            resultTitle.text = "ANALYSIS COMPLETE"
            resultTitle.setTextColor(green)

            resultDetails.setTextColor(white)

            resultDetails.text =
                "QUESTION\n" +
                "────────────────────────\n" +
                "$questionText\n\n" +

                "CATEGORY\n" +
                "────────────────────────\n" +
                "$category\n\n" +

                "PRESENT → EXPECTED → TARGET\n" +
                "────────────────────────\n" +
                "Present Value: %.2f\n".format(result.presentValue) +
                "Expected Value: %.2f\n".format(result.expectedValue) +
                "Market Value: %.2f\n".format(result.marketValue) +
                "Gap: %.2f\n\n".format(result.gap) +

                "CONTEXT\n" +
                "────────────────────────\n" +
                "Momentum: %.2f\n".format(result.momentum) +
                "Risk: %.2f\n".format(result.risk) +
                "Reverse Edge: %.2f\n".format(result.edge) +
                "Confidence: %.0f%%\n\n".format(result.confidence * 100) +

                "DECISION\n" +
                "────────────────────────\n" +
                "${result.decision}\n\n" +

                "REASONING\n" +
                "────────────────────────\n" +
                result.explanation

            Toast.makeText(
                this@AskGDMIEActivity,
                "GDMIE analysis calculated",
                Toast.LENGTH_SHORT
            ).show()
        }

        root.addView(space(20))

        root.addView(
            text(
                "PRESENT → EXPECTED → TARGET → GAP → MOVEMENT → RISK → TIMING → REVERSE EDGE",
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
