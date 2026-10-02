package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.gdmie.audio.GDMIEAudioManager

class SplashActivity : Activity() {

    private val bg = Color.rgb(2, 5, 15)
    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val gold = Color.rgb(255, 205, 70)
    private val white = Color.WHITE
    private val muted = Color.rgb(150, 170, 195)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = Color.rgb(5, 7, 18)

        setContentView(createSplash())

    }

    private fun createSplash(): View {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(28), dp(24), dp(24))

            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(2, 5, 15),
                    Color.rgb(6, 18, 42),
                    Color.rgb(18, 7, 42),
                    Color.rgb(3, 6, 18)
                )
            )
        }

        // Top AI badge
        val badge = TextView(this).apply {
            text = "✦  AI DECISION INTELLIGENCE"
            textSize = 10f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
            setPadding(dp(18), dp(9), dp(18), dp(9))

            background = GradientDrawable().apply {
                setColor(Color.rgb(8, 22, 42))
                cornerRadius = dp(30).toFloat()
                setStroke(dp(1), Color.rgb(35, 125, 190))
            }
        }

        root.addView(
            badge,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(38)
            )
        )

        addSpace(root, 52)

        // Futuristic symbol
        val symbol = TextView(this).apply {
            text = "✦"
            textSize = 62f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setShadowLayer(
                dp(18).toFloat(),
                0f,
                0f,
                Color.rgb(20, 170, 255)
            )
        }

        root.addView(
            symbol,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(82)
            )
        )

        addSpace(root, 8)

        // Main logo
        val logo = TextView(this).apply {
            text = "GDMIE"
            textSize = 58f
            setTextColor(white)
            typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )
            gravity = Gravity.CENTER
            letterSpacing = 0.12f
            setShadowLayer(
                dp(10).toFloat(),
                0f,
                0f,
                Color.rgb(90, 180, 255)
            )
        }

        root.addView(
            logo,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(82)
            )
        )

        addSpace(root, 8)

        val subtitle = TextView(this).apply {
            text = "GENERAL DECISION & MATHEMATICAL\nINTELLIGENCE ENGINE"
            textSize = 12f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.05f
            setLineSpacing(dp(3).toFloat(), 1f)
        }

        root.addView(subtitle)

        addSpace(root, 26)

        // Tagline
        val tagline = TextView(this).apply {
            text = "SEE THE EDGE\nBEFORE YOU DECIDE"
            textSize = 21f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.03f
            setLineSpacing(dp(4).toFloat(), 1f)
        }

        root.addView(tagline)

        addSpace(root, 18)

        val categories = TextView(this).apply {
            text = "Finance     •     Career     •     Life Decisions"
            textSize = 11f
            setTextColor(muted)
            gravity = Gravity.CENTER
            letterSpacing = 0.02f
        }

        root.addView(categories)

        addSpace(root, 28)

        // Premium CTA
        val begin = Button(this).apply {
            text = "LET'S BEGIN   →"
            textSize = 15f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            isAllCaps = false
            letterSpacing = 0.05f
            includeFontPadding = false

            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.rgb(35, 145, 255),
                    Color.rgb(45, 205, 255),
                    Color.rgb(115, 90, 255)
                )
            ).apply {
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(120, 225, 255))
            }

            elevation = dp(7).toFloat()

            setOnClickListener {
                GDMIEAudioManager.playUiClick(this@SplashActivity)
                animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction {
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        LoginActivity::class.java
                                    )
                                )
                                finish()
                            }
                            .start()
                    }
                    .start()
            }
        }

        root.addView(
            begin,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        addSpace(root, 14)

        val footer = TextView(this).apply {
            text = "DATA  •  ANALYSIS  •  CLARITY  •  BETTER DECISION"
            textSize = 9f
            setTextColor(muted)
            gravity = Gravity.CENTER
            letterSpacing = 0.04f
        }

        root.addView(footer)

        // Bottom identity
        val bottom = TextView(this).apply {
            text = "GDMIE  •  Learn  •  Analyze  •  Grow"
            textSize = 9f
            setTextColor(Color.rgb(100, 120, 150))
            gravity = Gravity.CENTER
        }

        root.addView(
            bottom,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        // CINEMATIC ENTRY — GDMIE awakens
        root.alpha = 0f

        root.animate()
            .alpha(1f)
            .setDuration(320)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // AI badge — soft arrival
        badge.alpha = 0f
        badge.translationY = -dp(18).toFloat()

        badge.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(60)
            .setDuration(320)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Core energy symbol — awaken
        symbol.alpha = 0f
        symbol.scaleX = 0.35f
        symbol.scaleY = 0.35f

        symbol.animate()
            .alpha(1f)
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setStartDelay(120)
            .setDuration(420)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                symbol.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(180)
                    .start()

                symbol.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .setDuration(420)
                    .withEndAction {
                        symbol.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(420)
                            .start()
                    }
                    .start()
            }
            .start()

        // GDMIE logo — cinematic rise
        logo.alpha = 0f
        logo.translationY = dp(28).toFloat()
        logo.scaleX = 0.92f
        logo.scaleY = 0.92f

        logo.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(220)
            .setDuration(420)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Subtitle
        subtitle.alpha = 0f
        subtitle.translationY = dp(18).toFloat()

        subtitle.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(320)
            .setDuration(320)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Tagline
        tagline.alpha = 0f
        tagline.translationY = dp(22).toFloat()

        tagline.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(400)
            .setDuration(380)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Categories
        categories.alpha = 0f

        categories.animate()
            .alpha(1f)
            .setStartDelay(470)
            .setDuration(450)
            .start()

        // Main CTA — energetic arrival
        begin.alpha = 0f
        begin.translationY = dp(26).toFloat()
        begin.scaleX = 0.94f
        begin.scaleY = 0.94f

        begin.animate()
            .alpha(1f)
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(550)
            .setDuration(400)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                begin.animate()
                    .scaleX(1.015f)
                    .scaleY(1.015f)
                    .setDuration(420)
                    .withEndAction {
                        begin.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(420)
                            .start()
                    }
                    .start()
            }
            .start()

        // Footer
        footer.alpha = 0f

        footer.animate()
            .alpha(1f)
            .setStartDelay(700)
            .setDuration(450)
            .start()

        // Bottom identity
        bottom.alpha = 0f
        bottom.animate()
            .alpha(1f)
            .setStartDelay(850)
            .setDuration(320)
            .start()

        return root
    }

    private fun addSpace(root: LinearLayout, height: Int) {
        root.addView(
            View(this),
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }
}
