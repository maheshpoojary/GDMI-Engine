package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class DecisionHistoryActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val card = Color.rgb(11, 18, 31)
    private val cyan = Color.rgb(0, 220, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val green = Color.rgb(55, 225, 135)
    private val red = Color.rgb(255, 90, 100)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun text(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView =
        TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply {
                setColor(card)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), Color.rgb(24, 52, 78))
            }
        }

    private fun space(h: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(h))
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
            setPadding(dp(18), dp(20), dp(18), dp(30))
        }

        root.addView(text("DECISION HISTORY", 28f, cyan, true))

        root.addView(
            text(
                "Previous GDMIE engine analyses",
                13f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(space(20))

        val historyContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        root.addView(
            historyContainer,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val prefs = getSharedPreferences("GDMIE_HISTORY", MODE_PRIVATE)
        val count = prefs.getInt("count", 0)

        if (count == 0) {
            val empty = card()

            empty.addView(
                text(
                    "NO ANALYSIS HISTORY",
                    17f,
                    white,
                    true
                )
            )

            empty.addView(
                text(
                    "Completed analyses will appear here automatically.",
                    12f,
                    muted
                ).apply {
                    setPadding(0, dp(7), 0, 0)
                }
            )

            historyContainer.addView(empty)
        } else {
            for (i in count downTo 1) {

                val item = card()

                val decision = prefs.getString(
                    "decision_$i",
                    "NEUTRAL"
                ) ?: "NEUTRAL"

                val edge = prefs.getString(
                    "edge_$i",
                    "0.00"
                ) ?: "0.00"

                val confidence = prefs.getString(
                    "confidence_$i",
                    "0%"
                ) ?: "0%"

                val momentum = prefs.getString(
                    "momentum_$i",
                    "0.00"
                ) ?: "0.00"

                val risk = prefs.getString(
                    "risk_$i",
                    "0.00"
                ) ?: "0.00"

                val gap = prefs.getString(
                    "gap_$i",
                    "0.00"
                ) ?: "0.00"

                val time = prefs.getString(
                    "time_$i",
                    "Unknown time"
                ) ?: "Unknown time"

                val decisionColor = when (decision) {
                    "POSITIVE EDGE" -> green
                    "NEGATIVE EDGE" -> red
                    else -> cyan
                }

                item.addView(
                    text(
                        "#$i  $decision",
                        18f,
                        decisionColor,
                        true
                    )
                )

                item.addView(
                    text(
                        time,
                        10f,
                        muted
                    ).apply {
                        setPadding(0, dp(4), 0, 0)
                    }
                )

                item.addView(space(10))

                item.addView(
                    text(
                        "Reverse Edge     $edge",
                        13f,
                        white,
                        true
                    )
                )

                item.addView(
                    text(
                        "Confidence       $confidence\n" +
                        "Momentum         $momentum\n" +
                        "Risk             $risk\n" +
                        "Gap              $gap",
                        11f,
                        muted
                    ).apply {
                        setPadding(0, dp(7), 0, 0)
                    }
                )

                historyContainer.addView(item)

                if (i > 1) {
                    historyContainer.addView(space(10))
                }
            }
        }

        root.addView(space(20))

        val clearButton = Button(this).apply {
            text = "CLEAR HISTORY"
            textSize = 12f
            setTextColor(white)

            background = GradientDrawable().apply {
                setColor(Color.rgb(30, 42, 58))
                cornerRadius = dp(14).toFloat()
            }

            setOnClickListener {
                prefs.edit().clear().apply()
                Toast.makeText(
                    this@DecisionHistoryActivity,
                    "Decision history cleared",
                    Toast.LENGTH_SHORT
                ).show()
                recreate()
            }
        }

        root.addView(
            clearButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(52)
            )
        )

        root.addView(space(18))

        root.addView(
            text(
                "GDMIE • LOCAL DECISION HISTORY",
                9f,
                Color.rgb(70, 95, 120)
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }
}
