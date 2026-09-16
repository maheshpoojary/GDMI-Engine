package com.gdmie

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {

    private val bg = Color.rgb(5, 9, 18)
    private val card = Color.rgb(11, 18, 31)
    private val card2 = Color.rgb(14, 23, 39)
    private val blue = Color.rgb(45, 155, 255)
    private val cyan = Color.rgb(0, 220, 255)
    private val green = Color.rgb(55, 225, 135)

    private var liveEdgeView: TextView? = null
    private var liveMetricsView: TextView? = null

    private var latestEdge = "—"
    private var latestMomentum = "—"
    private var latestRisk = "—"
    private var latestConfidence = "—"
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

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
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun panel(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(16))
            background = GradientDrawable().apply {
                setColor(card)
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(24, 52, 78))
            }
        }
    }

    private fun space(h: Int): View =
        Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(h))
        }

    private fun section(parent: LinearLayout, title: String) {
        parent.addView(
            text(title, 11f, cyan, true).apply {
                letterSpacing = 0.10f
            }
        )
        parent.addView(space(9))
    }

    private fun menuButton(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        action: () -> Unit
    ) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))

            background = GradientDrawable().apply {
                setColor(card2)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 82))
            }

            setOnClickListener { action() }
        }

        box.addView(text(title, 15f, white, true))

        box.addView(
            text(subtitle, 11f, muted).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun statCard(
        parent: LinearLayout,
        title: String,
        value: String,
        subtitle: String
    ) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = GradientDrawable().apply {
                setColor(card2)
                cornerRadius = dp(15).toFloat()
                setStroke(dp(1), Color.rgb(25, 55, 82))
            }
        }

        box.addView(text(title, 10f, muted, true))

        box.addView(
            text(value, 23f, cyan, true).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        box.addView(
            text(subtitle, 10f, muted).apply {
                setPadding(0, dp(3), 0, 0)
            }
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(30))
        }

        // ---------------------------------------------------------
        // HEADER
        // ---------------------------------------------------------

        root.addView(
            text("GDMIE", 32f, cyan, true)
        )

        root.addView(
            text(
                "GENERAL DECISION & MATHEMATICAL INTELLIGENCE ENGINE",
                9f,
                Color.rgb(95, 120, 145)
            ).apply {
                setPadding(0, dp(3), 0, 0)
            }
        )

        root.addView(space(24))

        // ---------------------------------------------------------
        // GREETING
        // ---------------------------------------------------------

        val hour = java.util.Calendar.getInstance()
            .get(java.util.Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }

        root.addView(
            text(greeting, 25f, white, true)
        )

        root.addView(
            text(
                "Decision intelligence at a glance.",
                13f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(space(18))

        // ---------------------------------------------------------
        // ENGINE STATUS
        // ---------------------------------------------------------

        val status = panel()

        status.addView(
            text("●  ENGINE READY", 12f, green, true)
        )

        status.addView(
            text(
                "Decision Intelligence System",
                18f,
                white,
                true
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        status.addView(
            text(
                "General-purpose analysis engine is ready.",
                11f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(
            status,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(16))

        // ---------------------------------------------------------
        // PRIMARY ACTION
        // ---------------------------------------------------------

        val runButton = Button(this).apply {
            text = "RUN GDM ENGINE"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            background = GradientDrawable().apply {
                setColor(blue)
                cornerRadius = dp(16).toFloat()
            }

            setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        DataInputActivity::class.java
                    )
                )
            }
        }

        root.addView(
            runButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        root.addView(space(22))

        // ---------------------------------------------------------
        // LIVE OVERVIEW
        // ---------------------------------------------------------

        section(root, "LIVE OVERVIEW")

        val overview = panel()

        overview.addView(
            text("LATEST ANALYSIS", 11f, cyan, true)
        )

        liveEdgeView = text("—", 38f, cyan, true).apply {
            setPadding(0, dp(5), 0, 0)
        }
        overview.addView(liveEdgeView)

        overview.addView(
            text("REVERSE EDGE", 11f, muted, true).apply {
                setPadding(0, dp(2), 0, 0)
            }
        )

        liveMetricsView = text(
            "Momentum: —    Risk: —    Confidence: —",
            11f,
            muted,
            true
        ).apply {
            setPadding(0, dp(10), 0, 0)
        }
        overview.addView(liveMetricsView)

        overview.addView(
            text(
                "Latest GDMIE calculation • updates automatically",
                10f,
                Color.rgb(95, 120, 145)
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        overview.addView(space(12))

        overview.addView(
            text(
                "ENGINE SIGNALS",
                10f,
                cyan,
                true
            )
        )

        overview.addView(
            text(
                "PRESENT → EXPECTED → MARKET → GAP → MOVEMENT",
                9f,
                Color.rgb(80, 110, 140),
                true
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        root.addView(
            overview,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(10))

        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        statCard(stats, "PRESENT", "—", "Current value")
        stats.addView(Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(8), 1)
        })
        statCard(stats, "EXPECTED", "—", "Expected value")
        stats.addView(Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(dp(8), 1)
        })
        statCard(stats, "RISK", "—", "Risk factor")

        root.addView(stats)

        root.addView(space(22))

        // ---------------------------------------------------------
        // INTELLIGENCE
        // ---------------------------------------------------------

        section(root, "INTELLIGENCE")

        menuButton(
            root,
            "MARKET ANALYSIS",
            "Analyze present, expected, market and edge values"
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DataInputActivity::class.java
                )
            )
        }

        root.addView(space(9))

        menuButton(
            root,
            "COMPARE",
            "Compare two decision profiles side-by-side"
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    CompareActivity::class.java
                )
            )
        }

        root.addView(space(9))

        menuButton(
            root,
            "DECISION HISTORY",
            "View previous GDMIE engine analyses"
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DecisionHistoryActivity::class.java
                )
            )
        }

        root.addView(space(9))

        menuButton(
            root,
            "ASK GDMIE",
            "Ask the intelligence engine for structured reasoning"
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    AskGDMIEActivity::class.java
                )
            )
        }

        root.addView(space(9))

        menuButton(
            root,
            "WHY & DETAILS",
            "Understand the factors behind every engine result"
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    WhyDetailsActivity::class.java
                )
            )
        }

        root.addView(space(22))

        // ---------------------------------------------------------
        // ENGINE FLOW
        // ---------------------------------------------------------

        val flow = panel()

        flow.addView(
            text("ENGINE FLOW", 11f, cyan, true)
        )

        flow.addView(
            text(
                "PRESENT  →  EXPECTED  →  TARGET\n" +
                "CONTEXT  →  MOMENTUM  →  GAP\n" +
                "MOVEMENT →  RISK  →  TIMING\n" +
                "REVERSE EDGE  →  DECISION",
                11f,
                muted
            ).apply {
                setPadding(0, dp(12), 0, 0)
                setLineSpacing(dp(3).toFloat(), 1f)
            }
        )

        root.addView(
            flow,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(22))

        // ---------------------------------------------------------
        // UPCOMING MODULES
        // ---------------------------------------------------------

        section(root, "GDMIE ECOSYSTEM")

        val ecosystem = panel()

        val strategyButton = TextView(this).apply {
            text = "STRATEGY & BACKTEST"
            textSize = 13f
            setTextColor(white)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 0)
            setOnClickListener {
                startActivity(
                    Intent(
                        this@MainActivity,
                        StrategyBacktestActivity::class.java
                    )
                )
            }
        }

        ecosystem.addView(strategyButton)

        ecosystem.addView(
            text(
                "WATCHLIST  •  ALERTS  •  TOOLS  •  MULTI-ASSET",
                10f,
                muted
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        ecosystem.addView(
            text(
                "LIFE DECISIONS  •  PROFILE & INSIGHTS",
                10f,
                muted
            ).apply {
                setPadding(0, dp(4), 0, 0)
            }
        )

        root.addView(
            ecosystem,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(space(20))

        root.addView(
            text(
                "GDMIE • GENERAL DECISION INTELLIGENCE",
                9f,
                Color.rgb(70, 95, 120)
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        scroll.addView(root)
        setContentView(scroll)
        updateHomeOverview()
    }


    private fun updateHomeOverview() {
        val prefs = getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)

        val edge = prefs.getString("edge", "—") ?: "—"
        val momentum = prefs.getString("momentum", "—") ?: "—"
        val risk = prefs.getString("risk", "—") ?: "—"
        val confidence = prefs.getString("confidence", "—") ?: "—"

        liveEdgeView?.text = edge
        liveMetricsView?.text =
            "Momentum: $momentum    Risk: $risk    Confidence: $confidence"
    }

    override fun onResume() {
        super.onResume()
        updateHomeOverview()
    }

}
