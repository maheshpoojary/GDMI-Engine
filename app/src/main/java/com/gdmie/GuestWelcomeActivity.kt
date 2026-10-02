package com.gdmie

import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class GuestWelcomeActivity : Activity() {

    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun animateCinematic(root: ViewGroup) {

        // CINEMATIC ENTRY — Explorer Initialization

        // Universe fade + subtle camera push
        root.alpha = 0f
        root.scaleX = 1.025f
        root.scaleY = 1.025f

        root.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800L)
            .setInterpolator(
                android.view.animation.DecelerateInterpolator(1.7f)
            )
            .start()

        // Sequential HUD-style reveal
        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i)

            child.alpha = 0f
            child.translationY = dp(30).toFloat()
            child.scaleX = 0.97f
            child.scaleY = 0.97f

            child.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(160L + (i * 105L))
                .setDuration(570L)
                .setInterpolator(
                    android.view.animation.DecelerateInterpolator(1.7f)
                )
                .start()
        }

        // GDMIE logo — identity ignition
        val logo = root.getChildAt(0)

        logo.alpha = 0f
        logo.scaleX = 0.72f
        logo.scaleY = 0.72f

        logo.animate()
            .alpha(1f)
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setStartDelay(180L)
            .setDuration(650L)
            .setInterpolator(
                android.view.animation.OvershootInterpolator(1.15f)
            )
            .withEndAction {
                logo.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300L)
                    .start()
            }
            .start()

        // Explorer profile card — system initialization
        if (root.childCount > 3) {
            val profile = root.getChildAt(3)

            profile.alpha = 0f
            profile.translationY = dp(24).toFloat()
            profile.scaleX = 0.94f
            profile.scaleY = 0.94f

            profile.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1.02f)
                .scaleY(1.02f)
                .setStartDelay(600L)
                .setDuration(650L)
                .setInterpolator(
                    android.view.animation.OvershootInterpolator(1.2f)
                )
                .withEndAction {
                    profile.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300L)
                        .start()
                }
                .start()
        }

        // START EXPLORING — final activation pulse
        root.postDelayed({
            for (i in 0 until root.childCount) {
                val child = root.getChildAt(i)

                if (child is Button &&
                    child.text.toString().contains("START EXPLORING")
                ) {
                    child.animate()
                        .scaleX(1.045f)
                        .scaleY(1.045f)
                        .setDuration(380L)
                        .setInterpolator(
                            android.view.animation.AccelerateDecelerateInterpolator()
                        )
                        .withEndAction {
                            child.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(520L)
                                .start()
                        }
                        .start()
                    break
                }
            }
        }, 1250L)
    }

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

                isEnabled = false

                GDMIEAudioManager.playSfx(
                    this@GuestWelcomeActivity,
                    R.raw.gdmie_guest_home
                )

                animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(120L)
                    .withEndAction {

                        val rootView =
                            this@GuestWelcomeActivity
                                .findViewById<ViewGroup>(android.R.id.content)

                        rootView.animate()
                            .alpha(0f)
                            .scaleX(1.045f)
                            .scaleY(1.045f)
                            .setDuration(520L)
                            .setInterpolator(
                                android.view.animation.AccelerateInterpolator(1.4f)
                            )
                            .withEndAction {

                                startActivity(
                                    Intent(
                                        this@GuestWelcomeActivity,
                                        MainActivity::class.java
                                    )
                                )

                                overridePendingTransition(0, 0)
                                finish()
                            }
                            .start()
                    }
                    .start()
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
        animateCinematic(root)
    }
}
