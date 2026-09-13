package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.setPadding(32, 32, 32, 32)
        layout.setBackgroundColor(Color.rgb(3, 10, 25))

        val title = TextView(this)
        title.text = "GDMIE"
        title.textSize = 48f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER

        val subtitle = TextView(this)
        subtitle.text = "DECISION INTELLIGENCE ENGINE"
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.rgb(80, 190, 255))
        subtitle.gravity = Gravity.CENTER

        val tagline = TextView(this)
        tagline.text = "\nTHINK SMARTER. DECIDE BETTER. LIVE AHEAD."
        tagline.textSize = 12f
        tagline.setTextColor(Color.LTGRAY)
        tagline.gravity = Gravity.CENTER

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(tagline)

        setContentView(layout)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
