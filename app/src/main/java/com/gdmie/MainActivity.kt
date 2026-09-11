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

        val subtitle = TextView(this)
        subtitle.text = "General Decision & Mathematical Intelligence Engine"
        subtitle.textSize = 16f
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 12, 0, 30)

        val scoreInput = EditText(this)
        scoreInput.hint = "Current Score (e.g. 122)"

        val oversInput = EditText(this)
        oversInput.hint = "Overs (e.g. 12.4)"

        val wicketsInput = EditText(this)
        wicketsInput.hint = "Wickets Lost (e.g. 4)"

        val marketInput = EditText(this)
        marketInput.hint = "Market Line (e.g. 185.5)"

        val calculateButton = Button(this)
        calculateButton.text = "ANALYZE"

        val result = TextView(this)
        result.text = "GDMIE Engine Ready"
        result.textSize = 20f
        result.setPadding(0, 30, 0, 0)
        result.gravity = Gravity.CENTER

        calculateButton.setOnClickListener {
            result.text = "INPUT RECEIVED\n\nGDMIE ANALYSIS READY"
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(scoreInput)
        layout.addView(oversInput)
        layout.addView(wicketsInput)
        layout.addView(marketInput)
        layout.addView(calculateButton)
        layout.addView(result)

        setContentView(layout)
    }
}
