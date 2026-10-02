package com.gdmie

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.gdmie.game.GDMIEGameProgress

class OutcomeCheckActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(5, 9, 18)
        window.navigationBarColor = Color.rgb(5, 9, 18)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(30, 30, 30, 30)
            setBackgroundColor(Color.rgb(5, 9, 18))
        }

        val title = TextView(this).apply {
            text = "🧠  GDMIE ANALYSIS"
            textSize = 28f
            setTextColor(Color.rgb(0, 220, 255))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = "◉\n\nSCANNING DECISION..."
            textSize = 18f
            setTextColor(Color.rgb(0, 220, 255))
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 40, 0, 40)
        }

        val decision = intent.getStringExtra("challenge_decision")
            ?: "Decision locked"

        val info = TextView(this).apply {
            text = "\nYOUR DECISION\n\n$decision\n\n\nGDMIE analysis is ready."
            textSize = 17f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            alpha = 0f
        }

        root.addView(title)
        root.addView(status)
        root.addView(info)

        val sourceMode = intent.getStringExtra("source_mode").orEmpty()
        val fastSignal = intent.getStringExtra("fast_signal").orEmpty()
        val analysisIndex = intent.getIntExtra("analysis_index", 0)

        if (sourceMode == "FAST" && analysisIndex > 0) {
            val outcomeXp = when (fastSignal) {
                "POSITIVE SIGNAL" -> 10
                "CAUTION SIGNAL" -> 5
                else -> 8
            }

            val awardedXp = GDMIEGameProgress.addOutcomeXpOnce(
                this,
                analysisIndex,
                outcomeXp
            )

            val reward = TextView(this).apply {
                text = if (awardedXp > 0) {
                    "\n⚡ FAST DECISION\n\n+$awardedXp XP"
                } else {
                    "\n⚡ FAST DECISION\n\nXP ALREADY AWARDED"
                }
                textSize = 22f
                setTextColor(Color.WHITE)
                setTypeface(null, Typeface.BOLD)
                gravity = Gravity.CENTER
                setPadding(0, 24, 0, 0)
            }

            reward.alpha = 0f
            reward.tag = "fast_reward"
            root.addView(reward)
        }

        setContentView(root)

        val handler = Handler(Looper.getMainLooper())

        val suspenseSteps = arrayOf(
            "◉\n\nSCANNING DECISION...",
            "◉\n\nREADING CURRENT SITUATION...",
            "◉\n\nCHECKING RISK & TIMING...",
            "◉\n\nBUILDING DECISION INSIGHT...",
            "✦\n\nANALYSIS READY"
        )

        suspenseSteps.forEachIndexed { index, message ->
            handler.postDelayed({
                status.animate()
                    .alpha(0f)
                    .setDuration(160)
                    .withEndAction {
                        status.text = message
                        status.animate()
                            .alpha(1f)
                            .setDuration(220)
                            .start()
                    }
                    .start()
            }, index * 520L)
        }

        handler.postDelayed({
            info.animate()
                .alpha(1f)
                .setDuration(450)
                .start()

            root.findViewWithTag<TextView>("fast_reward")?.animate()
                ?.alpha(1f)
                ?.setDuration(450)
                ?.start()
        }, 2550L)
    }
}
