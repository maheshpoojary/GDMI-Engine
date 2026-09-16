package com.gdmie

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.gdmie.network.GDMIEEngineGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.max

class DataInputActivity : Activity() {

    private val fields = mutableMapOf<String, EditText>()

    private val white = Color.WHITE
    private val muted = Color.rgb(150, 165, 190)
    private val blue = Color.rgb(45, 140, 255)
    private val green = Color.rgb(40, 210, 125)
    private val red = Color.rgb(245, 80, 90)
    private val bg = Color.rgb(5, 11, 22)
    private val card = Color.rgb(15, 25, 40)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val scroll = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(24), dp(20), dp(30))
        }

        val title = TextView(this).apply {
            text = "DATA INPUT"
            textSize = 28f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        content.addView(title)

        val subtitle = TextView(this).apply {
            text = "Enter present data for GDMIE calculation"
            textSize = 15f
            setTextColor(muted)
            setPadding(0, dp(6), 0, dp(20))
        }
        content.addView(subtitle)

        addSection(content, "CORE VALUES", blue)

        addField(content, "Present Value", "presentValue")
        addField(content, "Expected Value", "expectedValue")
        addField(content, "Target Value", "targetValue")

        addSection(content, "MOMENTUM", green)

        addField(content, "Recent Momentum", "recentMomentum")
        addField(content, "Immediate Momentum", "immediateMomentum")
        addField(content, "2-Min Market Advantage", "twoMinMarketAdvantage")

        addSection(content, "MARKET", blue)

        addField(content, "Exact Market Line", "exactMarketLine")
        addField(content, "Odds", "odds")
        addField(content, "Odds Movement", "oddsMovement")

        addSection(content, "RISK & TIMING", red)

        addField(content, "Timing Factor", "timingFactor")
        addField(content, "Risk Factor", "riskFactor")

        val runButton = Button(this).apply {
            text = "RUN GDM ENGINE"
            textSize = 17f
            setTextColor(Color.WHITE)
            isAllCaps = false
            background = rounded(blue, 18)
            setPadding(0, dp(6), 0, dp(6))
        }

        val buttonParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dp(58)
        )
        buttonParams.topMargin = dp(18)
        content.addView(runButton, buttonParams)

        runButton.setOnClickListener {
            calculateAndReturn()
        }

        scroll.addView(content)
        root.addView(scroll)

        setContentView(root)
    }

    private fun addSection(
        parent: LinearLayout,
        text: String,
        color: Int
    ) {
        val section = TextView(this).apply {
            this.text = text
            textSize = 14f
            setTextColor(color)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, dp(14), 0, dp(8))
        }

        parent.addView(
            section,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun addField(
        parent: LinearLayout,
        label: String,
        key: String
    ) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            background = rounded(card, 18)
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = dp(10)

        parent.addView(box, params)

        val labelView = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(muted)
        }
        box.addView(labelView)

        val edit = EditText(this).apply {
            hint = "Enter value"
            textSize = 18f
            setTextColor(white)
            setHintTextColor(Color.rgb(90, 105, 125))
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                    android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setSingleLine(true)
        }

        box.addView(
            edit,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
            )
        )

        fields[key] = edit
    }

    private fun calculateAndReturn() {

        fun v(key: String): Double {
            return fields[key]?.text?.toString()?.toDoubleOrNull() ?: 0.0
        }

        val input = GDMInput(
            presentValue = v("presentValue"),
            expectedValue = v("expectedValue"),
            targetValue = v("targetValue"),
            recentMomentum = v("recentMomentum"),
            immediateMomentum = v("immediateMomentum"),
            twoMinMarketAdvantage = v("twoMinMarketAdvantage"),
            exactMarketLine = v("exactMarketLine"),
            odds = v("odds"),
            oddsMovement = v("oddsMovement"),
            timingFactor = v("timingFactor"),
            riskFactor = v("riskFactor")
        )

        CoroutineScope(Dispatchers.IO).launch {

            val result = GDMIEEngineGateway.calculate(input)

            runOnUiThread {

                getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
                    .edit()
                    .putString("edge", "%.2f".format(result.edge))
                    .putString("momentum", "%.2f".format(result.momentum))
                    .putString("risk", "%.2f".format(result.risk))
                    .putString("confidence", "%.0f%%".format(result.confidence * 100))
                    .apply()

                val intent = android.content.Intent(
                    this@DataInputActivity,
                    DecisionOutputActivity::class.java
                )

                intent.putExtra("presentValue", result.presentValue)
                intent.putExtra("expectedValue", result.expectedValue)
                intent.putExtra("marketValue", result.marketValue)
                intent.putExtra("gap", result.gap)
                intent.putExtra("momentum", result.momentum)
                intent.putExtra("risk", result.risk)
                intent.putExtra("edge", result.edge)
                intent.putExtra("confidence", result.confidence)
                intent.putExtra("decision", result.decision)
                intent.putExtra("explanation", result.explanation)

                startActivity(intent)
            }
        }
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
