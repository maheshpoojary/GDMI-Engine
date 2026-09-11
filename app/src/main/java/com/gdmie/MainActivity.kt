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

        val button = Button(this)
        button.text = "RUN GDM ENGINE"

        layout.addView(button)

        val result = TextView(this)
        result.textSize = 18f
        result.setPadding(0, 40, 0, 0)

        layout.addView(result)

        button.setOnClickListener {

            val input = GDMInput(
                presentValue = 50.0,
                recentValue = 55.0,
                expectedValue = 60.0,
                marketValue = 52.0,
                contextFactor = 50.0,
                momentumFactor = 50.0,
                riskFactor = 20.0
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
