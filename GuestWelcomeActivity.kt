package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class GuestWelcomeActivity : Activity() {

    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(30), dp(24), dp(24))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(2, 6, 16),
                    Color.rgb(8, 18, 42),
                    Color.rgb(20, 8, 42),
                    Color.rgb(2, 6, 16)
                )
            )
        }

        val logo = TextView(this).apply {
            text = "GDMIE"
            textSize = 34f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
        }

        val title = TextView(this).apply {
            text = "WELCOME, EXPLORER"
            textSize = 21f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(20), 0, 0)
        }

        val subtitle = TextView(this).apply {
            text = "Your journey into smarter decisions starts here."
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(22))
        }

        root.addView(logo)
        root.addView(title)
        root.addView(subtitle)

        val profile = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = GradientDrawable().apply {
                setColor(Color.rgb(12, 21, 40))
                cornerRadius = dp(20).toFloat()
                setStroke(dp(1), cyan)
            }
            elevation = dp(5).toFloat()
        }

        profile.addView(
            TextView(this).apply {
                text = "GUEST EXPLORER"
                textSize = 18f
                setTextColor(white)
                typeface = Typeface.DEFAULT_BOLD
                gravity = Gravity.CENTER
            }
        )

        profile.addView(
            TextView(this).apply {
                text = "LEVEL 1   •   0 XP   •   0 DECISIONS"
                textSize = 10f
                setTextColor(cyan)
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, 0)
            }
        )

        root.addView(
            profile,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(125)
            )
        )

        val features = TextView(this).apply {
            text = "✓  Explore GDMIE modes\n✓  Earn XP and build your streak\n✓  Learn from every decision\n✓  Save progress later by signing in"
            textSize = 12f
            setTextColor(white)
            setPadding(dp(8), dp(22), dp(8), dp(22))
            setLineSpacing(dp(5).toFloat(), 1f)
        }

        root.addView(features)

        val start = Button(this).apply {
            text = "START EXPLORING  →"
            textSize = 14f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            letterSpacing = 0.04f
            background = GradientDrawable().apply {
                setColor(purple)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), cyan)
            }
            elevation = dp(5).toFloat()
            setOnClickListener {
                startActivity(Intent(this@GuestWelcomeActivity, MainActivity::class.java))
                finish()
            }
        }

        root.addView(
            start,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        val footer = TextView(this).apply {
            text = "Guest Mode • No registration required"
            textSize = 9f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }

        root.addView(
            footer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }
}
