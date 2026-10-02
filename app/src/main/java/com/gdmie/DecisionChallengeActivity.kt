package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.gdmie.audio.GDMIEAudioManager
import com.gdmie.game.GDMIEGameProgress

class DecisionChallengeActivity : Activity() {

    private val bg = Color.rgb(3, 6, 14)
    private val surface = Color.rgb(10, 18, 35)
    private val surface2 = Color.rgb(16, 27, 52)
    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val gold = Color.rgb(255, 205, 70)
    private val green = Color.rgb(45, 225, 135)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val border = Color.rgb(35, 75, 115)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun rounded(
        color: Int,
        radius: Int = 20,
        strokeColor: Int = border
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(1), strokeColor)
        }
    }

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            includeFontPadding = false
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun challengeCard(
        icon: String,
        title: String,
        subtitle: String,
        xp: String,
        color: Int,
        action: () -> Unit
    ) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(15), dp(12), dp(15))
            background = rounded(surface2, 22, color)
            elevation = dp(6).toFloat()
            isClickable = true
            setOnClickListener {
                animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(70)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .withEndAction { action() }
                            .start()
                    }
                    .start()
            }
        }

        val iconBox = TextView(this).apply {
            text = icon
            textSize = 25f
            gravity = Gravity.CENTER
            setTextColor(color)
            background = rounded(
                Color.rgb(7, 15, 30),
                17,
                color
            )
        }

        card.addView(
            iconBox,
            LinearLayout.LayoutParams(dp(52), dp(60))
        )

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, dp(8), 0)
        }

        info.addView(
            text(title, 15f, white, true)
        )

        info.addView(
            text(subtitle, 10f, muted).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        info.addView(
            text(xp, 9f, color, true).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        card.addView(
            info,
            LinearLayout.LayoutParams(0, dp(60), 1f)
        )

        val arrow = text("›", 30f, color, true).apply {
            gravity = Gravity.CENTER
        }

        card.addView(
            arrow,
            LinearLayout.LayoutParams(dp(30), dp(60))
        )

        window.decorView.findViewById<LinearLayout>(android.R.id.content)
            ?.let { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(35))
        }

        root.addView(
            text("DECISION", 11f, cyan, true).apply {
                letterSpacing = 0.12f
            }
        )

        root.addView(
            text("CHALLENGE", 30f, white, true).apply {
                setPadding(0, dp(3), 0, 0)
            }
        )

        root.addView(
            text(
                "Train your decision-making.\nAnalyze • Decide • Check • Learn.",
                12f,
                muted
            ).apply {
                setPadding(0, dp(8), 0, dp(22))
                setLineSpacing(2f, 1f)
            }
        )


        root.addView(
            text("CHOOSE YOUR CHALLENGE", 11f, cyan, true).apply {
                letterSpacing = 0.10f
                setPadding(0, dp(26), 0, dp(12))
            }
        )

        val quickCard = makeCard(
            "⚡",
            "QUICK DECISION",
            "Fast simulation • Instant analysis",
            "+10 XP  •  No time pressure",
            cyan
        ) { GDMIEAudioManager.playUiClick(this); openInput("QUICK") }

        root.addView(
            quickCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
            ).apply { bottomMargin = dp(12) }
        )

        val timeCard = makeCard(
            "⏱",
            "TIME ATTACK",
            "Make your decision before the clock runs out",
            "+20 XP  •  Speed + accuracy",
            purple
        ) { GDMIEAudioManager.playUiClick(this); openInput("TIME_ATTACK") }

        root.addView(
            timeCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
            ).apply { bottomMargin = dp(12) }
        )

        val dailyStreak = GDMIEGameProgress.getStreak(this)
        val dailyXp = GDMIEGameProgress.getXp(this)
        val dailyLevel = GDMIEGameProgress.getLevel(this)

        val today = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.US
        ).format(java.util.Date())

        val lastDailyDate = getSharedPreferences(
            "GDMIE_GAME_PROGRESS",
            MODE_PRIVATE
        ).getString("daily_last_date", null)

        val dailyCompletedToday = lastDailyDate == today

        val dailySubtitle =
            if (dailyCompletedToday) {
                "🔥 TODAY COMPLETE • Streak $dailyStreak"
            } else if (dailyStreak > 0) {
                "🔥 $dailyStreak day streak • Keep it going"
            } else {
                "One challenge every day • Build your streak"
            }

        val dailyReward =
            if (dailyCompletedToday) {
                "✓ Completed  •  Level $dailyLevel  •  $dailyXp XP"
            } else {
                "+30 XP  •  Level $dailyLevel  •  $dailyXp XP"
            }

        val dailyCard = makeCard(
            "🏆",
            "DAILY CHALLENGE",
            dailySubtitle,
            dailyReward,
            gold
        ) { GDMIEAudioManager.playUiClick(this); openInput("DAILY") }

        root.addView(
            dailyCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
            )
        )

        val learningCard = makeCard(
            "🧠",
            "DAILY LEARNING",
            "Review decisions • Learn from outcomes",
            "History  •  Insights  •  Growth",
            cyan
        ) {
            GDMIEAudioManager.playUiClick(this)
            startActivity(
                Intent(
                    this,
                    DailyLearningActivity::class.java
                )
            )
        }

        root.addView(
            learningCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
            ).apply {
                topMargin = dp(12)
            }
        )

        // ─────────────────────────────────────────────
        // DECISION JOURNEY
        // All four existing modes are connected through
        // the same decision-learning loop.
        // ─────────────────────────────────────────────
        root.addView(
            text(
                "YOUR DECISION JOURNEY",
                10f,
                cyan,
                true
            ).apply {
                gravity = Gravity.CENTER
                letterSpacing = 0.14f
                setPadding(0, dp(28), 0, dp(5))
            }
        )

        root.addView(
            text(
                "CHOOSE → OBSERVE → THINK → LOCK → REVEAL → LEARN",
                8.5f,
                Color.rgb(120, 145, 175),
                true
            ).apply {
                gravity = Gravity.CENTER
                letterSpacing = 0.06f
                setPadding(0, dp(3), 0, dp(18))
            }
        )

        root.addView(
            text(
                "Every mode trains the same core decision skill.",
                9f,
                muted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(18))
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun makeCard(
        icon: String,
        title: String,
        subtitle: String,
        xp: String,
        color: Int,
        action: () -> Unit
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(12), dp(12), dp(12))
            background = rounded(surface2, 22, color)
            elevation = dp(6).toFloat()

            setOnClickListener {
                animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(70)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .withEndAction { action() }
                            .start()
                    }
                    .start()
            }
        }

        val iconView = TextView(this).apply {
            text = icon
            textSize = 24f
            gravity = Gravity.CENTER
            background = rounded(Color.rgb(6, 14, 28), 17, color)
        }

        card.addView(
            iconView,
            LinearLayout.LayoutParams(dp(52), dp(62))
        )

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, dp(5), 0)
        }

        info.addView(text(title, 14f, white, true))

        info.addView(
            text(subtitle, 9.5f, muted).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        info.addView(
            text(xp, 8.5f, color, true).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        card.addView(
            info,
            LinearLayout.LayoutParams(0, dp(62), 1f)
        )

        card.addView(
            text("›", 30f, color, true).apply {
                gravity = Gravity.CENTER
            },
            LinearLayout.LayoutParams(dp(30), dp(62))
        )

        return card
    }

    private fun openInput(mode: String) {
        val intent = Intent(this, ChallengeScenarioActivity::class.java)
        intent.putExtra("challenge_mode", mode)
        startActivity(intent)
    }
}
