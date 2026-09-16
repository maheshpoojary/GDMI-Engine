package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class SplashActivity : Activity() {

    private val bg = Color.rgb(3, 10, 20)
    private val blue = Color.rgb(55, 165, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(155, 180, 200)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER
        root.setPadding(28, 30, 28, 30)
        root.background = GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                Color.rgb(2, 8, 18),
                Color.rgb(4, 20, 38),
                Color.rgb(2, 8, 18)
            )
        )

        val glow = TextView(this)
        glow.text = "✦"
        glow.textSize = 64f
        glow.setTextColor(blue)
        glow.gravity = Gravity.CENTER
        root.addView(glow)

        val logo = TextView(this)
        logo.text = "GDMIE"
        logo.textSize = 52f
        logo.setTextColor(white)
        logo.typeface = Typeface.DEFAULT_BOLD
        logo.gravity = Gravity.CENTER
        root.addView(logo)

        val subtitle = TextView(this)
        subtitle.text = "DECISION INTELLIGENCE ENGINE"
        subtitle.textSize = 13f
        subtitle.setTextColor(blue)
        subtitle.typeface = Typeface.DEFAULT_BOLD
        subtitle.gravity = Gravity.CENTER
        root.addView(subtitle)

        val line = TextView(this)
        line.text = "SEE THE EDGE\nBEFORE YOU DECIDE"
        line.textSize = 18f
        line.setTextColor(white)
        line.gravity = Gravity.CENTER
        line.typeface = Typeface.DEFAULT_BOLD
        root.addView(line)

        val description = TextView(this)
        description.text = "Markets\nInvestments\nLife Decisions\nA Smarter You"
        description.textSize = 15f
        description.setTextColor(muted)
        description.gravity = Gravity.CENTER
        root.addView(description)

        val begin = Button(this)
        begin.text = "LET'S BEGIN  →"
        begin.textSize = 15f
        begin.setTextColor(white)
        begin.typeface = Typeface.DEFAULT_BOLD
        begin.background = roundedButton(blue)

        root.addView(
            begin,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                58
            )
        )

        val footer = TextView(this)
        footer.text = "DATA • ANALYSIS • CLARITY • BETTER DECISION"
        footer.textSize = 9f
        footer.setTextColor(muted)
        footer.gravity = Gravity.CENTER
        root.addView(footer)

        begin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setContentView(root)
    }

    private fun roundedButton(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = 60f
            setStroke(2, Color.WHITE)
        }
    }
}
