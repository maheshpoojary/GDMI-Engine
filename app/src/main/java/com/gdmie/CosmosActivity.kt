package com.gdmie
import com.gdmie.audio.GDMIEAudioManager
import com.gdmie.game.GDMIEGameProgress

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Space

import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.app.AlertDialog
import android.widget.Toast
import com.gdmie.adapter.ContextState
import com.gdmie.adapter.CurrentState
import com.gdmie.adapter.GDMIESimpleDecision
import com.gdmie.adapter.GDMIESimpleDecisionAdapter
import com.gdmie.adapter.GoalLevel
import com.gdmie.adapter.MomentumState
import com.gdmie.adapter.RiskLevel
import com.gdmie.adapter.TimingState
import com.gdmie.network.GDMIEEngineGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class CosmosActivity : Activity() {

    private val bg = Color.rgb(2, 4, 16)
    private val cyan = Color.rgb(70, 230, 255)
    private val purple = Color.rgb(175, 95, 255)
    private val gold = Color.rgb(255, 210, 90)
    private val white = Color.WHITE
    private val muted = Color.rgb(175, 185, 205)

    private lateinit var radar: CosmosRadarView
    private lateinit var title: TextView
    private lateinit var subtitle: TextView
    private lateinit var signal: TextView
    private lateinit var hint: TextView

    // AIR overlay state for system Back.
    private var airRoot: FrameLayout? = null

    // COSMOS layer overlay state for clean SPACE/EARTH/TIME navigation.
    private var layerOverlay: FrameLayout? = null

    private var selectedLayer = 1

    private val layerNames = arrayOf(
        "☀️  ENERGY",
        "🪐  SPACE",
        "🌍  EARTH",
        "⏳  TIME"
    )

    private val layerDescriptions = arrayOf(
        "CHANGE • FORCE • CREATION",
        "INFINITE • UNKNOWN • UNIVERSE",
        "LIFE • NATURE • DECISIONS",
        "PAST • NOW • FUTURE"
    )

    private fun layerColor(): Int {
        return when (selectedLayer) {
            0 -> cyan
            1 -> purple
            2 -> Color.rgb(90, 220, 140)
            else -> gold
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🌌 COSMOS — deep ambient audio

        window.statusBarColor = bg
        window.navigationBarColor = bg

        val root = FrameLayout(this).apply {
            setBackgroundColor(bg)
        }

        radar = CosmosRadarView().apply {
            onLayerChanged = {
                selectedLayer = it
                updateLayerUI()
            }

            onLayerSelected = {
                enterSelectedLayer()
            }
        }

        root.addView(
            radar,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        title = TextView(this).apply {
            text = "🌌  THE COSMOS"
            textSize = 28f
            setTextColor(cyan)
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        root.addView(
            title,
            FrameLayout.LayoutParams(
                -1,
                dp(65)
            ).apply {
                topMargin = dp(22)
                leftMargin = dp(18)
                rightMargin = dp(18)
            }
        )

        subtitle = TextView(this).apply {
            text = "GDMIE DECISION UNIVERSE"
            textSize = 11f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }

        root.addView(
            subtitle,
            FrameLayout.LayoutParams(
                -1,
                dp(38)
            ).apply {
                topMargin = dp(80)
            }
        )

        signal = TextView(this).apply {
            text = layerNames[selectedLayer]
            textSize = 22f
            setTextColor(layerColor())
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        root.addView(
            signal,
            FrameLayout.LayoutParams(
                -1,
                dp(48)
            ).apply {
                topMargin = dp(470)
            }
        )

        hint = TextView(this).apply {
            text = "← SWIPE TO EXPLORE →\nTAP TO ENTER"
            textSize = 12f
            setTextColor(white)
            gravity = Gravity.CENTER
            alpha = 0.82f
        }

        root.addView(
            hint,
            FrameLayout.LayoutParams(
                -1,
                dp(55)
            ).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = dp(24)
            }
        )

        setContentView(root)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun updateLayerUI() {
        signal.text = layerNames[selectedLayer]
        signal.setTextColor(layerColor())
        subtitle.text = layerDescriptions[selectedLayer]
        radar.activeColor = layerColor()
        radar.invalidate()
    }

    private fun enterSelectedLayer() {
        when (selectedLayer) {
            0 -> showEnergyField()
            1 -> showSpaceLayer()
            2 -> showEarthLayer()
            3 -> showTimeLayer()
        }
    }

    private fun showHumanWorldField() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(8, 4, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(34), dp(24), dp(24))
        }

        val icon = TextView(this).apply {
            text = "🧑‍🤝‍🧑"
            textSize = 52f
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }

        val title = TextView(this).apply {
            text = "HUMAN WORLD"
            textSize = 27f
            setTextColor(Color.rgb(200, 150, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "PEOPLE • RELATIONSHIPS • DECISIONS"
            textSize = 13f
            setTextColor(Color.rgb(190, 165, 220))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(22))
        }

        val description = TextView(this).apply {
            text =
                "Human systems are shaped by people, relationships, needs and choices.\n\n" +
                "Observe the situation, connect the people and resources involved, trace the change and choose a balanced response."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setLineSpacing(0f, 1.15f)
            setPadding(dp(8), dp(10), dp(8), dp(24))
        }

        val continueButton = Button(this).apply {
            text = "CONTINUE  →  HUMAN WORLD INTERPRETATION"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)

            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(65, 35, 95))
                    setStroke(dp(1), Color.rgb(200, 150, 255))
                }

            setOnClickListener {
                showHumanWorldInterpretation()
            }
        }

        content.addView(
            icon,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            description,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            continueButton,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            ).apply {
                topMargin = dp(8)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .setNegativeButton("← BACK", null)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(190, 130, 255))
    }

    private fun createEarthHologramView(type: String): View {
    return object : View(this@CosmosActivity) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var animationTime = 0f

        private val animationDriver =
            android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 100000L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.RESTART

                addUpdateListener {
                    animationTime =
                        (it.animatedFraction * 100f)

                    invalidate()
                }
            }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            animationDriver.start()
        }

        override fun onDetachedFromWindow() {
            animationDriver.cancel()
            super.onDetachedFromWindow()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val t = animationTime

            val cx = width / 2f
            val cy = height / 2f
            val base = minOf(width, height) * 0.24f

            paint.strokeWidth =
                2.4f * resources.displayMetrics.density

            paint.style = Paint.Style.STROKE

            when (type) {

                "ENERGY" -> {

                    for (i in 0 until 3) {
                        canvas.save()

                        canvas.rotate(
                            t * (42f + i * 18f) *
                                if (i % 2 == 0) 1f else -1f,
                            cx,
                            cy
                        )

                        paint.color =
                            Color.argb(
                                220 - i * 35,
                                255,
                                190,
                                55
                            )

                        canvas.drawOval(
                            cx - base * (1.55f + i * 0.22f),
                            cy - base * (0.38f + i * 0.10f),
                            cx + base * (1.55f + i * 0.22f),
                            cy + base * (0.38f + i * 0.10f),
                            paint
                        )

                        canvas.restore()
                    }

                    val pulse =
                        1f +
                            0.20f *
                            kotlin.math.sin(t * 8f)

                    paint.style = Paint.Style.FILL
                    paint.color =
                        Color.rgb(255, 220, 80)

                    canvas.drawCircle(
                        cx,
                        cy,
                        base * 0.42f * pulse,
                        paint
                    )

                    for (i in 0 until 16) {
                        val angle =
                            t * 3f +
                                i *
                                (Math.PI * 2.0 / 16.0).toFloat()

                        val radius =
                            base *
                                (
                                    0.80f +
                                        0.20f *
                                        kotlin.math.sin(
                                            t * 6f + i
                                        )
                                )

                        canvas.drawCircle(
                            cx +
                                kotlin.math.cos(angle) *
                                radius,
                            cy +
                                kotlin.math.sin(angle) *
                                radius,
                            base * 0.045f,
                            paint
                        )
                    }
                }

                "WATER" -> {

                    paint.color =
                        Color.rgb(90, 190, 255)

                    for (layer in 0 until 5) {

                        val path = Path()

                        val left =
                            cx - base * 2.1f

                        val right =
                            cx + base * 2.1f

                        val yBase =
                            cy +
                                (layer - 2) *
                                base * 0.22f

                        path.moveTo(left, yBase)

                        for (i in 0..36) {

                            val x =
                                left +
                                    i *
                                    (right - left) /
                                    36f

                            val y =
                                yBase +
                                    kotlin.math.sin(
                                        t * 5f +
                                            i * 0.42f +
                                            layer
                                    ) *
                                    base *
                                    0.15f

                            path.lineTo(x, y)
                        }

                        paint.strokeWidth =
                            (2f + layer * 0.5f) *
                            resources.displayMetrics.density

                        canvas.drawPath(path, paint)
                    }

                    paint.style = Paint.Style.FILL

                    for (i in 0 until 18) {

                        val phase =
                            (t * 0.7f +
                                i * 0.07f) % 1f

                        val x =
                            cx -
                                base * 2f +
                                phase *
                                base * 4f

                        val y =
                            cy +
                                kotlin.math.sin(
                                    t * 4f + i
                                ) *
                                base * 0.45f

                        canvas.drawCircle(
                            x,
                            y,
                            base * 0.035f,
                            paint
                        )
                    }
                }

                "LIFE" -> {

                    paint.style = Paint.Style.STROKE

                    for (i in 0 until 6) {

                        val phase =
                            (t * 0.9f +
                                i * 0.16f) % 1f

                        val radius =
                            base *
                                (0.25f +
                                    phase * 1.45f)

                        paint.color =
                            Color.argb(
                                (
                                    220f *
                                        (1f - phase)
                                ).toInt().coerceIn(25, 220),
                                80,
                                240,
                                150
                            )

                        canvas.drawCircle(
                            cx,
                            cy,
                            radius,
                            paint
                        )
                    }

                    paint.style = Paint.Style.FILL

                    val pulse =
                        1f +
                            0.20f *
                            kotlin.math.sin(t * 8f)

                    paint.color =
                        Color.rgb(70, 225, 135)

                    canvas.drawCircle(
                        cx,
                        cy,
                        base * 0.32f * pulse,
                        paint
                    )

                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth =
                        3f *
                        resources.displayMetrics.density

                    val stem = Path()

                    stem.moveTo(
                        cx,
                        cy + base * 0.35f
                    )

                    stem.cubicTo(
                        cx - base * 0.30f,
                        cy,
                        cx + base * 0.35f,
                        cy - base * 0.65f,
                        cx,
                        cy - base * 1.20f
                    )

                    canvas.drawPath(stem, paint)
                }

                "HUMAN WORLD" -> {

                    val points = arrayOf(
                        floatArrayOf(
                            cx,
                            cy - base * 1.15f
                        ),
                        floatArrayOf(
                            cx - base * 1.35f,
                            cy + base * 0.75f
                        ),
                        floatArrayOf(
                            cx + base * 1.35f,
                            cy + base * 0.75f
                        ),
                        floatArrayOf(
                            cx,
                            cy + base * 0.15f
                        )
                    )

                    paint.color =
                        Color.rgb(205, 145, 255)

                    paint.strokeWidth =
                        2.5f *
                        resources.displayMetrics.density

                    val links = arrayOf(
                        intArrayOf(0, 1),
                        intArrayOf(0, 2),
                        intArrayOf(1, 2),
                        intArrayOf(0, 3),
                        intArrayOf(1, 3),
                        intArrayOf(2, 3)
                    )

                    for (link in links) {
                        canvas.drawLine(
                            points[link[0]][0],
                            points[link[0]][1],
                            points[link[1]][0],
                            points[link[1]][1],
                            paint
                        )
                    }

                    // Moving signal across the network
                    val edge =
                        (
                            kotlin.math.floor(t * 0.9f)
                                .toInt()
                        ) % links.size

                    val progress =
                        (t * 0.9f) % 1f

                    val a = links[edge][0]
                    val b = links[edge][1]

                    val sx =
                        points[a][0] +
                            (
                                points[b][0] -
                                    points[a][0]
                            ) *
                            progress

                    val sy =
                        points[a][1] +
                            (
                                points[b][1] -
                                    points[a][1]
                            ) *
                            progress

                    paint.style = Paint.Style.FILL
                    paint.color =
                        Color.rgb(240, 190, 255)

                    canvas.drawCircle(
                        sx,
                        sy,
                        base * 0.10f,
                        paint
                    )

                    for (i in points.indices) {

                        val pulse =
                            1f +
                                0.22f *
                                kotlin.math.sin(
                                    t * 7f + i
                                )

                        canvas.drawCircle(
                            points[i][0],
                            points[i][1],
                            base * 0.18f * pulse,
                            paint
                        )
                    }
                }

                "TIME MACHINE" -> {

                    canvas.save()

                    canvas.rotate(
                        t * 65f,
                        cx,
                        cy
                    )

                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth =
                        3f *
                        resources.displayMetrics.density

                    paint.color =
                        Color.rgb(190, 125, 255)

                    canvas.drawOval(
                        cx - base * 1.75f,
                        cy - base * 0.52f,
                        cx + base * 1.75f,
                        cy + base * 0.52f,
                        paint
                    )

                    canvas.restore()

                    canvas.save()

                    canvas.rotate(
                        -t * 85f,
                        cx,
                        cy
                    )

                    paint.color =
                        Color.rgb(90, 190, 255)

                    canvas.drawOval(
                        cx - base * 0.52f,
                        cy - base * 1.75f,
                        cx + base * 0.52f,
                        cy + base * 1.75f,
                        paint
                    )

                    canvas.restore()

                    for (i in 0 until 4) {

                        val phase =
                            (t * 0.8f +
                                i * 0.25f) % 1f

                        paint.color =
                            Color.argb(
                                (
                                    200f *
                                        (1f - phase)
                                ).toInt().coerceIn(25, 200),
                                175,
                                120,
                                255
                            )

                        canvas.drawCircle(
                            cx,
                            cy,
                            base *
                                (0.30f +
                                    phase * 1.20f),
                            paint
                        )
                    }

                    paint.style = Paint.Style.FILL

                    val pulse =
                        1f +
                            0.20f *
                            kotlin.math.sin(t * 8f)

                    paint.color =
                        Color.rgb(185, 125, 255)

                    canvas.drawCircle(
                        cx,
                        cy,
                        base * 0.30f * pulse,
                        paint
                    )
                }
            }
        }
    }
}

    private fun showHumanWorldInterpretation() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(8, 4, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(18))
        }

        val phaseTitle = TextView(this).apply {
            text = "OBSERVE"
            textSize = 12f
            setTextColor(Color.rgb(200, 150, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val counter = TextView(this).apply {
            text = "HUMAN WORLD 4D INTERPRETATION • 01 / 04"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(18))
        }

        val question = TextView(this).apply {
            text = "What should you observe first when a human situation begins to change?"
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setLineSpacing(0f, 1.12f)
        }

        val hint = TextView(this).apply {
            text = "Choose one signal that gives the clearest starting context."
            textSize = 13f
            setTextColor(Color.rgb(185, 165, 205))
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(10), dp(8), dp(18))
        }

        val optionsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val nextButton = Button(this).apply {
            text = "NEXT  →"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(65, 35, 95))
                    setStroke(dp(1), Color.rgb(200, 150, 255))
                }
        }

        val observeOptions = listOf(
            "A  PEOPLE SIGNAL\nObserve who is affected and how",
            "B  NEED SIGNAL\nIdentify the strongest need or goal",
            "C  RELATIONSHIP SIGNAL\nObserve how people influence one another",
            "D  CHANGE SIGNAL\nIdentify what changed most strongly"
        )

        val connectOptions = listOf(
            "A  PEOPLE + RESOURCES\nConnect people with the resources they need",
            "B  PEOPLE + RELATIONSHIPS\nTrace how relationships shape the situation",
            "C  PEOPLE + ENVIRONMENT\nConnect human choices with surroundings",
            "D  PEOPLE + SYSTEMS\nConnect individual choices with wider systems"
        )

        val traceOptions = listOf(
            "A  TRACE THE SOURCE\nFind where the situation began",
            "B  TRACE THE DECISION\nFollow the choice that changed the direction",
            "C  TRACE THE EFFECT\nFollow who or what is influenced next",
            "D  TRACE THE PATTERN\nFollow how the situation develops over time"
        )

        val respondOptions = listOf(
            "A  PROTECT\nReduce the strongest immediate human risk",
            "B  COMMUNICATE\nImprove understanding between people",
            "C  ADAPT\nAdjust to the changing situation",
            "D  BALANCE\nConsider people, needs and wider impact together"
        )

        val optionSets = listOf(
            observeOptions,
            connectOptions,
            traceOptions,
            respondOptions
        )

        val humanQuestionBanks = listOf(
            listOf(
                "What should you observe first when a human situation begins to change?",
                "Which signal gives the clearest starting view of the human situation?",
                "What should be noticed before interpreting the wider human context?",
                "Which human signal deserves attention first?",
                "What is the clearest first indicator of change between people?",
                "Which present condition should be observed before connecting other factors?",
                "What should you identify first in the changing human situation?",
                "Which signal provides the strongest starting context?",
                "What should the first observation focus on?",
                "Which change is most important to notice at the beginning?"
            ),
            listOf(
                "What should you connect to understand the wider human context?",
                "Which relationship helps explain the observed human situation?",
                "What wider system should be connected to the first signal?",
                "Which connection could reveal why the situation is changing?",
                "What should be connected beyond the immediate human signal?",
                "Which relationship expands the context of the situation?",
                "What interaction between people and systems should be considered?",
                "Which surrounding factor should be connected to understand the change?",
                "What relationship may explain the direction of the situation?",
                "Which wider connection gives the clearest human context?"
            ),
            listOf(
                "What should you trace to understand how the situation develops?",
                "Which path best explains how the human situation changed?",
                "How should the origin and direction of the situation be followed?",
                "Which sequence reveals what influenced the current condition?",
                "What should you follow from the first signal to its wider effect?",
                "Which part of the situation should be traced through time?",
                "How can the key change in the human situation be followed?",
                "Which pathway reveals the strongest cause-and-effect relationship?",
                "What should be traced to understand the developing pattern?",
                "Which direction of change deserves the closest tracing?"
            ),
            listOf(
                "What response should come first when the situation needs action?",
                "Which response best fits the current human context?",
                "What action could improve the situation while considering wider impact?",
                "Which response reduces the strongest immediate risk?",
                "What should happen after understanding the human situation?",
                "Which action supports a healthier direction for the people involved?",
                "What response balances immediate needs with longer-term effects?",
                "Which response best matches the connected human context?",
                "What action should follow after tracing the situation?",
                "Which response supports people while respecting the wider system?"
            )
        )

        val humanSessionQuestions =
            humanQuestionBanks.map { it.shuffled().first() }

        val displayOptionSets = optionSets.map { it.shuffled() }


        var phase = 0
        var selected = -1
        val humanAnswers = mutableListOf<String>()

        fun styleButton(button: Button, selectedState: Boolean) {
            button.background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(
                        if (selectedState)
                            Color.rgb(80, 45, 115)
                        else
                            Color.rgb(18, 10, 30)
                    )
                    setStroke(
                        dp(if (selectedState) 2 else 1),
                        if (selectedState)
                            Color.rgb(220, 175, 255)
                        else
                            Color.rgb(95, 65, 125)
                    )
                }

            button.setTextColor(Color.WHITE)
        }

        fun renderPhase() {
            selected = -1
            optionsContainer.removeAllViews()

            phaseTitle.text = when (phase) {
                0 -> "OBSERVE"
                1 -> "CONNECT"
                2 -> "TRACE"
                else -> "RESPOND"
            }

            counter.text =
                "HUMAN WORLD 4D INTERPRETATION • ${String.format("%02d", phase + 1)} / 04"

            question.text = humanSessionQuestions[phase]

            hint.text = when (phase) {
                0 -> "Start with the clearest human signal."
                1 -> "Find the relationship that changes the context."
                2 -> "Follow the strongest path of change."
                else -> "Choose a response that fits the context."
            }

            displayOptionSets[phase].forEachIndexed { index, option ->
                val button = Button(this).apply {
                    text = option
                    textSize = 14f
                    isAllCaps = false
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(16), dp(8), dp(16), dp(8))
                    minHeight = dp(68)
                    styleButton(this, false)

                    setOnClickListener {
                        selected = index

                        for (i in 0 until optionsContainer.childCount) {
                            val child = optionsContainer.getChildAt(i)
                            if (child is Button) {
                                styleButton(child, i == selected)
                                child.animate()
                                    .scaleX(if (i == selected) 1.015f else 1f)
                                    .scaleY(if (i == selected) 1.015f else 1f)
                                    .setDuration(140)
                                    .start()
                            }
                        }
                    }
                }

                optionsContainer.addView(
                    button,
                    LinearLayout.LayoutParams(
                        -1,
                        0,
                        1f
                    ).apply {
                        bottomMargin = dp(6)
                    }
                )
            }

            nextButton.text =
                if (phase == 3) "COMPLETE  →" else "NEXT  →"
        }

        nextButton.setOnClickListener {
            if (selected < 0) return@setOnClickListener

            GDMIEAudioManager.playUiClick(this@CosmosActivity)

            humanAnswers.add(displayOptionSets[phase][selected])

            if (phase < 3) {
                phase++
                renderPhase()
            } else {
                showHumanWorldGDMIEAnalysis(
                    humanAnswers = humanAnswers,
                    observeIndex = optionSets[0].indexOf(humanAnswers[0]),
                    connectIndex = optionSets[1].indexOf(humanAnswers[1]),
                    traceIndex = optionSets[2].indexOf(humanAnswers[2]),
                    respondIndex = optionSets[3].indexOf(humanAnswers[3])
                )
            }
        }

        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(-1, dp(24))
        )

        content.addView(
            counter,
            LinearLayout.LayoutParams(-1, dp(42))
        )

        val hologramView = createEarthHologramView("HUMAN WORLD")

        content.addView(
            hologramView,
            LinearLayout.LayoutParams(
                -1,
                dp(210)
            ).apply {
                setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(12)
                )
            }
        )


        content.addView(
            question,
            LinearLayout.LayoutParams(-1, dp(72))
        )

        content.addView(
            hint,
            LinearLayout.LayoutParams(-1, dp(44))
        )

        content.addView(
            optionsContainer,
            LinearLayout.LayoutParams(
                -1,
                0,
                1f
            )
        )

        content.addView(
            nextButton,
            LinearLayout.LayoutParams(-1, dp(52))
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        renderPhase()

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .create()

        dialog.show()
    }

    private fun showHumanWorldGDMIEAnalysis(
        humanAnswers: List<String>,
        observeIndex: Int,
        connectIndex: Int,
        traceIndex: Int,
        respondIndex: Int
    ) {
        val currentSituation = when (observeIndex) {
            0 -> CurrentState.STABLE
            1 -> CurrentState.UNCERTAIN
            2 -> CurrentState.STABLE
            3 -> CurrentState.UNCERTAIN
            else -> CurrentState.UNCERTAIN
        }

        val contextState = when (connectIndex) {
            0 -> ContextState.NORMAL
            1 -> ContextState.FAVOURABLE
            2 -> ContextState.NORMAL
            3 -> ContextState.UNCERTAIN
            else -> ContextState.UNCERTAIN
        }

        val goalLevel = when (traceIndex) {
            0 -> GoalLevel.SMALL_IMPROVEMENT
            1 -> GoalLevel.MODERATE_IMPROVEMENT
            2 -> GoalLevel.MAJOR_IMPROVEMENT
            3 -> GoalLevel.LONG_TERM_TRANSFORMATION
            else -> GoalLevel.MODERATE_IMPROVEMENT
        }

        val momentumState = when (traceIndex) {
            0 -> MomentumState.DECLINING
            1 -> MomentumState.IMPROVING
            2 -> MomentumState.IMPROVING
            3 -> MomentumState.STABLE
            else -> MomentumState.STABLE
        }

        val riskLevel = when (respondIndex) {
            0 -> RiskLevel.HIGH
            1 -> RiskLevel.MEDIUM
            2 -> RiskLevel.MEDIUM
            3 -> RiskLevel.LOW
            else -> RiskLevel.MEDIUM
        }

        val timingState = when (respondIndex) {
            0 -> TimingState.NOW
            1 -> TimingState.SOON
            2 -> TimingState.SOON
            3 -> TimingState.SOON
            else -> TimingState.SOON
        }

        val decision = GDMIESimpleDecision(
            decisionText = """
                HUMAN WORLD OBSERVE:
                ${humanAnswers.getOrElse(0) { "" }}

                HUMAN WORLD CONNECT:
                ${humanAnswers.getOrElse(1) { "" }}

                HUMAN WORLD TRACE:
                ${humanAnswers.getOrElse(2) { "" }}

                HUMAN WORLD RESPOND:
                ${humanAnswers.getOrElse(3) { "" }}
            """.trimIndent(),
            currentSituation = currentSituation,
            goalOutcome = goalLevel,
            context = contextState,
            momentum = momentumState,
            risk = riskLevel,
            timing = timingState
        )

        val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = GDMIEEngineGateway.calculate(input)

                val confidencePercent =
                    (result.confidence * 100).toInt().coerceIn(0, 100)

                val confidenceColor =
                    when {
                        confidencePercent >= 80 ->
                            Color.rgb(0, 255, 170)
                        confidencePercent >= 60 ->
                            Color.rgb(0, 235, 255)
                        confidencePercent >= 40 ->
                            Color.rgb(255, 210, 60)
                        else ->
                            Color.rgb(255, 90, 110)
                    }

                val analysisLayout = LinearLayout(this@CosmosActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(18), dp(8), dp(18), dp(8))
                }

                val analysisText = TextView(this@CosmosActivity).apply {
                    text =
                        "🧑‍🤝‍🧑 HUMAN WORLD CONTEXT\n\n" +
                        "✓ OBSERVE\n${humanAnswers.getOrElse(0) { "" }}\n\n" +
                        "✓ CONNECT\n${humanAnswers.getOrElse(1) { "" }}\n\n" +
                        "✓ TRACE\n${humanAnswers.getOrElse(2) { "" }}\n\n" +
                        "✓ RESPOND\n${humanAnswers.getOrElse(3) { "" }}\n\n" +
                        "🧠 GDMIE SIGNAL\n\n" +
                        result.decision

                    textSize = 15f
                    setTextColor(Color.rgb(25, 30, 45))
                    setLineSpacing(0f, 1.12f)
                }

                val confidenceText = TextView(this@CosmosActivity).apply {
                    text = "CONFIDENCE: 0%"
                    textSize = 24f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setTextColor(confidenceColor)
                    setPadding(0, dp(18), 0, dp(8))
                    alpha = 0.35f
                }

                analysisLayout.addView(
                    analysisText,
                    LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )

                analysisLayout.addView(
                    confidenceText,
                    LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )

                val analysisDialog =
                    AlertDialog.Builder(this@CosmosActivity)
                        .setTitle("🧠 GDMIE ANALYSIS")
                        .setView(analysisLayout)
                        .setPositiveButton("DONE") { dialog, _ ->
                            dialog.dismiss()

                            val humanRewardNode = SpaceNode(
                                "🧑‍🤝‍🧑",
                                "HUMAN WORLD",
                                0.80f,
                                0.68f,
                                48f,
                                Color.rgb(200, 150, 255)
                            )

                            showSpaceReward(humanRewardNode)
                        }
                        .create()

                analysisDialog.setOnShowListener {
                    val startTime = System.currentTimeMillis()
                    val duration = 1200L

                    confidenceText.post(object : Runnable {
                        override fun run() {
                            val elapsed =
                                System.currentTimeMillis() - startTime

                            val progress =
                                (elapsed.toFloat() / duration)
                                    .coerceIn(0f, 1f)

                            val value =
                                (confidencePercent * progress).toInt()

                            confidenceText.text =
                                "CONFIDENCE: $value%"

                            confidenceText.alpha =
                                0.35f + (0.65f * progress)

                            confidenceText.scaleX =
                                0.96f + (0.04f * progress)

                            confidenceText.scaleY =
                                0.96f + (0.04f * progress)

                            if (progress < 1f) {
                                confidenceText.postDelayed(this, 16L)
                            }
                        }
                    })
                }

                analysisDialog.show()

            } catch (e: Exception) {
                AlertDialog.Builder(this@CosmosActivity)
                    .setTitle("🧠 GDMIE ANALYSIS")
                    .setMessage(
                        "Analysis could not be completed.\n\n" +
                        (e.message ?: "Unknown error")
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun showTimeGDMIEAnalysis(
        selectedTime: String,
        timeAnswers: List<String>,
        observeIndex: Int,
        connectIndex: Int,
        traceIndex: Int,
        respondIndex: Int
    ) {
        val currentSituation = when (observeIndex) {
            0 -> CurrentState.STABLE
            1 -> CurrentState.UNCERTAIN
            2 -> CurrentState.STABLE
            3 -> CurrentState.UNCERTAIN
            else -> CurrentState.UNCERTAIN
        }

        val contextState = when (connectIndex) {
            0 -> ContextState.NORMAL
            1 -> ContextState.NORMAL
            2 -> ContextState.FAVOURABLE
            3 -> ContextState.UNCERTAIN
            else -> ContextState.UNCERTAIN
        }

        val goalLevel = when (traceIndex) {
            0 -> GoalLevel.SMALL_IMPROVEMENT
            1 -> GoalLevel.MODERATE_IMPROVEMENT
            2 -> GoalLevel.MAJOR_IMPROVEMENT
            3 -> GoalLevel.LONG_TERM_TRANSFORMATION
            else -> GoalLevel.MODERATE_IMPROVEMENT
        }

        val momentumState = when (traceIndex) {
            0 -> MomentumState.DECLINING
            1 -> MomentumState.IMPROVING
            2 -> MomentumState.IMPROVING
            3 -> MomentumState.STABLE
            else -> MomentumState.STABLE
        }

        val riskLevel = when (respondIndex) {
            0 -> RiskLevel.LOW
            1 -> RiskLevel.MEDIUM
            2 -> RiskLevel.MEDIUM
            3 -> RiskLevel.LOW
            else -> RiskLevel.MEDIUM
        }

        val timingState = when (respondIndex) {
            0 -> TimingState.LATER
            1 -> TimingState.NOW
            2 -> TimingState.SOON
            3 -> TimingState.SOON
            else -> TimingState.SOON
        }

        val decision = GDMIESimpleDecision(
            decisionText = """
                TIME PERSPECTIVE: $selectedTime

                TIME OBSERVE:
                ${timeAnswers.getOrElse(0) { "" }}

                TIME CONNECT:
                ${timeAnswers.getOrElse(1) { "" }}

                TIME TRACE:
                ${timeAnswers.getOrElse(2) { "" }}

                TIME RESPOND:
                ${timeAnswers.getOrElse(3) { "" }}
            """.trimIndent(),
            currentSituation = currentSituation,
            goalOutcome = goalLevel,
            context = contextState,
            momentum = momentumState,
            risk = riskLevel,
            timing = timingState
        )

        val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = GDMIEEngineGateway.calculate(input)

                val confidencePercent =
                    (result.confidence * 100).toInt().coerceIn(0, 100)

                val confidenceColor =
                    when {
                        confidencePercent >= 80 ->
                            Color.rgb(0, 255, 170)
                        confidencePercent >= 60 ->
                            Color.rgb(0, 235, 255)
                        confidencePercent >= 40 ->
                            Color.rgb(255, 210, 60)
                        else ->
                            Color.rgb(255, 90, 110)
                    }

                val analysisLayout = LinearLayout(this@CosmosActivity).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(18), dp(8), dp(18), dp(8))
                }

                val analysisText = TextView(this@CosmosActivity).apply {
                    text =
                        "⏳ TIME MACHINE • $selectedTime\n\n" +
                        "✓ OBSERVE\n${timeAnswers.getOrElse(0) { "" }}\n\n" +
                        "✓ CONNECT\n${timeAnswers.getOrElse(1) { "" }}\n\n" +
                        "✓ TRACE\n${timeAnswers.getOrElse(2) { "" }}\n\n" +
                        "✓ RESPOND\n${timeAnswers.getOrElse(3) { "" }}\n\n" +
                        "🧠 GDMIE SIGNAL\n\n" +
                        result.decision

                    textSize = 15f
                    setTextColor(Color.rgb(25, 30, 45))
                    setLineSpacing(0f, 1.12f)
                }

                val confidenceText = TextView(this@CosmosActivity).apply {
                    text = "CONFIDENCE: 0%"
                    textSize = 24f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setTextColor(confidenceColor)
                    setPadding(0, dp(18), 0, dp(8))
                    alpha = 0.35f
                }

                analysisLayout.addView(
                    analysisText,
                    LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )

                analysisLayout.addView(
                    confidenceText,
                    LinearLayout.LayoutParams(
                        -1,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                )

                val analysisDialog =
                    AlertDialog.Builder(this@CosmosActivity)
                        .setTitle("🧠 GDMIE ANALYSIS")
                        .setView(analysisLayout)
                        .setNeutralButton("🎁 WATCH +10 XP") { _, _ ->
                            RewardedAdManager.show(this@CosmosActivity) { awarded ->
                                Toast.makeText(
                                    this@CosmosActivity,
                                    "+$awarded XP earned!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setPositiveButton("DONE") { dialog, _ ->
                            dialog.dismiss()

                            val timeRewardNode = SpaceNode(
                                "⏳",
                                "TIME",
                                0.50f,
                                0.84f,
                                50f,
                                Color.WHITE
                            )

                            showSpaceReward(timeRewardNode, selectedTime)
                        }
                        .create()

                analysisDialog.setOnShowListener {
                    val startTime = System.currentTimeMillis()
                    val duration = 1200L

                    confidenceText.post(object : Runnable {
                        override fun run() {
                            val elapsed =
                                System.currentTimeMillis() - startTime

                            val progress =
                                (elapsed.toFloat() / duration)
                                    .coerceIn(0f, 1f)

                            val value =
                                (confidencePercent * progress).toInt()

                            confidenceText.text =
                                "CONFIDENCE: $value%"

                            confidenceText.alpha =
                                0.35f + (0.65f * progress)

                            confidenceText.scaleX =
                                0.96f + (0.04f * progress)

                            confidenceText.scaleY =
                                0.96f + (0.04f * progress)

                            if (progress < 1f) {
                                confidenceText.postDelayed(this, 16L)
                            }
                        }
                    })
                }

                analysisDialog.show()

            } catch (e: Exception) {
                AlertDialog.Builder(this@CosmosActivity)
                    .setTitle("🧠 GDMIE ANALYSIS")
                    .setMessage(
                        "Analysis could not be completed.\n\n" +
                        (e.message ?: "Unknown error")
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    private fun showEnergyLegacy() {

        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(4))
        }

        val scenario = TextView(this).apply {
            text =
                "Your city can invest in one major energy improvement."
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, dp(4), 0, dp(14))
        }

        container.addView(
            scenario,
            android.widget.LinearLayout.LayoutParams(-1, -2)
        )

        val choiceGroup = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }

        val choices = arrayOf(
            "⚡ Solar",
            "🌊 Hydro",
            "🌬️ Wind"
        )

        choices.forEachIndexed { index, text ->
            val radio = android.widget.RadioButton(this).apply {
                this.text = text
                textSize = 15f
                setTextColor(Color.WHITE)
                buttonTintList =
                    android.content.res.ColorStateList.valueOf(cyan)
                tag = index
                setPadding(0, dp(5), 0, dp(5))
            }

            choiceGroup.addView(radio)
        }

        container.addView(choiceGroup)

        fun addSection(
            label: String,
            options: Array<String>,
            defaultIndex: Int = -1
        ): android.widget.RadioGroup {

            val title = TextView(this).apply {
                text = label
                textSize = 12f
                setTextColor(cyan)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, dp(14), 0, dp(4))
            }

            container.addView(title)

            val group = android.widget.RadioGroup(this).apply {
                orientation = android.widget.RadioGroup.HORIZONTAL
            }

            options.forEachIndexed { index, option ->
                val radio =
                    android.widget.RadioButton(this).apply {
                        text = option
                        textSize = 12f
                        setTextColor(Color.WHITE)
                        tag = index
                        buttonTintList =
                            android.content.res.ColorStateList.valueOf(cyan)
                        setPadding(0, 0, dp(8), 0)
                    }

                group.addView(radio)

                // No automatic selection.
                // The user must explicitly choose an option.
            }

            container.addView(group)

            return group
        }

        val impactGroup =
            addSection(
                "IMPACT",
                arrayOf("Low", "Medium", "High"),
                2
            )

        val practicalityGroup =
            addSection(
                "PRACTICALITY",
                arrayOf("Low", "Medium", "High"),
                1
            )

        val riskGroup =
            addSection(
                "RISK",
                arrayOf("Low", "Medium", "High"),
                0
            )

        val momentumGroup =
            addSection(
                "MOMENTUM",
                arrayOf("Declining", "Stable", "Improving"),
                1
            )

        val timingGroup =
            addSection(
                "TIMING",
                arrayOf("Now", "Soon", "Later"),
                0
            )

        android.app.AlertDialog.Builder(this)
            .setTitle("☀️ ENERGY FIELD")
            .setView(container)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("⚡ ANALYZE MY DECISION") { _, _ ->

                if (
                    choiceGroup.checkedRadioButtonId == -1 ||
                    impactGroup.checkedRadioButtonId == -1 ||
                    practicalityGroup.checkedRadioButtonId == -1 ||
                    riskGroup.checkedRadioButtonId == -1 ||
                    momentumGroup.checkedRadioButtonId == -1 ||
                    timingGroup.checkedRadioButtonId == -1
                ) {
                    android.widget.Toast.makeText(
                        this,
                        "SELECT ALL OPTIONS BEFORE ANALYSIS",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val choice =
                    choiceGroup
                        .findViewById<android.widget.RadioButton>(
                            choiceGroup.checkedRadioButtonId
                        )
                        .tag as Int

                val impact =
                    impactGroup
                        .findViewById<android.widget.RadioButton>(
                            impactGroup.checkedRadioButtonId
                        ).tag as Int

                val practicality =
                    practicalityGroup
                        .findViewById<android.widget.RadioButton>(
                            practicalityGroup.checkedRadioButtonId
                        ).tag as Int

                val risk =
                    riskGroup
                        .findViewById<android.widget.RadioButton>(
                            riskGroup.checkedRadioButtonId
                        ).tag as Int

                val momentum =
                    momentumGroup
                        .findViewById<android.widget.RadioButton>(
                            momentumGroup.checkedRadioButtonId
                        ).tag as Int

                val timing =
                    timingGroup
                        .findViewById<android.widget.RadioButton>(
                            timingGroup.checkedRadioButtonId
                        ).tag as Int

                val decisionText =
                    when (choice) {
                        0 -> "Choose Solar energy for the city"
                        1 -> "Choose Hydro energy for the city"
                        else -> "Choose Wind energy for the city"
                    }

                val currentSituation =
                    when (practicality) {
                        0 -> CurrentState.WEAK
                        1 -> CurrentState.STABLE
                        else -> CurrentState.STRONG
                    }

                val goalOutcome =
                    when (impact) {
                        0 -> GoalLevel.SMALL_IMPROVEMENT
                        1 -> GoalLevel.MODERATE_IMPROVEMENT
                        else -> GoalLevel.MAJOR_IMPROVEMENT
                    }

                val context =
                    when (practicality) {
                        0 -> ContextState.UNFAVOURABLE
                        1 -> ContextState.NORMAL
                        else -> ContextState.FAVOURABLE
                    }

                val momentumState =
                    when (momentum) {
                        0 -> MomentumState.DECLINING
                        1 -> MomentumState.STABLE
                        else -> MomentumState.IMPROVING
                    }

                val riskLevel =
                    when (risk) {
                        0 -> RiskLevel.LOW
                        1 -> RiskLevel.MEDIUM
                        else -> RiskLevel.HIGH
                    }

                val timingState =
                    when (timing) {
                        0 -> TimingState.NOW
                        1 -> TimingState.SOON
                        else -> TimingState.LATER
                    }

                val decision =
                    GDMIESimpleDecision(
                        decisionText = decisionText,
                        currentSituation = currentSituation,
                        goalOutcome = goalOutcome,
                        context = context,
                        momentum = momentumState,
                        risk = riskLevel,
                        timing = timingState
                    )

                val input =
                    GDMIESimpleDecisionAdapter.toGDMInput(decision)

                CoroutineScope(Dispatchers.IO).launch {
                    val result =
                        GDMIEEngineGateway.calculate(input)

                    runOnUiThread {
                        android.app.AlertDialog.Builder(this@CosmosActivity)
                            .setTitle("🎯 GDMIE SIGNAL")
                            .setMessage(
                                "Decision: $decisionText\n\n" +
                                    "SIGNAL: ${result.decision}\n\n" +
                                    "Confidence: %.0f%%\n\n%s".format(
                                        result.confidence * 100,
                                        result.explanation
                                    )
                            )
                            .setPositiveButton("DONE", null)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun showSpaceLayer() {

        // 🪐 SPACE — switch ambient audio

        val spaceView = SpaceWorldView()

        val overlay = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(1, 2, 14))

            addView(
                spaceView,
                FrameLayout.LayoutParams(
                    -1,
                    -1
                )
            )
        }

        val back = TextView(this).apply {
            text = "← BACK"
            textSize = 16f
            setTextColor(Color.WHITE)
            setPadding(dp(18), dp(10), dp(18), dp(10))
            setBackgroundColor(Color.TRANSPARENT)

            setOnClickListener {
                (overlay.parent as? android.view.ViewGroup)
                    ?.removeView(overlay)

                if (layerOverlay === overlay) {
                    layerOverlay = null
                }
            }
        }

        overlay.addView(
            back,
            FrameLayout.LayoutParams(
                -2,
                -2
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                topMargin = dp(18)
            }
        )

        layerOverlay = overlay

        addContentView(
            overlay,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )
    }

    private fun showEarthLayer() {

        // 🌍 EARTH — switch ambient audio

        data class EarthNode(
            val icon: String,
            val name: String,
            val x: Float,
            val y: Float,
            val radius: Float,
            val color: Int
        )

        val nodes = arrayOf(
            EarthNode("🌍", "EARTH SYSTEM", 0.50f, 0.18f, 52f, cyan),
            EarthNode("🌬️", "AIR",          0.20f, 0.43f, 46f, cyan),
            EarthNode("💧", "WATER",        0.80f, 0.43f, 46f, Color.rgb(80, 150, 255)),
            EarthNode("🌱", "LIFE",         0.50f, 0.68f, 48f, Color.rgb(80, 220, 150)),
            EarthNode("🧑‍🤝‍🧑", "HUMAN WORLD", 0.50f, 0.43f, 48f, Color.rgb(180, 120, 255))
        )

        val earthView = object : View(this@CosmosActivity) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)

                // LIVE HOLOGRAM MOTION CLOCK
                val time = System.currentTimeMillis() / 1000f
                postInvalidateOnAnimation()

                canvas.drawColor(Color.rgb(2, 10, 16))

                paint.style = Paint.Style.FILL
                paint.textAlign = Paint.Align.CENTER

                paint.textSize = dp(40).toFloat()
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.color = Color.WHITE
                canvas.drawText(
                    "🌍  EARTH",
                    width / 2f,
                    dp(62).toFloat(),
                    paint
                )

                paint.textSize = dp(18).toFloat()
                paint.typeface = Typeface.DEFAULT
                paint.color = Color.rgb(150, 220, 235)
                canvas.drawText(
                    "EARTH DECISION UNIVERSE",
                    width / 2f,
                    dp(92).toFloat(),
                    paint
                )

                paint.textSize = dp(14).toFloat()
                paint.color = Color.rgb(120, 145, 165)
                canvas.drawText(
                    "TOUCH THE EARTH",
                    width / 2f,
                    height - dp(28).toFloat(),
                    paint
                )

                nodes.forEach { node ->
                    val cx = width * node.x
                    val cy = height * node.y
                    val r = dp(node.radius.toInt()).toFloat()

                    // Base holographic glow
                    paint.style = Paint.Style.FILL
                    paint.color = Color.argb(
                        32,
                        Color.red(node.color),
                        Color.green(node.color),
                        Color.blue(node.color)
                    )
                    canvas.drawCircle(cx, cy, r * 1.75f, paint)

                    paint.color = Color.argb(
                        55,
                        Color.red(node.color),
                        Color.green(node.color),
                        Color.blue(node.color)
                    )
                    canvas.drawCircle(cx, cy, r * 1.35f, paint)

                    // Dark hologram core
                    paint.color = Color.rgb(2, 10, 16)
                    canvas.drawCircle(cx, cy, r * 0.82f, paint)

                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 1.5f * resources.displayMetrics.density

                    when (node.name) {
                        "ENERGY" -> {
                            // Living plasma energy
                            val pulse = 1f + 0.10f * kotlin.math.sin(time * 4f)

                            paint.style = Paint.Style.FILL
                            paint.color = Color.argb(210, 255, 205, 70)
                            canvas.drawCircle(cx, cy, r * 0.24f * pulse, paint)

                            paint.color = Color.argb(70, 255, 190, 40)
                            canvas.drawCircle(cx, cy, r * 0.58f * pulse, paint)

                            // Rotating plasma rings
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 1.8f * resources.displayMetrics.density

                            canvas.save()
                            canvas.rotate(time * 38f, cx, cy)

                            paint.color = Color.argb(220, 255, 190, 55)
                            canvas.drawOval(
                                cx - r * 1.10f,
                                cy - r * 0.36f,
                                cx + r * 1.10f,
                                cy + r * 0.36f,
                                paint
                            )

                            canvas.restore()

                            canvas.save()
                            canvas.rotate(-time * 52f, cx, cy)

                            paint.color = Color.argb(150, 255, 225, 100)
                            canvas.drawOval(
                                cx - r * 0.38f,
                                cy - r * 1.05f,
                                cx + r * 0.38f,
                                cy + r * 1.05f,
                                paint
                            )

                            canvas.restore()

                            // Energy sparks
                            paint.style = Paint.Style.FILL
                            paint.color = Color.rgb(255, 230, 120)

                            for (i in 0 until 8) {
                                val a = time * 1.8f + i * 0.785f
                                val orbit = r * (0.72f + 0.10f * kotlin.math.sin(time * 3f + i))
                                canvas.drawCircle(
                                    cx + kotlin.math.cos(a) * orbit,
                                    cy + kotlin.math.sin(a) * orbit,
                                    r * 0.035f,
                                    paint
                                )
                            }
                        }
                        "WATER" -> {
                            // Flowing liquid hologram
                            val pulse = 1f + 0.08f * kotlin.math.sin(time * 3f)

                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 2f * resources.displayMetrics.density

                            for (i in 0 until 3) {
                                val phase = time * 2.2f + i * 0.9f
                                val scale = 0.58f + 0.20f * ((kotlin.math.sin(phase) + 1f) * 0.5f)

                                paint.color = Color.argb(
                                    (210 - i * 45).coerceAtLeast(70),
                                    80,
                                    180,
                                    255
                                )

                                canvas.drawOval(
                                    cx - r * scale * 1.25f,
                                    cy - r * scale * 0.42f,
                                    cx + r * scale * 1.25f,
                                    cy + r * scale * 0.42f,
                                    paint
                                )
                            }

                            // Moving wave
                            paint.color = Color.argb(230, 120, 220, 255)
                            paint.strokeWidth = 2.2f * resources.displayMetrics.density

                            val waveShift = kotlin.math.sin(time * 2.8f) * r * 0.16f

                            val path = android.graphics.Path()
                            path.moveTo(cx - r * 0.70f, cy + waveShift)

                            for (i in 0..12) {
                                val px = cx - r * 0.70f + i * r * 0.116f
                                val py = cy +
                                    kotlin.math.sin(time * 3.2f + i * 0.8f) * r * 0.10f
                                path.lineTo(px, py)
                            }

                            canvas.drawPath(path, paint)

                            // Liquid core
                            paint.style = Paint.Style.FILL
                            paint.color = Color.argb(90, 80, 190, 255)
                            canvas.drawCircle(cx, cy, r * 0.42f * pulse, paint)
                        }
                        "LIFE" -> {
                            // Biological growth pulse
                            val pulse = 1f + 0.12f * kotlin.math.sin(time * 2.5f)

                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 2f * resources.displayMetrics.density

                            for (i in 0 until 3) {
                                val grow = 0.42f + i * 0.24f +
                                    0.06f * kotlin.math.sin(time * 2f + i)

                                paint.color = Color.argb(
                                    210 - i * 45,
                                    80,
                                    235,
                                    150
                                )

                                canvas.drawCircle(
                                    cx,
                                    cy,
                                    r * grow * pulse,
                                    paint
                                )
                            }

                            // Living core
                            paint.style = Paint.Style.FILL
                            paint.color = Color.argb(180, 80, 235, 150)
                            canvas.drawCircle(cx, cy, r * 0.22f * pulse, paint)

                            // Growing stem
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 2.4f * resources.displayMetrics.density
                            paint.color = Color.argb(230, 110, 255, 165)

                            val growth = 0.75f + 0.10f * kotlin.math.sin(time * 1.8f)

                            canvas.drawLine(
                                cx,
                                cy + r * 0.38f,
                                cx,
                                cy - r * growth,
                                paint
                            )

                            // Animated leaves
                            val sway = kotlin.math.sin(time * 2f) * r * 0.08f

                            paint.style = Paint.Style.FILL
                            canvas.drawOval(
                                cx - r * 0.58f + sway,
                                cy - r * 0.32f,
                                cx - r * 0.02f + sway,
                                cy - r * 0.05f,
                                paint
                            )

                            canvas.drawOval(
                                cx + r * 0.02f - sway,
                                cy - r * 0.55f,
                                cx + r * 0.58f - sway,
                                cy - r * 0.27f,
                                paint
                            )
                        }
                        "HUMAN WORLD" -> {
                            // Living human connection network
                            val pulse = 1f + 0.08f * kotlin.math.sin(time * 2f)

                            val p1x = cx
                            val p1y = cy - r * 0.48f

                            val p2x = cx - r * 0.52f
                            val p2y = cy + r * 0.30f

                            val p3x = cx + r * 0.52f
                            val p3y = cy + r * 0.30f

                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 2f * resources.displayMetrics.density
                            paint.color = Color.argb(210, 195, 135, 255)

                            canvas.drawLine(p1x, p1y, p2x, p2y, paint)
                            canvas.drawLine(p1x, p1y, p3x, p3y, paint)
                            canvas.drawLine(p2x, p2y, p3x, p3y, paint)

                            // Expanding communication field
                            paint.color = Color.argb(100, 190, 120, 255)
                            canvas.drawCircle(
                                cx,
                                cy,
                                r * 0.90f * pulse,
                                paint
                            )

                            // People nodes pulse independently
                            paint.style = Paint.Style.FILL

                            val nodePulse1 =
                                1f + 0.14f * kotlin.math.sin(time * 3f)
                            val nodePulse2 =
                                1f + 0.14f * kotlin.math.sin(time * 3f + 2f)
                            val nodePulse3 =
                                1f + 0.14f * kotlin.math.sin(time * 3f + 4f)

                            paint.color = Color.rgb(210, 155, 255)

                            canvas.drawCircle(
                                p1x, p1y,
                                r * 0.14f * nodePulse1,
                                paint
                            )

                            canvas.drawCircle(
                                p2x, p2y,
                                r * 0.13f * nodePulse2,
                                paint
                            )

                            canvas.drawCircle(
                                p3x, p3y,
                                r * 0.13f * nodePulse3,
                                paint
                            )

                            // Moving connection signal
                            val progress =
                                (kotlin.math.sin(time * 2f) + 1f) * 0.5f

                            val signalX =
                                p2x + (p3x - p2x) * progress

                            val signalY =
                                p2y + (p3y - p2y) * progress

                            paint.color = Color.WHITE
                            canvas.drawCircle(
                                signalX,
                                signalY,
                                r * 0.045f,
                                paint
                            )
                        }
                        "TIME MACHINE" -> {
                            // Temporal portal / time vortex
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = 2f * resources.displayMetrics.density

                            canvas.save()
                            canvas.rotate(-time * 30f, cx, cy)

                            paint.color = Color.argb(220, 185, 115, 255)
                            canvas.drawOval(
                                cx - r * 1.12f,
                                cy - r * 0.38f,
                                cx + r * 1.12f,
                                cy + r * 0.38f,
                                paint
                            )

                            canvas.restore()

                            canvas.save()
                            canvas.rotate(time * 44f, cx, cy)

                            paint.color = Color.argb(180, 100, 190, 255)
                            canvas.drawOval(
                                cx - r * 0.38f,
                                cy - r * 1.12f,
                                cx + r * 0.38f,
                                cy + r * 1.12f,
                                paint
                            )

                            canvas.restore()

                            canvas.save()
                            canvas.rotate(time * 18f, cx, cy)

                            paint.color = Color.argb(130, 230, 130, 255)
                            canvas.drawCircle(
                                cx,
                                cy,
                                r * 0.88f,
                                paint
                            )

                            canvas.restore()

                            // Vortex core
                            val vortex =
                                1f + 0.12f * kotlin.math.sin(time * 3.5f)

                            paint.style = Paint.Style.FILL
                            paint.color = Color.argb(190, 170, 110, 255)

                            canvas.drawCircle(
                                cx,
                                cy,
                                r * 0.25f * vortex,
                                paint
                            )

                            // Time particles
                            paint.color = Color.WHITE

                            for (i in 0 until 6) {
                                val a = -time * 1.5f + i * 1.047f
                                val orbit = r * 0.72f

                                canvas.drawCircle(
                                    cx + kotlin.math.cos(a) * orbit,
                                    cy + kotlin.math.sin(a) * orbit,
                                    r * 0.035f,
                                    paint
                                )
                            }
                        }


                        else -> {
                            // EARTH SYSTEM + AIR remain exactly in the simple
                            // holographic style already used for them.
                            paint.color = node.color
                            paint.style = Paint.Style.STROKE
                            paint.strokeWidth = dp(2).toFloat()
                            canvas.drawCircle(cx, cy, r, paint)

                            paint.color = Color.WHITE
                            paint.style = Paint.Style.FILL
                            paint.textSize = dp(34).toFloat()
                            canvas.drawText(
                                node.icon,
                                cx,
                                cy + dp(12).toFloat(),
                                paint
                            )
                        }
                    }

                    // Node icon
                    paint.style = Paint.Style.FILL
                    paint.textSize = dp(30).toFloat()
                    paint.typeface = Typeface.DEFAULT_BOLD
                    paint.color = Color.WHITE
                    canvas.drawText(
                        node.icon,
                        cx,
                        cy + dp(10).toFloat(),
                        paint
                    )

                    // Node name
                    paint.textSize = dp(13).toFloat()
                    paint.typeface = Typeface.DEFAULT_BOLD
                    paint.color = node.color
                    canvas.drawText(
                        node.name,
                        cx,
                        cy + dp((node.radius + 25f).toInt()).toFloat(),
                        paint
                    )
                }
            }

            override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
                if (event.action != android.view.MotionEvent.ACTION_UP) return true

                nodes.forEach { node ->
                    val cx = width * node.x
                    val cy = height * node.y
                    val r = dp((node.radius * 3.10f).toInt()).toFloat()

                    if ((event.x - cx) * (event.x - cx) +
                        (event.y - cy) * (event.y - cy) <= r * r
                    ) {
                        val builder =
                            android.app.AlertDialog.Builder(this@CosmosActivity)
                                .setTitle("${node.icon} ${node.name}")
                                .setMessage(
                                    "EARTH exploration node\n\n" +
                                    "This node will open its own knowledge and decision experience."
                                )
                                .setPositiveButton("EXPLORE", null)
                                .setNegativeButton("CLOSE", null)

                        val nodeDialog = builder.create()

                        nodeDialog.setOnShowListener {
                            nodeDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                                .setOnClickListener {
                                    nodeDialog.dismiss()

                                    if (node.name == "EARTH SYSTEM") {
                                        showEarthSystemTheory()
                                    } else if (node.name == "AIR") {
                                        showAirWindField()
                                    } else if (node.name == "WATER") {
                                        showWaterField()
                                    } else if (node.name == "LIFE") {
                                        showLifeField()
                                    } else if (node.name == "ENERGY") {
                                        showEnergyField()
                                    } else if (node.name == "HUMAN WORLD") {
                                        showHumanWorldField()
                                    } else if (node.name == "TIME MACHINE") {
                                        showTimeLayer()
                                    }
                                    }
                                }

                        nodeDialog.show()
                        return true
                    }
                }

                return true
            }
        }

        val overlay = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(2, 10, 16))
            addView(
                earthView,
                FrameLayout.LayoutParams(-1, -1)
            )
        }

        val back = TextView(this).apply {
            text = "← BACK"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(dp(18), dp(10), dp(18), dp(10))
            setBackgroundColor(Color.TRANSPARENT)
            setOnClickListener {
                (overlay.parent as? android.view.ViewGroup)
                    ?.removeView(overlay)

                if (layerOverlay === overlay) {
                    layerOverlay = null
                }
            }
        }

        overlay.addView(
            back,
            FrameLayout.LayoutParams(-2, -2).apply {
                gravity = Gravity.TOP or Gravity.START
                topMargin = dp(18)
            }
        )

        layerOverlay = overlay

        addContentView(
            overlay,
            FrameLayout.LayoutParams(-1, -1)
        )
    }

    private fun showAirWindField() {
        airRoot?.let { oldRoot ->
            (oldRoot.parent as? android.view.ViewGroup)?.removeView(oldRoot)
        }
        airRoot = null

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(2, 10, 16))
        }

        airRoot = root

        val windView = object : View(this) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val handler = Handler(Looper.getMainLooper())
            private var phase = 0f

            private val animator = object : Runnable {
                override fun run() {
                    phase += 0.035f
                    invalidate()
                    handler.postDelayed(this, 32L)
                }
            }

            init {
                handler.post(animator)
            }

            override fun onDetachedFromWindow() {
                handler.removeCallbacks(animator)
                super.onDetachedFromWindow()
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)
                canvas.drawColor(Color.rgb(2, 10, 16))

                val cx = width / 2f
                val cy = height * 0.25f
                val field = minOf(width, height) * 0.20f

                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(3, 28, 42)
                canvas.drawCircle(cx, cy, field * 1.15f, paint)

                paint.color = Color.rgb(5, 52, 70)
                canvas.drawCircle(cx, cy, field * 0.82f, paint)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(2).toFloat()

                for (i in 0..4) {
                    val pulse = ((phase * 90f + i * 70f) % 280f)
                    val radius = field * (0.25f + pulse / 420f)

                    paint.color = Color.argb(
                        (130 - pulse * 0.35f).toInt().coerceIn(15, 130),
                        70, 220, 255
                    )

                    canvas.drawCircle(cx, cy, radius, paint)
                }

                for (line in 0..8) {
                    val yOffset = (line - 4f) * field * 0.13f
                    val path = Path()

                    path.moveTo(
                        cx - field * 1.35f,
                        cy + yOffset
                    )

                    path.cubicTo(
                        cx - field * 0.70f,
                        cy + yOffset - field * 0.18f,
                        cx + field * 0.30f,
                        cy + yOffset + field * 0.18f,
                        cx + field * 1.35f,
                        cy + yOffset - field * 0.04f
                    )

                    paint.color = Color.argb(125, 70, 220, 255)
                    canvas.drawPath(path, paint)
                }

                paint.style = Paint.Style.FILL

                for (i in 0 until 34) {
                    val seed = i * 1.73f
                    val t = (phase + seed) % 6.28f

                    val x =
                        cx + sin(t + seed) * field *
                        (0.45f + (i % 5) * 0.08f)

                    val y =
                        cy + cos(t * 0.72f + seed) *
                        field * 0.55f

                    val alpha =
                        (90 + 120 * ((sin(t) + 1f) / 2f)).toInt()

                    paint.color = Color.argb(alpha, 80, 225, 255)

                    canvas.drawCircle(
                        x,
                        y,
                        dp(2 + (i % 3)).toFloat(),
                        paint
                    )
                }

                paint.color = Color.rgb(8, 80, 105)
                canvas.drawCircle(cx, cy, field * 0.22f, paint)

                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(3).toFloat()
                paint.color = Color.rgb(60, 220, 255)

                canvas.drawCircle(
                    cx,
                    cy,
                    field * 0.22f,
                    paint
                )

                paint.style = Paint.Style.FILL
                paint.textAlign = Paint.Align.CENTER
                paint.typeface = Typeface.DEFAULT_BOLD
                paint.textSize = dp(18).toFloat()
                paint.color = Color.WHITE


                paint.typeface = Typeface.DEFAULT
                paint.textSize = dp(13).toFloat()
                paint.color = Color.rgb(150, 215, 230)

            }
        }

        root.addView(
            windView,
            FrameLayout.LayoutParams(-1, -1)
        )

        val title = TextView(this).apply {
            text = "🌬️  AIR"
            textSize = 40f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        root.addView(
            title,
            FrameLayout.LayoutParams(-1, dp(70)).apply {
                topMargin = dp(38)
            }
        )

        val subtitle = TextView(this).apply {
            text = "WIND FIELD"
            textSize = 19f
            setTextColor(Color.rgb(90, 220, 255))
            gravity = Gravity.CENTER
        }

        root.addView(
            subtitle,
            FrameLayout.LayoutParams(-1, dp(45)).apply {
                topMargin = dp(98)
            }
        )

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(18), dp(8), dp(18), dp(24))
        }

        val phaseTitle = TextView(this).apply {
            text = "👁️  OBSERVE"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(4) }
        )

        val phaseCounter = TextView(this).apply {
            text = "AIR WIND FIELD • 01 / 04"
            textSize = 12f
            setTextColor(Color.rgb(70, 220, 255))
            gravity = Gravity.CENTER
        }

        content.addView(
            phaseCounter,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(6) }
        )

        // AIR dynamic question banks.
        val airQuestionBanks = listOf(
            listOf(
                "Which air signal should you examine first when wind conditions change?",
                "What is the clearest first signal of a changing air condition?",
                "Which present air condition deserves attention before looking wider?",
                "What should you observe first in the changing wind field?",
                "Which atmospheric signal gives the strongest starting point?",
                "What air signal should be identified before tracing connected effects?",
                "Which visible change in the air system should be noticed first?",
                "What is the best starting signal for understanding the wind condition?",
                "Which current air condition provides the clearest initial context?",
                "What should you measure or observe before interpreting the wider air system?"
            ),
            listOf(
                "Which system should you connect with the changing air movement?",
                "What wider system could explain the changing air condition?",
                "Which relationship is most useful for understanding the wind change?",
                "What should be connected with the observed air signal?",
                "Which surrounding system may be affected by the changing air?",
                "What connection could reveal why the air condition is changing?",
                "Which air-system relationship deserves attention next?",
                "What wider factor should be connected to the wind signal?",
                "Which interaction helps explain the changing atmospheric condition?",
                "What should you connect before tracing the air pattern further?"
            ),
            listOf(
                "Where could the changing air pattern carry its effect?",
                "Which path should you follow to understand the air movement?",
                "How should the changing wind pattern be traced?",
                "Where does the observed air change move next?",
                "Which pathway best reveals the wider effect of the wind?",
                "What should you follow from the local air signal to its broader effect?",
                "Which direction or system should be traced through the air pattern?",
                "How can the movement of the changing air be followed?",
                "What path could connect the present air signal with its wider impact?",
                "Which atmospheric pathway deserves closer tracing?"
            ),
            listOf(
                "What response fits the connected air-system context?",
                "Which response best fits the current air condition?",
                "What should be done after understanding the wider air pattern?",
                "Which action considers both the wind change and its effects?",
                "What response could reduce the strongest immediate air-related risk?",
                "Which action best supports adaptation to the changing air condition?",
                "What response balances the local air signal with wider effects?",
                "Which response should follow after tracing the atmospheric pattern?",
                "What action fits the connected air-system situation?",
                "Which response is most appropriate after understanding the air context?"
            )
        )

        val airSessionQuestions = airQuestionBanks.map { it.shuffled().first() }


        val questionText = TextView(this).apply {
              text = airSessionQuestions[0]
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        content.addView(
            questionText,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        val hintText = TextView(this).apply {
            text = "Choose the signal that gives the clearest starting point."
            textSize = 13f
            setTextColor(Color.rgb(170, 180, 205))
            gravity = Gravity.CENTER
        }

        content.addView(
            hintText,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        val observeOptions = listOf(
            "A  WIND SPEED\nMeasure how fast the air is moving",
            "B  AIR PRESSURE\nCompare the pressure pattern",
            "C  WIND DIRECTION\nIdentify where the air is moving",
            "D  TEMPERATURE SHIFT\nCheck the changing air temperature"
        )

        val connectOptions = listOf(
            "A  AIR + WATER\nCheck how wind can influence water movement",
            "B  AIR + LAND\nConnect wind with surface conditions",
            "C  AIR + ENERGY\nTrace how heat drives air movement",
            "D  AIR + LIFE\nLook for effects on living systems"
        )

        val traceOptions = listOf(
            "A  LOCAL AREA\nFollow the change nearby",
            "B  DOWNWIND PATH\nTrace where the moving air travels",
            "C  WEATHER SYSTEM\nFollow the larger atmospheric pattern",
            "D  WIDER REGION\nLook for effects across a broader area"
        )

        val respondOptions = listOf(
            "A  PROTECT\nReduce the strongest immediate air-related risk",
            "B  ADAPT\nAdjust to the changing air condition",
            "C  TRACE MORE\nCollect more connected information first",
            "D  BALANCE\nChoose a response considering the wider system"
        )

        var stage = 0
        var selected = -1
    val airAnswers = mutableListOf<String>()
        val optionSets = listOf(
            observeOptions,
            connectOptions,
            traceOptions,
            respondOptions
        )


        val displayOptionSets = listOf(
            observeOptions.shuffled(),
            connectOptions.shuffled(),
            traceOptions.shuffled(),
            respondOptions.shuffled()
        )

        val buttons = mutableListOf<Button>()

        fun refreshButtons(options: List<String>) {
            buttons.forEachIndexed { index, button ->
                button.text = options[index]
                button.setBackgroundColor(Color.rgb(7, 25, 38))
            }
        }

        displayOptionSets[0].forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                textSize = 13f
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.WHITE)
                setPadding(dp(16), dp(4), dp(16), dp(4))
                setBackgroundColor(Color.rgb(7, 25, 38))

                setOnClickListener {
                    selected = index

                    buttons.forEachIndexed { i, b ->
                        b.setBackgroundColor(
                            if (i == selected)
                                Color.rgb(0, 105, 135)
                            else
                                Color.rgb(7, 25, 38)
                        )
                    }
                }
            }

            buttons.add(button)

            content.addView(
                button,
                LinearLayout.LayoutParams(-1, dp(52)).apply {
                    topMargin = dp(22)
                }
            )
        }

        val next = Button(this).apply {
            text = "NEXT  →  CONNECT"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 95, 125))
        }

        next.setOnClickListener {
            if (selected < 0) {
                Toast.makeText(
                    this@CosmosActivity,
                    "SELECT AN AIR OBSERVATION",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Capture the current AIR decision before moving to the next stage.
              airAnswers.add(displayOptionSets[stage][selected])

            when (stage) {
                0 -> {
                    stage = 1
                    selected = -1

                    phaseTitle.text = "🔗  CONNECT"
                    phaseCounter.text = "AIR WIND FIELD • 02 / 04"
                      questionText.text = airSessionQuestions[1]
                    hintText.text =
                        "Look beyond the wind and trace its relationship."

                      refreshButtons(displayOptionSets[1])
                    next.text = "NEXT  →  TRACE"
                }

                1 -> {
                    stage = 2
                    selected = -1

                    phaseTitle.text = "🧭  TRACE"
                    phaseCounter.text = "AIR WIND FIELD • 03 / 04"
                      questionText.text = airSessionQuestions[2]
                    hintText.text =
                        "Follow the movement from its source to its wider effect."

                      refreshButtons(displayOptionSets[2])
                    next.text = "NEXT  →  RESPOND"
                }

                2 -> {
                    stage = 3
                    selected = -1

                    phaseTitle.text = "🧠  RESPOND"
                    phaseCounter.text = "AIR WIND FIELD • 04 / 04"
                      questionText.text = airSessionQuestions[3]
                    hintText.text =
                        "Choose a response that considers both change and wider effects."

                      refreshButtons(displayOptionSets[3])
                    next.text = "COMPLETE  →  AIR CONTEXT"
                }

                else -> {

                    val observe = airAnswers.getOrNull(0) ?: ""
                    val connect = airAnswers.getOrNull(1) ?: ""
                    val trace = airAnswers.getOrNull(2) ?: ""
                    val respond = airAnswers.getOrNull(3) ?: ""

                    val observeIndex = observeOptions.indexOf(observe)
                    val connectIndex = connectOptions.indexOf(connect)
                    val traceIndex = traceOptions.indexOf(trace)
                    val respondIndex = respondOptions.indexOf(respond)

                    val currentSituation = when (observeIndex) {
                        0 -> CurrentState.STABLE
                        1 -> CurrentState.UNCERTAIN
                        2 -> CurrentState.STABLE
                        else -> CurrentState.UNCERTAIN
                    }

                    val contextState = when (connectIndex) {
                        0 -> ContextState.NORMAL
                        1 -> ContextState.FAVOURABLE
                        2 -> ContextState.FAVOURABLE
                        3 -> ContextState.NORMAL
                        else -> ContextState.UNCERTAIN
                    }

                    val goalLevel = when (traceIndex) {
                        0 -> GoalLevel.SMALL_IMPROVEMENT
                        1 -> GoalLevel.MODERATE_IMPROVEMENT
                        2 -> GoalLevel.MAJOR_IMPROVEMENT
                        3 -> GoalLevel.LONG_TERM_TRANSFORMATION
                        else -> GoalLevel.MODERATE_IMPROVEMENT
                    }

                    val momentumState = when (traceIndex) {
                        0 -> MomentumState.STABLE
                        1 -> MomentumState.IMPROVING
                        2 -> MomentumState.IMPROVING
                        3 -> MomentumState.STABLE
                        else -> MomentumState.STABLE
                    }

                    val riskLevel = when (respondIndex) {
                        0 -> RiskLevel.HIGH
                        1 -> RiskLevel.MEDIUM
                        2 -> RiskLevel.MEDIUM
                        3 -> RiskLevel.LOW
                        else -> RiskLevel.MEDIUM
                    }

                    val timingState = when (respondIndex) {
                        0 -> TimingState.NOW
                        1 -> TimingState.SOON
                        2 -> TimingState.LATER
                        3 -> TimingState.SOON
                        else -> TimingState.SOON
                    }

                    val decision = GDMIESimpleDecision(
                        decisionText = """
                            AIR OBSERVE:
                            $observe

                            AIR CONNECT:
                            $connect

                            AIR TRACE:
                            $trace

                            AIR RESPOND:
                            $respond
                        """.trimIndent(),
                        currentSituation = currentSituation,
                        goalOutcome = goalLevel,
                        context = contextState,
                        momentum = momentumState,
                        risk = riskLevel,
                        timing = timingState
                    )

                    val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = GDMIEEngineGateway.calculate(input)

                            val analysis = StringBuilder()

                            analysis.append("🌬️ AIR\n\n")
                            analysis.append("CONTEXT ANALYSIS\n\n")

                            analysis.append("✓ OBSERVE\n")
                            analysis.append(observe)

                            analysis.append("\n\n✓ CONNECT\n")
                            analysis.append(connect)

                            analysis.append("\n\n✓ TRACE\n")
                            analysis.append(trace)

                            analysis.append("\n\n✓ RESPOND\n")
                            analysis.append(respond)

                            analysis.append("\n\n🧠 GDMIE SIGNAL\n\n")
                            analysis.append(result.decision)

                              val confidencePercent =
                                  (result.confidence * 100).toInt().coerceIn(0, 100)

                              val confidenceColor = when {
                                  confidencePercent >= 80 -> Color.rgb(0, 255, 170)
                                  confidencePercent >= 60 -> Color.rgb(0, 235, 255)
                                  confidencePercent >= 40 -> Color.rgb(255, 210, 60)
                                  else -> Color.rgb(255, 90, 110)
                              }

                              val analysisLayout =
                                  android.widget.LinearLayout(this@CosmosActivity).apply {
                                      orientation =
                                          android.widget.LinearLayout.VERTICAL
                                      setPadding(
                                          dp(24),
                                          dp(20),
                                          dp(24),
                                          dp(12)
                                      )
                                  }

                              val analysisText = TextView(this@CosmosActivity).apply {
                                  text = analysis.toString()
                                  textSize = 15f
                                  setTextColor(Color.rgb(25, 30, 45))
                              }

                              val confidenceText = TextView(this@CosmosActivity).apply {
                                  text = "CONFIDENCE: 0%"
                                  textSize = 24f
                                  setTypeface(
                                      null,
                                      android.graphics.Typeface.BOLD
                                  )
                                  gravity = Gravity.CENTER
                                  setTextColor(confidenceColor)
                                  setPadding(0, dp(18), 0, dp(8))
                                  alpha = 0.35f
                              }

                              analysisLayout.addView(analysisText)
                              analysisLayout.addView(confidenceText)

                                val analysisDialog =
                                    AlertDialog.Builder(this@CosmosActivity)
                                        .setTitle("🧠 GDMIE ANALYSIS")
                                        .setView(analysisLayout)
                                        .setPositiveButton("DONE") { dialog, _ ->
                                            dialog.dismiss()

                                            val airRewardNode = SpaceNode(
                                                "🌬️",
                                                "AIR",
                                                0.20f,
                                                0.38f,
                                                46f,
                                                cyan
                                            )

                                            showSpaceReward(airRewardNode)
                                        }
                                        .create()

        styleCinematicDialog(analysisDialog, Color.rgb(70, 220, 255))

                                analysisDialog.setOnShowListener {
                                    confidenceText.animate()
                                        .alpha(1f)
                                        .setDuration(350L)
                                        .withEndAction {
                                            val animator =
                                                android.animation.ValueAnimator.ofInt(
                                                    0,
                                                    confidencePercent
                                                )

                                            animator.duration = 1200L

                                            animator.addUpdateListener { valueAnimator ->
                                                val value =
                                                    valueAnimator.animatedValue as Int

                                                confidenceText.text =
                                                    "CONFIDENCE: $value%"

                                                val pulse =
                                                    1f + (value / 100f) * 0.08f

                                                confidenceText.scaleX = pulse
                                                confidenceText.scaleY = pulse
                                            }

                                            animator.start()
                                        }
                                }

                                analysisDialog.show()

                        } catch (e: Exception) {
                            AlertDialog.Builder(this@CosmosActivity)
                                .setTitle("GDMIE ANALYSIS")
                                .setMessage(
                                    "Analysis could not be completed.\n\n" +
                                            (e.message ?: "Please try again.")
                                )
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }

        content.addView(
            next,
            LinearLayout.LayoutParams(-1, dp(44)).apply {
                topMargin = dp(12)
            }
        )

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            addView(content)
        }

        root.addView(
            scroll,
            FrameLayout.LayoutParams(-1, -1).apply {
                topMargin = dp(145)
                bottomMargin = dp(20)
            }
        )


        addContentView(
            root,
            FrameLayout.LayoutParams(-1, -1)
        )
    }

    override fun onBackPressed() {
        val layer = layerOverlay
        if (layer != null) {
            (layer.parent as? android.view.ViewGroup)
                ?.removeView(layer)

            layerOverlay = null
            return
        }

        val air = airRoot
        if (air != null) {
            (air.parent as? android.view.ViewGroup)?.removeView(air)
            airRoot = null
            return
        }
        super.onBackPressed()
    }

    private fun showEnergyField() {

        // ☀️ ENERGY — switch ambient audio

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(18, 10, 2))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(34), dp(24), dp(24))
        }

        val icon = TextView(this).apply {
            text = "☀️"
            textSize = 58f
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }

        val title = TextView(this).apply {
            text = "ENERGY SYSTEM"
            textSize = 27f
            setTextColor(Color.rgb(255, 215, 80))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "ENERGY • FORCE • CREATION"
            textSize = 13f
            setTextColor(Color.rgb(230, 200, 130))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(22))
        }

        val description = TextView(this).apply {
            text =
                "Energy drives change, movement and transformation.\n\n" +
                "Observe the energy signal, connect its source and effects, trace its movement and choose a response."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setLineSpacing(0f, 1.15f)
            setPadding(dp(8), dp(10), dp(8), dp(24))
        }

        val continueButton = Button(this).apply {
            text = "CONTINUE  →  ENERGY INTERPRETATION"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)

            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(105, 72, 18))
                    setStroke(dp(1), Color.rgb(255, 215, 80))
                }

            setOnClickListener {
                showEnergyInterpretation()
            }
        }

        content.addView(
            icon,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            description,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            continueButton,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            ).apply {
                topMargin = dp(8)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .setNegativeButton("← BACK", null)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(255, 200, 60))
    }

    private fun styleCinematicDialog(
        dialog: AlertDialog,
        accent: Int,
        fullScreen: Boolean = false
    ) {

        dialog.setOnShowListener {

            val window = dialog.window ?: return@setOnShowListener

            if (fullScreen) {
                window.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
                window.setGravity(Gravity.CENTER)
            }

            window.setBackgroundDrawable(
                android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)
            )

            window.setDimAmount(0.78f)

            val buttonPanel = window.findViewById<android.view.View>(
                resources.getIdentifier("buttonPanel", "id", "android")
            )

            buttonPanel?.background =
                android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)

            buttonPanel?.setPadding(0, 0, 0, 0)

            val backButton =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            backButton.setTextColor(accent)

            backButton.background =
                android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)

            (backButton.parent as? android.view.ViewGroup)?.apply {
                background =
                    android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)
                setPadding(0, 0, 0, 0)
            }

            /*
             * ==================================================
             * GDMIE CINEMATIC ENTRANCE
             * ==================================================
             */

            val decor = window.decorView

            decor.alpha = 0f
            decor.scaleX = 0.94f
            decor.scaleY = 0.94f
            decor.translationY = dp(45).toFloat()

            decor.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(420L)
                .setInterpolator(
                    android.view.animation.DecelerateInterpolator()
                )
                .start()

            /*
             * Find the actual custom dialog content.
             */
            val customPanel =
                window.decorView.findViewById<android.view.View>(
                    resources.getIdentifier("custom", "id", "android")
                )

            if (customPanel != null) {

                /*
                 * Start the whole internal world invisible.
                 */
                customPanel.alpha = 0f
                customPanel.translationY = dp(18).toFloat()

                customPanel.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300L)
                    .setStartDelay(120L)
                    .setInterpolator(
                        android.view.animation.DecelerateInterpolator()
                    )
                    .start()

                /*
                 * ==================================================
                 * STAGGERED INTERNAL REVEAL
                 * ==================================================
                 */

                fun animateChildren(
                    parent: android.view.ViewGroup,
                    baseDelay: Long = 180L
                ) {
                    var delay = baseDelay

                    for (i in 0 until parent.childCount) {

                        val child = parent.getChildAt(i)

                        child.alpha = 0f
                        child.translationY = dp(22).toFloat()
                        child.scaleX = 0.97f
                        child.scaleY = 0.97f

                        child.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(280L)
                            .setStartDelay(delay)
                            .setInterpolator(
                                android.view.animation.DecelerateInterpolator()
                            )
                            .start()

                        delay += 55L

                        /*
                         * Animate nested content too.
                         * This gives option cards and internal
                         * controls their own reveal.
                         */
                        if (child is android.view.ViewGroup) {
                            animateChildren(
                                child,
                                delay + 40L
                            )
                        }
                    }
                }

                if (customPanel is android.view.ViewGroup) {
                    animateChildren(customPanel, 170L)
                }

                /*
                 * ==================================================
                 * ACCENT GLOW / PULSE
                 * ==================================================
                 */

                customPanel.animate()
                    .scaleX(1.008f)
                    .scaleY(1.008f)
                    .setDuration(900L)
                    .setStartDelay(650L)
                    .setInterpolator(
                        android.view.animation.AccelerateDecelerateInterpolator()
                    )
                    .withEndAction {
                        customPanel.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(900L)
                            .setInterpolator(
                                android.view.animation.AccelerateDecelerateInterpolator()
                            )
                            .start()
                    }
                    .start()
            }
        }
    }


    private fun showLifeField() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(3, 14, 10))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(24), dp(34), dp(24), dp(24))
        }

        val icon = TextView(this).apply {
            text = "🌱"
            textSize = 58f
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        }

        val title = TextView(this).apply {
            text = "LIFE SYSTEM"
            textSize = 27f
            setTextColor(Color.rgb(100, 235, 150))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "LIFE • GROWTH • ADAPTATION"
            textSize = 13f
            setTextColor(Color.rgb(150, 210, 175))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(22))
        }

        val description = TextView(this).apply {
            text =
                "Life is a changing system of growth, interaction and adaptation.\n\n" +
                "Observe the living signal, connect its relationships, trace the change and choose a response."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setLineSpacing(0f, 1.15f)
            setPadding(dp(8), dp(10), dp(8), dp(24))
        }

        val continueButton = Button(this).apply {
            text = "CONTINUE  →  LIFE INTERPRETATION"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)

            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(18, 72, 48))
                    setStroke(dp(1), Color.rgb(100, 235, 150))
                }

            setOnClickListener {
                showLifeInterpretation()
            }
        }

        content.addView(
            icon,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            title,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            subtitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            description,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            continueButton,
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            ).apply {
                topMargin = dp(8)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .setNegativeButton("← BACK", null)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(80, 230, 150))
    }

    private fun showWaterField() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(2, 10, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(30), dp(24), dp(24))
        }

        val icon = TextView(this).apply {
            text = "💧"
            textSize = 62f
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "WATER SYSTEM"
            textSize = 24f
            setTextColor(Color.rgb(80, 180, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "4D HYDROLOGICAL DECISION FIELD"
            textSize = 13f
            setTextColor(Color.rgb(170, 200, 225))
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(20))
        }

        val description = TextView(this).apply {
            text =
                "Water connects atmosphere, land, life and human systems.\n\n" +
                "Follow the changing signal and build a context before responding."
            textSize = 15f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(8), dp(8), dp(24))
        }

        val continueButton = Button(this).apply {
            text = "CONTINUE  →  WATER INTERPRETATION"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 105, 145))
            setOnClickListener {
                showWaterInterpretation()
            }
        }

        content.addView(icon)
        content.addView(title)
        content.addView(subtitle)
        content.addView(description)
        content.addView(
            continueButton,
            LinearLayout.LayoutParams(
                -1,
                dp(56)
            ).apply {
                topMargin = dp(8)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog = AlertDialog.Builder(this)
            .setView(root)
            .setNegativeButton("← BACK", null)
            .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(80, 170, 255))
    }

    private fun showEnergyInterpretation() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(18, 10, 2))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val phaseTitle = TextView(this).apply {
            text = "👁️  OBSERVE"
            textSize = 20f
            setTextColor(Color.rgb(255, 215, 80))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val phaseCounter = TextView(this).apply {
            text = "ENERGY 4D INTERPRETATION • 01 / 04"
            textSize = 12f
            setTextColor(Color.rgb(230, 200, 130))
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(16))
        }

        val energyQuestionBanks = listOf(
            listOf(
                "What energy signal should be examined first when the system begins to change?",
                "What present energy condition gives the clearest starting point?",
                "What recent energy change deserves attention first?",
                "What signal best reveals the current energy state?",
                "What energy evidence is strongest right now?",
                "What part of the energy condition should be measured first?",
                "What signal could show that energy flow is shifting?",
                "What observation would give the clearest energy baseline?",
                "What energy level or intensity needs closer attention?",
                "What output signal could reveal a change in energy conditions?"
            ),
            listOf(
                "What connection between energy and its source matters most?",
                "How does energy interact with matter in this situation?",
                "What relationship between energy and life could explain the condition?",
                "How does energy use connect with the human world?",
                "Which energy system should be connected to understand the change?",
                "What source and output relationship could explain the signal?",
                "What interaction could amplify or reduce the energy change?",
                "How could energy movement affect another system?",
                "What connection between energy flow and surrounding conditions matters?",
                "What wider energy relationship should be examined?"
            ),
            listOf(
                "Where could the energy change have started?",
                "How is the energy moving through the system?",
                "Where is the energy being transferred?",
                "How is the energy changing from one form to another?",
                "What source could explain the strongest energy signal?",
                "What path is the energy following?",
                "What transfer point could explain the observed change?",
                "What transformation could be driving the current output?",
                "What chain connects the source to the final energy effect?",
                "What repeated energy pattern could reveal the underlying process?"
            ),
            listOf(
                "What response could reduce unnecessary energy loss?",
                "How should the system adapt to the changing energy condition?",
                "How could available energy be used more effectively?",
                "What response could balance source, use and output?",
                "What action could improve energy efficiency?",
                "What should be protected before the energy condition changes further?",
                "What adjustment could reduce wasted energy?",
                "What response could improve the useful energy output?",
                "What action could balance immediate energy needs with wider effects?",
                "What response could create a more stable energy system?"
            )
        )

        val energySessionQuestions = energyQuestionBanks.map { it.shuffled().first() }

        val questionText = TextView(this).apply {
            text = energySessionQuestions[0]
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val hintText = TextView(this).apply {
            text = "Choose the signal that gives the clearest starting point."
            textSize = 13f
            setTextColor(Color.rgb(210, 185, 130))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(12))
        }

        val observeOptions = listOf(
            "A  ENERGY LEVEL\nMeasure the present energy level or intensity",
            "B  ENERGY FLOW\nObserve the direction and movement of energy",
            "C  RECENT CHANGE\nIdentify what changed most strongly",
            "D  OUTPUT SIGNAL\nObserve the useful output being produced"
        )

        val connectOptions = listOf(
            "A  SOURCE + ENERGY\nConnect energy with where it originates",
            "B  ENERGY + MATTER\nTrace how energy interacts with matter",
            "C  ENERGY + LIFE\nConnect energy with living systems",
            "D  ENERGY + HUMAN WORLD\nTrace energy use and human activity"
        )

        val traceOptions = listOf(
            "A  TRACE THE SOURCE\nFind where the energy change began",
            "B  TRACE THE FLOW\nFollow how energy moves through the system",
            "C  TRACE THE TRANSFER\nFollow where the energy is transferred",
            "D  TRACE THE TRANSFORMATION\nFollow how energy changes form"
        )

        val respondOptions = listOf(
            "A  REDUCE LOSS\nLimit unnecessary energy loss",
            "B  ADAPT\nAdjust to the changing energy condition",
            "C  OPTIMIZE\nUse the available energy more effectively",
            "D  BALANCE\nConsider source, use, output and wider impact"
        )

        val displayObserveOptions = observeOptions.shuffled()
        val displayConnectOptions = connectOptions.shuffled()
        val displayTraceOptions = traceOptions.shuffled()
        val displayRespondOptions = respondOptions.shuffled()

        var phase = 0
        var selected = -1
        val energyAnswers = mutableListOf<String>()
        val buttons = mutableListOf<Button>()

        fun styleEnergyButton(button: Button, selectedState: Boolean) {
            val drawable =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(
                        if (selectedState) {
                            Color.rgb(130, 88, 18)
                        } else {
                            Color.rgb(38, 25, 7)
                        }
                    )
                    setStroke(
                        dp(if (selectedState) 2 else 1),
                        if (selectedState) {
                            Color.rgb(255, 225, 100)
                        } else {
                            Color.rgb(135, 105, 45)
                        }
                    )
                }

            button.background = drawable
        }

        observeOptions.forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                textSize = 13f
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.WHITE)
                setPadding(dp(16), dp(4), dp(16), dp(4))

                styleEnergyButton(this, false)

                setOnClickListener {
                    selected = index

                    buttons.forEachIndexed { i, b ->
                        styleEnergyButton(b, i == selected)

                        b.animate()
                            .scaleX(if (i == selected) 1.015f else 1.0f)
                            .scaleY(if (i == selected) 1.015f else 1.0f)
                            .setDuration(140L)
                            .start()
                    }
                }
            }

            buttons.add(button)
        }

        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            phaseCounter,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val hologramView = createEarthHologramView("ENERGY")

        content.addView(
            hologramView,
            LinearLayout.LayoutParams(
                -1,
                dp(210)
            ).apply {
                setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(12)
                )
            }
        )


        content.addView(
            questionText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            hintText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        buttons.forEach { button ->
            content.addView(
                button,
                LinearLayout.LayoutParams(
                    -1,
                    0,
                    1f
                ).apply {
                    topMargin = dp(3)
                    bottomMargin = dp(3)
                }
            )
        }

        val next = Button(this).apply {
            text = "NEXT  →  CONNECT"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)

            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(Color.rgb(105, 72, 18))
                    setStroke(dp(1), Color.rgb(255, 215, 80))
                }
        }

        next.setOnClickListener {
            if (selected < 0) {
                Toast.makeText(
                    this@CosmosActivity,
                    "SELECT AN ENERGY SIGNAL",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (phase) {
                0 -> {
                    energyAnswers.add(displayObserveOptions[selected])
                    phase = 1
                    selected = -1

                    phaseTitle.text = "🔗  CONNECT"
                    phaseCounter.text = "ENERGY 4D INTERPRETATION • 02 / 04"
                    questionText.text =
                        energySessionQuestions[1]
                    hintText.text =
                        "Trace the relationship beyond the first energy signal."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayConnectOptions[index]
                        styleEnergyButton(button, false)
                    }

                    next.text = "NEXT  →  TRACE"
                }

                1 -> {
                    energyAnswers.add(displayConnectOptions[selected])
                    phase = 2
                    selected = -1

                    phaseTitle.text = "🧭  TRACE"
                    phaseCounter.text = "ENERGY 4D INTERPRETATION • 03 / 04"
                    questionText.text =
                        energySessionQuestions[2]
                    hintText.text =
                        "Follow the source, flow, transfer or transformation."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayTraceOptions[index]
                        styleEnergyButton(button, false)
                    }

                    next.text = "NEXT  →  RESPOND"
                }

                2 -> {
                    energyAnswers.add(displayTraceOptions[selected])
                    phase = 3
                    selected = -1

                    phaseTitle.text = "🧠  RESPOND"
                    phaseCounter.text = "ENERGY 4D INTERPRETATION • 04 / 04"
                    questionText.text =
                        energySessionQuestions[3]
                    hintText.text =
                        "Choose a response that considers efficiency, output and wider impact."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayRespondOptions[index]
                        styleEnergyButton(button, false)
                    }

                    next.text = "COMPLETE  CONTEXT"
                }

                3 -> {
                    energyAnswers.add(displayRespondOptions[selected])

                    val observe = energyAnswers[0]
                    val connect = energyAnswers[1]
                    val trace = energyAnswers[2]
                    val respond = energyAnswers[3]

                    val observeIndex = observeOptions.indexOf(observe)
                    val connectIndex = connectOptions.indexOf(connect)
                    val traceIndex = traceOptions.indexOf(trace)
                    val respondIndex = respondOptions.indexOf(respond)

                    val currentSituation = when (observeIndex) {
                        0 -> CurrentState.STABLE
                        1 -> CurrentState.STRONG
                        2 -> CurrentState.UNCERTAIN
                        3 -> CurrentState.STRONG
                        else -> CurrentState.UNCERTAIN
                    }

                    val contextState = when (connectIndex) {
                        0 -> ContextState.FAVOURABLE
                        1 -> ContextState.NORMAL
                        2 -> ContextState.FAVOURABLE
                        3 -> ContextState.NORMAL
                        else -> ContextState.UNCERTAIN
                    }

                    val goalLevel = when (traceIndex) {
                        0 -> GoalLevel.SMALL_IMPROVEMENT
                        1 -> GoalLevel.MODERATE_IMPROVEMENT
                        2 -> GoalLevel.MAJOR_IMPROVEMENT
                        3 -> GoalLevel.LONG_TERM_TRANSFORMATION
                        else -> GoalLevel.MODERATE_IMPROVEMENT
                    }

                    val momentumState = when (traceIndex) {
                        0 -> MomentumState.DECLINING
                        1 -> MomentumState.IMPROVING
                        2 -> MomentumState.STABLE
                        3 -> MomentumState.IMPROVING
                        else -> MomentumState.STABLE
                    }

                    val riskLevel = when (respondIndex) {
                        0 -> RiskLevel.MEDIUM
                        1 -> RiskLevel.MEDIUM
                        2 -> RiskLevel.LOW
                        3 -> RiskLevel.LOW
                        else -> RiskLevel.MEDIUM
                    }

                    val timingState = when (respondIndex) {
                        0 -> TimingState.NOW
                        1 -> TimingState.SOON
                        2 -> TimingState.SOON
                        3 -> TimingState.LATER
                        else -> TimingState.SOON
                    }

                    val decision = GDMIESimpleDecision(
                        decisionText = """
                            ENERGY OBSERVE:
                            $observe

                            ENERGY CONNECT:
                            $connect

                            ENERGY TRACE:
                            $trace

                            ENERGY RESPOND:
                            $respond
                        """.trimIndent(),
                        currentSituation = currentSituation,
                        goalOutcome = goalLevel,
                        context = contextState,
                        momentum = momentumState,
                        risk = riskLevel,
                        timing = timingState
                    )

                    val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = GDMIEEngineGateway.calculate(input)

                            val confidencePercent =
                                (result.confidence * 100).toInt().coerceIn(0, 100)

                            val confidenceColor = when {
                                confidencePercent >= 80 ->
                                    Color.rgb(0, 255, 170)
                                confidencePercent >= 60 ->
                                    Color.rgb(0, 235, 255)
                                confidencePercent >= 40 ->
                                    Color.rgb(255, 210, 60)
                                else ->
                                    Color.rgb(255, 90, 110)
                            }

                            val analysisLayout =
                                LinearLayout(this@CosmosActivity).apply {
                                    orientation = LinearLayout.VERTICAL
                                    setPadding(
                                        dp(18),
                                        dp(8),
                                        dp(18),
                                        dp(8)
                                    )
                                }

                            val analysisText =
                                TextView(this@CosmosActivity).apply {
                                    text = """
                                        ☀️ ENERGY CONTEXT

                                        ✓ OBSERVE
                                        $observe

                                        ✓ CONNECT
                                        $connect

                                        ✓ TRACE
                                        $trace

                                        ✓ RESPOND
                                        $respond

                                        🧠 GDMIE SIGNAL

                                        ${result.decision}
                                    """.trimIndent()

                                    textSize = 16f
                                    setTextColor(Color.rgb(25, 30, 45))
                                    setPadding(0, dp(8), 0, dp(8))
                                }

                            val confidenceText =
                                TextView(this@CosmosActivity).apply {
                                    text = "CONFIDENCE: 0%"
                                    textSize = 24f
                                    setTypeface(
                                        null,
                                        android.graphics.Typeface.BOLD
                                    )
                                    gravity = Gravity.CENTER
                                    setTextColor(confidenceColor)
                                    setPadding(0, dp(18), 0, dp(8))
                                    alpha = 0.35f
                                }

                            analysisLayout.addView(
                                analysisText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            analysisLayout.addView(
                                confidenceText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            val analysisDialog =
                                AlertDialog.Builder(this@CosmosActivity)
                                    .setTitle("🧠 GDMIE ANALYSIS")
                                    .setView(analysisLayout)
                                    .setNeutralButton("🎁 WATCH +10 XP") { _, _ ->
                                        RewardedAdManager.show(this@CosmosActivity) { awarded ->
                                            Toast.makeText(
                                                this@CosmosActivity,
                                                "+$awarded XP earned!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .setPositiveButton("DONE") { dialog, _ ->
                                        dialog.dismiss()

                                        val energyRewardNode = SpaceNode(
                                            "☀️",
                                            "ENERGY",
                                            0.20f,
                                            0.68f,
                                            46f,
                                            Color.rgb(255, 215, 80)
                                        )

                                        showSpaceReward(energyRewardNode)
                                    }
                                    .create()

                            analysisDialog.setOnShowListener {
                                val animator =
                                    android.animation.ValueAnimator.ofInt(
                                        0,
                                        confidencePercent
                                    ).apply {
                                        duration = 1200L

                                        addUpdateListener { animation ->
                                            val value =
                                                animation.animatedValue as Int

                                            confidenceText.text =
                                                "CONFIDENCE: $value%"

                                            confidenceText.alpha =
                                                0.35f +
                                                    (value / 100f) * 0.65f

                                            val scale =
                                                0.96f +
                                                    (value / 100f) * 0.04f

                                            confidenceText.scaleX = scale
                                            confidenceText.scaleY = scale
                                        }
                                    }

                                animator.start()
                            }

                            analysisDialog.show()

                        } catch (e: Exception) {
                            AlertDialog.Builder(this@CosmosActivity)
                                .setTitle("🧠 GDMIE ANALYSIS")
                                .setMessage(
                                    "Analysis could not be completed.\n\n" +
                                        (e.message ?: "Unknown error")
                                )
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }

        content.addView(
            next,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(6)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(255, 200, 60), true)
    }

    private fun showLifeInterpretation() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(3, 14, 10))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val phaseTitle = TextView(this).apply {
            text = "👁️  OBSERVE"
            textSize = 20f
            setTextColor(Color.rgb(100, 235, 150))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val phaseCounter = TextView(this).apply {
            text = "LIFE 4D INTERPRETATION • 01 / 04"
            textSize = 12f
            setTextColor(Color.rgb(150, 200, 170))
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(16))
        }

        val questionText = TextView(this).apply {
            text = "What should you observe first when a living system begins to change?"
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val hintText = TextView(this).apply {
            text = "Choose the signal that gives the clearest starting point."
            textSize = 13f
            setTextColor(Color.rgb(150, 200, 170))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(12))
        }

        val observeOptions = listOf(
            "A  GROWTH SIGNAL\nObserve changes in growth or development",
            "B  HEALTH SIGNAL\nCheck the present condition of life",
            "C  BEHAVIOUR CHANGE\nIdentify a meaningful change in behaviour",
            "D  ENVIRONMENT SIGNAL\nObserve changes around the living system"
        )

        val connectOptions = listOf(
            "A  LIFE + ENVIRONMENT\nConnect living systems with their surroundings",
            "B  LIFE + RESOURCES\nConnect life with food, water or energy",
            "C  LIFE + OTHER LIFE\nTrace relationships between living systems",
            "D  LIFE + HUMAN WORLD\nConnect life with human activity"
        )

        val traceOptions = listOf(
            "A  TRACE THE SOURCE\nFind where the change began",
            "B  TRACE THE ADAPTATION\nFollow how the system is adjusting",
            "C  TRACE THE EFFECT\nFollow what the change influences",
            "D  TRACE THE CYCLE\nFollow how the pattern develops over time"
        )

        val respondOptions = listOf(
            "A  PROTECT\nReduce the strongest immediate threat",
            "B  SUPPORT\nStrengthen the living system's ability to adapt",
            "C  RESTORE\nHelp the affected system recover",
            "D  BALANCE\nConsider life and its wider environment together"
        )

        val lifeQuestionBanks = listOf(
            listOf(
                "What should you observe first when a living system begins to change?",
                "Which signal reveals the clearest present condition of the living system?",
                "What change should be noticed before interpreting the wider life system?",
                "Which visible signal gives the strongest starting point for understanding life?",
                "What should you identify first in the changing living system?",
                "Which present signal deserves attention before connecting other factors?",
                "What is the clearest first indicator of change in this living system?",
                "Which condition should be observed before tracing the wider pattern?",
                "What should the first observation focus on in the living system?",
                "Which life signal provides the best starting point for analysis?"
            ),
            listOf(
                "Which relationship should you connect to understand the life change?",
                "What surrounding system should be connected to the observed life signal?",
                "Which relationship could explain why the living system is changing?",
                "What should be connected with the life signal to understand its context?",
                "Which wider connection is most useful for interpreting the change?",
                "What relationship should be examined beyond the first life signal?",
                "Which system interaction may explain the observed living-system condition?",
                "What should you connect before deciding what the change means?",
                "Which relationship expands the understanding of the life signal?",
                "What wider connection should be considered with the living system?"
            ),
            listOf(
                "How should you trace the change through the living system?",
                "Which path should you follow to understand how the life change developed?",
                "How can the origin and development of the change be traced?",
                "Which pathway best reveals how the living system is responding?",
                "What should you follow to understand the pattern behind the change?",
                "How should the change be tracked across the living system?",
                "Which sequence helps reveal where the life change leads?",
                "What path should be examined from signal to wider effect?",
                "How should the developing life pattern be followed?",
                "Which part of the change should be traced through time and relationships?"
            ),
            listOf(
                "What response best supports the living system?",
                "Which response fits the condition of the living system now?",
                "What action could support recovery or adaptation in the system?",
                "Which response considers both the life signal and its surroundings?",
                "What should be done after understanding the living-system context?",
                "Which response reduces harm while supporting the wider life system?",
                "What response best fits the observed change and its wider effects?",
                "Which action supports a healthier direction for the living system?",
                "What response should follow after tracing the life-system pattern?",
                "Which response balances immediate needs with longer-term life-system stability?"
            )
        )

        val lifeSessionQuestions =
            lifeQuestionBanks.map { it.shuffled().first() }

        val displayObserveOptions = observeOptions.shuffled()
        val displayConnectOptions = connectOptions.shuffled()
        val displayTraceOptions = traceOptions.shuffled()
        val displayRespondOptions = respondOptions.shuffled()


        var phase = 0
        var selected = -1
        val lifeAnswers = mutableListOf<String>()
        val buttons = mutableListOf<Button>()

        fun styleLifeButton(button: Button, selectedState: Boolean) {
            val drawable =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(
                        if (selectedState) {
                            Color.rgb(25, 105, 65)
                        } else {
                            Color.rgb(8, 30, 20)
                        }
                    )
                    setStroke(
                        dp(if (selectedState) 2 else 1),
                        if (selectedState) {
                            Color.rgb(100, 235, 150)
                        } else {
                            Color.rgb(45, 110, 75)
                        }
                    )
                }

            button.background = drawable
        }

        displayObserveOptions.forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                textSize = 13f
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.WHITE)
                setPadding(dp(16), dp(4), dp(16), dp(4))

                styleLifeButton(this, false)

                setOnClickListener {
                    selected = index

                    buttons.forEachIndexed { i, b ->
                        styleLifeButton(b, i == selected)

                        b.animate()
                            .scaleX(if (i == selected) 1.015f else 1.0f)
                            .scaleY(if (i == selected) 1.015f else 1.0f)
                            .setDuration(140L)
                            .start()
                    }
                }
            }

            buttons.add(button)
        }

        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            phaseCounter,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val hologramView = createEarthHologramView("LIFE")

        content.addView(
            hologramView,
            LinearLayout.LayoutParams(
                -1,
                dp(210)
            ).apply {
                setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(12)
                )
            }
        )


        content.addView(
            questionText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            hintText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        buttons.forEach { button ->
            content.addView(
                button,
                LinearLayout.LayoutParams(
                    -1,
                    0,
                    1f
                ).apply {
                    topMargin = dp(3)
                    bottomMargin = dp(3)
                }
            )
        }

        val next = Button(this).apply {
            text = "NEXT  →  CONNECT"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(25, 105, 65))
        }

        next.setOnClickListener {
            if (selected < 0) {
                Toast.makeText(
                    this@CosmosActivity,
                    "SELECT AN OBSERVATION",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (phase) {
                0 -> {
                    lifeAnswers.add(displayObserveOptions[selected])
                    phase = 1
                    selected = -1

                    phaseTitle.text = "🔗  CONNECT"
                    phaseCounter.text = "LIFE 4D INTERPRETATION • 02 / 04"
                    questionText.text = lifeSessionQuestions[1]
                    hintText.text =
                        "Trace the relationship beyond the first signal."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayConnectOptions[index]
                        styleLifeButton(button, false)
                    }

                    next.text = "NEXT  →  TRACE"
                }

                1 -> {
                    lifeAnswers.add(displayConnectOptions[selected])
                    phase = 2
                    selected = -1

                    phaseTitle.text = "🧭  TRACE"
                    phaseCounter.text = "LIFE 4D INTERPRETATION • 03 / 04"
                    questionText.text = lifeSessionQuestions[2]
                    hintText.text =
                        "Follow the source, adaptation, effect or wider cycle."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayTraceOptions[index]
                        styleLifeButton(button, false)
                    }

                    next.text = "NEXT  →  RESPOND"
                }

                2 -> {
                    lifeAnswers.add(displayTraceOptions[selected])
                    phase = 3
                    selected = -1

                    phaseTitle.text = "🧠  RESPOND"
                    phaseCounter.text = "LIFE 4D INTERPRETATION • 04 / 04"
                    questionText.text = lifeSessionQuestions[3]
                    hintText.text =
                        "Choose a response that considers life and its wider environment."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayRespondOptions[index]
                        styleLifeButton(button, false)
                    }

                    next.text = "COMPLETE  CONTEXT"
                }

                3 -> {
                    lifeAnswers.add(displayRespondOptions[selected])

                    val observe = lifeAnswers[0]
                    val connect = lifeAnswers[1]
                    val trace = lifeAnswers[2]
                    val respond = lifeAnswers[3]

                    val observeIndex = observeOptions.indexOf(observe)
                    val connectIndex = connectOptions.indexOf(connect)
                    val traceIndex = traceOptions.indexOf(trace)
                    val respondIndex = respondOptions.indexOf(respond)

                    val currentSituation = when (observeIndex) {
                        0 -> CurrentState.STRONG
                        1 -> CurrentState.STABLE
                        2 -> CurrentState.UNCERTAIN
                        3 -> CurrentState.UNCERTAIN
                        else -> CurrentState.UNCERTAIN
                    }

                    val contextState = when (connectIndex) {
                        0 -> ContextState.FAVOURABLE
                        1 -> ContextState.NORMAL
                        2 -> ContextState.FAVOURABLE
                        3 -> ContextState.UNCERTAIN
                        else -> ContextState.UNCERTAIN
                    }

                    val goalLevel = when (traceIndex) {
                        0 -> GoalLevel.SMALL_IMPROVEMENT
                        1 -> GoalLevel.MODERATE_IMPROVEMENT
                        2 -> GoalLevel.MAJOR_IMPROVEMENT
                        3 -> GoalLevel.LONG_TERM_TRANSFORMATION
                        else -> GoalLevel.MODERATE_IMPROVEMENT
                    }

                    val momentumState = when (traceIndex) {
                        0 -> MomentumState.DECLINING
                        1 -> MomentumState.IMPROVING
                        2 -> MomentumState.STABLE
                        3 -> MomentumState.IMPROVING
                        else -> MomentumState.STABLE
                    }

                    val riskLevel = when (respondIndex) {
                        0 -> RiskLevel.HIGH
                        1 -> RiskLevel.MEDIUM
                        2 -> RiskLevel.MEDIUM
                        3 -> RiskLevel.LOW
                        else -> RiskLevel.MEDIUM
                    }

                    val timingState = when (respondIndex) {
                        0 -> TimingState.NOW
                        1 -> TimingState.SOON
                        2 -> TimingState.LATER
                        3 -> TimingState.SOON
                        else -> TimingState.SOON
                    }

                    val decision = GDMIESimpleDecision(
                        decisionText = """
                            LIFE OBSERVE:
                            $observe

                            LIFE CONNECT:
                            $connect

                            LIFE TRACE:
                            $trace

                            LIFE RESPOND:
                            $respond
                        """.trimIndent(),
                        currentSituation = currentSituation,
                        goalOutcome = goalLevel,
                        context = contextState,
                        momentum = momentumState,
                        risk = riskLevel,
                        timing = timingState
                    )

                    val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = GDMIEEngineGateway.calculate(input)

                            val confidencePercent =
                                (result.confidence * 100).toInt().coerceIn(0, 100)

                            val confidenceColor = when {
                                confidencePercent >= 80 ->
                                    Color.rgb(0, 255, 170)
                                confidencePercent >= 60 ->
                                    Color.rgb(0, 235, 255)
                                confidencePercent >= 40 ->
                                    Color.rgb(255, 210, 60)
                                else ->
                                    Color.rgb(255, 90, 110)
                            }

                            val analysisLayout =
                                LinearLayout(this@CosmosActivity).apply {
                                    orientation = LinearLayout.VERTICAL
                                    setPadding(
                                        dp(18),
                                        dp(8),
                                        dp(18),
                                        dp(8)
                                    )
                                }

                            val analysisText =
                                TextView(this@CosmosActivity).apply {
                                    text = """
                                        🌱 LIFE CONTEXT

                                        ✓ OBSERVE
                                        $observe

                                        ✓ CONNECT
                                        $connect

                                        ✓ TRACE
                                        $trace

                                        ✓ RESPOND
                                        $respond

                                        🧠 GDMIE SIGNAL

                                        ${result.decision}
                                    """.trimIndent()

                                    textSize = 16f
                                    setTextColor(Color.rgb(25, 30, 45))
                                    setPadding(0, dp(8), 0, dp(8))
                                }

                            val confidenceText =
                                TextView(this@CosmosActivity).apply {
                                    text = "CONFIDENCE: 0%"
                                    textSize = 24f
                                    setTypeface(
                                        null,
                                        android.graphics.Typeface.BOLD
                                    )
                                    gravity = Gravity.CENTER
                                    setTextColor(confidenceColor)
                                    setPadding(0, dp(18), 0, dp(8))
                                    alpha = 0.35f
                                }

                            analysisLayout.addView(
                                analysisText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            analysisLayout.addView(
                                confidenceText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            val analysisDialog =
                                AlertDialog.Builder(this@CosmosActivity)
                                    .setTitle("🧠 GDMIE ANALYSIS")
                                    .setView(analysisLayout)
                                    .setPositiveButton("DONE") { dialog, _ ->
                                        dialog.dismiss()

                                        val lifeRewardNode = SpaceNode(
                                            "🌱",
                                            "LIFE",
                                            0.50f,
                                            0.43f,
                                            48f,
                                            Color.rgb(80, 220, 150)
                                        )

                                        showSpaceReward(lifeRewardNode)
                                    }
                                    .create()

                            analysisDialog.setOnShowListener {
                                val animator =
                                    android.animation.ValueAnimator.ofInt(
                                        0,
                                        confidencePercent
                                    ).apply {
                                        duration = 1200L

                                        addUpdateListener { animation ->
                                            val value =
                                                animation.animatedValue as Int

                                            confidenceText.text =
                                                "CONFIDENCE: $value%"

                                            confidenceText.alpha =
                                                0.35f +
                                                    (value / 100f) * 0.65f

                                            val scale =
                                                0.96f +
                                                    (value / 100f) * 0.04f

                                            confidenceText.scaleX = scale
                                            confidenceText.scaleY = scale
                                        }
                                    }

                                animator.start()
                            }

                            analysisDialog.show()

                        } catch (e: Exception) {
                            AlertDialog.Builder(this@CosmosActivity)
                                .setTitle("🧠 GDMIE ANALYSIS")
                                .setMessage(
                                    "Analysis could not be completed.\n\n" +
                                        (e.message ?: "Unknown error")
                                )
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }

        content.addView(
            next,
            LinearLayout.LayoutParams(
                -1,
                dp(50)
            ).apply {
                topMargin = dp(6)
            }
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(80, 230, 150), true)
    }

    private fun showWaterInterpretation() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(2, 10, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        val phaseTitle = TextView(this).apply {
            text = "👁️  OBSERVE"
            textSize = 20f
            setTextColor(Color.rgb(80, 190, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val phaseCounter = TextView(this).apply {
            text = "WATER 4D INTERPRETATION • 01 / 04"
            textSize = 12f
            setTextColor(Color.rgb(150, 175, 200))
            gravity = Gravity.CENTER
            setPadding(0, dp(5), 0, dp(16))
        }

        val waterQuestionBanks = listOf(
            listOf(
                "What water signal should be examined first when conditions begin to change?",
                "What present water condition gives the clearest starting point?",
                "What recent water change deserves attention first?",
                "What signal best reveals the current water state?",
                "What water evidence is strongest right now?",
                "What part of the water condition should be observed first?",
                "What signal could show that water conditions are shifting?",
                "What observation would give the clearest water baseline?",
                "What water level or quality signal needs closer attention?",
                "What flow signal could reveal a change in the water system?"
            ),
            listOf(
                "What connection between rain and water matters most?",
                "How could land conditions influence the water situation?",
                "What relationship between water and life could explain the condition?",
                "How does human demand connect with the water system?",
                "Which water-related system should be connected to understand the change?",
                "What relationship could explain the strongest water signal?",
                "What interaction could amplify or reduce the water change?",
                "How could water conditions affect another Earth system?",
                "What connection between water flow and surrounding conditions matters?",
                "What wider water-system relationship should be examined?"
            ),
            listOf(
                "Where could the water change have started?",
                "How is the water change moving through the system?",
                "What effect could follow from the observed water change?",
                "How is the water cycle involved in the changing condition?",
                "What source could explain the strongest water signal?",
                "What path is the water change following?",
                "What transfer point could explain the observed change?",
                "What consequence could appear in another Earth system?",
                "What chain connects the source to the final water effect?",
                "What repeated water pattern could reveal the underlying process?"
            ),
            listOf(
                "What response could reduce the immediate water risk?",
                "How should the system adapt to the changing water condition?",
                "What could help restore the affected water system?",
                "What response could balance water, life and human needs?",
                "What action could improve water resilience?",
                "What should be protected before the water condition changes further?",
                "What adjustment could reduce pressure on the water system?",
                "What response could support recovery of the affected system?",
                "What action could balance immediate water needs with wider effects?",
                "What response could create a more stable water system?"
            )
        )

        val waterSessionQuestions = waterQuestionBanks.map { it.shuffled().first() }

        val questionText = TextView(this).apply {
            text = waterSessionQuestions[0]
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val hintText = TextView(this).apply {
            text =
                "Choose the signal that gives the clearest starting point."
            textSize = 13f
            setTextColor(Color.rgb(150, 175, 200))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(12))
        }

        val observeOptions = listOf(
            "A  WATER LEVEL\nMeasure the present level or volume",
            "B  WATER QUALITY\nCheck the condition of the water",
            "C  RECENT CHANGE\nIdentify what changed most strongly",
            "D  FLOW SIGNAL\nObserve movement, inflow or outflow"
        )

        val connectOptions = listOf(
            "A  RAIN + WATER\nConnect rainfall with water availability",
            "B  LAND + WATER\nTrace how land conditions affect water",
            "C  LIFE + WATER\nConnect water with living systems",
            "D  HUMAN + WATER\nTrace the relationship with human demand"
        )

        val traceOptions = listOf(
            "A  TRACE THE SOURCE\nFind where the change began",
            "B  TRACE THE FLOW\nFollow how the change moves",
            "C  TRACE THE EFFECT\nFollow what the change influences",
            "D  TRACE THE CYCLE\nFollow how the pattern develops over time"
        )

        val respondOptions = listOf(
            "A  PROTECT\nReduce the immediate water risk",
            "B  ADAPT\nAdjust to the changing water condition",
            "C  RESTORE\nSupport recovery of the affected system",
            "D  BALANCE\nConsider water, life and human needs together"
        )

        val displayObserveOptions = observeOptions.shuffled()
        val displayConnectOptions = connectOptions.shuffled()
        val displayTraceOptions = traceOptions.shuffled()
        val displayRespondOptions = respondOptions.shuffled()

        var phase = 0
        var selected = -1
        val waterAnswers = mutableListOf<String>()

        val buttons = mutableListOf<Button>()

        fun styleWaterButton(button: Button, selectedState: Boolean) {
            val drawable =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(
                        if (selectedState) {
                            Color.rgb(0, 105, 145)
                        } else {
                            Color.rgb(7, 25, 38)
                        }
                    )
                    setStroke(
                        dp(if (selectedState) 2 else 1),
                        if (selectedState) {
                            Color.rgb(0, 235, 255)
                        } else {
                            Color.rgb(35, 95, 125)
                        }
                    )
                }

            button.background = drawable
        }

        observeOptions.forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                textSize = 13f
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.WHITE)
                setPadding(dp(16), dp(4), dp(16), dp(4))
                styleWaterButton(this, false)

                setOnClickListener {
                    selected = index

                    buttons.forEachIndexed { i, b ->
                        styleWaterButton(b, i == selected)

                        b.animate()
                            .scaleX(if (i == selected) 1.015f else 1.0f)
                            .scaleY(if (i == selected) 1.015f else 1.0f)
                            .setDuration(140L)
                            .start()
                    }
                }
            }

            buttons.add(button)
        }

        // WATER UI ORDER:
        // TITLE → COUNTER → QUESTION → HINT → OPTIONS → NEXT
        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            phaseCounter,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val hologramView = createEarthHologramView("WATER")

        content.addView(
            hologramView,
            LinearLayout.LayoutParams(
                -1,
                dp(210)
            ).apply {
                setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(12)
                )
            }
        )


        content.addView(
            questionText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        content.addView(
            hintText,
            LinearLayout.LayoutParams(
                -1,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        buttons.forEach { button ->
            content.addView(
                button,
                LinearLayout.LayoutParams(
                    -1,
                    0,
                    1f
                ).apply {
                    topMargin = dp(3)
                    bottomMargin = dp(3)
                }
            )
        }

        val next = Button(this).apply {
            text = "NEXT  →  CONNECT"
            textSize = 14f
            isAllCaps = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(0, 95, 125))
        }

        next.setOnClickListener {
            if (selected < 0) {
                Toast.makeText(
                    this@CosmosActivity,
                    "SELECT AN OBSERVATION",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (phase) {
                0 -> {
                    waterAnswers.add(displayObserveOptions[selected])
                    phase = 1
                    selected = -1

                    phaseTitle.text = "🔗  CONNECT"
                    phaseCounter.text = "WATER 4D INTERPRETATION • 02 / 04"
                    questionText.text = waterSessionQuestions[1]
                    hintText.text =
                        "Trace the relationship beyond the water signal itself."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayConnectOptions[index]
                        styleWaterButton(button, false)
                    }

                    next.text = "NEXT  →  TRACE"
                }

                1 -> {
                    waterAnswers.add(displayConnectOptions[selected])
                    phase = 2
                    selected = -1

                    phaseTitle.text = "🧭  TRACE"
                    phaseCounter.text = "WATER 4D INTERPRETATION • 03 / 04"
                    questionText.text = waterSessionQuestions[2]
                    hintText.text =
                        "Follow the source, flow, effect or wider cycle."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayTraceOptions[index]
                        styleWaterButton(button, false)
                    }

                    next.text = "NEXT  →  RESPOND"
                }

                2 -> {
                    waterAnswers.add(displayTraceOptions[selected])
                    phase = 3
                    selected = -1

                    phaseTitle.text = "🧠  RESPOND"
                    phaseCounter.text = "WATER 4D INTERPRETATION • 04 / 04"
                    questionText.text = waterSessionQuestions[3]
                    hintText.text =
                        "Choose a response that considers the wider system."

                    buttons.forEachIndexed { index, button ->
                        button.text = displayRespondOptions[index]
                        styleWaterButton(button, false)
                    }

                    next.text = "COMPLETE  CONTEXT"
                }

                3 -> {
                    waterAnswers.add(displayRespondOptions[selected])

                    val observe = waterAnswers[0]
                    val connect = waterAnswers[1]
                    val trace = waterAnswers[2]
                    val respond = waterAnswers[3]

                    val observeIndex = observeOptions.indexOf(observe)
                    val connectIndex = connectOptions.indexOf(connect)
                    val traceIndex = traceOptions.indexOf(trace)
                    val respondIndex = respondOptions.indexOf(respond)

                    val currentSituation = when (observeIndex) {
                        0 -> CurrentState.STABLE
                        1 -> CurrentState.UNCERTAIN
                        2 -> CurrentState.WEAK
                        3 -> CurrentState.UNCERTAIN
                        else -> CurrentState.UNCERTAIN
                    }

                    val contextState = when (connectIndex) {
                        0 -> ContextState.FAVOURABLE
                        1 -> ContextState.NORMAL
                        2 -> ContextState.FAVOURABLE
                        3 -> ContextState.NORMAL
                        else -> ContextState.UNCERTAIN
                    }

                    val goalLevel = when (traceIndex) {
                        0 -> GoalLevel.SMALL_IMPROVEMENT
                        1 -> GoalLevel.MODERATE_IMPROVEMENT
                        2 -> GoalLevel.MAJOR_IMPROVEMENT
                        3 -> GoalLevel.LONG_TERM_TRANSFORMATION
                        else -> GoalLevel.MODERATE_IMPROVEMENT
                    }

                    val momentumState = when (traceIndex) {
                        0 -> MomentumState.DECLINING
                        1 -> MomentumState.IMPROVING
                        2 -> MomentumState.STABLE
                        3 -> MomentumState.IMPROVING
                        else -> MomentumState.STABLE
                    }

                    val riskLevel = when (respondIndex) {
                        0 -> RiskLevel.HIGH
                        1 -> RiskLevel.MEDIUM
                        2 -> RiskLevel.MEDIUM
                        3 -> RiskLevel.LOW
                        else -> RiskLevel.MEDIUM
                    }

                    val timingState = when (respondIndex) {
                        0 -> TimingState.NOW
                        1 -> TimingState.SOON
                        2 -> TimingState.LATER
                        3 -> TimingState.SOON
                        else -> TimingState.SOON
                    }

                    val decision = GDMIESimpleDecision(
                        decisionText = """
                            WATER OBSERVE:
                            $observe

                            WATER CONNECT:
                            $connect

                            WATER TRACE:
                            $trace

                            WATER RESPOND:
                            $respond
                        """.trimIndent(),
                        currentSituation = currentSituation,
                        goalOutcome = goalLevel,
                        context = contextState,
                        momentum = momentumState,
                        risk = riskLevel,
                        timing = timingState
                    )

                    val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

                    CoroutineScope(Dispatchers.Main).launch {
                        try {
                            val result = GDMIEEngineGateway.calculate(input)

                            val confidencePercent =
                                (result.confidence * 100).toInt().coerceIn(0, 100)

                            val confidenceColor = when {
                                confidencePercent >= 80 ->
                                    Color.rgb(0, 255, 170)
                                confidencePercent >= 60 ->
                                    Color.rgb(0, 235, 255)
                                confidencePercent >= 40 ->
                                    Color.rgb(255, 210, 60)
                                else ->
                                    Color.rgb(255, 90, 110)
                            }

                            val analysisLayout = LinearLayout(this@CosmosActivity).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(
                                    dp(18),
                                    dp(8),
                                    dp(18),
                                    dp(8)
                                )
                            }

                            val analysisText = TextView(this@CosmosActivity).apply {
                                text = """
                                    💧 WATER CONTEXT

                                    ✓ OBSERVE
                                    $observe

                                    ✓ CONNECT
                                    $connect

                                    ✓ TRACE
                                    $trace

                                    ✓ RESPOND
                                    $respond

                                    🧠 GDMIE SIGNAL

                                    ${result.decision}
                                """.trimIndent()
                                textSize = 16f
                                setTextColor(Color.rgb(25, 30, 45))
                                setPadding(0, dp(8), 0, dp(8))
                            }

                            val confidenceText = TextView(this@CosmosActivity).apply {
                                text = "CONFIDENCE: 0%"
                                textSize = 24f
                                setTypeface(null, android.graphics.Typeface.BOLD)
                                gravity = Gravity.CENTER
                                setTextColor(confidenceColor)
                                setPadding(0, dp(18), 0, dp(8))
                                alpha = 0.35f
                            }

                            analysisLayout.addView(
                                analysisText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            analysisLayout.addView(
                                confidenceText,
                                LinearLayout.LayoutParams(
                                    -1,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            val analysisDialog =
                                AlertDialog.Builder(this@CosmosActivity)
                                    .setTitle("🧠 GDMIE ANALYSIS")
                                    .setView(analysisLayout)
                                    .setPositiveButton("DONE") { dialog, _ ->
                                        dialog.dismiss()

                                        val waterRewardNode = SpaceNode(
                                            "💧",
                                            "WATER",
                                            0.80f,
                                            0.38f,
                                            46f,
                                            Color.rgb(80, 150, 255)
                                        )

                                        showSpaceReward(waterRewardNode)
                                    }
                                    .create()

                            analysisDialog.setOnShowListener {
                                val start = 0
                                val end = confidencePercent

                                val animator =
                                    android.animation.ValueAnimator.ofInt(start, end).apply {
                                        duration = 1200L
                                        addUpdateListener { animation ->
                                            val value =
                                                animation.animatedValue as Int

                                            confidenceText.text =
                                                "CONFIDENCE: $value%"

                                            confidenceText.alpha =
                                                0.35f + (value / 100f) * 0.65f

                                            val scale =
                                                0.96f + (value / 100f) * 0.04f

                                            confidenceText.scaleX = scale
                                            confidenceText.scaleY = scale
                                        }
                                    }

                                animator.start()
                            }

                            analysisDialog.show()

                        } catch (e: Exception) {
                            AlertDialog.Builder(this@CosmosActivity)
                                .setTitle("🧠 GDMIE ANALYSIS")
                                .setMessage(
                                    "Analysis could not be completed.\n\n" +
                                    (e.message ?: "Unknown error")
                                )
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
            }
        }

        val spacer = Space(this)

        content.addView(
            spacer,
            LinearLayout.LayoutParams(
                -1,
                dp(10)
            )
        )

        content.addView(
            next,
            LinearLayout.LayoutParams(
                -1,
                dp(54)
            ).apply {
                topMargin = dp(14)
            }
        )

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            addView(content)
        }

        root.addView(
            scroll,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog = AlertDialog.Builder(this)
            .setView(root)
            .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(80, 170, 255), true)
    }

    private fun showEarthSystemTheory() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(2, 8, 16))
        }

        val hologram = object : View(this) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private var rotation = 0f
            private val handler = Handler(Looper.getMainLooper())

            private val animator = object : Runnable {
                override fun run() {
                    rotation += 0.8f
                    invalidate()
                    handler.postDelayed(this, 40L)
                }
            }

            init {
                handler.post(animator)
            }

            override fun onDetachedFromWindow() {
                handler.removeCallbacks(animator)
                super.onDetachedFromWindow()
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)

                val cx = width / 2f
                val cy = height * 0.43f
                val r = minOf(width, height) * 0.20f

                // Deep holographic atmosphere
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(3, 20, 32)
                canvas.drawCircle(cx, cy, r * 1.12f, paint)

                // Earth core
                paint.color = Color.rgb(8, 48, 68)
                canvas.drawCircle(cx, cy, r, paint)

                // Latitude / longitude hologram
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(1).toFloat()
                paint.color = Color.argb(150, 70, 220, 255)

                canvas.drawOval(
                    cx - r,
                    cy - r * 0.32f,
                    cx + r,
                    cy + r * 0.32f,
                    paint
                )

                canvas.drawOval(
                    cx - r,
                    cy - r * 0.65f,
                    cx + r,
                    cy + r * 0.65f,
                    paint
                )

                canvas.drawOval(
                    cx - r * 0.45f,
                    cy - r,
                    cx + r * 0.45f,
                    cy + r,
                    paint
                )

                // Rotating holographic meridian
                val sweep = (rotation % 360f)
                val scale = kotlin.math.cos(Math.toRadians(sweep.toDouble())).toFloat()

                canvas.save()
                canvas.scale(
                    kotlin.math.max(0.08f, kotlin.math.abs(scale)),
                    1f,
                    cx,
                    cy
                )

                paint.color = Color.argb(210, 90, 235, 255)
                canvas.drawOval(
                    cx - r,
                    cy - r,
                    cx + r,
                    cy + r,
                    paint
                )
                canvas.restore()

                // Energy rings
                paint.color = Color.argb(120, 170, 100, 255)
                canvas.drawOval(
                    cx - r * 1.32f,
                    cy - r * 0.34f,
                    cx + r * 1.32f,
                    cy + r * 0.34f,
                    paint
                )

                paint.color = Color.argb(100, 70, 220, 255)
                canvas.drawOval(
                    cx - r * 1.48f,
                    cy - r * 0.48f,
                    cx + r * 1.48f,
                    cy + r * 0.48f,
                    paint
                )

                // Central core
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawCircle(cx, cy, dp(4).toFloat(), paint)

                // Holographic particles
                for (i in 0 until 18) {
                    val angle = Math.toRadians(
                        ((i * 20f + rotation * 0.7f) % 360f).toDouble()
                    )
                    val distance = r * (1.35f + (i % 4) * 0.12f)
                    val px = cx + kotlin.math.cos(angle).toFloat() * distance
                    val py = cy + kotlin.math.sin(angle).toFloat() * distance * 0.55f

                    paint.color = if (i % 2 == 0) {
                        Color.argb(180, 80, 220, 255)
                    } else {
                        Color.argb(150, 180, 120, 255)
                    }

                    canvas.drawCircle(px, py, dp(2).toFloat(), paint)
                }
            }
        }

        root.addView(
            hologram,
            FrameLayout.LayoutParams(
                -1,
                dp(310)
            ).apply {
                topMargin = dp(70)
            }
        )

        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(18), dp(22), dp(18))
        }

        fun addText(text: String, size: Float, color: Int) {
            val tv = TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, dp(4))
            }

            overlay.addView(
                tv,
                LinearLayout.LayoutParams(
                    -1,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }

        addText("🌍  EARTH SYSTEM", 27f, Color.WHITE)
        addText("4D HOLOGRAPHIC EARTH", 13f, Color.rgb(80, 220, 255))

        val spacer = Space(this)
        overlay.addView(
            spacer,
            LinearLayout.LayoutParams(-1, dp(265))
        )

        fun addLayer(icon: String, title: String, subtitle: String, color: Int) {
            val card = TextView(this).apply {
                text = "$icon  $title\n$subtitle"
                textSize = 15f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(18), dp(8), dp(18), dp(8))
                setBackgroundColor(Color.argb(45, Color.red(color), Color.green(color), Color.blue(color)))
            }

            overlay.addView(
                card,
                LinearLayout.LayoutParams(-1, dp(62)).apply {
                    topMargin = dp(7)
                }
            )
        }

        addLayer("👁️", "OBSERVE", "See the present Earth state", Color.rgb(70, 220, 255))
        addLayer("🔗", "CONNECT", "See how Earth systems interact", Color.rgb(170, 100, 255))
        addLayer("🧭", "TRACE", "Follow change through the system", Color.rgb(255, 205, 80))
        addLayer("🧠", "RESPOND", "Build a decision from the context", Color.rgb(90, 230, 170))

        val continueButton = Button(this).apply {
            text = "CONTINUE  →  EARTH INTERPRETATION"
            textSize = 15f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            isAllCaps = false
            includeFontPadding = false
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            minHeight = 0
            minimumHeight = 0
            setPadding(dp(12), 0, dp(12), 0)

            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = dp(14).toFloat()
                setColor(Color.rgb(10, 45, 58))
                setStroke(
                    dp(1),
                    Color.rgb(70, 220, 255)
                )
            }

            setOnClickListener {
                showEarthSystemInterpretation()
            }
        }

        overlay.addView(
            continueButton,
            LinearLayout.LayoutParams(-1, dp(56)).apply {
                topMargin = dp(14)
            }
        )

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            addView(overlay)
        }

        root.addView(
            scroll,
            FrameLayout.LayoutParams(
                -1,
                -1
            )
        )

        AlertDialog.Builder(this)
            .setView(root)
            .create()
            .apply {
                show()
                styleCinematicDialog(
                    this,
                    Color.rgb(70, 220, 255),
                    false
                )
            }
    }

    private fun showEarthSystemInterpretation() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(1, 7, 14))
        }

        val hologram = object : View(this) {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val handler = Handler(Looper.getMainLooper())
            private var rotation = 0f
            private var pulse = 0f

            private val ticker = object : Runnable {
                override fun run() {
                    rotation += 0.65f
                    pulse += 0.055f
                    invalidate()
                    handler.postDelayed(this, 40L)
                }
            }

            init {
                handler.post(ticker)
            }

            override fun onDetachedFromWindow() {
                handler.removeCallbacks(ticker)
                super.onDetachedFromWindow()
            }

            override fun onDraw(canvas: Canvas) {
                super.onDraw(canvas)

                val cx = width / 2f
                val cy = height * 0.47f
                val r = minOf(width, height) * 0.205f

                // Atmospheric hologram glow
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(3, 25, 43)
                canvas.drawCircle(cx, cy, r * 1.22f, paint)

                paint.color = Color.rgb(5, 48, 72)
                canvas.drawCircle(cx, cy, r, paint)

                // Ocean scan bands
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(1).toFloat()
                paint.color = Color.argb(175, 70, 220, 255)

                canvas.drawOval(
                    cx - r,
                    cy - r * 0.30f,
                    cx + r,
                    cy + r * 0.30f,
                    paint
                )

                canvas.drawOval(
                    cx - r,
                    cy - r * 0.62f,
                    cx + r,
                    cy + r * 0.62f,
                    paint
                )

                // Rotating longitude
                val angle = Math.toRadians(rotation.toDouble())
                val longitudeScale =
                    kotlin.math.abs(kotlin.math.cos(angle).toFloat())
                        .coerceAtLeast(0.06f)

                canvas.save()
                canvas.scale(longitudeScale, 1f, cx, cy)

                canvas.drawOval(
                    cx - r,
                    cy - r,
                    cx + r,
                    cy + r,
                    paint
                )

                canvas.restore()

                // Stylized holographic continents
                paint.style = Paint.Style.FILL
                paint.color = Color.argb(185, 70, 220, 150)

                val land = Path()

                // North / central landmass
                land.moveTo(cx - r * 0.72f, cy - r * 0.34f)
                land.cubicTo(
                    cx - r * 0.55f, cy - r * 0.58f,
                    cx - r * 0.20f, cy - r * 0.62f,
                    cx + r * 0.02f, cy - r * 0.43f
                )
                land.cubicTo(
                    cx + r * 0.12f, cy - r * 0.28f,
                    cx - r * 0.04f, cy - r * 0.14f,
                    cx - r * 0.26f, cy - r * 0.17f
                )
                land.cubicTo(
                    cx - r * 0.45f, cy - r * 0.12f,
                    cx - r * 0.62f, cy - r * 0.20f,
                    cx - r * 0.72f, cy - r * 0.34f
                )
                land.close()

                // South / east landmass
                val land2 = Path()
                land2.moveTo(cx + r * 0.02f, cy - r * 0.12f)
                land2.cubicTo(
                    cx + r * 0.27f, cy - r * 0.20f,
                    cx + r * 0.58f, cy - r * 0.08f,
                    cx + r * 0.70f, cy + r * 0.14f
                )
                land2.cubicTo(
                    cx + r * 0.53f, cy + r * 0.24f,
                    cx + r * 0.44f, cy + r * 0.43f,
                    cx + r * 0.23f, cy + r * 0.55f
                )
                land2.cubicTo(
                    cx + r * 0.08f, cy + r * 0.38f,
                    cx + r * 0.13f, cy + r * 0.17f,
                    cx + r * 0.02f, cy - r * 0.12f
                )
                land2.close()

                canvas.drawPath(land, paint)
                canvas.drawPath(land2, paint)

                // Bright Earth rim
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(2).toFloat()
                paint.color = Color.argb(220, 80, 230, 255)

                canvas.drawCircle(cx, cy, r, paint)

                // Rotating orbital rings
                paint.strokeWidth = dp(1).toFloat()
                paint.color = Color.argb(125, 180, 100, 255)

                canvas.drawOval(
                    cx - r * 1.48f,
                    cy - r * 0.34f,
                    cx + r * 1.48f,
                    cy + r * 0.34f,
                    paint
                )

                paint.color = Color.argb(95, 70, 225, 255)

                canvas.drawOval(
                    cx - r * 1.60f,
                    cy - r * 0.50f,
                    cx + r * 1.60f,
                    cy + r * 0.50f,
                    paint
                )

                // Scan pulse
                val scan = (pulse % 2f) / 2f

                paint.color = Color.argb(
                    (145f * (1f - scan)).toInt(),
                    70,
                    235,
                    255
                )

                canvas.drawCircle(
                    cx,
                    cy,
                    r * (0.72f + scan * 0.52f),
                    paint
                )

                // Core light
                paint.style = Paint.Style.FILL
                paint.color = Color.WHITE
                canvas.drawCircle(cx, cy, dp(3).toFloat(), paint)

                // Data particles
                for (i in 0 until 24) {
                    val a = Math.toRadians(
                        ((i * 15f + rotation * 1.2f) % 360f).toDouble()
                    )
                    val d = r * (1.28f + (i % 5) * 0.09f)

                    val px = cx + kotlin.math.cos(a).toFloat() * d
                    val py = cy + kotlin.math.sin(a).toFloat() * d * 0.55f

                    paint.color =
                        if (i % 3 == 0)
                            Color.argb(190, 180, 110, 255)
                        else
                            Color.argb(185, 70, 225, 255)

                    canvas.drawCircle(px, py, dp(2).toFloat(), paint)
                }
            }
        }

        root.addView(
            hologram,
            FrameLayout.LayoutParams(
                -1,
                dp(330)
            ).apply {
                topMargin = dp(38)
            }
        )

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(14), dp(18), dp(24))
        }

        fun addText(value: String, size: Float, color: Int) {
            val tv = TextView(this).apply {
                text = value
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
            }

            content.addView(
                tv,
                LinearLayout.LayoutParams(
                    -1,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(5)
                }
            )
        }

        val phaseTitle = TextView(this).apply {
            text = "👁️  OBSERVE"
            textSize = 24f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        content.addView(
            phaseTitle,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        val phaseCounter = TextView(this).apply {
            text = "EARTH 4D INTERPRETATION • 01 / 04"
            textSize = 12f
            setTextColor(cyan)
            gravity = Gravity.CENTER
        }

        content.addView(
            phaseCounter,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        content.addView(
            Space(this),
            LinearLayout.LayoutParams(-1, dp(292))
        )

        val earthQuestionBanks = listOf(
            listOf(
                "What Earth signal should be examined first when conditions begin to change?",
                "What present Earth condition gives the clearest starting point?",
                "What recent environmental change deserves attention first?",
                "What visible signal could reveal the current state of the Earth system?",
                "What condition should be compared before interpreting the change?",
                "What environmental evidence is strongest right now?",
                "What part of the current Earth condition is most important to observe?",
                "What signal could show that the system is shifting?",
                "What observation would give the clearest baseline?",
                "What wider environmental sign could confirm the initial change?"
            ),
            listOf(
                "Which Earth systems should be connected to understand the change?",
                "What relationship between Earth systems could explain the observed condition?",
                "How could air and water influence the situation together?",
                "What connection between water and life could matter here?",
                "How might land and energy interact with the observed change?",
                "Which Earth systems could amplify or reduce the signal?",
                "What system connection could reveal a wider effect?",
                "How could multiple Earth systems respond to the same change?",
                "What interaction could be hidden behind the visible signal?",
                "What broader Earth-system relationship should be examined?"
            ),
            listOf(
                "Where could the Earth-system change have started?",
                "What effect could follow from the observed environmental change?",
                "What pattern should be traced across the changing system?",
                "What connection could reveal how the change spreads?",
                "What source could explain the strongest signal?",
                "What consequence might appear in another Earth system?",
                "How could the change develop over time?",
                "What chain of effects could connect the first signal to the wider system?",
                "What repeated pattern could reveal the underlying process?",
                "What link between source and effect deserves closer attention?"
            ),
            listOf(
                "What response could best protect the affected Earth system?",
                "How should the system adapt to the changing condition?",
                "What could help restore the affected part of the Earth system?",
                "What response could balance immediate and wider effects?",
                "What action could reduce the strongest environmental risk?",
                "What response could support recovery without creating another imbalance?",
                "What should be protected before the situation develops further?",
                "What adaptation could reduce pressure on the wider system?",
                "What restoration step could address the underlying condition?",
                "What balanced response could consider multiple Earth systems?"
            )
        )

        val earthSessionQuestions = earthQuestionBanks.map { it.shuffled().first() }

        val questionText = TextView(this).apply {
            text = earthSessionQuestions[0]
            textSize = 18f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        content.addView(
            questionText,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        val hintText = TextView(this).apply {
            text = "Select the observation that gives the clearest starting point."
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
        }

        content.addView(
            hintText,
            LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply { bottomMargin = dp(5) }
        )

        val observeOptions = listOf(
            "A  PRESENT CONDITION\nMeasure what is happening now",
            "B  EXPECTED STATE\nCompare it with the normal pattern",
            "C  RECENT CHANGE\nFind what changed most strongly",
            "D  WIDER SIGNAL\nLook for connected environmental signs"
        ).shuffled()

        val connectOptions = listOf(
            "A  AIR + WATER\nCheck how atmosphere and water influence each other",
            "B  WATER + LIFE\nTrace how water conditions affect living systems",
            "C  LAND + ENERGY\nConnect surface change with energy flow",
            "D  ALL SYSTEMS\nLook for the strongest connection across Earth"
        ).shuffled()

        val traceOptions = listOf(
            "A  FOLLOW THE SOURCE\nTrace where the change began",
            "B  FOLLOW THE EFFECT\nTrace what the change could influence next",
            "C  FOLLOW THE PATTERN\nCompare how the change develops over time",
            "D  FOLLOW THE LINKS\nTrace the wider Earth-system connection"
        ).shuffled()

        val respondOptions = listOf(
            "A  PROTECT\nReduce the strongest immediate risk",
            "B  ADAPT\nAdjust to the changing condition",
            "C  RESTORE\nHelp the affected system recover",
            "D  BALANCE\nChoose a response that considers the whole system"
        ).shuffled()

        var phase = 0
        var selected = -1
        val earthAnswers = mutableListOf<String>()

        val buttons = mutableListOf<Button>()

        observeOptions.forEachIndexed { index, option ->
            val button = Button(this).apply {
                text = option
                textSize = 13f
                isAllCaps = false
                gravity = Gravity.CENTER_VERTICAL
                setTextColor(Color.WHITE)
                setPadding(dp(16), dp(4), dp(16), dp(4))
                setBackgroundColor(Color.rgb(7, 25, 38))

                setOnClickListener {
                    selected = index

                    buttons.forEachIndexed { i, b ->
                        b.setBackgroundColor(
                            if (i == selected)
                                Color.rgb(0, 105, 135)
                            else
                                Color.rgb(7, 25, 38)
                        )
                    }
                }
            }

            buttons.add(button)

            content.addView(
                button,
                LinearLayout.LayoutParams(-1, dp(60)).apply {
                    topMargin = dp(6)
                }
            )
        }

        val next = Button(this)
        next.text = "NEXT  →  CONNECT"
        next.textSize = 14f
        next.isAllCaps = false
        next.setTextColor(Color.WHITE)
        next.setBackgroundColor(Color.rgb(0, 95, 125))

        next.setOnClickListener {
            if (selected < 0) {
                Toast.makeText(
                    this@CosmosActivity,
                    "SELECT AN OBSERVATION",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            when (phase) {

                0 -> {
                    earthAnswers.add(observeOptions[selected])
                    phase = 1
                    selected = -1

                    phaseTitle.text = "🔗  CONNECT"
                    phaseCounter.text = "EARTH 4D INTERPRETATION • 02 / 04"

                    questionText.text =
                        earthSessionQuestions[1]

                    hintText.text =
                        "Look beyond one signal and trace the relationship."

                    buttons.forEachIndexed { index, button ->
                        button.text = connectOptions[index]
                        button.setBackgroundColor(Color.rgb(7, 25, 38))
                    }

                    next.text = "NEXT  →  TRACE"
                }

                1 -> {
                    earthAnswers.add(connectOptions[selected])
                    phase = 2
                    selected = -1

                    phaseTitle.text = "🧭  TRACE"
                    phaseCounter.text = "EARTH 4D INTERPRETATION • 03 / 04"

                    questionText.text =
                        earthSessionQuestions[2]

                    hintText.text =
                        "Follow the source, effect, pattern or wider connection."

                    buttons.forEachIndexed { index, button ->
                        button.text = traceOptions[index]
                        button.setBackgroundColor(Color.rgb(7, 25, 38))
                    }

                    next.text = "NEXT  →  RESPOND"
                }

                2 -> {
                    earthAnswers.add(traceOptions[selected])
                    phase = 3
                    selected = -1

                    phaseTitle.text = "🧠  RESPOND"
                    phaseCounter.text = "EARTH 4D INTERPRETATION • 04 / 04"

                    questionText.text =
                        earthSessionQuestions[3]

                    hintText.text =
                        "Choose a response that considers both the change and its wider effects."

                    buttons.forEachIndexed { index, button ->
                        button.text = respondOptions[index]
                        button.setBackgroundColor(Color.rgb(7, 25, 38))
                    }

                    next.text = "COMPLETE  CONTEXT"
                }

                  3 -> {
                      earthAnswers.add(respondOptions[selected])

                      val observe = earthAnswers.getOrNull(0) ?: ""
                      val connect = earthAnswers.getOrNull(1) ?: ""
                      val trace = earthAnswers.getOrNull(2) ?: ""
                      val respond = earthAnswers.getOrNull(3) ?: ""

                      val observeIndex = observeOptions.indexOf(observe)
                      val connectIndex = connectOptions.indexOf(connect)
                      val traceIndex = traceOptions.indexOf(trace)
                      val respondIndex = respondOptions.indexOf(respond)

                      val currentSituation = when (observeIndex) {
                          0 -> CurrentState.STABLE
                          1 -> CurrentState.UNCERTAIN
                          2 -> CurrentState.WEAK
                          3 -> CurrentState.UNCERTAIN
                          else -> CurrentState.UNCERTAIN
                      }

                      val contextState = when (connectIndex) {
                          0 -> ContextState.NORMAL
                          1 -> ContextState.FAVOURABLE
                          2 -> ContextState.NORMAL
                          3 -> ContextState.FAVOURABLE
                          else -> ContextState.UNCERTAIN
                      }

                      val goalLevel = when (traceIndex) {
                          0 -> GoalLevel.SMALL_IMPROVEMENT
                          1 -> GoalLevel.MODERATE_IMPROVEMENT
                          2 -> GoalLevel.MAJOR_IMPROVEMENT
                          3 -> GoalLevel.LONG_TERM_TRANSFORMATION
                          else -> GoalLevel.MODERATE_IMPROVEMENT
                      }

                      val momentumState = when (traceIndex) {
                          0 -> MomentumState.DECLINING
                          1 -> MomentumState.IMPROVING
                          2 -> MomentumState.STABLE
                          3 -> MomentumState.IMPROVING
                          else -> MomentumState.STABLE
                      }

                      val riskLevel = when (respondIndex) {
                          0 -> RiskLevel.HIGH
                          1 -> RiskLevel.MEDIUM
                          2 -> RiskLevel.MEDIUM
                          3 -> RiskLevel.LOW
                          else -> RiskLevel.MEDIUM
                      }

                      val timingState = when (respondIndex) {
                          0 -> TimingState.NOW
                          1 -> TimingState.SOON
                          2 -> TimingState.LATER
                          3 -> TimingState.SOON
                          else -> TimingState.SOON
                      }

                      val decision = GDMIESimpleDecision(
                          decisionText = """
                              EARTH OBSERVE:
                              $observe

                              EARTH CONNECT:
                              $connect

                              EARTH TRACE:
                              $trace

                              EARTH RESPOND:
                              $respond
                          """.trimIndent(),
                          currentSituation = currentSituation,
                          goalOutcome = goalLevel,
                          context = contextState,
                          momentum = momentumState,
                          risk = riskLevel,
                          timing = timingState
                      )

                      val input = GDMIESimpleDecisionAdapter.toGDMInput(decision)

                      CoroutineScope(Dispatchers.Main).launch {
                          try {
                              val result = GDMIEEngineGateway.calculate(input)

                              val analysis = StringBuilder()

                              analysis.append("🌍 EARTH\n\n")
                              analysis.append("CONTEXT ANALYSIS\n\n")

                              analysis.append("✓ OBSERVE\n")
                              analysis.append(observe)

                              analysis.append("\n\n✓ CONNECT\n")
                              analysis.append(connect)

                              analysis.append("\n\n✓ TRACE\n")
                              analysis.append(trace)

                              analysis.append("\n\n✓ RESPOND\n")
                              analysis.append(respond)

                              analysis.append("\n\n🧠 GDMIE SIGNAL\n\n")
                              analysis.append(result.decision)

                              val confidencePercent =
                                  (result.confidence * 100).toInt().coerceIn(0, 100)

                              val confidenceColor = when {
                                  confidencePercent >= 80 ->
                                      Color.rgb(0, 255, 170)
                                  confidencePercent >= 60 ->
                                      Color.rgb(0, 235, 255)
                                  confidencePercent >= 40 ->
                                      Color.rgb(255, 210, 60)
                                  else ->
                                      Color.rgb(255, 90, 110)
                              }

                              val analysisLayout =
                                  android.widget.LinearLayout(this@CosmosActivity).apply {
                                      orientation =
                                          android.widget.LinearLayout.VERTICAL
                                      setPadding(
                                          dp(24),
                                          dp(20),
                                          dp(24),
                                          dp(12)
                                      )
                                  }

                              val analysisText =
                                  TextView(this@CosmosActivity).apply {
                                      text = analysis.toString()
                                      textSize = 15f
                                      setTextColor(Color.rgb(25, 30, 45))
                                  }

                              val confidenceText =
                                  TextView(this@CosmosActivity).apply {
                                      text = "CONFIDENCE: 0%"
                                      textSize = 24f
                                      setTypeface(
                                          null,
                                          android.graphics.Typeface.BOLD
                                      )
                                      gravity = Gravity.CENTER
                                      setTextColor(confidenceColor)
                                      setPadding(0, dp(18), 0, dp(8))
                                      alpha = 0.35f
                                  }

                              analysisLayout.addView(analysisText)
                              analysisLayout.addView(confidenceText)

                              val analysisDialog =
                                  AlertDialog.Builder(this@CosmosActivity)
                                      .setTitle("🧠 GDMIE ANALYSIS")
                                      .setView(analysisLayout)
                                      .setNeutralButton("🎁 WATCH +10 XP") { _, _ ->
                                          RewardedAdManager.show(this@CosmosActivity) { awarded ->
                                              Toast.makeText(
                                                  this@CosmosActivity,
                                                  "+$awarded XP earned!",
                                                  Toast.LENGTH_SHORT
                                              ).show()
                                          }
                                      }
                                      .setPositiveButton("DONE") { dialog, _ ->
                                          dialog.dismiss()

                                          val earthRewardNode = SpaceNode(
                                              "🌍",
                                              "EARTH",
                                              0.50f,
                                              0.43f,
                                              48f,
                                              Color.rgb(80, 220, 150)
                                          )

                                          showSpaceReward(earthRewardNode)
                                      }
                                      .create()

                              analysisDialog.setOnShowListener {
                                  confidenceText.animate()
                                      .alpha(1f)
                                      .setDuration(350L)
                                      .withEndAction {
                                          val animator =
                                              android.animation.ValueAnimator.ofInt(
                                                  0,
                                                  confidencePercent
                                              )

                                          animator.duration = 1200L

                                          animator.addUpdateListener { valueAnimator ->
                                              val value =
                                                  valueAnimator.animatedValue as Int

                                              confidenceText.text =
                                                  "CONFIDENCE: $value%"

                                              val pulse =
                                                  1f + (value / 100f) * 0.08f

                                              confidenceText.scaleX = pulse
                                              confidenceText.scaleY = pulse
                                          }

                                          animator.start()
                                      }
                              }

                              analysisDialog.show()

                          } catch (e: Exception) {
                              AlertDialog.Builder(this@CosmosActivity)
                                  .setTitle("GDMIE ANALYSIS")
                                  .setMessage(
                                      "Analysis could not be completed.\n\n" +
                                          (e.message ?: "Please try again.")
                                  )
                                  .setPositiveButton("OK", null)
                                  .show()
                          }
                      }
                  }
            }
        }

        content.addView(
            next,
            LinearLayout.LayoutParams(-1, dp(44)).apply {
                topMargin = dp(14)
            }
        )

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            addView(content)
        }

        root.addView(
            scroll,
            FrameLayout.LayoutParams(-1, -1)
        )

val dialog = AlertDialog.Builder(this)
    .setView(root)
    .create()

dialog.show()

styleCinematicDialog(
    dialog,
    Color.rgb(70, 220, 255),
    true
)
    }

    private fun showTimeLayer() {

        // ⏳ TIME — switch ambient audio

        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(5, 6, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(30), dp(22), dp(22))
        }

        val icon = TextView(this).apply {
            text = "⏳"
            textSize = 52f
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(6))
        }

        val title = TextView(this).apply {
            text = "TIME MACHINE"
            textSize = 27f
            setTextColor(Color.rgb(170, 220, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "PAST • NOW • FUTURE"
            textSize = 13f
            setTextColor(Color.rgb(150, 185, 220))
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(22))
        }

        val description = TextView(this).apply {
            text =
                "Time reveals how decisions change across moments.\n\n" +
                "Explore what happened, understand what is happening now, " +
                "and examine what could develop next."
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setLineSpacing(0f, 1.15f)
            setPadding(dp(8), dp(10), dp(8), dp(24))
        }

        val pastButton = Button(this).apply {
            text = "⏪  PAST"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
        }

        val nowButton = Button(this).apply {
            text = "●  NOW"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
        }

        val futureButton = Button(this).apply {
            text = "⏩  FUTURE"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
        }

        fun styleTimeButton(button: Button, color: Int) {
            button.background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(15, 18, 34))
                    setStroke(dp(1), color)
                }
        }

        styleTimeButton(pastButton, Color.rgb(120, 150, 255))
        styleTimeButton(nowButton, Color.rgb(0, 235, 255))
        styleTimeButton(futureButton, Color.rgb(200, 150, 255))

        pastButton.setOnClickListener {
            showTimeInterpretation("PAST")
        }

        nowButton.setOnClickListener {
            showTimeInterpretation("NOW")
        }

        futureButton.setOnClickListener {
            showTimeInterpretation("FUTURE")
        }

        content.addView(icon, LinearLayout.LayoutParams(-1, dp(76)))
        content.addView(title, LinearLayout.LayoutParams(-1, dp(42)))
        content.addView(subtitle, LinearLayout.LayoutParams(-1, dp(46)))
        content.addView(description, LinearLayout.LayoutParams(-1, LinearLayout.LayoutParams.WRAP_CONTENT))

        content.addView(
            pastButton,
            LinearLayout.LayoutParams(-1, dp(58)).apply {
                bottomMargin = dp(12)
            }
        )

        content.addView(
            nowButton,
            LinearLayout.LayoutParams(-1, dp(58)).apply {
                bottomMargin = dp(12)
            }
        )

        content.addView(
            futureButton,
            LinearLayout.LayoutParams(-1, dp(58))
        )

        root.addView(
            content,
            FrameLayout.LayoutParams(-1, -1)
        )

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .setNegativeButton("← BACK", null)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(0, 235, 255))
    }

    private fun showTimeInterpretation(selectedTime: String) {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(5, 6, 18))
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(18), dp(20), dp(18))
        }

        val phaseTitle = TextView(this).apply {
            text = "OBSERVE"
            textSize = 12f
            setTextColor(Color.rgb(0, 235, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val counter = TextView(this).apply {
            text = "$selectedTime 4D TIME INTERPRETATION • 01 / 04"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, dp(18))
        }

        val question = TextView(this).apply {
            text = when (selectedTime) {
                "PAST" ->
                    "What should you examine first to understand what already happened?"
                "NOW" ->
                    "What should you observe first to understand the present moment?"
                else ->
                    "What should you examine first to understand what may develop next?"
            }
            textSize = 20f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setLineSpacing(0f, 1.12f)
        }

        val hint = TextView(this).apply {
            text = "Choose the strongest time signal."
            textSize = 13f
            setTextColor(Color.rgb(165, 180, 205))
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(10), dp(8), dp(18))
        }

        val optionsContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val nextButton = Button(this).apply {
            text = "NEXT  →"
            textSize = 15f
            isAllCaps = false
            setTextColor(Color.WHITE)
            background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(16).toFloat()
                    setColor(Color.rgb(20, 30, 50))
                    setStroke(dp(1), Color.rgb(0, 235, 255))
                }
        }

        val observeOptions = listOf(
            "A  KEY EVENT\nIdentify the event that changed the direction",
            "B  CONDITION\nMeasure the condition at that moment",
            "C  DECISION\nFind the decision that shaped the outcome",
            "D  SIGNAL\nIdentify the strongest evidence from that time"
        )

        val connectOptions = listOf(
            "A  EVENT + DECISION\nConnect what happened with the choice made",
            "B  CONDITION + EFFECT\nConnect the situation with its result",
            "C  PEOPLE + DECISION\nConnect people with the decision context",
            "D  SIGNALS + PATTERN\nConnect multiple signals into one pattern"
        )

        val traceOptions = listOf(
            "A  TRACE BACK\nFollow the chain toward its origin",
            "B  TRACE FORWARD\nFollow the next consequence",
            "C  TRACE CHANGE\nFollow how the situation shifted",
            "D  TRACE PATTERN\nFollow how the pattern developed across time"
        )

        val respondOptions = listOf(
            "A  LEARN\nUse the time signal to improve the next decision",
            "B  ACT NOW\nUse the present information immediately",
            "C  PREPARE\nBuild a response for the next possible change",
            "D  BALANCE\nCombine past evidence, present context and future direction"
        )

        val optionSets = listOf(
            observeOptions,
            connectOptions,
            traceOptions,
            respondOptions
        )

        val displayOptionSets = listOf(
            observeOptions.shuffled(),
            connectOptions.shuffled(),
            traceOptions.shuffled(),
            respondOptions.shuffled()
        )

        val timeQuestionBanks = when (selectedTime) {
            "PAST" -> listOf(
                listOf(
                    "What past signal should be examined first to understand what already happened?",
                    "Which earlier condition gives the clearest starting point?",
                    "What event from the past deserves attention first?",
                    "What evidence best reveals the earlier situation?",
                    "Which past change created the strongest signal?",
                    "What condition should be compared with the earlier state?",
                    "What earlier decision could explain the present result?",
                    "Which historical signal gives the strongest baseline?",
                    "What past evidence could reveal where the change began?",
                    "What earlier pattern should be examined before drawing a conclusion?"
                ),
                listOf(
                    "What past connection could explain how the situation developed?",
                    "Which earlier event and decision should be connected?",
                    "How could a past condition have influenced its result?",
                    "What relationship between earlier signals matters most?",
                    "Which people, events or conditions were connected?",
                    "What connection could explain the change from then to now?",
                    "Which earlier systems influenced the outcome together?",
                    "What relationship may have been hidden in the past?",
                    "Which past signals form the clearest pattern?",
                    "What wider historical connection should be examined?"
                ),
                listOf(
                    "Where could the past change have started?",
                    "What consequence followed the earlier event?",
                    "What pattern developed after the first change?",
                    "Which decision created the next major effect?",
                    "What chain connects the original event to the outcome?",
                    "How did the situation change step by step?",
                    "What repeated pattern can be traced through the past?",
                    "Which earlier cause best explains the later effect?",
                    "What link between event and consequence deserves attention?",
                    "How did the past situation develop over time?"
                ),
                listOf(
                    "What lesson from the past could improve the next decision?",
                    "What earlier mistake or success should influence the response?",
                    "What should be preserved from the past experience?",
                    "What change could prevent the same pattern from repeating?",
                    "What past evidence should guide the next action?",
                    "How could the earlier outcome improve future preparation?",
                    "What should be adapted based on what already happened?",
                    "What response could turn past information into useful learning?",
                    "What balance between past evidence and present needs matters?",
                    "What lesson deserves to carry forward?"
                )
            )

            "NOW" -> listOf(
                listOf(
                    "What present signal should be examined first?",
                    "Which current condition gives the clearest starting point?",
                    "What is changing most strongly right now?",
                    "What visible evidence best describes the present moment?",
                    "Which current signal deserves attention first?",
                    "What condition should be measured before interpreting the situation?",
                    "What is the strongest signal in the current state?",
                    "Which present pattern needs closer observation?",
                    "What evidence gives the clearest baseline right now?",
                    "What current change could influence the next moment?"
                ),
                listOf(
                    "What present connection gives the clearest context?",
                    "Which current signals should be connected?",
                    "How are the present conditions influencing each other?",
                    "What relationship explains the current situation?",
                    "Which people, systems or conditions are connected now?",
                    "What interaction could strengthen or reduce the current signal?",
                    "Which surrounding factor matters most right now?",
                    "What hidden connection may exist in the present?",
                    "Which signals form the strongest current pattern?",
                    "What wider context should be connected to the present state?"
                ),
                listOf(
                    "What current change should be traced first?",
                    "Where is the strongest present signal coming from?",
                    "What effect is developing right now?",
                    "What pattern is forming in the current situation?",
                    "Which connection could explain the present movement?",
                    "What consequence may already be appearing?",
                    "How is the current condition shifting?",
                    "What chain connects the present signal to its effect?",
                    "What repeated pattern is visible now?",
                    "Which source-to-effect link deserves attention?"
                ),
                listOf(
                    "What response fits the situation right now?",
                    "What action could address the strongest current signal?",
                    "What should be protected before the situation changes?",
                    "How should the current condition be handled?",
                    "What response could reduce the immediate risk?",
                    "What adjustment could improve the present situation?",
                    "What action could create a better immediate balance?",
                    "What should happen before the next change appears?",
                    "Which response best fits the current evidence?",
                    "What present action could support the next decision?"
                )
            )

            else -> listOf(
                listOf(
                    "What future signal should be examined first?",
                    "Which current condition could reveal what may develop next?",
                    "What emerging change deserves attention first?",
                    "What evidence could indicate the next direction?",
                    "Which early signal may become important later?",
                    "What present condition could influence the future?",
                    "What developing pattern should be observed now?",
                    "Which signal could reveal an approaching change?",
                    "What baseline would help compare future movement?",
                    "What wider sign could indicate where the situation is heading?"
                ),
                listOf(
                    "What connection could reveal the future direction?",
                    "Which current signals should be connected to understand what comes next?",
                    "How could present conditions influence the future together?",
                    "What relationship could amplify or reduce the next change?",
                    "Which people, systems or conditions may shape the future?",
                    "What hidden connection could affect the next stage?",
                    "Which interaction could change the future direction?",
                    "What wider system could influence what happens next?",
                    "Which signals form a developing future pattern?",
                    "What connection deserves attention before the next change?"
                ),
                listOf(
                    "What future consequence should be traced first?",
                    "Where could the current change lead?",
                    "What next effect could follow the present signal?",
                    "What pattern may develop over time?",
                    "Which current decision could shape the next outcome?",
                    "What chain of effects could appear next?",
                    "How could the situation evolve from here?",
                    "What future link connects the current signal to a possible result?",
                    "What emerging pattern should be followed?",
                    "Which direction deserves the closest attention?"
                ),
                listOf(
                    "What preparation could support the next stage?",
                    "What future risk should be reduced early?",
                    "What should be protected before conditions change?",
                    "How should the response adapt to a possible future shift?",
                    "What action could prepare for the strongest emerging signal?",
                    "What could improve readiness for the next change?",
                    "What response could keep future options open?",
                    "What should be prepared before the situation develops further?",
                    "What balance between present action and future uncertainty matters?",
                    "What response could create resilience for the next stage?"
                )
            )
        }

        val timeSessionQuestions = timeQuestionBanks.map { it.shuffled().first() }



        var phase = 0
        var selected = -1
        val timeAnswers = mutableListOf<String>()

        fun styleButton(button: Button, selectedState: Boolean) {
            button.setTextColor(Color.WHITE)
            button.background =
                android.graphics.drawable.GradientDrawable().apply {
                    shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                    cornerRadius = dp(14).toFloat()
                    setColor(
                        if (selectedState)
                            Color.rgb(20, 65, 85)
                        else
                            Color.rgb(12, 15, 28)
                    )
                    setStroke(
                        dp(if (selectedState) 2 else 1),
                        if (selectedState)
                            Color.rgb(0, 245, 255)
                        else
                            Color.rgb(65, 90, 120)
                    )
                }
        }

        fun renderPhase() {
            selected = -1
            optionsContainer.removeAllViews()

            phaseTitle.text = when (phase) {
                0 -> "OBSERVE"
                1 -> "CONNECT"
                2 -> "TRACE"
                else -> "RESPOND"
            }

            counter.text =
                "$selectedTime 4D TIME INTERPRETATION • " +
                "${String.format("%02d", phase + 1)} / 04"

            question.text = timeSessionQuestions[phase]

            hint.text = when (phase) {
                0 -> "Start with the strongest time signal."
                1 -> "Connect the signal with its surrounding context."
                2 -> "Follow the strongest path through time."
                else -> "Choose a response that fits the time context."
            }

            displayOptionSets[phase].forEachIndexed { index, option ->
                val button = Button(this@CosmosActivity).apply {
                    text = option
                    textSize = 14f
                    isAllCaps = false
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(16), dp(8), dp(16), dp(8))
                    minHeight = dp(68)
                    styleButton(this, false)

                    setOnClickListener {
                        selected = index

                        for (i in 0 until optionsContainer.childCount) {
                            val child = optionsContainer.getChildAt(i)
                            if (child is Button) {
                                styleButton(child, i == selected)
                                child.animate()
                                    .scaleX(if (i == selected) 1.015f else 1f)
                                    .scaleY(if (i == selected) 1.015f else 1f)
                                    .setDuration(140)
                                    .start()
                            }
                        }
                    }
                }

                optionsContainer.addView(
                    button,
                    LinearLayout.LayoutParams(-1, 0, 1f).apply {
                        bottomMargin = dp(6)
                    }
                )
            }

            nextButton.text =
                if (phase == 3) "COMPLETE  →" else "NEXT  →"
        }

        nextButton.setOnClickListener {
            if (selected < 0) return@setOnClickListener

            timeAnswers.add(displayOptionSets[phase][selected])

            if (phase < 3) {
                phase++
                renderPhase()
            } else {
                showTimeGDMIEAnalysis(
                    selectedTime = selectedTime,
                    timeAnswers = timeAnswers,
                    observeIndex = optionSets[0].indexOf(timeAnswers[0]),
                    connectIndex = optionSets[1].indexOf(timeAnswers[1]),
                    traceIndex = optionSets[2].indexOf(timeAnswers[2]),
                    respondIndex = optionSets[3].indexOf(timeAnswers[3])
                )
            }
        }

        content.addView(phaseTitle, LinearLayout.LayoutParams(-1, dp(24)))
        content.addView(counter, LinearLayout.LayoutParams(-1, dp(42)))

        val hologramView = createEarthHologramView("TIME MACHINE")

        content.addView(
            hologramView,
            LinearLayout.LayoutParams(
                -1,
                dp(210)
            ).apply {
                setMargins(
                    dp(8),
                    dp(8),
                    dp(8),
                    dp(12)
                )
            }
        )

        content.addView(question, LinearLayout.LayoutParams(-1, dp(72)))
        content.addView(hint, LinearLayout.LayoutParams(-1, dp(44)))

        content.addView(
            optionsContainer,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        content.addView(
            nextButton,
            LinearLayout.LayoutParams(-1, dp(58))
        )

        root.addView(content, FrameLayout.LayoutParams(-1, -1))

        renderPhase()

        val dialog =
            AlertDialog.Builder(this)
                .setView(root)
                .create()

        dialog.show()
        styleCinematicDialog(dialog, Color.rgb(0, 235, 255), true)
    }


    private fun showTimeQuestion(
        timeIndex: Int,
        titleText: String,
        question: String
    ) {

        val container =
            android.widget.LinearLayout(this).apply {
                orientation =
                    android.widget.LinearLayout.VERTICAL

                setPadding(
                    dp(20),
                    dp(8),
                    dp(20),
                    dp(10)
                )
            }

        val questionView =
            TextView(this).apply {
                text = question
                textSize = 16f

                // Dark text on the default light AlertDialog.
                setTextColor(
                    Color.rgb(35, 45, 65)
                )

                setPadding(
                    0,
                    dp(4),
                    0,
                    dp(14)
                )
            }

        container.addView(
            questionView,
            android.widget.LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        val input =
            android.widget.EditText(this).apply {
                hint = "Your thought..."
                setHintTextColor(
                    Color.rgb(125, 135, 155)
                )

                // Make typed text clearly visible.
                setTextColor(
                    Color.rgb(25, 35, 55)
                )

                textSize = 16f
                gravity =
                    Gravity.TOP or Gravity.START

                minLines = 3
                maxLines = 5

                inputType =
                    android.text.InputType.TYPE_CLASS_TEXT or
                        android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or
                        android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE

                setPadding(
                    dp(14),
                    dp(12),
                    dp(14),
                    dp(12)
                )

                setBackgroundColor(
                    Color.rgb(242, 245, 250)
                )
            }

        container.addView(
            input,
            android.widget.LinearLayout.LayoutParams(
                -1,
                -2
            )
        )

        fun addFactor(
            label: String,
            values: Array<String>,
            defaultIndex: Int = -1
        ): android.widget.RadioGroup {

            val labelView =
                TextView(this).apply {
                    text = label
                    textSize = 12f
                    setTextColor(cyan)

                    setTypeface(
                        null,
                        android.graphics.Typeface.BOLD
                    )

                    setPadding(
                        0,
                        dp(13),
                        0,
                        dp(4)
                    )
                }

            container.addView(labelView)

            val group =
                android.widget.RadioGroup(this).apply {
                    orientation =
                        android.widget.RadioGroup.HORIZONTAL
                }

            values.forEachIndexed { index, value ->

                val radio =
                    android.widget.RadioButton(this).apply {
                        text = value
                        textSize = 12f
                        setTextColor(
                            Color.rgb(45, 55, 75)
                        )

                        tag = index

                        buttonTintList =
                            android.content.res.ColorStateList.valueOf(
                                cyan
                            )

                        setPadding(
                            0,
                            0,
                            dp(8),
                            0
                        )

                        if (index == defaultIndex) {
                            isChecked = true
                        }
                    }

                group.addView(radio)
            }

            container.addView(group)

            return group
        }

        val impactGroup =
            addFactor(
                "IMPACT",
                arrayOf(
                    "Low",
                    "Medium",
                    "High"
                ),
                1
            )

        val uncertaintyGroup =
            addFactor(
                "UNCERTAINTY",
                arrayOf(
                    "Low",
                    "Medium",
                    "High"
                ),
                1
            )

        val controlGroup =
            addFactor(
                "CONTROL",
                arrayOf(
                    "Low",
                    "Medium",
                    "High"
                ),
                1
            )

        val riskGroup =
            addFactor(
                "RISK",
                arrayOf(
                    "Low",
                    "Medium",
                    "High"
                ),
                1
            )

        android.app.AlertDialog.Builder(this)
            .setTitle(titleText)
            .setView(container)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("🧠 ANALYZE TIME DECISION") { _, _ ->

                val answer =
                    input.text.toString().trim()

                if (answer.isBlank()) {
                    android.widget.Toast.makeText(
                        this,
                        "ENTER YOUR THOUGHT",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()

                    return@setPositiveButton
                }

                if (
                    impactGroup.checkedRadioButtonId == -1 ||
                    uncertaintyGroup.checkedRadioButtonId == -1 ||
                    controlGroup.checkedRadioButtonId == -1 ||
                    riskGroup.checkedRadioButtonId == -1
                ) {
                    android.widget.Toast.makeText(
                        this,
                        "SELECT ALL TIME FACTORS BEFORE ANALYSIS",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val impact =
                    impactGroup
                        .findViewById<android.widget.RadioButton>(
                            impactGroup.checkedRadioButtonId
                        )
                        .tag as Int

                val uncertainty =
                    uncertaintyGroup
                        .findViewById<android.widget.RadioButton>(
                            uncertaintyGroup.checkedRadioButtonId
                        )
                        .tag as Int

                val control =
                    controlGroup
                        .findViewById<android.widget.RadioButton>(
                            controlGroup.checkedRadioButtonId
                        )
                        .tag as Int

                val risk =
                    riskGroup
                        .findViewById<android.widget.RadioButton>(
                            riskGroup.checkedRadioButtonId
                        )
                        .tag as Int

                val decision =
                    GDMIESimpleDecision(
                        decisionText = answer,

                        currentSituation =
                            when (control) {
                                0 -> CurrentState.WEAK
                                1 -> CurrentState.STABLE
                                else -> CurrentState.STRONG
                            },

                        goalOutcome =
                            when (impact) {
                                0 -> GoalLevel.SMALL_IMPROVEMENT
                                1 -> GoalLevel.MODERATE_IMPROVEMENT
                                else -> GoalLevel.MAJOR_IMPROVEMENT
                            },

                        context =
                            when (uncertainty) {
                                0 -> ContextState.FAVOURABLE
                                1 -> ContextState.UNCERTAIN
                                else -> ContextState.UNFAVOURABLE
                            },

                        momentum =
                            when (timeIndex) {
                                0 -> MomentumState.DECLINING
                                1 -> MomentumState.STABLE
                                else -> MomentumState.IMPROVING
                            },

                        risk =
                            when (risk) {
                                0 -> RiskLevel.LOW
                                1 -> RiskLevel.MEDIUM
                                else -> RiskLevel.HIGH
                            },

                        timing =
                            when (timeIndex) {
                                0 -> TimingState.LATER
                                1 -> TimingState.NOW
                                else -> TimingState.SOON
                            }
                    )

                val input =
                    GDMIESimpleDecisionAdapter
                        .toGDMInput(decision)

                CoroutineScope(Dispatchers.IO).launch {

                    val result =
                        GDMIEEngineGateway.calculate(input)

                    runOnUiThread {

                        android.app.AlertDialog.Builder(
                            this@CosmosActivity
                        )
                            .setTitle(
                                "🎯 GDMIE TIME SIGNAL"
                            )
                            .setMessage(
                                "$titleText\n\n" +
                                    "SIGNAL: ${result.decision}\n\n" +
                                    "CONFIDENCE: %.0f%%\n\n%s".format(
                                        result.confidence * 100,
                                        result.explanation
                                    )
                            )
                            .setPositiveButton(
                                "DONE",
                                null
                            )
                            .show()
                    }
                }
            }
            .show()
    }

    private inner class CosmosRadarView :
        View(this@CosmosActivity) {

        private val paint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private val glow =
            Paint(Paint.ANTI_ALIAS_FLAG)

        private var rotation = 0f
        private var pulse = 0f

        private var downX = 0f
        private var downY = 0f
        private var multiTouch = false

        var activeColor = cyan

        var onLayerChanged:
            ((Int) -> Unit)? = null

        var onLayerSelected:
            (() -> Unit)? = null

        private val animator =
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 5000L
                repeatCount =
                    ValueAnimator.INFINITE
                repeatMode =
                    ValueAnimator.REVERSE

                addUpdateListener {
                    pulse =
                        it.animatedValue as Float
                    rotation += 0.18f
                    invalidate()
                }
            }

        init {
            animator.start()
            isClickable = true
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            val cx = w / 2f
            val cy = h * 0.46f

            drawStars(
                canvas,
                w,
                h
            )

            drawRadar(
                canvas,
                cx,
                cy
            )

            drawSignalCore(
                canvas,
                cx,
                cy
            )

            drawLayerNodes(
                canvas,
                cx,
                cy
            )
        }

        private fun drawStars(
            canvas: Canvas,
            w: Float,
            h: Float
        ) {
            paint.style = Paint.Style.FILL

            for (i in 0 until 70) {
                val x =
                    ((i * 83) % w.toInt())
                        .toFloat()

                val y =
                    ((i * 137) % h.toInt())
                        .toFloat()

                val alpha =
                    70 + ((i * 17) % 150)

                paint.color =
                    Color.argb(
                        alpha,
                        110,
                        210,
                        255
                    )

                canvas.drawCircle(
                    x,
                    y,
                    if (i % 7 == 0) 2.2f else 1.1f,
                    paint
                )
            }
        }

        private fun drawRadar(
            canvas: Canvas,
            cx: Float,
            cy: Float
        ) {
            val maxRadius =
                width.coerceAtMost(height) * 0.30f

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp(1).toFloat()

            val rings =
                floatArrayOf(
                    0.38f,
                    0.62f,
                    0.86f,
                    1.0f
                )

            for (ring in rings) {
                paint.color =
                    Color.argb(
                        65,
                        activeColor shr 16 and 255,
                        activeColor shr 8 and 255,
                        activeColor and 255
                    )

                canvas.drawCircle(
                    cx,
                    cy,
                    maxRadius * ring,
                    paint
                )
            }

            val sweep =
                rotation * 0.03f

            val sweepX =
                cx + cos(sweep) * maxRadius

            val sweepY =
                cy + sin(sweep) * maxRadius

            paint.color =
                Color.argb(
                    150,
                    activeColor shr 16 and 255,
                    activeColor shr 8 and 255,
                    activeColor and 255
                )

            paint.strokeWidth =
                dp(2).toFloat()

            canvas.drawLine(
                cx,
                cy,
                sweepX,
                sweepY,
                paint
            )
        }

        private fun drawSignalCore(
            canvas: Canvas,
            cx: Float,
            cy: Float
        ) {
            val radius =
                dp(54).toFloat() +
                    dp(8).toFloat() * pulse

            glow.shader =
                RadialGradient(
                    cx,
                    cy,
                    radius * 2f,
                    intArrayOf(
                        Color.argb(
                            190,
                            activeColor shr 16 and 255,
                            activeColor shr 8 and 255,
                            activeColor and 255
                        ),
                        Color.argb(
                            30,
                            activeColor shr 16 and 255,
                            activeColor shr 8 and 255,
                            activeColor and 255
                        ),
                        Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

            canvas.drawCircle(
                cx,
                cy,
                radius * 2f,
                glow
            )

            paint.style = Paint.Style.FILL
            paint.color = Color.WHITE

            canvas.drawCircle(
                cx,
                cy,
                dp(40).toFloat(),
                paint
            )

            paint.color = activeColor

            canvas.drawCircle(
                cx,
                cy,
                dp(32).toFloat(),
                paint
            )

            paint.color = Color.WHITE
            paint.textAlign =
                Paint.Align.CENTER
            paint.textSize =
                dp(24).toFloat()
            paint.typeface =
                android.graphics.Typeface.DEFAULT_BOLD

            canvas.drawText(
                "🧠",
                cx,
                cy + dp(8),
                paint
            )
        }

        private fun drawLayerNodes(
            canvas: Canvas,
            cx: Float,
            cy: Float
        ) {
            val radius =
                width.coerceAtMost(height) * 0.30f

            val angles = floatArrayOf(
                -2.35f,
                -0.78f,
                0.78f,
                2.35f
            )

            for (i in 0..3) {

                val x =
                    cx +
                        cos(angles[i]) *
                        radius

                val y =
                    cy +
                        sin(angles[i]) *
                        radius

                val color =
                    when (i) {
                        0 -> cyan
                        1 -> purple
                        2 -> Color.rgb(90, 220, 140)
                        else -> gold
                    }

                val speed =
                    when (i) {
                        0 -> 1.00f
                        1 -> 0.72f
                        2 -> 0.88f
                        else -> 0.58f
                    }

                val wave =
                    kotlin.math.sin(
                        rotation * 0.055f * speed +
                            i * 1.7f
                    )

                val pulseRadius =
                    dp(19).toFloat() +
                        dp(3).toFloat() * wave

                // ------------------------------------------------
                // OUTER LIVE GLOW
                // ------------------------------------------------
                paint.style = Paint.Style.FILL

                paint.color =
                    Color.argb(
                        (35 + 18 * ((wave + 1f) * 0.5f))
                            .toInt()
                            .coerceIn(20, 60),
                        color shr 16 and 255,
                        color shr 8 and 255,
                        color and 255
                    )

                canvas.drawCircle(
                    x,
                    y,
                    dp(38).toFloat() +
                        dp(7).toFloat() * ((wave + 1f) * 0.5f),
                    paint
                )

                // ------------------------------------------------
                // DARK CORE
                // ------------------------------------------------
                paint.color = Color.rgb(8, 12, 28)

                canvas.drawCircle(
                    x,
                    y,
                    dp(25).toFloat(),
                    paint
                )

                // ------------------------------------------------
                // FOUR DIFFERENT LIVE VISUAL IDENTITIES
                // ------------------------------------------------
                when (i) {

                    // ☀️ ENERGY
                    0 -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth =
                            dp(1).toFloat()

                        for (ring in 0 until 3) {
                            canvas.save()

                            canvas.rotate(
                                rotation *
                                    (1.2f + ring * 0.65f),
                                x,
                                y
                            )

                            paint.color =
                                Color.argb(
                                    180 - ring * 35,
                                    255,
                                    205,
                                    70
                                )

                            val rx =
                                dp(
                                    20 + ring * 5
                                ).toFloat()

                            val ry =
                                dp(
                                    7 + ring * 2
                                ).toFloat()

                            canvas.drawOval(
                                x - rx,
                                y - ry,
                                x + rx,
                                y + ry,
                                paint
                            )

                            canvas.restore()
                        }

                        // Pulsing energy core
                        paint.style = Paint.Style.FILL
                        paint.color =
                            Color.rgb(
                                255,
                                215,
                                70
                            )

                        canvas.drawCircle(
                            x,
                            y,
                            dp(8).toFloat() +
                                dp(2).toFloat() * wave,
                            paint
                        )

                        // Moving energy sparks
                        for (p in 0 until 6) {
                            val a =
                                rotation * 0.08f +
                                    p *
                                    (Math.PI * 2.0 / 6.0)
                                        .toFloat()

                            val orbit =
                                dp(22).toFloat() +
                                    dp(3).toFloat() *
                                    kotlin.math.sin(
                                        rotation * 0.10f + p
                                    )

                            paint.color =
                                Color.rgb(
                                    255,
                                    230,
                                    120
                                )

                            canvas.drawCircle(
                                x + cos(a) * orbit,
                                y + sin(a) * orbit,
                                dp(1 + (p % 2)).toFloat(),
                                paint
                            )
                        }
                    }

                    // 🪐 SPACE
                    1 -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth =
                            dp(1).toFloat()

                        // Orbital rings
                        for (ring in 0 until 3) {
                            canvas.save()

                            canvas.rotate(
                                -rotation *
                                    (0.8f + ring * 0.35f),
                                x,
                                y
                            )

                            paint.color =
                                Color.argb(
                                    150 - ring * 30,
                                    150,
                                    100,
                                    255
                                )

                            val rx =
                                dp(
                                    18 + ring * 6
                                ).toFloat()

                            val ry =
                                dp(
                                    8 + ring * 3
                                ).toFloat()

                            canvas.drawOval(
                                x - rx,
                                y - ry,
                                x + rx,
                                y + ry,
                                paint
                            )

                            canvas.restore()
                        }

                        // Moving cosmic particles
                        paint.style = Paint.Style.FILL

                        for (p in 0 until 7) {
                            val a =
                                -rotation * 0.065f +
                                    p *
                                    (Math.PI * 2.0 / 7.0)
                                        .toFloat()

                            val orbit =
                                dp(
                                    10 + (p % 3) * 7
                                ).toFloat()

                            paint.color =
                                if (p % 2 == 0)
                                    Color.rgb(
                                        100,
                                        220,
                                        255
                                    )
                                else
                                    Color.rgb(
                                        200,
                                        130,
                                        255
                                    )

                            canvas.drawCircle(
                                x + cos(a) * orbit,
                                y + sin(a) * orbit,
                                dp(1.5f.toInt()).toFloat(),
                                paint
                            )
                        }

                        // Space-time core
                        paint.color =
                            Color.rgb(
                                175,
                                105,
                                255
                            )

                        canvas.drawCircle(
                            x,
                            y,
                            dp(7).toFloat() +
                                dp(2).toFloat() * wave,
                            paint
                        )
                    }

                    // 🌍 EARTH
                    2 -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth =
                            dp(2).toFloat()

                        // Latitude
                        for (lat in 0 until 3) {
                            val offset =
                                (lat - 1) *
                                    dp(7).toFloat()

                            paint.color =
                                Color.argb(
                                    150,
                                    90,
                                    220,
                                    140
                                )

                            canvas.drawOval(
                                x - dp(18).toFloat(),
                                y - dp(9).toFloat() + offset,
                                x + dp(18).toFloat(),
                                y + dp(9).toFloat() + offset,
                                paint
                            )
                        }

                        // Rotating longitude
                        canvas.save()

                        canvas.rotate(
                            rotation * 0.9f,
                            x,
                            y
                        )

                        paint.color =
                            Color.rgb(
                                90,
                                230,
                                155
                            )

                        canvas.drawOval(
                            x - dp(10).toFloat(),
                            y - dp(21).toFloat(),
                            x + dp(10).toFloat(),
                            y + dp(21).toFloat(),
                            paint
                        )

                        canvas.restore()

                        // Living Earth core
                        paint.style = Paint.Style.FILL
                        paint.color =
                            Color.rgb(
                                80,
                                220,
                                140
                            )

                        canvas.drawCircle(
                            x,
                            y,
                            dp(8).toFloat() +
                                dp(2).toFloat() * wave,
                            paint
                        )

                        // Orbiting life signal
                        val a =
                            rotation * 0.075f

                        paint.color =
                            Color.rgb(
                                150,
                                255,
                                190
                            )

                        canvas.drawCircle(
                            x + cos(a) *
                                dp(24).toFloat(),
                            y + sin(a) *
                                dp(24).toFloat(),
                            dp(2).toFloat(),
                            paint
                        )
                    }

                    // ⏳ TIME
                    else -> {
                        paint.style = Paint.Style.STROKE
                        paint.strokeWidth =
                            dp(2).toFloat()

                        // Counter-rotating temporal rings
                        canvas.save()

                        canvas.rotate(
                            rotation * 1.15f,
                            x,
                            y
                        )

                        paint.color =
                            Color.argb(
                                190,
                                255,
                                210,
                                90
                            )

                        canvas.drawOval(
                            x - dp(22).toFloat(),
                            y - dp(8).toFloat(),
                            x + dp(22).toFloat(),
                            y + dp(8).toFloat(),
                            paint
                        )

                        canvas.restore()

                        canvas.save()

                        canvas.rotate(
                            -rotation * 1.65f,
                            x,
                            y
                        )

                        paint.color =
                            Color.argb(
                                150,
                                255,
                                170,
                                70
                            )

                        canvas.drawOval(
                            x - dp(8).toFloat(),
                            y - dp(22).toFloat(),
                            x + dp(8).toFloat(),
                            y + dp(22).toFloat(),
                            paint
                        )

                        canvas.restore()

                        // Temporal pulse
                        paint.style = Paint.Style.FILL
                        paint.color =
                            Color.rgb(
                                255,
                                215,
                                90
                            )

                        canvas.drawCircle(
                            x,
                            y,
                            dp(7).toFloat() +
                                dp(2).toFloat() * wave,
                            paint
                        )

                        // Time particles
                        for (p in 0 until 5) {
                            val a =
                                -rotation * 0.10f +
                                    p *
                                    (Math.PI * 2.0 / 5.0)
                                        .toFloat()

                            paint.color =
                                Color.rgb(
                                    255,
                                    225,
                                    130
                                )

                            canvas.drawCircle(
                                x + cos(a) *
                                    dp(24).toFloat(),
                                y + sin(a) *
                                    dp(24).toFloat(),
                                dp(2).toFloat(),
                                paint
                            )
                        }
                    }
                }

                // ------------------------------------------------
                // ACTIVE NODE PULSE RING
                // ------------------------------------------------
                if (selectedLayer == i) {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth =
                        dp(2).toFloat()

                    paint.color =
                        Color.argb(
                            190,
                            color shr 16 and 255,
                            color shr 8 and 255,
                            color and 255
                        )

                    canvas.drawCircle(
                        x,
                        y,
                        pulseRadius +
                            dp(5).toFloat(),
                        paint
                    )
                }
            }
        }

        override fun onTouchEvent(
            event: MotionEvent
        ): Boolean {
            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {
                    downX = event.x
                    downY = event.y
                    multiTouch = false
                    return true
                }

                MotionEvent.ACTION_POINTER_DOWN -> {
                    // ✌️ Ignore multi-finger gestures completely.
                    multiTouch = true
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    if (multiTouch) {
                        multiTouch = false
                        performClick()
                        return true
                    }

                    val dx = event.x - downX
                    val dy = event.y - downY

                    // ☝️ Single-finger swipe only.
                    if (
                        abs(dx) > dp(70) &&
                        abs(dx) > abs(dy) * 1.2f
                    ) {
                        if (dx < 0) {
                            selectedLayer =
                                (selectedLayer + 1) % 4
                        } else {
                            selectedLayer =
                                (selectedLayer + 2) % 4
                        }

                        onLayerChanged?.invoke(
                            selectedLayer
                        )
                    } else {
                        // ☝️ Single-finger tap:
                        // only the selected node can open.
                        val w = width.toFloat()
                        val h = height.toFloat()

                        val cx = w / 2f
                        val cy = h * 0.46f
                        val radius =
                            width.coerceAtMost(height) * 0.30f

                        val angles =
                            floatArrayOf(
                -2.35f,
                -0.78f,
                0.78f,
                2.35f
            )

                        val nodeX =
                            cx +
                                cos(angles[selectedLayer]) *
                                radius

                        val nodeY =
                            cy +
                                sin(angles[selectedLayer]) *
                                radius

                        val nodeDx =
                            event.x - nodeX
                        val nodeDy =
                            event.y - nodeY

                        val distance =
                            kotlin.math.sqrt(
                                nodeDx * nodeDx +
                                    nodeDy * nodeDy
                            )

                        if (distance <= dp(60)) {
                            onLayerSelected?.invoke()
                        }
                    }

                    performClick()
                    return true
                }

                MotionEvent.ACTION_CANCEL -> {
                    multiTouch = false
                    return true
                }
            }

            return true
        }

        override fun performClick(): Boolean {
            super.performClick()
            return true
        }
    }

    private fun dp(value: Int): Int {
        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }

    // ========================================================
    // SPACE WORLD — STEP 1
    // 7 floating cosmic theory nodes
    // ========================================================

    

    // ========================================================
    // SPACE WORLD — 7 COSMIC THEORY NODES
    // ========================================================

    private data class SpaceNode(
        val icon: String,
        val name: String,
        val x: Float,
        val y: Float,
        val radius: Float,
        val color: Int
    )

    private inner class SpaceWorldView : View(this@CosmosActivity) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        private val nodes = arrayOf(
            SpaceNode("🌌", "UNIVERSE",            0.50f, 0.20f, 48f, cyan),
            SpaceNode("⏳", "SPACE-TIME",           0.22f, 0.36f, 45f, purple),
            SpaceNode("🌌", "EXPANDING UNIVERSE",  0.78f, 0.37f, 48f, cyan),
            SpaceNode("🕳️", "BLACK HOLES",         0.50f, 0.43f, 52f, purple),
            SpaceNode("🌑", "DARK MATTER",          0.20f, 0.65f, 46f, purple),
            SpaceNode("⚡", "DARK ENERGY",          0.80f, 0.65f, 46f, gold),
            SpaceNode("❓", "THE UNKNOWN",          0.50f, 0.82f, 50f, Color.WHITE)
        )

        private var phase = 0f
        private var selectedNode = -1

        private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 6000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART

            addUpdateListener {
                phase = it.animatedValue as Float
                invalidate()
            }
        }

        init {
            setBackgroundColor(Color.rgb(1, 2, 14))
            animator.start()
        }

        override fun onDetachedFromWindow() {
            animator.cancel()
            super.onDetachedFromWindow()
        }

        private fun scaleX(v: Float): Float {
            return width.toFloat() * v
        }

        private fun scaleY(v: Float): Float {
            return height.toFloat() * v
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            drawBackground(canvas)
            drawTitle(canvas)
            drawNodes(canvas)
        }

        private fun drawBackground(canvas: Canvas) {

            canvas.drawColor(Color.rgb(1, 2, 14))

            val w = width.toFloat()
            val h = height.toFloat()

            // Deep-space glow
            paint.style = Paint.Style.FILL
            paint.shader = RadialGradient(
                w * 0.50f,
                h * 0.48f,
                w * 0.70f,
                intArrayOf(
                    Color.rgb(20, 10, 55),
                    Color.rgb(5, 5, 25),
                    Color.rgb(1, 2, 14)
                ),
                null,
                Shader.TileMode.CLAMP
            )

            canvas.drawRect(0f, 0f, w, h, paint)
            paint.shader = null

            // Deterministic stars
            paint.style = Paint.Style.FILL

            for (i in 0 until 110) {

                val sx =
                    ((i * 83) % 1000) / 1000f * w

                val sy =
                    ((i * 137 + 41) % 1000) / 1000f * h

                val twinkle =
                    0.35f +
                    0.65f *
                    kotlin.math.abs(
                        kotlin.math.sin(
                            (phase * Math.PI * 2.0) +
                            i.toDouble()
                        ).toFloat()
                    )

                paint.color =
                    Color.argb(
                        (255f * twinkle).toInt(),
                        180,
                        200,
                        255
                    )

                val r =
                    if (i % 9 == 0) 1.8f else 0.8f

                canvas.drawCircle(sx, sy, r, paint)
            }
        }

        private fun drawTitle(canvas: Canvas) {

            val w = width.toFloat()

            paint.shader = null
            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

            paint.textSize = 40f
            paint.color = Color.WHITE

            canvas.drawText(
                "🌌  SPACE",
                w * 0.50f,
                58f,
                paint
            )

            paint.textSize = 19f
            paint.color = Color.rgb(180, 170, 255)

            canvas.drawText(
                "THE UNKNOWN UNIVERSE",
                w * 0.50f,
                82f,
                paint
            )

            paint.textSize = 15f
            paint.color = Color.rgb(120, 150, 190)

            canvas.drawText(
                "EXPLORE • DISCOVER • UNDERSTAND",
                w * 0.50f,
                102f,
                paint
            )
        }

        private fun drawNodes(canvas: Canvas) {

            for (i in nodes.indices) {

                val node = nodes[i]

                // 🌌 LIVE COSMIC MOTION — every SPACE node moves independently
                val t = phase * Math.PI * 2.0

                val speed = when (i) {
                    0 -> 0.55
                    1 -> 0.80
                    2 -> 0.42
                    3 -> 1.05
                    4 -> 0.68
                    5 -> 1.25
                    else -> 0.92
                }

                val driftX =
                    kotlin.math.sin(t * speed + i * 1.37).toFloat() *
                    (10f + i * 1.5f)

                val driftY =
                    kotlin.math.cos(t * (speed * 0.73) + i * 0.91).toFloat() *
                    (8f + i * 1.2f)

                val x = scaleX(node.x) + driftX
                val y = scaleY(node.y) + driftY

                // Living pulse — each node breathes at a different rate
                val pulse =
                    1f +
                    kotlin.math.sin(
                        t * (0.75 + i * 0.11) + i
                    ).toFloat() * 0.055f

                val radius = node.radius * 3.10f * pulse

                val selected =
                    selectedNode == i

                // Outer glow
                paint.style = Paint.Style.FILL
                paint.shader = RadialGradient(
                    x,
                    y,
                    radius * 1.9f,
                    intArrayOf(
                        Color.argb(
                            if (selected) 110 else 65,
                            Color.red(node.color),
                            Color.green(node.color),
                            Color.blue(node.color)
                        ),
                        Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

                canvas.drawCircle(
                    x,
                    y,
                    radius * 1.9f,
                    paint
                )

                paint.shader = null

                // Planet body
                paint.style = Paint.Style.FILL
                paint.color = Color.rgb(8, 12, 30)

                canvas.drawCircle(
                    x,
                    y,
                    radius,
                    paint
                )

                // Ring
                paint.style = Paint.Style.STROKE
                paint.strokeWidth =
                    if (selected) 3f else 1.5f

                paint.color = node.color

                canvas.drawCircle(
                    x,
                    y,
                    radius,
                    paint
                )

                // Inner highlight
                paint.strokeWidth = 1f
                paint.color = Color.argb(
                    90,
                    255,
                    255,
                    255
                )

                canvas.drawCircle(
                    x,
                    y,
                    radius * 0.72f,
                    paint
                )

                // Icon
                paint.style = Paint.Style.FILL
                paint.textAlign = Paint.Align.CENTER
                paint.textSize = 100f
                paint.color = Color.WHITE

                canvas.drawText(
                    node.icon,
                    x,
                    y + 9f,
                    paint
                )

                // Label
                paint.textSize = 30f
                paint.color =
                    if (selected)
                        Color.WHITE
                    else
                        Color.rgb(190, 200, 230)

                canvas.drawText(
                    node.name,
                    x,
                    y + radius + 18f,
                    paint
                )
            }
        }

        private fun showSpaceNodePanel(node: SpaceNode) {

            val description = when (node.name) {

                "UNIVERSE" ->
                    "🌌 The universe contains galaxies, stars, planets and enormous cosmic structures.\n\n" +
                    "Known: observable matter and cosmic structures.\n" +
                    "Uncertain: many details about the universe's deepest history.\n" +
                    "Unknown: what may exist beyond what we can observe."

                "SPACE-TIME" ->
                    "⏳ Space and time are connected as spacetime.\n\n" +
                    "Known: gravity affects spacetime.\n" +
                    "Uncertain: the complete connection between gravity and quantum physics.\n" +
                    "Unknown: what happens at the deepest physical limits."

                "EXPANDING UNIVERSE" ->
                    "🌌 The universe is expanding.\n\n" +
                    "Known: distant galaxies show evidence of cosmic expansion.\n" +
                    "Uncertain: the complete nature of the expansion mechanism.\n" +
                    "Unknown: the ultimate future of the universe."

                "BLACK HOLES" ->
                    "🕳️ Black holes are regions where gravity becomes extremely strong.\n\n" +
                    "Known: their effects can be observed through matter, light and gravitational waves.\n" +
                    "Uncertain: what happens beyond the event horizon.\n" +
                    "Unknown: the complete physics of the singularity."

                "DARK MATTER" ->
                    "🌑 Dark matter is a name for unseen matter inferred from gravitational effects.\n\n" +
                    "Known: gravitational evidence exists.\n" +
                    "Uncertain: its exact nature and composition.\n" +
                    "Unknown: what dark matter fundamentally is."

                "DARK ENERGY" ->
                    "⚡ Dark energy is the name given to whatever is associated with the universe's accelerating expansion.\n\n" +
                    "Known: observations show accelerated cosmic expansion.\n" +
                    "Uncertain: the physical nature of dark energy.\n" +
                    "Unknown: whether our current model fully explains it."

                else ->
                    "❓ Some questions about the universe remain unanswered.\n\n" +
                    "Known: what observations and experiments support.\n" +
                    "Uncertain: what current evidence cannot determine completely.\n" +
                    "Unknown: questions for which reliable evidence is not yet available.\n\n" +
                    "UNKNOWN ≠ ANSWER"

            }

            val dialog = AlertDialog.Builder(this@CosmosActivity)
                .setTitle("${node.icon}  ${node.name}")
                .setMessage(description)
                .setNegativeButton("CLOSE", null)
                .setPositiveButton("EXPLORE THEORY") { _, _ ->
                    showSpaceTheory(node)
                }
                .create()

            dialog.show()
        }


        private fun showSpaceTheory(node: SpaceNode) {

            val data = when (node.name) {

                "UNIVERSE" -> arrayOf(
                    "The universe contains galaxies, stars, planets and enormous cosmic structures.",
                    "Many details about the universe's deepest history remain uncertain.",
                    "What may exist beyond what we can observe is still unknown."
                )

                "SPACE-TIME" -> arrayOf(
                    "Space and time are connected as spacetime, and gravity affects spacetime.",
                    "The complete connection between gravity and quantum physics remains uncertain.",
                    "The deepest physical nature of spacetime remains unknown."
                )

                "EXPANDING UNIVERSE" -> arrayOf(
                    "Observations show that the universe is expanding.",
                    "The complete mechanism behind cosmic expansion is still being studied.",
                    "The ultimate future of the universe remains unknown."
                )

                "BLACK HOLES" -> arrayOf(
                    "Black holes have extremely strong gravity and their effects can be observed.",
                    "The physics beyond the event horizon is not completely understood.",
                    "The complete nature of the singularity remains unknown."
                )

                "DARK MATTER" -> arrayOf(
                    "Gravitational observations provide evidence for unseen matter.",
                    "Its exact nature and composition are uncertain.",
                    "What dark matter fundamentally is remains unknown."
                )

                "DARK ENERGY" -> arrayOf(
                    "Observations show that cosmic expansion is accelerating.",
                    "The physical nature of dark energy is uncertain.",
                    "Whether current models completely explain it remains unknown."
                )

                else -> arrayOf(
                    "Science can describe what observations and experiments support.",
                    "Some questions remain uncertain because evidence is incomplete.",
                    "Some questions currently have no reliable answer.",
                )
            }

            val scroll =
                android.widget.ScrollView(this@CosmosActivity).apply {
                    setBackgroundColor(Color.rgb(3, 4, 20))
                }

            val container =
                android.widget.LinearLayout(this@CosmosActivity).apply {
                    orientation =
                        android.widget.LinearLayout.VERTICAL

                    setPadding(
                        dp(24),
                        dp(28),
                        dp(24),
                        dp(32)
                    )
                }

            fun addText(
                text: String,
                size: Float,
                color: Int
            ) {
                val tv =
                    TextView(this@CosmosActivity).apply {
                        this.text = text
                        textSize = size
                        setTextColor(color)
                        setPadding(
                            0,
                            dp(6),
                            0,
                            dp(10)
                        )
                    }

                container.addView(
                    tv,
                    android.widget.LinearLayout.LayoutParams(
                        -1,
                        -2
                    )
                )
            }

            addText(
                "${node.icon}  ${node.name}",
                28f,
                Color.WHITE
            )

            addText(
                "SPACE THEORY",
                14f,
                Color.rgb(150, 170, 255)
            )

            addText(
                "01  •  WHAT WE KNOW",
                18f,
                cyan
            )

            addText(
                data[0],
                16f,
                Color.rgb(225, 230, 245)
            )

            addText(
                "02  •  WHAT IS UNCERTAIN",
                18f,
                purple
            )

            addText(
                data[1],
                16f,
                Color.rgb(225, 230, 245)
            )

            addText(
                "03  •  WHAT IS UNKNOWN",
                18f,
                Color.WHITE
            )

            addText(
                data[2],
                16f,
                Color.rgb(225, 230, 245)
            )

            addText(
                "UNKNOWN ≠ ANSWER",
                17f,
                gold
            )

            addText(
                "Unknown information stays unknown. " +
                "GDMIE must not invent an answer where reliable evidence is unavailable.",
                14f,
                Color.rgb(170, 180, 205)
            )

            val continueButton =
                android.widget.Button(this@CosmosActivity).apply {

                    text = "CONTINUE  →  YOUR INTERPRETATION"
                    textSize = 15f
                    isAllCaps = false
                    gravity = Gravity.CENTER
                    isEnabled = true

                    setTextColor(Color.BLACK)

                    setPadding(
                        dp(18),
                        0,
                        dp(18),
                        0
                    )

                    setLayerType(
                        android.view.View.LAYER_TYPE_SOFTWARE,
                        null
                    )

                    setBackgroundColor(
                        Color.rgb(0, 220, 255)
                    )

                    setShadowLayer(
                        dp(12).toFloat(),
                        0f,
                        0f,
                        Color.rgb(0, 220, 255)
                    )

                    setOnClickListener {
                        animate()
                            .scaleX(0.97f)
                            .scaleY(0.97f)
                            .setDuration(70L)
                            .withEndAction {
                                animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(90L)
                                    .start()

                                showSpaceInterpretation(node)
                            }
                            .start()
                    }
                }

            container.addView(
                continueButton,
                android.widget.LinearLayout.LayoutParams(
                    -1,
                    dp(56)
                ).apply {
                    topMargin = dp(14)
                }
            )

            scroll.addView(container)

            AlertDialog.Builder(this@CosmosActivity)
                .setView(scroll)
                .setNegativeButton("CLOSE", null)
                .show()
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {

            if (event.action != MotionEvent.ACTION_UP) {
                return true
            }

            for (i in nodes.indices) {

                val node = nodes[i]

                val x = scaleX(node.x)

                val y =
                    scaleY(node.y) +
                    kotlin.math.sin(
                        phase * Math.PI * 2.0 +
                        i.toDouble()
                    ).toFloat() * 5f

                val dx = event.x - x
                val dy = event.y - y

                val distance =
                    kotlin.math.sqrt(
                        dx * dx + dy * dy
                    )

                if (distance <= node.radius * 1.35f) {

                    selectedNode = i
                    invalidate()

                    showSpaceNodePanel(node)

                    return true
                }
            }

            return true
        }
    }


    // ================================================================
    // SPACE INTERPRETATION ENGINE
    // 7 NODES × 3000 QUESTIONS = 21,000 NODE-SPECIFIC QUESTIONS
    // ================================================================

    // ================================================================
    // SPACE QUESTION HOLOGRAM — STEP 1
    // Native cinematic floating cosmic visual
    // ================================================================

    // ================================================================
    // SPACE QUESTION DEPTH — STEP 3
    // Progressive cosmic depth from Q1 → Q4
    // ================================================================

    private inner class SpaceDepthView(
        private val depthLevel: Int
    ) : android.view.View(this@CosmosActivity) {

        private val paint =
            android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

        private val stars =
            ArrayList<Triple<Float, Float, Float>>()

        private var phase = 0f

        private val animator =
            android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 5200L
                repeatCount = android.animation.ValueAnimator.INFINITE
                addUpdateListener {
                    phase = it.animatedValue as Float
                    invalidate()
                }
            }

        init {
            val random = java.util.Random(700L + depthLevel)

            repeat(42) {
                stars.add(
                    Triple(
                        random.nextFloat(),
                        random.nextFloat(),
                        0.35f + random.nextFloat() * 0.65f
                    )
                )
            }

            animator.start()
        }

        override fun onDraw(canvas: android.graphics.Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()

            val depth =
                (depthLevel.coerceIn(0, 3) / 3f)

            paint.style = android.graphics.Paint.Style.FILL

            stars.forEachIndexed { index, star ->

                val baseX = star.first * w
                val baseY = star.second * h

                val centerX = w / 2f
                val centerY = h / 2f

                val zoom =
                    1f + depth * 0.55f

                val drift =
                    kotlin.math.sin(
                        phase * Math.PI * 2.0 +
                            index * 0.37
                    ).toFloat() * (2f + depth * 4f)

                val x =
                    centerX +
                        (baseX - centerX) * zoom

                val y =
                    centerY +
                        (baseY - centerY) * zoom +
                        drift

                if (x < -10f ||
                    x > w + 10f ||
                    y < -10f ||
                    y > h + 10f
                ) {
                    return@forEachIndexed
                }

                val size =
                    (0.7f + depth * 1.4f) *
                        star.third

                paint.color =
                    if (index % 7 == 0)
                        android.graphics.Color.CYAN
                    else
                        android.graphics.Color.WHITE

                paint.alpha =
                    (70 + depth * 100).toInt()

                canvas.drawCircle(
                    x,
                    y,
                    size,
                    paint
                )
            }
        }

        override fun onDetachedFromWindow() {
            animator.cancel()
            super.onDetachedFromWindow()
        }
    }

    private inner class SpaceHologramView(
        private val node: SpaceNode
    ) : android.view.View(this@CosmosActivity) {

        private val glowPaint = android.graphics.Paint(
            android.graphics.Paint.ANTI_ALIAS_FLAG
        )

        private val corePaint = android.graphics.Paint(
            android.graphics.Paint.ANTI_ALIAS_FLAG
        )

        private var phase = 0f

        private val animator =
            android.animation.ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 2600L
                repeatCount = android.animation.ValueAnimator.INFINITE
                repeatMode = android.animation.ValueAnimator.REVERSE
                addUpdateListener {
                    phase = it.animatedValue as Float
                    invalidate()
                }
            }

        init {
            setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null)
            animator.start()
        }

        override fun onDraw(canvas: android.graphics.Canvas) {
            super.onDraw(canvas)

            val cx = width / 2f
            val cy = height / 2f

            val floatY =
                kotlin.math.sin(
                    phase * Math.PI * 2.0
                ).toFloat() * dp(5)

            val pulse =
                0.78f +
                    kotlin.math.sin(
                        phase * Math.PI * 2.0
                    ).toFloat() * 0.12f

            val color =
                when {
                    node.name.contains("DARK ENERGY") ->
                        Color.rgb(255, 210, 70)

                    node.name.contains("BLACK HOLES") ||
                        node.name.contains("DARK MATTER") ->
                        Color.rgb(170, 100, 255)

                    else ->
                        Color.rgb(0, 220, 255)
                }

            // Outer atmospheric glow
            glowPaint.style = android.graphics.Paint.Style.FILL
            glowPaint.color = color
            glowPaint.alpha = (42 * pulse).toInt().coerceIn(0, 255)
            glowPaint.maskFilter =
                android.graphics.BlurMaskFilter(
                    dp(18).toFloat(),
                    android.graphics.BlurMaskFilter.Blur.NORMAL
                )

            canvas.drawCircle(
                cx,
                cy + floatY,
                dp(30).toFloat(),
                glowPaint
            )

            // Orbit ring
            glowPaint.style = android.graphics.Paint.Style.STROKE
            glowPaint.strokeWidth = dp(2).toFloat()
            glowPaint.alpha = (125 * pulse).toInt()
            glowPaint.maskFilter =
                android.graphics.BlurMaskFilter(
                    dp(3).toFloat(),
                    android.graphics.BlurMaskFilter.Blur.NORMAL
                )

            val orbit = android.graphics.RectF(
                cx - dp(34),
                cy - dp(13) + floatY,
                cx + dp(34),
                cy + dp(13) + floatY
            )

            canvas.drawOval(orbit, glowPaint)

            // Core
            corePaint.style = android.graphics.Paint.Style.FILL
            corePaint.color = color
            corePaint.alpha = (215 * pulse).toInt()
            corePaint.setShadowLayer(
                dp(8).toFloat(),
                0f,
                0f,
                color
            )

            canvas.drawCircle(
                cx,
                cy + floatY,
                dp(8).toFloat(),
                corePaint
            )

            // Node icon
            corePaint.clearShadowLayer()
            corePaint.color = Color.WHITE
            corePaint.alpha = 255
            corePaint.textSize = dp(28).toFloat()
            corePaint.textAlign = android.graphics.Paint.Align.CENTER
            corePaint.typeface = Typeface.DEFAULT

            canvas.drawText(
                node.icon,
                cx,
                cy + floatY + dp(10),
                corePaint
            )
        }

        override fun onDetachedFromWindow() {
            animator.cancel()
            super.onDetachedFromWindow()
        }
    }

    private fun showSpaceInterpretation(node: SpaceNode) {
        val questions = ArrayList<String>()
        while (questions.size < 4) {
            val nextQuestion = getDynamicSpaceQuestion(node)
            if (!questions.contains(nextQuestion)) {
                questions.add(nextQuestion)
            }
        }

        val answers = MutableList(4) { "" }
        var questionIndex = 0

        val dialogContainer =
            android.widget.LinearLayout(this@CosmosActivity).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(dp(24), dp(12), dp(24), dp(8))
                setBackgroundColor(Color.rgb(3, 5, 22))
            }

        fun addText(
            text: String,
            size: Float,
            color: Int,
            paddingTop: Int = 6,
            paddingBottom: Int = 6
        ) {
            dialogContainer.addView(
                TextView(this@CosmosActivity).apply {
                    this.text = text
                    textSize = size
                    setTextColor(color)
                    setPadding(0, dp(paddingTop), 0, dp(paddingBottom))
                }
            )
        }

        lateinit var dialog: AlertDialog
        lateinit var nextButton: android.widget.Button

        fun showQuestion() {
            dialogContainer.removeAllViews()

            val questionNumber = questionIndex + 1
            val selectedAnswer = answers[questionIndex]

            val depthView =
                SpaceDepthView(questionIndex)

            dialogContainer.addView(
                depthView,
                0,
                android.widget.LinearLayout.LayoutParams(
                    -1,
                    dp(24)
                )
            )

            val hologramView = SpaceHologramView(node)

            dialogContainer.addView(
                hologramView,
                android.widget.LinearLayout.LayoutParams(
                    -1,
                    dp(52)
                ).apply {
                    bottomMargin = dp(0)
                }
            )

            addText(
                "${node.icon}  ${node.name}",
                24f,
                Color.WHITE
            )

            addText(
                "SPACE INTERPRETATION",
                13f,
                Color.rgb(150, 170, 255)
            )

            addText(
                "QUESTION $questionNumber OF 4",
                17f,
                Color.CYAN,
                14,
                4
            )

            addText(
                "●".repeat(questionNumber) +
                    "○".repeat(4 - questionNumber),
                15f,
                Color.rgb(120, 210, 255),
                2,
                12
            )

            val radarView =
                TextView(this@CosmosActivity).apply {
                    text =
                        "📡 LIVE CONTEXT RADAR    " +
                        "●".repeat(questionNumber) +
                        "○".repeat(4 - questionNumber)

                    textSize = 12f
                    setTextColor(Color.rgb(80, 220, 255))
                    setPadding(
                        dp(0),
                        dp(2),
                        dp(0),
                        dp(6)
                    )
                }

            radarView.tag = "GDMIE_RADAR"
            dialogContainer.addView(radarView)

            val questionView =
                TextView(this@CosmosActivity).apply {
                    text = "⌁ SIGNAL DETECTED  •  DECODING..."
                    textSize = 18f
                    setTextColor(Color.rgb(120, 220, 255))
                    setPadding(0, dp(4), 0, dp(12))
                }

            dialogContainer.addView(questionView)

            val fullQuestion = questions[questionIndex]
            val decodeChars = "01ABCDEFGHIJKLMNOPQRSTUVWXYZ"

            questionView.postDelayed({
                questionView.text = "⌁ CONTEXT SIGNAL  •  SYNCHRONIZING..."

                questionView.postDelayed({
                    var revealed = 0

                    fun revealNextCharacter() {
                        if (revealed >= fullQuestion.length) {
                            questionView.text = fullQuestion
                            questionView.setTextColor(Color.WHITE)
                            return
                        }

                        revealed++

                        val visibleText = fullQuestion.substring(0, revealed)
                        val remaining = minOf(
                            3,
                            fullQuestion.length - revealed
                        )

                        val codeTail =
                            buildString {
                                repeat(remaining) {
                                    append(
                                        decodeChars[
                                            (revealed + it * 7) %
                                                decodeChars.length
                                        ]
                                    )
                                }
                            }

                        questionView.text =
                            "$visibleText$codeTail"

                        questionView.postDelayed(
                            { revealNextCharacter() },
                            9L
                        )
                    }

                    questionView.setTextColor(
                        Color.rgb(0, 220, 255)
                    )
                    revealNextCharacter()
                }, 260L)
            }, 220L)

            addText(
                "SELECT ONE OPTION",
                14f,
                Color.rgb(180, 190, 215),
                4,
                8
            )

            val options = getShuffledSpaceOptions(node, questionIndex)
            val optionButtons = ArrayList<android.widget.Button>()

            options.forEachIndexed { displayIndex, option ->
                val cleanOption = option
                    .replaceFirst(Regex("^[A-D]\\s*•\\s*"), "")

                val parts = cleanOption.split(" • ", limit = 2)
                val optionTitle = parts.getOrElse(0) { cleanOption }
                val optionValue = parts.getOrNull(1) ?: ""

                val displayLabel =
                    when (displayIndex) {
                        0 -> "A"
                        1 -> "B"
                        2 -> "C"
                        else -> "D"
                    }

                val button =
                    android.widget.Button(this@CosmosActivity).apply {
                        text =
                            if (optionValue.isNotBlank()) {
                                "$displayLabel • $optionTitle\n    $optionValue"
                            } else {
                                "$displayLabel • $optionTitle"
                            }

                        textSize = 14f
                        isAllCaps = false
                        gravity = Gravity.CENTER_VERTICAL or Gravity.START
                        setPadding(
                            dp(18),
                            dp(8),
                            dp(18),
                            dp(8)
                        )

                        setLayerType(
                            android.view.View.LAYER_TYPE_SOFTWARE,
                            null
                        )

                        if (selectedAnswer == option) {
                            setBackgroundColor(Color.rgb(0, 205, 235))
                            setTextColor(Color.BLACK)
                            setShadowLayer(
                                dp(10).toFloat(),
                                0f,
                                0f,
                                Color.rgb(0, 220, 255)
                            )
                        } else {
                            setBackgroundColor(Color.rgb(32, 38, 58))
                            setTextColor(Color.WHITE)
                            setShadowLayer(
                                dp(4).toFloat(),
                                0f,
                                0f,
                                Color.rgb(70, 90, 150)
                            )
                        }

                        setOnClickListener {
                            answers[questionIndex] = option

                            val radarText =
                                "📡 LIVE CONTEXT RADAR    " +
                                "●".repeat(questionIndex + 1) +
                                "○".repeat(3 - questionIndex)

                            dialogContainer
                                .findViewWithTag<TextView>("GDMIE_RADAR")
                                ?.apply {
                                    text = radarText
                                    setTextColor(
                                        Color.rgb(0, 240, 255)
                                    )
                                }

                            optionButtons.forEach { other ->
                                if (other == this) {
                                    other.setBackgroundColor(
                                        Color.rgb(0, 205, 235)
                                    )
                                    other.setTextColor(Color.BLACK)
                                    other.setShadowLayer(
                                        dp(10).toFloat(),
                                        0f,
                                        0f,
                                        Color.rgb(0, 220, 255)
                                    )
                                } else {
                                    other.setBackgroundColor(
                                        Color.rgb(32, 38, 58)
                                    )
                                    other.setTextColor(Color.WHITE)
                                    other.setShadowLayer(
                                        dp(4).toFloat(),
                                        0f,
                                        0f,
                                        Color.rgb(70, 90, 150)
                                    )
                                }
                            }

                            nextButton.isEnabled = true
                            nextButton.setTextColor(Color.BLACK)
                            nextButton.setBackgroundColor(
                                Color.rgb(0, 220, 255)
                            )
                        }
                    }

                optionButtons.add(button)

                dialogContainer.addView(
                    button,
                    android.widget.LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                    ).apply {
                        bottomMargin = dp(6)
                    }
                )
            }

            addText(
                "UNKNOWN ≠ ANSWER",
                15f,
                Color.rgb(255, 210, 70),
                10,
                4
            )

            addText(
                "Your selections become part of the decision context. " +
                    "GDMIE does not invent missing evidence.",
                13f,
                Color.rgb(170, 180, 205),
                2,
                4
            )

            val navigation =
                android.widget.LinearLayout(this@CosmosActivity).apply {
                    orientation =
                        android.widget.LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                }

            val backButton =
                android.widget.Button(this@CosmosActivity).apply {
                    text = "← BACK"
                    textSize = 13f
                    isAllCaps = false

                    if (questionIndex == 0) {
                        isEnabled = false
                        setTextColor(Color.rgb(90, 95, 110))
                        setBackgroundColor(Color.rgb(30, 32, 45))
                    } else {
                        isEnabled = true
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.rgb(55, 60, 78))

                        setOnClickListener {
                            questionIndex--
                            showQuestion()
                        }
                    }
                }

            nextButton =
                android.widget.Button(this@CosmosActivity).apply {
                    text =
                        if (questionIndex == 3)
                            "COMPLETE CONTEXT →"
                        else
                            "NEXT →"

                    textSize = 13f
                    isAllCaps = false

                    if (selectedAnswer.isBlank()) {
                        isEnabled = false
                        setTextColor(Color.rgb(120, 125, 140))
                        setBackgroundColor(Color.rgb(35, 38, 52))
                    } else {
                        isEnabled = true
                        setTextColor(Color.BLACK)
                        setBackgroundColor(Color.rgb(0, 220, 255))
                    }

                    setOnClickListener {
                        if (answers[questionIndex].isBlank()) {
                            return@setOnClickListener
                        }

                        GDMIEAudioManager.playUiClick(this@CosmosActivity)

                        if (questionIndex < 3) {
                            questionIndex++
                            showQuestion()
                        } else {
                            showSpaceCompleteContext(
                                node,
                                questions,
                                answers,
                                dialog
                            )
                        }
                    }
                }

            navigation.addView(
                backButton,
                android.widget.LinearLayout.LayoutParams(
                    0,
                    dp(52),
                    1f
                ).apply {
                    marginEnd = dp(6)
                }
            )

            navigation.addView(
                nextButton,
                android.widget.LinearLayout.LayoutParams(
                    0,
                    dp(52),
                    1f
                ).apply {
                    marginStart = dp(6)
                }
            )

            dialogContainer.addView(
                navigation,
                android.widget.LinearLayout.LayoutParams(
                    -1,
                    dp(52)
                )
            )

            dialog.setView(dialogContainer)
        }

        dialog =
            AlertDialog.Builder(this@CosmosActivity)
                .setView(dialogContainer)
                .create()

        dialog.setOnShowListener {
            showQuestion()
        }

        dialog.show()
    }

    private fun getShuffledSpaceOptions(
        node: SpaceNode,
        questionIndex: Int
    ): Array<String> {
        val baseOptions = getSpaceOptions(node, questionIndex)
        return baseOptions.toList().shuffled().toTypedArray()
    }

    private fun getSpaceOptions(
        node: SpaceNode,
        questionIndex: Int
    ): Array<String> {

        return when (node.name) {

            "UNIVERSE" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Current Evidence • 10%",
                    "B • Missing Information • 25%",
                    "C • Alternative Explanations • 50%",
                    "D • Unverifiable Factors • 75%"
                )
                1 -> arrayOf(
                    "A • Stable Pattern • 1–3×",
                    "B • Gradual Change • 4–8×",
                    "C • Sudden Shift • 9–15×",
                    "D • No Clear Pattern • 16+×"
                )
                2 -> arrayOf(
                    "A • Hidden Influence • 5–10%",
                    "B • Indirect Effect • 15–30%",
                    "C • Larger System • 40–60%",
                    "D • Unknown Factor • 70–90%"
                )
                else -> arrayOf(
                    "A • Local Context • 1× scope",
                    "B • Related Systems • 3× scope",
                    "C • Wider Environment • 5× scope",
                    "D • Universal Scale • 10× scope"
                )
            }

            "SPACE-TIME" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Immediate Evidence • 1×",
                    "B • Recent History • 3×",
                    "C • Long-Term Context • 6×",
                    "D • Unknown Timing • 10×"
                )
                1 -> arrayOf(
                    "A • Seconds • 1–10 s",
                    "B • Minutes • 10–60 min",
                    "C • Days • 1–7 days",
                    "D • Years • 1–10 years"
                )
                2 -> arrayOf(
                    "A • Earlier Cause • 10%",
                    "B • Current State • 30%",
                    "C • Future Effect • 60%",
                    "D • Unknown Sequence • 90%"
                )
                else -> arrayOf(
                    "A • One Time Point • 1×",
                    "B • Two Time Points • 2×",
                    "C • Multiple Stages • 5×",
                    "D • Full Timeline • 10×"
                )
            }

            "EXPANDING UNIVERSE" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Small Change • 1–5%",
                    "B • Moderate Growth • 10–25%",
                    "C • Strong Growth • 30–60%",
                    "D • Major Expansion • 70–100%"
                )
                1 -> arrayOf(
                    "A • Stable • 1×",
                    "B • Increasing • 2×",
                    "C • Accelerating • 5×",
                    "D • Rapid Shift • 10×"
                )
                2 -> arrayOf(
                    "A • Emerging Signal • 5–10%",
                    "B • Developing Trend • 20–40%",
                    "C • Established Movement • 50–70%",
                    "D • Unknown Driver • 75–95%"
                )
                else -> arrayOf(
                    "A • Present State • 1×",
                    "B • Next Stage • 2×",
                    "C • Long-Term Direction • 5×",
                    "D • Wider Expansion • 10×"
                )
            }

            "BLACK HOLES" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Visible Risk • 10%",
                    "B • Hidden Risk • 30%",
                    "C • Concentrated Risk • 60%",
                    "D • Extreme Unknown • 85%"
                )
                1 -> arrayOf(
                    "A • Surface Signal • 1×",
                    "B • Deeper Factor • 3×",
                    "C • Hidden Relationship • 5×",
                    "D • Deep Unknown • 10×"
                )
                2 -> arrayOf(
                    "A • Low Visibility • 5–15%",
                    "B • Partial Blind Spot • 20–40%",
                    "C • Major Blind Spot • 50–75%",
                    "D • Unknown Boundary • 80–95%"
                )
                else -> arrayOf(
                    "A • One Risk Factor • 1×",
                    "B • Several Factors • 3×",
                    "C • Concentrated Influence • 5×",
                    "D • Extreme Uncertainty • 10×"
                )
            }

            "DARK MATTER" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Visible Evidence • 10%",
                    "B • Indirect Evidence • 30%",
                    "C • Missing Evidence • 60%",
                    "D • Unknown Influence • 90%"
                )
                1 -> arrayOf(
                    "A • Direct Effect • 1×",
                    "B • Indirect Effect • 2×",
                    "C • Background Influence • 5×",
                    "D • Unexplained Effect • 10×"
                )
                2 -> arrayOf(
                    "A • Small Gap • 5–10%",
                    "B • Moderate Gap • 20–35%",
                    "C • Large Gap • 40–70%",
                    "D • Unknown Gap • 75–95%"
                )
                else -> arrayOf(
                    "A • Immediate Context • 1×",
                    "B • Connected Context • 3×",
                    "C • Background Context • 5×",
                    "D • Hidden Context • 10×"
                )
            }

            "DARK ENERGY" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Low Pressure • 10%",
                    "B • Rising Pressure • 30%",
                    "C • Strong Pressure • 60%",
                    "D • Extreme Driver • 85%"
                )
                1 -> arrayOf(
                    "A • Slow Movement • 1×",
                    "B • Steady Movement • 3×",
                    "C • Accelerating Movement • 5×",
                    "D • Rapid Movement • 10×"
                )
                2 -> arrayOf(
                    "A • Early Signal • 5–15%",
                    "B • Developing Force • 20–40%",
                    "C • Strong Momentum • 50–75%",
                    "D • Unknown Driver • 80–95%"
                )
                else -> arrayOf(
                    "A • Local Pressure • 1×",
                    "B • Connected Pressure • 3×",
                    "C • System Pressure • 5×",
                    "D • Large-Scale Driver • 10×"
                )
            }

            "THE UNKNOWN" -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Known Gap • 10%",
                    "B • Uncertain Factor • 30%",
                    "C • Unresolved Factor • 60%",
                    "D • True Unknown • 90%"
                )
                1 -> arrayOf(
                    "A • Low Uncertainty • 1×",
                    "B • Moderate Uncertainty • 3×",
                    "C • High Uncertainty • 5×",
                    "D • Extreme Uncertainty • 10×"
                )
                2 -> arrayOf(
                    "A • Small Possibility • 5–10%",
                    "B • Possible Factor • 20–35%",
                    "C • Strong Possibility • 40–70%",
                    "D • Unresolved Possibility • 75–95%"
                )
                else -> arrayOf(
                    "A • What We Know • 1×",
                    "B • What We Suspect • 3×",
                    "C • What We Need • 5×",
                    "D • What We Cannot Know Yet • 10×"
                )
            }

            else -> when (questionIndex) {
                0 -> arrayOf(
                    "A • Direct Evidence • 10%",
                    "B • Missing Evidence • 30%",
                    "C • Alternative Explanation • 60%",
                    "D • Unverified Factor • 90%"
                )
                1 -> arrayOf(
                    "A • Stable Pattern • 1–3×",
                    "B • Gradual Change • 4–8×",
                    "C • Sudden Shift • 9–15×",
                    "D • No Clear Pattern • 16+×"
                )
                2 -> arrayOf(
                    "A • Hidden Influence • 5–10%",
                    "B • Indirect Effect • 15–30%",
                    "C • Larger System • 40–60%",
                    "D • Unknown Factor • 70–90%"
                )
                else -> arrayOf(
                    "A • Local Context • 1×",
                    "B • Related Systems • 3×",
                    "C • Wider Environment • 5×",
                    "D • Universal Scale • 10×"
                )
            }
        }
    }

    private fun showSpaceCompleteContext(
        node: SpaceNode,
        questions: List<String>,
        answers: List<String>,
        dialog: AlertDialog
    ) {
        // Close the Q4 dialog first.
        dialog.dismiss()

        val container =
            android.widget.LinearLayout(this@CosmosActivity).apply {
                orientation = android.widget.LinearLayout.VERTICAL
                setPadding(
                    dp(24),
                    dp(20),
                    dp(24),
                    dp(12)
                )
                setBackgroundColor(Color.rgb(3, 5, 22))
            }

        fun addText(
            text: String,
            size: Float,
            color: Int
        ) {
            container.addView(
                TextView(this@CosmosActivity).apply {
                    this.text = text
                    textSize = size
                    setTextColor(color)
                    setPadding(
                        0,
                        dp(6),
                        0,
                        dp(6)
                    )
                }
            )
        }

        addText(
            "${node.icon}  ${node.name}",
            24f,
            Color.WHITE
        )

        addText(
            "4 RESPONSES CAPTURED",
            14f,
            Color.CYAN
        )

        addText(
            "COMPLETE CONTEXT",
            20f,
            Color.WHITE
        )

        answers.forEachIndexed { index, answer ->
            addText(
                "Q${index + 1}  •  $answer",
                14f,
                Color.rgb(215, 220, 240)
            )
        }

        addText(
            "Your four selections are now one complete decision context.",
            14f,
            Color.rgb(180, 190, 215)
        )

        addText(
            "GDMIE is processing this context...",
            15f,
            Color.rgb(150, 170, 255)
        )

        val contextDialog =
            AlertDialog.Builder(this@CosmosActivity)
                .setView(container)
                .setPositiveButton(
                    "CONTINUE → ANALYSIS"
                ) { _, _ ->

                    val scores = answers.map { answer ->
                        when {
                            answer.startsWith("A") -> 2.0
                            answer.startsWith("B") -> 0.0
                            answer.startsWith("C") -> 1.0
                            answer.startsWith("D") -> -1.0
                            else -> 0.0
                        }
                    }

                    val presentValue =
                        ((scores.getOrElse(0) { 0.0 } + 1.0) / 3.0 * 100.0)
                            .coerceIn(0.0, 100.0)

                    val expectedValue =
                        ((scores.getOrElse(1) { 0.0 } + 1.0) / 3.0 * 100.0)
                            .coerceIn(0.0, 100.0)

                    val momentumValue =
                        ((scores.getOrElse(2) { 0.0 } + 1.0) / 3.0 * 100.0)
                            .coerceIn(0.0, 100.0)

                    val riskValue =
                        ((1.0 - ((scores.getOrElse(3) { 0.0 } + 1.0) / 3.0)) * 100.0)
                            .coerceIn(0.0, 100.0)

                    val targetValue =
                        ((presentValue + expectedValue) / 2.0)
                            .coerceIn(0.0, 100.0)

                    val input = GDMInput(
                        presentValue = presentValue,
                        expectedValue = expectedValue,
                        targetValue = targetValue,
                        recentMomentum = momentumValue,
                        immediateMomentum = momentumValue,
                        twoMinMarketAdvantage = 0.0,
                        exactMarketLine = targetValue,
                        oddsMovement = 0.0,
                        timingFactor = 50.0,
                        riskFactor = riskValue
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val result =
                                GDMIEEngineGateway.calculate(input)

                            runOnUiThread {
                                val worked =
                                    answers.count {
                                        it.startsWith("A")
                                    }

                                val uncertain =
                                    answers.count {
                                        it.startsWith("B")
                                    }

                                val changing =
                                    answers.count {
                                        it.startsWith("C")
                                    }

                                val low =
                                    answers.count {
                                        it.startsWith("D")
                                    }

                                val analysis = StringBuilder()

                                analysis.append(
                                    "${node.icon} ${node.name}\n\n"
                                )

                                analysis.append(
                                    "RESULT ANALYSIS\n\n"
                                )

                                analysis.append(
                                    "✓ WHAT WORKED\n"
                                )

                                analysis.append(
                                    if (worked > 0) {
                                        "$worked selection(s) identified an important factor."
                                    } else {
                                        "No strong-factor selection was identified."
                                    }
                                )

                                analysis.append(
                                    "\n\n⚠ WHAT WAS WEAK\n"
                                )

                                analysis.append(
                                    if (uncertain + low > 0) {
                                        "${uncertain + low} selection(s) indicate uncertainty or currently low significance."
                                    } else {
                                        "No major weak-context signal was identified."
                                    }
                                )

                                analysis.append(
                                    "\n\n✕ WHAT WAS MISSED\n"
                                )

                                analysis.append(
                                    if (changing == 0) {
                                        "No changing/emerging factor was selected."
                                    } else {
                                        "$changing selection(s) identified changing or emerging conditions."
                                    }
                                )

                                analysis.append(
                                        "\n\n🎯 FINAL SIGNAL\n\n" +
                                        result.decision +
                                        "\n\n" +
                                        "CONFIDENCE: %.0f%%".format(
                                            result.confidence * 100
                                        )
                                    )
                                AlertDialog.Builder(this@CosmosActivity)
                                    .setTitle("🧠 GDMIE ANALYSIS")
                                    .setMessage(analysis.toString())
                                    .setNeutralButton("🎁 WATCH +10 XP") { _, _ ->
                                        RewardedAdManager.show(this@CosmosActivity) { awarded ->
                                            Toast.makeText(
                                                this@CosmosActivity,
                                                "+$awarded XP earned!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .setPositiveButton("DONE") { d, _ ->
                                        d.dismiss()
                                        showSpaceReward(node)
                                    }
                                    .show()
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                AlertDialog.Builder(this@CosmosActivity)
                                    .setTitle("GDMIE ANALYSIS")
                                    .setMessage(
                                        "Analysis could not be completed.\n\n" +
                                            (e.message ?: "Please try again.")
                                    )
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        }
                    }
                }
                .create()

        contextDialog.show()
    }

    private fun getDynamicSpaceQuestion(node: SpaceNode): String {
        val questionBank = mapOf(
            "UNIVERSE" to listOf(
                "What larger cosmic pattern could be influencing this situation?",
                "What could exist beyond what is currently observable?",
                "What connection between different parts of the universe might matter here?",
                "What wider cosmic context could change your interpretation?",
                "What alternative explanation could fit the observed evidence?",
                "What larger structure might this situation belong to?",
                "What could become visible from a much wider cosmic perspective?",
                "What part of the universe remains outside your current view?",
                "What relationship between distant factors could be important?",
                "What larger pattern could connect seemingly separate observations?",
                "What possibility lies beyond the current field of observation?",
                "What could the observable evidence be missing?",
                "What larger system could explain the pattern you see?",
                "What cosmic scale might change the meaning of the situation?",
                "What remains uncertain when the view expands beyond the immediate evidence?",
                "What hidden connection could exist between different observations?",
                "What could be happening beyond the obvious cosmic signal?",
                "What larger process might be unfolding across the universe?",
                "What assumption could change when you consider the whole universe?",
                "What evidence would strengthen a cosmic-scale interpretation?",
                "What possibility should remain open until more evidence appears?",
                "What pattern could emerge across a much larger scale?",
                "What could be true even without direct observation?",
                "What information would provide a wider cosmic perspective?",
                "What part of the larger picture is still missing?",
                "What unknown factor could influence the observed pattern?",
                "What could connect local observations to a universal process?",
                "What larger relationship could change the current interpretation?",
                "What should be investigated before treating the cosmic interpretation as certain?",
                "What remains unknown when the observable universe is the context?"
            ),
            "SPACE-TIME" to listOf(
                "What earlier event could be connected to what you observe now?",
                "How could the passage of time change this interpretation?",
                "What future development could alter the current picture?",
                "What part of the timeline is still unknown?",
                "What sequence of events might explain the present state?",
                "What could have changed between the earlier and current states?",
                "What delayed effect might become visible later?",
                "What timing information is missing from the current picture?",
                "What relationship between time and change could matter here?",
                "What happened before the current observation?",
                "What could happen after the current state?",
                "What event might connect the past and present?",
                "What future possibility depends on the current state?",
                "What part of the sequence cannot yet be confirmed?",
                "What time scale could change the interpretation?",
                "What pattern might appear when the situation is viewed across time?",
                "What could look different at another point in time?",
                "What cause may have occurred earlier than expected?",
                "What effect may only become visible later?",
                "What timing assumption could be misleading?",
                "What information would complete the timeline?",
                "What stage of the process are you actually observing?",
                "What could be happening between two known events?",
                "What unknown sequence could connect the available evidence?",
                "What change could accelerate or slow the process?",
                "What long-term context might be missing?",
                "What short-term signal might hide a longer pattern?",
                "What future observation could clarify the present?",
                "What part of the timeline should remain uncertain?",
                "What could the complete timeline reveal that the present alone cannot?"
            ),
            "EXPANDING UNIVERSE" to listOf(
                "What could be changing as the larger system expands?",
                "What pattern might appear during continued expansion?",
                "What could be driving the observed growth?",
                "What direction might the expansion take next?",
                "What could accelerate the current movement?",
                "What could slow the apparent expansion?",
                "What larger process might be behind the change?",
                "What could become more significant as the system grows?",
                "What relationship between growth and change might matter?",
                "What evidence would indicate continued expansion?",
                "What could happen if the current trend continues?",
                "What new pattern might emerge at a larger scale?",
                "What could be changing beyond the current observation?",
                "What factor might influence the rate of expansion?",
                "What could distinguish steady growth from acceleration?",
                "What long-term direction might the system follow?",
                "What could become visible as the scale increases?",
                "What hidden driver might influence the expansion?",
                "What could change if the current movement accelerates?",
                "What part of the expansion remains uncertain?",
                "What evidence could reveal the next stage of development?",
                "What larger context could explain the observed growth?",
                "What could be expanding beyond the currently measured region?",
                "What pattern might disappear or strengthen over time?",
                "What could cause the direction of change to shift?",
                "What future state might follow the current expansion?",
                "What assumption about growth should be questioned?",
                "What could remain unknown despite observing expansion?",
                "What larger movement might connect different changes?",
                "What could the expansion reveal about the system's future?"
            ),
            "BLACK HOLES" to listOf(
                "What hidden factor could strongly influence what you observe?",
                "What information might be impossible to observe directly?",
                "Where is the largest blind spot in your interpretation?",
                "What extreme uncertainty could change the entire picture?",
                "What could be hidden behind the visible signal?",
                "What deeper factor might influence the observed effect?",
                "What information could be lost from direct observation?",
                "What boundary might limit what can be known?",
                "What unseen relationship could affect the situation?",
                "What could appear different beyond the visible region?",
                "What evidence might reveal an indirect effect?",
                "What part of the system has the lowest visibility?",
                "What unknown consequence could follow from the hidden factor?",
                "What could concentrate influence into a small region?",
                "What assumption could fail under extreme conditions?",
                "What information might remain inaccessible?",
                "What hidden process could explain the observed signal?",
                "What could be happening beyond the observable boundary?",
                "What uncertainty becomes larger as visibility decreases?",
                "What evidence could distinguish a visible effect from a hidden cause?",
                "What deeper relationship might be missed?",
                "What could change if the hidden factor were revealed?",
                "What part of the interpretation depends on incomplete information?",
                "What extreme condition could change the expected pattern?",
                "What could remain unknown even with more observation?",
                "What blind spot deserves the most attention?",
                "What hidden influence could distort the current interpretation?",
                "What evidence would reduce the uncertainty?",
                "What cannot safely be assumed from the visible signal?",
                "What remains beyond the boundary of reliable knowledge?"
            ),
            "DARK MATTER" to listOf(
                "What unseen influence could explain what you observe?",
                "What indirect evidence could reveal a hidden factor?",
                "Where is the gap between observation and explanation?",
                "What background influence might be missing?",
                "What could be affecting the system without being directly visible?",
                "What hidden relationship might explain the observed movement?",
                "What evidence could point toward an unseen influence?",
                "What part of the explanation remains incomplete?",
                "What effect could exist without direct observation?",
                "What missing factor could connect the available evidence?",
                "What could be inferred indirectly from the observed pattern?",
                "What unexplained effect deserves further investigation?",
                "What hidden structure might influence visible behavior?",
                "What evidence is still missing from the current model?",
                "What could change if the unseen influence were confirmed?",
                "What background factor could be shaping the observation?",
                "What relationship might exist between visible and invisible effects?",
                "What observation could reveal a hidden influence?",
                "What uncertainty comes from incomplete evidence?",
                "What part of the current explanation depends on inference?",
                "What unseen factor could alter the larger context?",
                "What evidence would distinguish influence from coincidence?",
                "What could remain hidden despite strong indirect evidence?",
                "What gap exists between what is measured and what is explained?",
                "What larger system might contain the missing influence?",
                "What unknown effect could be operating quietly?",
                "What indirect pattern could reveal something unseen?",
                "What assumption should remain open until more evidence appears?",
                "What hidden influence could change the current interpretation?",
                "What remains unexplained after considering the visible evidence?"
            ),
            "DARK ENERGY" to listOf(
                "What force could be driving the larger change?",
                "What could be accelerating the current movement?",
                "What pressure might be pushing the system toward change?",
                "What could increase the pace of the observed trend?",
                "What larger driver might influence the direction?",
                "What could cause the current movement to accelerate?",
                "What force might remain outside the immediate observation?",
                "What could sustain the current rate of change?",
                "What evidence could reveal a larger driving influence?",
                "What might happen if the current trend continues?",
                "What could change the direction of the movement?",
                "What hidden driver might be increasing the pace?",
                "What relationship between pressure and movement matters here?",
                "What could explain an accelerating pattern?",
                "What larger process might be pushing the system forward?",
                "What could make the current trend stronger?",
                "What evidence would distinguish steady movement from acceleration?",
                "What force could influence the next stage?",
                "What could happen if the driving influence changes?",
                "What larger context could explain the observed momentum?",
                "What remains uncertain about the source of the movement?",
                "What could increase the scale of the effect?",
                "What might cause the trend to continue?",
                "What could cause the trend to slow?",
                "What unseen driver could alter the direction?",
                "What long-term effect could follow continued acceleration?",
                "What evidence could reveal the underlying driver?",
                "What part of the larger force remains unknown?",
                "What could change the system's future direction?",
                "What larger-scale effect could emerge from the current movement?"
            ),
            "THE UNKNOWN" to listOf(
                "What do you know with reasonable confidence?",
                "What remains uncertain despite the available evidence?",
                "What possibility cannot yet be confirmed or rejected?",
                "What can you honestly say is still unknown?",
                "What information is missing before a stronger conclusion is possible?",
                "What assumption has not yet been verified?",
                "What evidence would reduce the uncertainty?",
                "What possibility should remain open?",
                "What part of the situation cannot currently be explained?",
                "What is known directly versus inferred indirectly?",
                "What remains unresolved after examining the evidence?",
                "What could change if new information appears?",
                "What should not be treated as certain yet?",
                "What unknown factor could influence the outcome?",
                "What information would challenge the current interpretation?",
                "What possibility has not been investigated?",
                "Where does reliable evidence end?",
                "What part of the explanation depends on assumption?",
                "What cannot currently be observed or verified?",
                "What uncertainty has the greatest effect on the interpretation?",
                "What could remain unknown even after further investigation?",
                "What evidence would separate possibility from fact?",
                "What question remains unanswered?",
                "What information should be sought next?",
                "What conclusion would be premature right now?",
                "What alternative possibility should remain open?",
                "What boundary limits current knowledge?",
                "What could be true without enough evidence to confirm it?",
                "What should remain unresolved until reliable evidence appears?",
                "What is the clearest thing you can say about what is still unknown?"
            )
        )

        val bank = questionBank[node.name] ?: questionBank["UNIVERSE"]!!
        return bank.random()
    }

    private fun showSpaceReward(
        node: SpaceNode,
        timeSelection: String? = null
    ) {
        val rewardId = ("SPACE_REWARD_" + node.name).hashCode()

        val rewardLayout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(24), dp(28), dp(24))
        }

        val ship = TextView(this).apply {
            text = "🚀"
            textSize = 58f
            gravity = Gravity.CENTER
        }

        val status = TextView(this).apply {
            text = "SPACE SHIP ARRIVES"
            textSize = 20f
            setTextColor(Color.rgb(0, 235, 255))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, dp(12), 0, dp(10))
        }

        val detail = TextView(this).apply {
            text = "Preparing exploration reward..."
            textSize = 14f
            setTextColor(Color.rgb(180, 190, 215))
            gravity = Gravity.CENTER
        }

        rewardLayout.addView(ship)
        rewardLayout.addView(status)
        rewardLayout.addView(detail)

        val rewardDialog = AlertDialog.Builder(this)
            .setView(rewardLayout)
            .setPositiveButton("CONTINUE", null)
            .create()

        rewardDialog.setOnShowListener {
            val button = rewardDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.isEnabled = false

            ship.animate()
                .translationY(-dp(8).toFloat())
                .alpha(1f)
                .setDuration(900L)
                .withEndAction {
                    status.text = "✨ CONTEXT ACCEPTED"
                      detail.text = when (node.name) {
                          "AIR" ->
                              "Your four-stage AIR context has been accepted."
                          "EARTH" ->
                              "Your four-stage EARTH context has been accepted."
                          else ->
                              "Your four-layer SPACE context has been accepted."
                      }

                    ship.animate()
                        .translationY(0f)
                        .setDuration(500L)
                        .withEndAction {
                            status.text = "🛸 ENERGY / REWARD"
                            detail.text = "GDMIE exploration energy is being transferred..."

                            rewardLayout.postDelayed({
                                val xpResult = GDMIEGameProgress.addAnalysisXpOnce(
                                    this,
                                    rewardId,
                                    5
                                )

                                status.text = if (xpResult.awardedXp > 0) {
                                    "⚡ +${xpResult.awardedXp} XP"
                                } else {
                                    "✓ XP ALREADY CLAIMED"
                                }

                                  detail.text = when (node.name) {
                                      "AIR" ->
                                          "AIR exploration reward processed."
                                      "EARTH" ->
                                          "EARTH exploration reward processed."
                                      else ->
                                          "SPACE exploration reward processed."
                                  }

                                rewardLayout.postDelayed({
                                      status.text = when (node.name) {
                                          "AIR" ->
                                              "🏆 AIR EXPLORATION COMPLETE"
                                          "EARTH" ->
                                              "🏆 EARTH EXPLORATION COMPLETE"
                                          else ->
                                              "🏆 SPACE EXPLORATION COMPLETE"
                                      }
                                      detail.text = when (node.name) {
                                          "AIR" ->
                                              "AIR • CONTEXT • INTELLIGENCE"
                                          "EARTH" ->
                                              "EARTH • CONTEXT • INTELLIGENCE"
                                          else ->
                                              "UNIVERSE • CONTEXT • INTELLIGENCE"
                                      }

                                    button.isEnabled = true

                                button.setOnClickListener {
                                    rewardDialog.dismiss()

                                    when (node.name) {
                                        "AIR" -> {
                                            showAirWindField()
                                        }

                                        "ENERGY" -> {
                                            showEnergyInterpretation()
                                        }

                                        "LIFE" -> {
                                            showLifeInterpretation()
                                        }

                                        "WATER" -> {
                                            showWaterInterpretation()
                                        }

                                        "HUMAN WORLD" -> {
                                            showHumanWorldInterpretation()
                                        }

                                        "EARTH" -> {
                                            showEarthSystemInterpretation()
                                        }

                                        "TIME" -> {
                                            showTimeInterpretation(
                                                timeSelection ?: "NOW"
                                            )
                                        }

                                        else -> {
                                            // Keep existing behavior
                                        }
                                    }
                                }
                                }, 900L)
                            }, 900L)
                        }
                }
        }

        rewardDialog.show()
    }

}
