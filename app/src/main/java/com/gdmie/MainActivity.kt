package com.gdmie

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.text.InputType
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
        presentInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(presentInput)

        val recentInput = EditText(this)
        recentInput.hint = "Recent Value"
        recentInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(recentInput)

        val expectedInput = EditText(this)
        expectedInput.hint = "Expected Value"
        expectedInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(expectedInput)

        val marketInput = EditText(this)
        marketInput.hint = "Market Value"
        marketInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(marketInput)

        val contextInput = EditText(this)
        contextInput.hint = "Context Factor"
        contextInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(contextInput)

        val momentumInput = EditText(this)
        momentumInput.hint = "Momentum Factor"
        momentumInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(momentumInput)

        val riskInput = EditText(this)
        riskInput.hint = "Risk Factor"
        riskInput.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        layout.addView(riskInput)

        val button = Button(this)
        button.text = "RUN GDM ENGINE"
        layout.addView(button)

        val result = TextView(this)
        result.textSize = 18f
        result.setPadding(0, 40, 0, 0)
        layout.addView(result)

        button.setOnClickListener {

            val present = presentInput.text.toString().trim().toDoubleOrNull()
            val recent = recentInput.text.toString().trim().toDoubleOrNull()
            val expected = expectedInput.text.toString().trim().toDoubleOrNull()
            val market = marketInput.text.toString().trim().toDoubleOrNull()
            val context = contextInput.text.toString().trim().toDoubleOrNull()
            val momentum = momentumInput.text.toString().trim().toDoubleOrNull()
            val risk = riskInput.text.toString().trim().toDoubleOrNull()

            if (
                present == null ||
                recent == null ||
                expected == null ||
                market == null ||
                context == null ||
                momentum == null ||
                risk == null
            ) {
                result.text = "Please enter all values."
                return@setOnClickListener
            }

            val input = GDMInput(
                presentValue = present,
                recentValue = recent,
                expectedValue = expected,
                marketValue = market,
                contextFactor = context,
                momentumFactor = momentum,
                riskFactor = risk
            )

            val output = GDMEngine.calculate(input)

            result.text =
                "Expected Value: ${output.expectedValue}\n\n" +
                "Edge: ${output.edge}\n\n" +
                "Confidence: ${output.confidence}\n\n" +
                "Decision: ${output.decision}"
        }

        setContentView(layout)
    }
}
