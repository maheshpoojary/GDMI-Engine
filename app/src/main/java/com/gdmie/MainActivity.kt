package com.gdmie

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 40, 32, 32)

        val title = TextView(this)
        title.text = "GDMIE"
        title.textSize = 32f
        title.setTextColor(Color.BLACK)
        title.gravity = Gravity.CENTER
        layout.addView(title)

        val info = TextView(this)
        info.text = "General Decision & Mathematical Intelligence Engine"
        info.textSize = 18f
        info.gravity = Gravity.CENTER
        layout.addView(info)

        val presentInput = EditText(this)
        presentInput.hint = "Present Value"
        presentInput.inputType = 2
        layout.addView(presentInput)

        val recentInput = EditText(this)
        recentInput.hint = "Recent Value"
        recentInput.inputType = 2
        layout.addView(recentInput)

        val expectedInput = EditText(this)
        expectedInput.hint = "Expected Value"
        expectedInput.inputType = 2
        layout.addView(expectedInput)

        val marketInput = EditText(this)
        marketInput.hint = "Market Value"
        marketInput.inputType = 2
        layout.addView(marketInput)

        val contextInput = EditText(this)
        contextInput.hint = "Context Factor"
        contextInput.inputType = 2
        layout.addView(contextInput)

        val momentumInput = EditText(this)
        momentumInput.hint = "Momentum Factor"
        momentumInput.inputType = 2
        layout.addView(momentumInput)

        val riskInput = EditText(this)
        riskInput.hint = "Risk Factor"
        riskInput.inputType = 2
        layout.addView(riskInput)

        val button = Button(this)
        button.text = "RUN GDM ENGINE"
        layout.addView(button)

        val result = TextView(this)
        result.textSize = 18f
        result.setPadding(0, 40, 0, 0)
        layout.addView(result)

        button.setOnClickListener {

            val input = GDMInput(
                presentValue = presentInput.text.toString().toDoubleOrNull() ?: 0.0,
                recentValue = recentInput.text.toString().toDoubleOrNull() ?: 0.0,
                expectedValue = expectedInput.text.toString().toDoubleOrNull() ?: 0.0,
                marketValue = marketInput.text.toString().toDoubleOrNull() ?: 0.0,
                contextFactor = contextInput.text.toString().toDoubleOrNull() ?: 0.0,
                momentumFactor = momentumInput.text.toString().toDoubleOrNull() ?: 0.0,
                riskFactor = riskInput.text.toString().toDoubleOrNull() ?: 0.0
            )

            val output = GDMEngine.calculate(input)

            result.text =
                "Expected Value: ${output.expectedValue}\n" +
                "Edge: ${output.edge}\n" +
                "Confidence: ${output.confidence}\n" +
                "Decision: ${output.decision}"
        }

        setContentView(layout)
    }
}
