package com.gdmie

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

import com.gdmie.game.GDMIEGameProgress

class DailyLearningActivity : Activity() {

    private val bg = Color.rgb(3, 6, 14)
    private val surface = Color.rgb(10, 18, 35)
    private val surface2 = Color.rgb(16, 27, 52)
    private val cyan = Color.rgb(35, 205, 255)
    private val gold = Color.rgb(255, 205, 70)
    private val green = Color.rgb(45, 225, 135)
    private val red = Color.rgb(255, 80, 100)
    private val purple = Color.rgb(155, 90, 255)
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
            includeFontPadding = false
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private class DecisionPatternRadarView(
        context: android.content.Context,
        private val values: FloatArray,
        private val gridColor: Int,
        private val dataColor: Int,
        private val textColor: Int
    ) : android.view.View(context) {

        private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }

        private val dataPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 11f * resources.displayMetrics.scaledDensity
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            val cx = w / 2f
            val cy = h / 2f
            val radius = minOf(w, h) * 0.30f

            gridPaint.color = gridColor
            dataPaint.color = dataColor
            labelPaint.color = textColor

            val labels = arrayOf(
                "ACCURACY",
                "CONSISTENCY",
                "RISK",
                "TIMING"
            )

            fun point(index: Int, r: Float): android.graphics.PointF {
                val angle = -Math.PI / 2.0 +
                    index * 2.0 * Math.PI / 4.0

                return android.graphics.PointF(
                    cx + (Math.cos(angle) * r).toFloat(),
                    cy + (Math.sin(angle) * r).toFloat()
                )
            }

            // Radar grid
            for (level in 1..4) {
                val r = radius * level / 4f
                val path = Path()

                for (i in 0 until 4) {
                    val pt = point(i, r)
                    if (i == 0) path.moveTo(pt.x, pt.y)
                    else path.lineTo(pt.x, pt.y)
                }

                path.close()
                canvas.drawPath(path, gridPaint)
            }

            // Axes
            for (i in 0 until 4) {
                val pt = point(i, radius)
                canvas.drawLine(cx, cy, pt.x, pt.y, gridPaint)
            }

        // User data
        // Only real numeric values are plotted.
        // Unavailable dimensions are not treated as zero.
        for (i in 0 until 4) {
            val rawValue = values.getOrElse(i) { Float.NaN }

            if (!rawValue.isNaN()) {
                val value = rawValue.coerceIn(0f, 100f)
                val pt = point(i, radius * value / 100f)
                canvas.drawCircle(pt.x, pt.y, 5f, dataPaint)
            }
        }

        // Labels
        for (i in 0 until 4) {
            val pt = point(i, radius + 24f)

            val rawValue = values.getOrElse(i) { Float.NaN }
            val label = if (rawValue.isNaN()) {
                labels[i] + " • N/A"
            } else {
                labels[i]
            }

            canvas.drawText(
                label,
                pt.x,
                pt.y + 4f,
                labelPaint
            )
        }
        }
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
            tv("GDMIE", 11f, cyan, true).apply {
                letterSpacing = 0.12f
            }
        )

        root.addView(
            tv("DAILY LEARNING", 29f, white, true).apply {
                setPadding(0, dp(4), 0, 0)
            }
        )

        root.addView(
            tv(
                "Review your decisions.\nLearn from the outcome. Grow with every challenge.",
                12f,
                muted
            ).apply {
                setPadding(0, dp(8), 0, dp(22))
                setLineSpacing(2f, 1f)
            }
        )

        val streak = GDMIEGameProgress.getStreak(this)
        val xp = GDMIEGameProgress.getXp(this)
        val level = GDMIEGameProgress.getLevel(this)

        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(14), dp(15), dp(14))
            background = rounded(Color.rgb(9, 22, 42), 20, cyan)
        }

        val summaryInfo = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        summaryInfo.addView(
            tv("🧠 LEARNING PROGRESS", 10f, cyan, true)
        )

        summaryInfo.addView(
            tv(
                "🔥 $streak day streak   •   ⭐ Level $level   •   $xp XP",
                11f,
                white,
                true
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        summary.addView(
            summaryInfo,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(
            summary,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(22)
            }
        )

        val history = GDMIEGameProgress.getDailyLearningHistory(this)

        var workedCount = 0
        var mixedCount = 0
        var didntWorkCount = 0
        var totalEarnedXp = 0

        history.forEach { entry ->
            val parts = entry.split("||")
            val result = parts.getOrNull(3) ?: ""
            val entryXp = parts.getOrNull(4)?.toIntOrNull() ?: 0

            when {
                result.contains("DIDN'T", true) ||
                    result.contains("NOT SUCCESSFUL", true) -> didntWorkCount++

                result.contains("MIXED", true) ||
                    result.contains("NEUTRAL", true) -> mixedCount++

                result.contains("WORKED", true) ||
                    result.contains("SUCCESSFUL", true) -> workedCount++
            }

            totalEarnedXp += entryXp
        }

        root.addView(
            tv("LEARNING STATS", 10f, cyan, true).apply {
                letterSpacing = 0.10f
                setPadding(0, 0, 0, dp(12))
            }
        )

        val statsCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(14), dp(15), dp(14))
            background = rounded(surface2, 22, purple)
        }

        statsCard.addView(
            tv(
                "📊 YOUR DECISION PATTERN",
                10f,
                purple,
                true
            )
        )

        val statsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(14), 0, 0)
        }

        fun statBlock(
            value: String,
            label: String,
            color: Int
        ): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                addView(tv(value, 18f, color, true).apply {
                    gravity = Gravity.CENTER
                })
                addView(tv(label, 8f, muted).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(4), 0, 0)
                })
            }
        }

        statsRow.addView(
            statBlock(history.size.toString(), "TOTAL", white),
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        statsRow.addView(
            statBlock(workedCount.toString(), "WORKED", green),
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        statsRow.addView(
            statBlock(mixedCount.toString(), "MIXED", gold),
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        statsRow.addView(
            statBlock(didntWorkCount.toString(), "DIDN'T", red),
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )

        statsCard.addView(statsRow)

        // Decision Pattern Radar — grounded only in stored Daily Learning outcomes.
        val totalDecisions = history.size.toFloat()

        val accuracy = if (totalDecisions > 0f) {
            (workedCount.toFloat() / totalDecisions * 100f)
                .coerceIn(0f, 100f)
        } else Float.NaN

        val consistency = if (totalDecisions > 0f) {
            (maxOf(workedCount, mixedCount, didntWorkCount)
                .toFloat() / totalDecisions * 100f)
                .coerceIn(0f, 100f)
        } else Float.NaN

        // Risk and Timing are not scored until real data is stored.
        val riskAwareness = Float.NaN
        val timing = Float.NaN

        statsCard.addView(
            tv("DECISION PATTERN", 9f, muted, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, dp(4))
            }
        )

        statsCard.addView(
            tv(
                if (totalDecisions > 0f)
                    "Built from your recorded Daily Challenge outcomes"
                else
                    "Complete a challenge to activate your pattern radar",
                8f,
                muted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, dp(4))
            }
        )

        statsCard.addView(
            DecisionPatternRadarView(
                this,
                floatArrayOf(
                    accuracy,
                    consistency,
                    riskAwareness,
                    timing
                ),
                border,
                cyan,
                white
            ),
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(245)
            )
        )

        if (totalDecisions == 0f) {
            statsCard.addView(
                tv(
                    "NO DATA YET\nComplete your first Daily Challenge to start building your learning pattern.",
                    9f,
                    muted
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(2), 0, dp(8))
                }
            )
        }

        statsCard.addView(
            tv(
                "Accuracy  •  Consistency  •  Risk  •  Timing",
                8f,
                cyan,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(2), 0, dp(4))
            }
        )

        statsCard.addView(
            tv(
                "🔥 $streak streak   •   ⭐ $totalEarnedXp Challenge XP",
                9f,
                cyan,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(10), 0, 0)
            }
        )

        root.addView(
            statsCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(22)
            }
        )

        root.addView(
            tv("RECENT CHALLENGES", 10f, cyan, true).apply {
                letterSpacing = 0.10f
                setPadding(0, 0, 0, dp(12))
            }
        )

        if (history.isEmpty()) {
            val empty = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(22), dp(35), dp(22), dp(35))
                background = rounded(surface2, 22, border)
            }

            empty.addView(
                tv("🧠", 34f, cyan).apply {
                    gravity = Gravity.CENTER
                }
            )

            empty.addView(
                tv(
                    "NO LEARNING RECORDS YET",
                    13f,
                    white,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(12), 0, 0)
                }
            )

            empty.addView(
                tv(
                    "Complete your first Daily Challenge\nto start building your learning history.",
                    10f,
                    muted
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(8), 0, 0)
                }
            )

            root.addView(empty)
        } else {
            history.forEachIndexed { index, entry ->
                addHistoryCard(root, entry, index)
            }
        }

        root.addView(
            tv(
                "GDMIE supports your thinking.\nYou make the final decision.",
                11f,
                Color.rgb(90, 120, 150)
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(28), 0, 0)
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun addHistoryCard(
        root: LinearLayout,
        entry: String,
        index: Int
    ) {
        val parts = entry.split("||")

        val date = parts.getOrNull(0) ?: "Unknown date"
        val decision = parts.getOrNull(1) ?: "Unknown decision"
        val priority = parts.getOrNull(2) ?: "Unknown priority"
        val result = parts.getOrNull(3) ?: "Unknown result"
        val xp = parts.getOrNull(4) ?: "0"
        val streak = parts.getOrNull(5) ?: "0"

        val resultColor = when {
            result.contains("DIDN'T", true) ||
                result.contains("NOT SUCCESSFUL", true) -> red
            result.contains("MIXED", true) ||
                result.contains("NEUTRAL", true) -> gold
            result.contains("WORKED", true) ||
                result.contains("SUCCESSFUL", true) -> green
            else -> cyan
        }

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(15), dp(15), dp(15), dp(15))
            background = rounded(surface2, 22, resultColor)
            elevation = dp(4).toFloat()
        }

        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        top.addView(
            tv(
                if (index == 0) "LATEST" else "CHALLENGE ${index + 1}",
                9f,
                cyan,
                true
            )
        )

        top.addView(
            tv(date, 9f, muted).apply {
                gravity = Gravity.END
            },
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        card.addView(top)

        card.addView(
            tv(decision, 16f, white, true).apply {
                setPadding(0, dp(10), 0, 0)
            }
        )

        card.addView(
            tv("Priority: $priority", 10f, muted).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        card.addView(
            tv("Result: $result", 11f, resultColor, true).apply {
                setPadding(0, dp(9), 0, 0)
            }
        )

        card.addView(
            tv("+$xp XP    🔥 Streak $streak", 9f, resultColor, true).apply {
                setPadding(0, dp(9), 0, 0)
            }
        )

        root.addView(
            card,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        )
    }
}
