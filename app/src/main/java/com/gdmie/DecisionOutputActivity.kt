package com.gdmie

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class DecisionOutputActivity : Activity() {

    private val bg = Color.rgb(4, 8, 18)
    private val panel = Color.rgb(10, 17, 32)
    private val cyan = Color.rgb(0, 220, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(155, 170, 190)
    private val green = Color.rgb(60, 230, 140)
    private val red = Color.rgb(255, 80, 100)

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(panel)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 80))
            }
        }
    }

    private fun metricRow(
        parent: LinearLayout,
        name: String,
        value: String
    ) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        row.addView(
            label(name, 14f, muted),
            LinearLayout.LayoutParams(0, dp(46), 1f)
        )

        row.addView(
            label(value, 15f, white, true).apply {
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
            },
            LinearLayout.LayoutParams(0, dp(46), 1f)
        )

        parent.addView(row)
    }

    private fun divider(): View {
        return View(this).apply {
            setBackgroundColor(Color.rgb(28, 42, 58))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val present = intent.getDoubleExtra("presentValue", 0.0)
        val expected = intent.getDoubleExtra("expectedValue", 0.0)
        val market = intent.getDoubleExtra("marketValue", 0.0)
        val gap = intent.getDoubleExtra("gap", 0.0)
        val momentum = intent.getDoubleExtra("momentum", 0.0)
        val risk = intent.getDoubleExtra("risk", 0.0)
        val edge = intent.getDoubleExtra("edge", 0.0)
        val confidence = intent.getDoubleExtra("confidence", 0.0)
        val decision = intent.getStringExtra("decision") ?: "NEUTRAL"
        val explanation =
            intent.getStringExtra("explanation")
                ?: "No explanation available."

        // ---------------------------------------------------------
        // SAVE ANALYSIS TO DECISION HISTORY
        // ---------------------------------------------------------
        val historyPrefs = getSharedPreferences("GDMIE_HISTORY", MODE_PRIVATE)
        val historyCount = historyPrefs.getInt("count", 0)
        val index = historyCount + 1

        historyPrefs.edit()
            .putInt("count", index)
            .putString("time_$index", java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                java.util.Locale.getDefault()
            ).format(java.util.Date()))
            .putString("decision_$index", decision)
            .putString("edge_$index", "%.2f".format(edge))
            .putString("confidence_$index", "%.0f%%".format(confidence * 100))
            .putString("momentum_$index", "%.2f".format(momentum))
            .putString("risk_$index", "%.2f".format(risk))
            .putString("gap_$index", "%.2f".format(gap))
            .apply()

        val decisionColor = when (decision) {
            "POSITIVE EDGE" -> green
            "NEGATIVE EDGE" -> red
            else -> cyan
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(14))
        }

        header.addView(
            label("GDMIE", 28f, cyan, true)
        )

        header.addView(
            label("DECISION OUTPUT", 13f, muted, true).apply {
                letterSpacing = 0.12f
            }
        )

        header.addView(
            label(
                "GENERAL DECISION & MATHEMATICAL INTELLIGENCE ENGINE",
                9f,
                Color.rgb(100, 120, 140)
            )
        )

        root.addView(header)

        val scroll = ScrollView(this).apply {
            isFillViewport = true
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(4), dp(20), dp(28))
        }

        val decisionCard = card()

        decisionCard.addView(
            label("FINAL DECISION", 12f, muted, true)
        )

        decisionCard.addView(
            label(decision, 38f, decisionColor, true).apply {
                setPadding(0, dp(8), 0, dp(4))
            }
        )

        decisionCard.addView(
            label(
                "Reverse Edge Decision",
                12f,
                Color.rgb(110, 135, 155)
            )
        )

        content.addView(
            decisionCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val edgeCard = card()

        edgeCard.addView(
            label("EDGE & CONFIDENCE", 12f, cyan, true)
        )

        edgeCard.addView(
            label(
                "%.2f".format(edge),
                32f,
                white,
                true
            ).apply {
                setPadding(0, dp(8), 0, dp(2))
            }
        )

        edgeCard.addView(
            label(
                "EDGE SCORE",
                10f,
                muted,
                true
            )
        )

        val edgeBar = android.widget.ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            progress = ((50.0 + edge.coerceIn(-50.0, 50.0)) * 1.0).toInt()
        }

        edgeCard.addView(
            edgeBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(8)
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(4)
            }
        )

        edgeCard.addView(
            label(
                "NEGATIVE  ←  EDGE  →  POSITIVE",
                9f,
                Color.rgb(90, 110, 130)
            )
        )

        val confidenceBar = android.widget.ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            progress = (confidence * 100.0).coerceIn(0.0, 100.0).toInt()
        }

        edgeCard.addView(
            confidenceBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(8)
            ).apply {
                topMargin = dp(12)
                bottomMargin = dp(4)
            }
        )

        edgeCard.addView(
            label(
                "CONFIDENCE METER",
                9f,
                Color.rgb(90, 110, 130)
            )
        )

        edgeCard.addView(
            label(
                "Confidence  %.0f%%".format(confidence * 100),
                15f,
                decisionColor,
                true
            ).apply {
                setPadding(0, dp(12), 0, 0)
            }
        )

        content.addView(
            edgeCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(14)
            }
        )

        val valuesCard = card()

        valuesCard.addView(
            label("CORE VALUES", 12f, cyan, true)
        )

        valuesCard.addView(
            divider(),
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
            ).apply {
                topMargin = dp(8)
                bottomMargin = dp(4)
            }
        )

        metricRow(valuesCard, "Present Value", "%.2f".format(present))
        metricRow(valuesCard, "Expected Value", "%.2f".format(expected))
        metricRow(valuesCard, "Market Value", "%.2f".format(market))
        metricRow(valuesCard, "Market Gap", "%.2f".format(gap))
        metricRow(valuesCard, "Momentum Factor", "%.2f".format(momentum))
        metricRow(valuesCard, "Risk Factor", "%.2f".format(risk))

        content.addView(
            valuesCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(14)
            }
        )

        val whyCard = card()

        whyCard.addView(
            label("WHY THIS DECISION?", 12f, cyan, true)
        )

        whyCard.addView(
            label(
                explanation,
                14f,
                muted
            ).apply {
                setPadding(0, dp(12), 0, 0)
                setLineSpacing(dp(3).toFloat(), 1f)
            }
        )

        content.addView(
            whyCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(14)
            }
        )

        content.addView(
            label(
                "PRESENT  →  EXPECTED  →  MARKET  →  GAP  →  EDGE  →  DECISION",
                9f,
                Color.rgb(75, 100, 120)
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(22), 0, dp(4))
            }
        )

        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val backButton = android.widget.Button(this).apply {
            text = "BACK TO INPUT"
            setTextColor(white)
            textSize = 12f
            setOnClickListener {
                finish()
            }
        }

        val newButton = android.widget.Button(this).apply {
            text = "NEW ANALYSIS"
            setTextColor(cyan)
            textSize = 12f
            setOnClickListener {
                val newIntent = android.content.Intent(
                    this@DecisionOutputActivity,
                    DataInputActivity::class.java
                )
                startActivity(newIntent)
                finish()
            }
        }

        buttonLayout.addView(
            backButton,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            ).apply {
                marginEnd = dp(6)
            }
        )

        buttonLayout.addView(
            newButton,
            LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
            ).apply {
                marginStart = dp(6)
            }
        )

        content.addView(
            buttonLayout,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
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
