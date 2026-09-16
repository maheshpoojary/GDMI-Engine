package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*

class BacktestHistoryActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val panel = Color.rgb(11, 18, 31)
    private val cyan = Color.rgb(0, 220, 255)
    private val blue = Color.rgb(45, 155, 255)
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
    ): TextView = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun card(): LinearLayout =
        LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(panel)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 80))
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
            setPadding(dp(20), dp(22), dp(20), dp(30))
        }

        root.addView(
            text("BACKTEST HISTORY", 27f, cyan, true)
        )

        root.addView(
            text(
                "Previous GDMIE strategy backtest results",
                13f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        root.addView(space(22))

        val prefs = getSharedPreferences(
            "GDMIE_BACKTEST_HISTORY",
            MODE_PRIVATE
        )

        val history = prefs.getStringSet(
            "records",
            emptySet()
        )?.toList()?.reversed() ?: emptyList()

        if (history.isEmpty()) {
            val emptyCard = card()

            emptyCard.addView(
                text(
                    "NO BACKTEST RECORDS",
                    14f,
                    blue,
                    true
                )
            )

            emptyCard.addView(
                text(
                    "Run a strategy backtest to create your first history record.",
                    13f,
                    muted
                ).apply {
                    setPadding(0, dp(10), 0, 0)
                }
            )

            root.addView(
                emptyCard,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        } else {
            history.forEachIndexed { index, record ->

                val card = card()

                card.addView(
                    text(
                        "BACKTEST #${history.size - index}",
                        12f,
                        cyan,
                        true
                    )
                )

                card.addView(
                    text(
                        record,
                        13f,
                        white
                    ).apply {
                        setPadding(0, dp(10), 0, 0)
                        setLineSpacing(dp(3).toFloat(), 1f)
                    }
                )

                root.addView(
                    card,
                    LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, dp(12))
                    }
                )
            }

            root.addView(space(8))

            val clearButton = Button(this).apply {
                text = "CLEAR HISTORY"
                textSize = 12f
                setTextColor(white)
                background = GradientDrawable().apply {
                    setColor(Color.rgb(90, 35, 45))
                    cornerRadius = dp(12).toFloat()
                }
            }

            root.addView(
                clearButton,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(50)
                )
            )

            clearButton.setOnClickListener {
                prefs.edit().clear().apply()
                Toast.makeText(
                    this,
                    "Backtest history cleared",
                    Toast.LENGTH_SHORT
                ).show()
                recreate()
            }
        }

        root.addView(space(20))

        root.addView(
            text(
                "GDMIE • BACKTEST HISTORY",
                9f,
                Color.rgb(70, 95, 120)
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }
}
