package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class DailyRevealActivity : Activity() {

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun tv(
        text: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, android.graphics.Typeface.BOLD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cyan = Color.rgb(0, 220, 255)
        val purple = Color.rgb(170, 90, 255)
        val gold = Color.rgb(255, 200, 70)
        val white = Color.WHITE
        val muted = Color.rgb(170, 180, 195)
        val bg = Color.rgb(7, 10, 20)
        val card = Color.rgb(18, 22, 38)

        val mode =
            intent.getStringExtra("challenge_mode") ?: "DAILY"

        val decision =
            intent.getStringExtra("challenge_decision") ?: "Unknown"

        val priority =
            intent.getStringExtra("challenge_priority") ?: "Unknown"

        val rethink =
            intent.getStringExtra("challenge_rethink") ?: "KEEP"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(20), dp(24), dp(20), dp(30))
        }

        val scroll = ScrollView(this)

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        content.addView(
            tv("GDMIE • DECISION JOURNEY", 10f, cyan, true)
        )

        content.addView(
            tv("🧠 GDMIE REVEAL", 29f, white, true).apply {
                setPadding(0, dp(8), 0, dp(6))
            }
        )

        content.addView(
            tv(
                "Your decision is locked. Now see the reasoning layer.",
                13f,
                muted
            ).apply {
                setPadding(0, 0, 0, dp(20))
            }
        )

        val decisionCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            setBackgroundColor(card)
        }

        decisionCard.addView(
            tv("YOUR LOCKED DECISION", 10f, purple, true)
        )

        decisionCard.addView(
            tv(decision, 18f, white, true).apply {
                setPadding(0, dp(9), 0, dp(8))
            }
        )

        decisionCard.addView(
            tv("Priority: $priority", 12f, muted)
        )

        decisionCard.addView(
            tv("Rethink: $rethink", 12f, muted).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        content.addView(
            decisionCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        val signalCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(17), dp(17), dp(17), dp(18))
            setBackgroundColor(card)
        }

        signalCard.addView(
            tv("GDMIE SIGNAL LAYER", 11f, cyan, true)
        )

        signalCard.addView(
            tv(
                "GDMIE evaluates the decision through:",
                14f,
                white,
                true
            ).apply {
                setPadding(0, dp(10), 0, dp(10))
            }
        )

        signalCard.addView(
            tv("• Present Situation", 13f, muted)
        )

        signalCard.addView(
            tv("• Expected Outcome", 13f, muted).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        signalCard.addView(
            tv("• Context & Momentum", 13f, muted).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        signalCard.addView(
            tv("• Risk & Timing", 13f, muted).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        content.addView(
            signalCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(18)
            }
        )

        content.addView(
            tv(
                "The reveal explains the decision process — it does not replace your judgment.",
                11f,
                muted
            ).apply {
                setPadding(dp(4), 0, dp(4), dp(18))
            }
        )

        val continueButton = TextView(this).apply {
            text = "🎯 CONTINUE TO ACTUAL OUTCOME"
            textSize = 15f
            setTextColor(bg)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setBackgroundColor(gold)
            isClickable = true

            setOnClickListener {
                GDMIEAudioManager.playUiClick(
                    this@DailyRevealActivity
                )

                val scenarioTheme =
                    intent.getStringExtra("scenario_theme") ?: "GENERAL"

                val challengeId =
                    intent.getStringExtra("challenge_id")
                        ?: "DAILY_${scenarioTheme}_${System.currentTimeMillis()}"

                val next = Intent(
                    this@DailyRevealActivity,
                    GameOutcomeActivity::class.java
                )

                next.putExtra("challenge_mode", mode)
                next.putExtra("challenge_id", challengeId)
                next.putExtra("challenge_decision", decision)
                next.putExtra("challenge_priority", priority)
                next.putExtra("challenge_rethink", rethink)
                next.putExtra("decision_locked", true)
                next.putExtra("from_reveal", true)

                next.putExtra(
                    "scenario_id",
                    when (scenarioTheme) {
                        "OPPORTUNITY" -> "opportunity"
                        "TIMING" -> "timing"
                        "CONFLICT" -> "conflict"
                        "UNCERTAINTY" -> "hidden_risk"
                        "SHORTCUT" -> "reversal"
                        "TRANSITION" -> "momentum"
                        "ADAPTIVE" -> "momentum"
                        "TRADE_OFF" -> "conflict"
                        "CONSTRAINT" -> "resource"
                        else -> "signal_shift"
                    }
                )

                next.putExtra("scenario_title", "Daily Challenge")
                next.putExtra("scenario_category", scenarioTheme)

                startActivity(next)
            }
        }

        content.addView(
            continueButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
            )
        )

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }
}
