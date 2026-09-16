package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class WhyDetailsActivity : Activity() {

    private val bg = Color.rgb(4, 8, 18)
    private val panel = Color.rgb(10, 17, 32)
    private val cyan = Color.rgb(0, 220, 255)
    private val blue = Color.rgb(45, 155, 255)
    private val green = Color.rgb(60, 230, 140)
    private val red = Color.rgb(255, 80, 100)
    private val white = Color.WHITE
    private val muted = Color.rgb(155, 170, 190)

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

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

    private fun space(height: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(height))
        }

    private fun addFactor(
        parent: LinearLayout,
        number: String,
        title: String,
        description: String,
        color: Int
    ) {
        val c = card()

        c.addView(
            text("$number  $title", 16f, color, true)
        )

        c.addView(
            text(
                description,
                13f,
                muted
            ).apply {
                setPadding(0, dp(8), 0, 0)
                setLineSpacing(dp(3).toFloat(), 1f)
            }
        )

        parent.addView(
            c,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

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
            text("WHY & DETAILS", 28f, cyan, true)
        )

        root.addView(
            text(
                "Understand how GDMIE reaches each engine result",
                13f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        root.addView(space(22))

        val overview = card()

        overview.addView(
            text("HOW GDMIE THINKS", 12f, cyan, true)
        )

        overview.addView(
            text(
                "GDMIE does not rely on a single input. " +
                "It combines present conditions, expected value, " +
                "market context, momentum, movement, risk and timing " +
                "to calculate a structured Reverse Edge.",
                14f,
                muted
            ).apply {
                setPadding(0, dp(10), 0, 0)
                setLineSpacing(dp(3).toFloat(), 1f)
            }
        )

        root.addView(
            overview,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(18))

        root.addView(
            text("ENGINE FACTORS", 12f, blue, true)
        )

        root.addView(space(8))

        addFactor(
            root,
            "01",
            "PRESENT VALUE",
            "The current measured value supplied to the engine. " +
                "This establishes the starting point for the calculation.",
            blue
        )

        root.addView(space(10))

        addFactor(
            root,
            "02",
            "EXPECTED VALUE",
            "The engine's expected reference value used to compare " +
                "the present state with the expected state.",
            cyan
        )

        root.addView(space(10))

        addFactor(
            root,
            "03",
            "MARKET VALUE",
            "The exact market reference entered by the user. " +
                "GDMIE compares this against the expected value.",
            blue
        )

        root.addView(space(10))

        addFactor(
            root,
            "04",
            "MARKET GAP",
            "The difference between expected value and market value. " +
                "The gap is one of the core inputs into Reverse Edge.",
            green
        )

        root.addView(space(10))

        addFactor(
            root,
            "05",
            "MOMENTUM",
            "Recent momentum, immediate momentum and the 2-minute " +
                "market advantage are combined into the momentum factor.",
            green
        )

        root.addView(space(10))

        addFactor(
            root,
            "06",
            "MOVEMENT",
            "Odds movement provides additional context about how the " +
                "market value is changing.",
            cyan
        )

        root.addView(space(10))

        addFactor(
            root,
            "07",
            "RISK",
            "Risk factor reduces the strength of a decision when the " +
                "available conditions indicate greater uncertainty.",
            red
        )

        root.addView(space(10))

        addFactor(
            root,
            "08",
            "TIMING",
            "Timing is included because the same numerical edge can " +
                "have different significance in different situations.",
            blue
        )

        root.addView(space(18))

        val flow = card()

        flow.addView(
            text("REVERSE EDGE FLOW", 12f, cyan, true)
        )

        flow.addView(
            text(
                "PRESENT\n↓\nEXPECTED\n↓\nMARKET\n↓\nGAP\n↓\nMOMENTUM + MOVEMENT\n↓\nRISK + TIMING\n↓\nREVERSE EDGE\n↓\nDECISION",
                13f,
                white,
                true
            ).apply {
                setPadding(0, dp(12), 0, 0)
                setLineSpacing(dp(2).toFloat(), 1f)
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            flow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(18))

        root.addView(
            text(
                "GDMIE • GENERAL DECISION & MATHEMATICAL INTELLIGENCE ENGINE",
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
