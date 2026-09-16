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

    // ============================================================
    // GDMIE HOME V2
    // Premium dark / blue decision-intelligence dashboard
    // ============================================================

    private val bg = Color.rgb(4, 8, 17)
    private val surface = Color.rgb(9, 16, 29)
    private val surface2 = Color.rgb(13, 23, 40)
    private val surface3 = Color.rgb(17, 30, 51)

    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val green = Color.rgb(45, 225, 135)
    private val yellow = Color.rgb(255, 220, 70)

    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val dim = Color.rgb(82, 108, 135)
    private val border = Color.rgb(25, 57, 87)

    private var edgeView: TextView? = null
    private var momentumView: TextView? = null
    private var riskView: TextView? = null
    private var confidenceView: TextView? = null

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    // ------------------------------------------------------------
    // TEXT
    // ------------------------------------------------------------

    private fun tv(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) {
                setTypeface(null, Typeface.BOLD)
            }
        }
    }

    // ------------------------------------------------------------
    // BACKGROUND
    // ------------------------------------------------------------

    private fun rounded(
        color: Int,
        radius: Int = 18,
        strokeColor: Int = border,
        strokeWidth: Int = 1
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (strokeWidth > 0) {
                setStroke(dp(strokeWidth), strokeColor)
            }
        }
    }

    private fun card(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = rounded(surface)
        }
    }

    private fun gap(height: Int): View {
        return Space(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        }
    }

    // ------------------------------------------------------------
    // SECTION TITLE
    // ------------------------------------------------------------

    private fun sectionTitle(parent: LinearLayout, title: String) {

        parent.addView(
            tv(
                title,
                11f,
                cyan,
                true
            ).apply {
                letterSpacing = 0.12f
            }
        )

        parent.addView(gap(9))
    }

    // ------------------------------------------------------------
    // CATEGORY CHIP
    // ------------------------------------------------------------

    private fun chip(
        parent: LinearLayout,
        title: String,
        selected: Boolean
    ) {

        val item = TextView(this).apply {

            text = title
            textSize = 11f
            gravity = Gravity.CENTER
            setPadding(
                dp(15),
                dp(8),
                dp(15),
                dp(8)
            )

            setTextColor(
                if (selected) Color.WHITE else muted
            )

            background = rounded(
                if (selected) blue else surface2,
                18,
                if (selected) blue else border
            )
        }

        parent.addView(
            item,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                dp(36)
            ).apply {
                marginEnd = dp(7)
            }
        )
    }

    // ------------------------------------------------------------
    // MARKET CARD
    // ------------------------------------------------------------

    private fun marketCard(
        parent: LinearLayout,
        name: String,
        value: String,
        change: String
    ) {

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(13), dp(14), dp(13))
            background = rounded(surface2)
        }

        box.addView(
            tv(name, 11f, muted, true)
        )

        box.addView(
            tv(value, 20f, white, true).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        box.addView(
            tv(change, 10f, green, true).apply {
                setPadding(0, dp(3), 0, 0)
            }
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                0,
                dp(94),
                1f
            )
        )
    }

    // ------------------------------------------------------------
    // FEATURE BUTTON
    // ------------------------------------------------------------

    private fun featureButton(
        parent: LinearLayout,
        title: String,
        subtitle: String,
        action: () -> Unit
    ) {

        val box = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(12),
                dp(13),
                dp(12),
                dp(12)
            )

            background = rounded(surface2)

            setOnClickListener {
                action()
            }
        }

        box.addView(
            tv(title, 13f, white, true)
        )

        box.addView(
            tv(subtitle, 9f, muted).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        parent.addView(
            box,
            LinearLayout.LayoutParams(
                0,
                dp(82),
                1f
            )
        )
    }

    // ------------------------------------------------------------
    // SMALL NAVIGATION
    // ------------------------------------------------------------

    private fun navItem(
        parent: LinearLayout,
        title: String,
        selected: Boolean,
        action: () -> Unit
    ) {

        val item = LinearLayout(this).apply {

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            setPadding(
                dp(5),
                dp(8),
                dp(5),
                dp(6)
            )

            setOnClickListener {
                action()
            }
        }

        val dot = TextView(this).apply {
            text = "●"
            textSize = 11f
            gravity = Gravity.CENTER
            setTextColor(
                if (selected) cyan else dim
            )
        }

        item.addView(dot)

        item.addView(
            tv(
                title,
                9f,
                if (selected) white else muted,
                selected
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(2), 0, 0)
            }
        )

        parent.addView(
            item,
            LinearLayout.LayoutParams(
                0,
                dp(54),
                1f
            )
        )
    }

    // ------------------------------------------------------------
    // MAIN
    // ------------------------------------------------------------

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
            isFillViewport = true
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(16),
                dp(18),
                dp(16),
                dp(85)
            )
        }

        // ========================================================
        // HEADER
        // ========================================================

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        brand.addView(
            tv(
                "GDMIE",
                29f,
                cyan,
                true
            )
        )

        brand.addView(
            tv(
                "DECISION INTELLIGENCE ENGINE",
                8f,
                dim,
                true
            ).apply {
                setPadding(0, dp(2), 0, 0)
                letterSpacing = 0.08f
            }
        )

        header.addView(
            brand,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val profile = TextView(this).apply {
            text = "●"
            textSize = 20f
            gravity = Gravity.CENTER
            setTextColor(white)
            background = rounded(
                surface2,
                20,
                border
            )
        }

        header.addView(
            profile,
            LinearLayout.LayoutParams(
                dp(42),
                dp(42)
            )
        )

        root.addView(header)

        root.addView(gap(24))

        // ========================================================
        // GREETING
        // ========================================================

        val hour = java.util.Calendar
            .getInstance()
            .get(java.util.Calendar.HOUR_OF_DAY)

        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }

        root.addView(
            tv(
                "$greeting",
                25f,
                white,
                true
            )
        )

        root.addView(
            tv(
                "Smarter decisions. Brighter future.",
                12f,
                muted
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        root.addView(gap(16))

        // ========================================================
        // CATEGORIES
        // ========================================================

        val categories = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
        }

        val chipRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        chip(chipRow, "Trading", true)
        chip(chipRow, "Investing", false)
        chip(chipRow, "Crypto", false)
        chip(chipRow, "Life", false)

        categories.addView(chipRow)

        root.addView(
            categories,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(40)
            )
        )

        root.addView(gap(15))

        // ========================================================
        // MARKET SNAPSHOT
        // ========================================================

        sectionTitle(root, "MARKET SNAPSHOT")

        val markets = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        marketCard(
            markets,
            "NIFTY",
            "24,198.30",
            "+0.74%"
        )

        markets.addView(
            Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(9),
                    1
                )
            }
        )

        marketCard(
            markets,
            "BANK NIFTY",
            "51,240.70",
            "+0.95%"
        )

        root.addView(markets)

        root.addView(gap(15))

        // ========================================================
        // SEARCH
        // ========================================================

        val search = TextView(this).apply {

            text = "⌕   Search Stock / Index / Asset"
            textSize = 12f
            setTextColor(muted)

            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(16),
                0,
                dp(16),
                0
            )

            background = rounded(
                surface2,
                15,
                border
            )

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
            search,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        root.addView(gap(18))

        // ========================================================
        // PRIMARY ACTION
        // ========================================================

        val run = Button(this).apply {

            text = "RUN GDM ENGINE   →"
            textSize = 14f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)

            background = rounded(
                blue,
                16,
                blue,
                0
            )

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
            run,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        root.addView(gap(22))

        // ========================================================
        // LATEST ANALYSIS
        // ========================================================

        sectionTitle(root, "LATEST ANALYSIS")

        val latest = card()

        latest.addView(
            tv(
                "REVERSE EDGE",
                10f,
                muted,
                true
            )
        )

        edgeView = tv(
            "—",
            34f,
            cyan,
            true
        ).apply {
            setPadding(0, dp(4), 0, 0)
        }

        latest.addView(edgeView)

        latest.addView(
            tv(
                "Latest GDMIE engine calculation",
                10f,
                dim
            ).apply {
                setPadding(0, dp(2), 0, 0)
            }
        )

        val metrics = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(15), 0, 0)
        }

        momentumView = tv(
            "Momentum\n—",
            10f,
            muted,
            true
        )

        riskView = tv(
            "Risk\n—",
            10f,
            muted,
            true
        )

        confidenceView = tv(
            "Confidence\n—",
            10f,
            muted,
            true
        )

        metrics.addView(
            momentumView,
            LinearLayout.LayoutParams(
                0,
                dp(45),
                1f
            )
        )

        metrics.addView(
            riskView,
            LinearLayout.LayoutParams(
                0,
                dp(45),
                1f
            )
        )

        metrics.addView(
            confidenceView,
            LinearLayout.LayoutParams(
                0,
                dp(45),
                1f
            )
        )

        latest.addView(metrics)

        latest.addView(
            tv(
                "PRESENT  →  EXPECTED  →  MARKET  →  GAP  →  MOVEMENT",
                8f,
                dim,
                true
            ).apply {
                setPadding(0, dp(8), 0, 0)
            }
        )

        root.addView(latest)

        root.addView(gap(22))

        // ========================================================
        // INTELLIGENCE MODULES
        // ========================================================

        sectionTitle(root, "INTELLIGENCE")

        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        featureButton(
            row1,
            "LIVE ANALYSIS",
            "Real-time engine",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DataInputActivity::class.java
                )
            )
        }

        row1.addView(
            Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(9),
                    1
                )
            }
        )

        featureButton(
            row1,
            "COMPARE",
            "Side-by-side",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    CompareActivity::class.java
                )
            )
        }

        root.addView(row1)

        root.addView(gap(9))

        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        featureButton(
            row2,
            "STRATEGY LAB",
            "Test & backtest",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    StrategyBacktestActivity::class.java
                )
            )
        }

        row2.addView(
            Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(9),
                    1
                )
            }
        )

        featureButton(
            row2,
            "ASK GDMIE",
            "Ask the engine",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    AskGDMIEActivity::class.java
                )
            )
        }

        root.addView(row2)

        root.addView(gap(9))

        val row3 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        featureButton(
            row3,
            "HISTORY",
            "Past decisions",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DecisionHistoryActivity::class.java
                )
            )
        }

        row3.addView(
            Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dp(9),
                    1
                )
            }
        )

        featureButton(
            row3,
            "WHY & DETAILS",
            "Understand results",
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    WhyDetailsActivity::class.java
                )
            )
        }

        root.addView(row3)

        root.addView(gap(22))

        // ========================================================
        // ENGINE FLOW
        // ========================================================

        val flow = card()

        flow.addView(
            tv(
                "GDMIE ENGINE FLOW",
                10f,
                cyan,
                true
            )
        )

        flow.addView(
            tv(
                "PRESENT  →  EXPECTED  →  TARGET\n" +
                        "CONTEXT  →  MOMENTUM  →  GAP\n" +
                        "MOVEMENT →  RISK  →  TIMING\n" +
                        "REVERSE EDGE  →  DECISION",
                10f,
                muted
            ).apply {
                setPadding(0, dp(10), 0, 0)
                setLineSpacing(
                    dp(3).toFloat(),
                    1f
                )
            }
        )

        root.addView(flow)

        root.addView(gap(22))

        // ========================================================
        // ECOSYSTEM
        // ========================================================

        sectionTitle(root, "GDMIE ECOSYSTEM")

        val ecosystem = card()

        ecosystem.addView(
            tv(
                "WATCHLIST   •   ALERTS   •   TOOLS",
                10f,
                white,
                true
            )
        )

        ecosystem.addView(
            tv(
                "MULTI-ASSET   •   LIFE DECISIONS   •   INSIGHTS",
                10f,
                muted
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        root.addView(ecosystem)

        root.addView(gap(20))

        // ========================================================
        // FOOTER
        // ========================================================

        root.addView(
            tv(
                "THINK SMARTER. DECIDE BETTER. LIVE AHEAD.",
                9f,
                dim,
                true
            ).apply {
                gravity = Gravity.CENTER
                letterSpacing = 0.08f
            }
        )

        scroll.addView(root)

        // ========================================================
        // BOTTOM NAV
        // ========================================================

        val bottom = LinearLayout(this).apply {

            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(7),
                0,
                dp(7),
                0
            )

            background = rounded(
                Color.rgb(7, 14, 26),
                0,
                border
            )
        }

        navItem(
            bottom,
            "Home",
            true
        ) {
            // Already on Home
        }

        navItem(
            bottom,
            "Markets",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DataInputActivity::class.java
                )
            )
        }

        navItem(
            bottom,
            "Ask GDMIE",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    AskGDMIEActivity::class.java
                )
            )
        }

        navItem(
            bottom,
            "Insights",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DecisionHistoryActivity::class.java
                )
              )

        }

        navItem(
            bottom,
            "Profile",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    WhyDetailsActivity::class.java
                )
            )
        }

        val frame = FrameLayout(this)

        frame.addView(
            scroll,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        frame.addView(
            bottom,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(58),
                Gravity.BOTTOM
            )
        )

        setContentView(frame)

        updateHomeOverview()
    }

    // ============================================================
    // HOME DATA
    // ============================================================

    private fun updateHomeOverview() {

        val prefs = getSharedPreferences(
            "GDMIE_HOME",
            MODE_PRIVATE
        )

        val edge =
            prefs.getString("edge", "—") ?: "—"

        val momentum =
            prefs.getString("momentum", "—") ?: "—"

        val risk =
            prefs.getString("risk", "—") ?: "—"

        val confidence =
            prefs.getString("confidence", "—") ?: "—"

        edgeView?.text = edge

        momentumView?.text =
            "Momentum\n$momentum"

        riskView?.text =
            "Risk\n$risk"

        confidenceView?.text =
            "Confidence\n$confidence"
    }

    override fun onResume() {
        super.onResume()
        updateHomeOverview()
    }
}
