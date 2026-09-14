package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    private val bg = Color.rgb(4, 14, 25)
    private val card = Color.rgb(9, 29, 47)
    private val card2 = Color.rgb(12, 37, 58)
    private val blue = Color.rgb(55, 165, 255)
    private val green = Color.rgb(30, 220, 145)
    private val yellow = Color.rgb(255, 220, 60)
    private val red = Color.rgb(255, 90, 105)
    private val white = Color.WHITE
    private val muted = Color.rgb(155, 180, 200)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(dp(18), dp(20), dp(18), dp(30))
        scroll.addView(root)

        // TOP BAR
        val top = LinearLayout(this)
        top.orientation = LinearLayout.HORIZONTAL
        top.gravity = Gravity.CENTER_VERTICAL

        val back = TextView(this)
        back.text = "‹"
        back.textSize = 38f
        back.setTextColor(white)
        back.gravity = Gravity.CENTER

        top.addView(back, LinearLayout.LayoutParams(dp(45), dp(55)))

        val titleBox = LinearLayout(this)
        titleBox.orientation = LinearLayout.VERTICAL

        addText(titleBox, "NIFTY", 21f, white, true)
        addText(titleBox, "NSE", 10f, blue, true)

        top.addView(
            titleBox,
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        val star = TextView(this)
        star.text = "★"
        star.textSize = 27f
        star.setTextColor(yellow)
        star.gravity = Gravity.CENTER

        top.addView(star, LinearLayout.LayoutParams(dp(50), dp(55)))

        root.addView(top)

        // PRICE
        addText(root, "24,198.30", 30f, white, true)
        addText(root, "+178.45  (+0.74%)", 14f, green, true)

        space(root, 15)

        // TIME PERIODS
        val periods = LinearLayout(this)
        periods.orientation = LinearLayout.HORIZONTAL
        periods.gravity = Gravity.CENTER

        val values = arrayOf("1D", "1W", "1M", "3M", "1Y", "ALL")

        for (i in values.indices) {
            val p = TextView(this)
            p.text = values[i]
            p.textSize = 12f
            p.gravity = Gravity.CENTER
            p.setTextColor(if (i == 0) white else muted)

            if (i == 0) {
                p.background = rounded(blue, 18)
            }

            periods.addView(
                p,
                LinearLayout.LayoutParams(0, dp(38), 1f).apply {
                    setMargins(dp(3), 0, dp(3), 0)
                }
            )
        }

        root.addView(periods)

        space(root, 18)

        // SIMPLE CHART
        val chart = TextView(this)
        chart.text = """
            ╱╲       ╱╲
        ╱╲╱  ╲╱╲ ╱  ╲
       ╱          ╲   ╲╱╲
      ╱              ╲     ╲
    ╱                  ╲╱╲
        """.trimIndent()
        chart.textSize = 22f
        chart.setTextColor(green)
        chart.gravity = Gravity.CENTER
        chart.setPadding(dp(5), dp(5), dp(5), dp(5))
        chart.background = rounded(card, 20)

        root.addView(
            chart,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(155)
            )
        )

        space(root, 20)

        // GDMIE ANALYSIS HEADER
        val analysisTitle = LinearLayout(this)
        analysisTitle.orientation = LinearLayout.HORIZONTAL

        addText(analysisTitle, "GDMIE ANALYSIS", 17f, white, true)

        val updated = TextView(this)
        updated.text = "Last updated 5 min ago"
        updated.textSize = 10f
        updated.setTextColor(muted)
        updated.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT

        analysisTitle.addView(
            updated,
            LinearLayout.LayoutParams(0, dp(30), 1f)
        )

        root.addView(analysisTitle)

        space(root, 10)

        // MAIN ANALYSIS CARD
        val analysis = LinearLayout(this)
        analysis.orientation = LinearLayout.VERTICAL
        analysis.setPadding(dp(17), dp(17), dp(17), dp(17))
        analysis.background = rounded(card, 22)

        val mainRow = LinearLayout(this)
        mainRow.orientation = LinearLayout.HORIZONTAL
        mainRow.gravity = Gravity.CENTER_VERTICAL

        // CONFIDENCE
        val confidenceBox = LinearLayout(this)
        confidenceBox.orientation = LinearLayout.VERTICAL
        confidenceBox.gravity = Gravity.CENTER
        confidenceBox.background = rounded(card2, 60)

        addText(
            confidenceBox,
            "72%",
            29f,
            green,
            true,
            Gravity.CENTER
        )

        addText(
            confidenceBox,
            "CONFIDENCE",
            9f,
            muted,
            true,
            Gravity.CENTER
        )

        mainRow.addView(
            confidenceBox,
            LinearLayout.LayoutParams(dp(125), dp(125))
        )

        spaceHorizontal(mainRow, 15)

        val resultBox = LinearLayout(this)
        resultBox.orientation = LinearLayout.VERTICAL

        addText(resultBox, "POSITIVE EDGE", 18f, green, true)
        addText(resultBox, "Expected Range", 11f, muted, false)
        addText(resultBox, "24,320 – 24,480", 17f, white, true)
        addText(resultBox, "Current Price", 11f, muted, false)
        addText(resultBox, "24,198.30", 15f, blue, true)

        mainRow.addView(
            resultBox,
            LinearLayout.LayoutParams(0, dp(125), 1f)
        )

        analysis.addView(mainRow)

        space(analysis, 18)

        // QUICK FACTORS
        addText(analysis, "QUICK FACTORS", 12f, muted, true)

        space(analysis, 8)

        val factors = LinearLayout(this)
        factors.orientation = LinearLayout.HORIZONTAL

        factors.addView(
            factor("Trend", "+8", green),
            factorParams()
        )
        factors.addView(
            factor("Momentum", "+6", green),
            factorParams()
        )
        factors.addView(
            factor("Volume", "+4", green),
            factorParams()
        )
        factors.addView(
            factor("Volatility", "-3", red),
            factorParams()
        )

        analysis.addView(factors)

        space(analysis, 18)

        val button = Button(this)
        button.text = "VIEW FULL ANALYSIS   →"
        button.textSize = 14f
        button.setTextColor(white)
        button.typeface = Typeface.DEFAULT_BOLD
        button.background = rounded(blue, 28)

        analysis.addView(
            button,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        root.addView(analysis)

        space(root, 20)

        // DECISION
        val decision = LinearLayout(this)
        decision.orientation = LinearLayout.VERTICAL
        decision.setPadding(dp(16), dp(14), dp(16), dp(14))
        decision.background = rounded(card, 18)

        addText(decision, "DECISION", 12f, blue, true)
        addText(
            decision,
            "Positive edge, but be cautious near resistance.",
            15f,
            white,
            true
        )
        addText(
            decision,
            "Wait for confirmation if unsure.",
            12f,
            muted,
            false
        )

        root.addView(decision)

        space(root, 20)

        // WHAT WOULD CHANGE IT
        addText(root, "WHAT WOULD CHANGE IT?", 14f, white, true)
        space(root, 8)

        addChange(root, "●", "Break above 24,350", "Increases confidence", green)
        addChange(root, "●", "Drop below 24,050", "Turns neutral / negative", red)
        addChange(root, "●", "Higher volume confirmation", "Strengthens the setup", green)

        space(root, 15)

        // BOTTOM NAV
        val nav = LinearLayout(this)
        nav.orientation = LinearLayout.HORIZONTAL
        nav.gravity = Gravity.CENTER
        nav.setPadding(0, dp(8), 0, 0)

        val navItems = arrayOf(
            "⌂\nHome",
            "⌁\nMarkets",
            "✦\nAsk GDMIE",
            "◉\nInsights",
            "●\nProfile"
        )

        for (i in navItems.indices) {
            val item = TextView(this)
            item.text = navItems[i]
            item.textSize = 10f
            item.gravity = Gravity.CENTER
            item.setTextColor(if (i == 1) blue else muted)

            nav.addView(
                item,
                LinearLayout.LayoutParams(0, dp(58), 1f)
            )
        }

        root.addView(nav)

        setContentView(scroll)
    }

    private fun factor(
        name: String,
        value: String,
        color: Int
    ): LinearLayout {
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.gravity = Gravity.CENTER
        box.setPadding(dp(3), dp(8), dp(3), dp(8))
        box.background = rounded(card2, 13)

        addText(box, name, 9f, muted, false, Gravity.CENTER)
        addText(box, value, 17f, color, true, Gravity.CENTER)

        return box
    }

    private fun factorParams(): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(0, dp(70), 1f).apply {
            setMargins(dp(3), 0, dp(3), 0)
        }
    }

    private fun addChange(
        parent: LinearLayout,
        icon: String,
        title: String,
        subtitle: String,
        color: Int
    ) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(dp(12), dp(10), dp(12), dp(10))
        row.background = rounded(card, 14)

        val dot = TextView(this)
        dot.text = icon
        dot.textSize = 15f
        dot.setTextColor(color)
        dot.gravity = Gravity.CENTER

        row.addView(dot, LinearLayout.LayoutParams(dp(35), dp(55)))

        val texts = LinearLayout(this)
        texts.orientation = LinearLayout.VERTICAL

        addText(texts, title, 13f, white, true)
        addText(texts, subtitle, 11f, muted, false)

        row.addView(
            texts,
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        parent.addView(
            row,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(75)
            ).apply {
                bottomMargin = dp(7)
            }
        )
    }

    private fun addText(
        parent: LinearLayout,
        text: String,
        size: Float,
        color: Int,
        bold: Boolean,
        gravity: Int = Gravity.START
    ) {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = size
        tv.setTextColor(color)
        tv.gravity = gravity

        if (bold) {
            tv.setTypeface(null, Typeface.BOLD)
        }

        parent.addView(
            tv,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(4)
            }
        )
    }

    private fun space(parent: LinearLayout, height: Int) {
        val s = Space(this)
        parent.addView(
            s,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(height)
            )
        )
    }

    private fun spaceHorizontal(parent: LinearLayout, width: Int) {
        val s = Space(this)
        parent.addView(
            s,
            LinearLayout.LayoutParams(dp(width), dp(1))
        )
    }

    private fun rounded(color: Int, radius: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
