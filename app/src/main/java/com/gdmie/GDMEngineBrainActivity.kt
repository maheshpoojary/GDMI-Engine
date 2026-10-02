package com.gdmie

import com.gdmie.audio.GDMIEAudioManager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.widget.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class GDMEngineBrainActivity : Activity() {

    private val bg = Color.rgb(3, 6, 16)
    private val cyan = Color.rgb(35, 220, 255)
    private val purple = Color.rgb(145, 80, 255)
    private val gold = Color.rgb(255, 195, 65)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 190)
    private val red = Color.rgb(255, 75, 105)
    private val green = Color.rgb(70, 255, 170)

    private lateinit var scene: LockKeyScene
    private lateinit var brainPanel: LinearLayout
    private lateinit var statusText: TextView
    private lateinit var instructionText: TextView
    private lateinit var root: FrameLayout

    private var solarLayer: FrameLayout? = null
    private var solarImage: ImageView? = null
    private var solarCard: LinearLayout? = null

    private var unlocked = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildScreen()
    }

    private fun buildScreen() {

        root = FrameLayout(this).apply {
            setBackgroundColor(bg)
        }

        scene = LockKeyScene(this)

        root.addView(
            scene,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(28), dp(22), dp(24))
        }

        val title = TextView(this).apply {
            text = "🧠  GDMIE BRAIN"
            textSize = 27f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        overlay.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(45)
            )
        )

        val subtitle = TextView(this).apply {
            text = "UNLOCK THE DECISION ENGINE"
            textSize = 12f
            setTextColor(cyan)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            letterSpacing = 0.14f
        }

        overlay.addView(
            subtitle,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(30)
            )
        )

        instructionText = TextView(this).apply {
            text = "DRAG  1  →  INTO  0"
            textSize = 16f
            setTextColor(gold)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, dp(12), 0, 0)
        }

        overlay.addView(instructionText)

        statusText = TextView(this).apply {
            text = "THE ENGINE IS LOCKED"
            textSize = 12f
            setTextColor(red)
            gravity = Gravity.CENTER
            setPadding(0, dp(6), 0, 0)
        }

        overlay.addView(statusText)

        root.addView(
            overlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            )
        )

        brainPanel = createBrainPanel()

        brainPanel.visibility = View.INVISIBLE
        brainPanel.alpha = 0f
        brainPanel.scaleX = 0.82f
        brainPanel.scaleY = 0.82f

        root.addView(
            brainPanel,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply {
                leftMargin = dp(22)
                rightMargin = dp(22)
            }
        )

        setContentView(root)

        scene.onUnlock = {
            unlockSequence()
        }

        scene.onGatewayComplete = {
            revealBrainPanel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun createBrainPanel(): LinearLayout {

        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(24), dp(28), dp(24), dp(28))
            background = rounded(
                Color.rgb(8, 18, 34),
                26,
                cyan,
                2
            )
            elevation = dp(18).toFloat()
        }

        panel.addView(TextView(this).apply {
            text = "🧠"
            textSize = 58f
            gravity = Gravity.CENTER
            setTextColor(cyan)
        })

        panel.addView(TextView(this).apply {
            text = "GDMIE BRAIN"
            textSize = 25f
            setTextColor(white)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(8))
        })

        panel.addView(TextView(this).apply {
            text = "GENERAL DECISION\n& MATHEMATICAL INTELLIGENCE"
            textSize = 14f
            setTextColor(cyan)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        })

        panel.addView(TextView(this).apply {
            text = "ENGINE UNLOCKED\n\nYour decision can now enter the GDMIE intelligence layer."
            textSize = 13f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(18), 0, 0)
        })

        val continueButton = TextView(this).apply {
            text = "▶  CONTINUE"
            textSize = 13f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            background = rounded(
                Color.argb(150, 20, 45, 75),
                18,
                cyan,
                1
            )
            elevation = dp(8).toFloat()
            setPadding(dp(18), dp(12), dp(18), dp(12))
            isClickable = true
            isFocusable = true

            setOnClickListener {
                GDMIEAudioManager.playUiClick(
                    this@GDMEngineBrainActivity
                )
                animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(70)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(90)
                            .withEndAction {
                                startActivity(
                                    android.content.Intent(
                                        this@GDMEngineBrainActivity,
                                        CosmosActivity::class.java
                                    )
                                )
                            }
                            .start()
                    }
                    .start()
            }
        }

        panel.addView(
            continueButton,
            LinearLayout.LayoutParams(
                dp(190),
                dp(52)
            ).apply {
                topMargin = dp(22)
            }
        )

        return panel
    }

    private fun unlockSequence() {

        if (unlocked) return
        unlocked = true

        // 🔊 GDMIE BRAIN unlock sound
        GDMIEAudioManager.playSfx(
            this,
            R.raw.gdmie_brain_unlock
        )

        instructionText.text = "ACCESS GRANTED"
        instructionText.setTextColor(green)

        statusText.text = "GDMIE ENGINE UNLOCKING..."
        statusText.setTextColor(cyan)

        scene.playUnlockAnimation()
    }

    private fun revealBrainPanel() {

        statusText.text = "POSITIVE ENERGY • ENGINE ONLINE"
        statusText.setTextColor(green)

        brainPanel.visibility = View.VISIBLE

        val scaleX = ObjectAnimator.ofFloat(
            brainPanel,
            View.SCALE_X,
            0.82f,
            1.04f,
            1f
        )

        val scaleY = ObjectAnimator.ofFloat(
            brainPanel,
            View.SCALE_Y,
            0.82f,
            1.04f,
            1f
        )

        val alpha = ObjectAnimator.ofFloat(
            brainPanel,
            View.ALPHA,
            0f,
            1f
        )

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 550

            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // 🧠 BRAIN REVEAL COMPLETE
                        // Stay directly on the GDMIE Brain.
                    }
                }
            )

            start()
        }
    }

    
    // ============================================================
    // 🌌 GDMIE SOLAR → EARTH CINEMATIC TRANSITION
    // ============================================================

    private fun startSolarEarthTransition() {
        val layer = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
        }

        val image = ImageView(this).apply {
            setImageResource(com.gdmie.R.drawable.gdmie_solar_earth)
            scaleType = ImageView.ScaleType.CENTER_CROP
            scaleX = 1f
            scaleY = 1f
        }

        layer.addView(
            image,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        solarLayer = layer
        solarImage = image

        root.addView(
            layer,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // Hide the Brain panel as space takes over.
        brainPanel.animate()
            .alpha(0f)
            .scaleX(0.94f)
            .scaleY(0.94f)
            .setDuration(450L)
            .start()

        layer.animate()
            .alpha(1f)
            .setDuration(700L)
            .withEndAction {
                layer.postDelayed({
                    animateEarthLanding(layer, image)
                }, 180L)
            }
            .start()
    }

    private fun animateEarthLanding(
        layer: FrameLayout,
        image: ImageView
    ) {
        layer.post {
            val w = layer.width.toFloat()
            val h = layer.height.toFloat()

            // 🌍 Earth target in the generated artwork.
            // Keep the camera target slightly right and below center.
            val earthX = w * 0.70f
            val earthY = h * 0.60f

            // 🎥 Camera target
            image.pivotX = earthX
            image.pivotY = earthY

            val targetScale = 3.6f
            val targetTranslationX = w * 0.5f - earthX
            val targetTranslationY = h * 0.5f - earthY

            // Start from the original wide-space view.
            image.scaleX = 1f
            image.scaleY = 1f
            image.translationX = 0f
            image.translationY = 0f

            // 🌌 Slow cinematic camera flight.
            image.animate()
                .scaleX(targetScale)
                .scaleY(targetScale)
                .translationX(targetTranslationX)
                .translationY(targetTranslationY)
                .setDuration(5500L)
                .setInterpolator(
                    android.view.animation.AccelerateDecelerateInterpolator()
                )
                .withEndAction {
                    showSolarIntelligenceCard(layer)
                }
                .start()
        }
    }

    private fun showSolarIntelligenceCard(layer: FrameLayout) {
        // 🌑 Space → soft light
        val light = View(this).apply {
            setBackgroundColor(Color.WHITE)
            alpha = 0f
        }

        layer.addView(
            light,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        light.animate()
            .alpha(1f)
            .setDuration(850L)
            .withEndAction {
                createSolarIntelligenceCard(layer)
            }
            .start()
    }

    private fun createSolarIntelligenceCard(layer: FrameLayout) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(28), dp(30), dp(28), dp(30))
            alpha = 0f
            scaleX = 0.92f
            scaleY = 0.92f

            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadius = dp(24).toFloat()
                setStroke(dp(1), Color.rgb(225, 232, 240))
            }

            elevation = dp(14).toFloat()
        }

        val title = TextView(this).apply {
            text = "GDMIE INTELLIGENCE"
            textSize = 15f
            setTextColor(Color.rgb(25, 35, 55))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
        }

        val message = TextView(this).apply {
            text = "Your decision can now enter the GDMIE intelligence layer."
            textSize = 17f
            setTextColor(Color.rgb(45, 55, 75))
            gravity = Gravity.CENTER
            setPadding(0, dp(14), 0, 0)
        }

        card.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        card.addView(
            message,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        layer.addView(
            card,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            ).apply {
                leftMargin = dp(24)
                rightMargin = dp(24)
            }
        )

        solarCard = card

        card.animate()
        .alpha(1f)
        .scaleX(1f)
        .scaleY(1f)
        .setDuration(700L)
        .withEndAction {
            card.postDelayed({
                card.animate()
                    .alpha(0f)
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(500L)
                    .withEndAction {
                        startActivity(
                            android.content.Intent(
                                this@GDMEngineBrainActivity,
                                CosmosActivity::class.java
                            )
                        )
                    }
                    .start()
            }, 1800L)
        }
        .start()
    }

private inner class LockKeyScene(context: Activity) : View(context) {

        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        private var keyX = 0f
        private var keyY = 0f

        private var lockX = 0f
        private var lockY = 0f

        private var dragOffsetX = 0f
        private var dragOffsetY = 0f
        private var dragging = false
        private var aligned = false
        private var unlocking = false

        private var energy = 0f
        private var shake = 0f
        private var rotation = 0f

          // Mechanical unlock progress.
          private var unlockProgress = 0f

          // Extraordinary gateway transition.
          private var gatewayProgress = 0f
          private var gatewayAnimator: ValueAnimator? = null

    // 🚪 GDMIE futuristic Brain door visual
    private val brainDoorBitmap by lazy {
        BitmapFactory.decodeResource(
            resources,
            R.drawable.gdmie_brain_door
        )
    }

        var onUnlock: (() -> Unit)? = null
        var onGatewayComplete: (() -> Unit)? = null

          private val particles = ArrayList<Particle>()

        init {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)

            for (i in 0 until 55) {
                particles.add(
                    Particle(
                        Math.random().toFloat(),
                        Math.random().toFloat(),
                        0.3f + Math.random().toFloat() * 1.7f,
                        Math.random().toFloat() * 6.28f
                    )
                )
            }

            startAmbientAnimation()
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

            lockX = w * 0.50f
            lockY = h * 0.53f

            if (keyX == 0f) {
                keyX = w * 0.25f
                keyY = h * 0.53f
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            drawAtmosphere(canvas)
            drawEnergyParticles(canvas)
            drawLock(canvas)
            drawKey(canvas)
              if (gatewayProgress > 0f) {
                  drawGateway(canvas)
              }
        }


        private fun drawAtmosphere(canvas: Canvas) {

            val cx = width * 0.5f
            val cy = height * 0.52f
            val radius = width * 0.72f

            val negative = RadialGradient(
                cx,
                cy,
                radius,
                intArrayOf(
                    Color.argb(65, 255, 40, 80),
                    Color.argb(25, 100, 30, 120),
                    Color.TRANSPARENT
                ),
                floatArrayOf(0f, 0.45f, 1f),
                Shader.TileMode.CLAMP
            )

            paint.shader = negative
            canvas.drawCircle(cx, cy, radius, paint)
            paint.shader = null

            val positive = RadialGradient(
                cx,
                cy,
                radius * 0.65f,
                intArrayOf(
                    Color.argb((energy * 100).toInt(), 0, 220, 255),
                    Color.argb((energy * 45).toInt(), 120, 60, 255),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )

            paint.shader = positive
            canvas.drawCircle(cx, cy, radius * 0.65f, paint)
            paint.shader = null
        }

        private fun drawLock(canvas: Canvas) {

            val sx =
                if (unlocking) sin(shake.toDouble()).toFloat() * dp(3)
                else 0f

            val sy =
                if (unlocking) cos(shake.toDouble()).toFloat() * dp(2)
                else 0f

            val x = lockX + sx
            val y = lockY + sy

            glowPaint.style = Paint.Style.FILL
            glowPaint.color =
                if (aligned || unlocking)
                    Color.argb(100, 35, 220, 255)
                else
                    Color.argb(55, 255, 50, 90)

            glowPaint.maskFilter = BlurMaskFilter(
                dp(if (aligned) 22 else 15).toFloat(),
                BlurMaskFilter.Blur.NORMAL
            )

            canvas.drawRoundRect(
                x - dp(72),
                y - dp(55),
                x + dp(72),
                y + dp(55),
                dp(20).toFloat(),
                dp(20).toFloat(),
                glowPaint
            )

            glowPaint.maskFilter = null

            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(34, 38, 50)

            canvas.drawRoundRect(
                x - dp(64),
                y - dp(48),
                x + dp(64),
                y + dp(48),
                dp(18).toFloat(),
                dp(18).toFloat(),
                paint
            )

            val bodyGradient = LinearGradient(
                x - dp(64).toFloat(),
                y - dp(48).toFloat(),
                x + dp(64).toFloat(),
                y + dp(48).toFloat(),
                Color.rgb(75, 80, 96),
                Color.rgb(18, 21, 31),
                Shader.TileMode.CLAMP
            )

            paint.shader = bodyGradient

            canvas.drawRoundRect(
                x - dp(58),
                y - dp(43),
                x + dp(58),
                y + dp(43),
                dp(15).toFloat(),
                dp(15).toFloat(),
                paint
            )

            paint.shader = null

            // MECHANICAL SHACKLE
            // 0.0 = locked
            // 1.0 = fully opened

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = dp(16).toFloat()
            paint.strokeCap = Paint.Cap.ROUND

            val shackleLift = unlockProgress * dp(38)

            paint.color =
                if (unlockProgress > 0.45f)
                    Color.rgb(120, 210, 225)
                else
                    Color.rgb(100, 105, 120)

            val shackle = RectF(
                x - dp(39),
                y - dp(91) - shackleLift,
                x + dp(39),
                y - dp(8) - shackleLift
            )

            if (unlockProgress < 0.72f) {

                // Locked circular shackle
                canvas.drawArc(
                    shackle,
                    180f,
                    180f,
                    false,
                    paint
                )

            } else {

                // Opening mechanical arms
                canvas.drawArc(
                    shackle,
                    205f,
                    125f,
                    false,
                    paint
                )

                canvas.drawArc(
                    shackle,
                    30f,
                    125f,
                    false,
                    paint
                )
            }

            paint.style = Paint.Style.FILL

            // LOCK ENERGY STATE
            // Red = locked
            // Cyan = activating
            // Bright cyan = unlocked
            paint.color =
                when {
                    unlockProgress >= 0.82f ->
                        Color.rgb(80, 245, 255)

                    unlockProgress > 0.45f ->
                        Color.rgb(35, 205, 255)

                    aligned || unlocking ->
                        Color.rgb(45, 180, 220)

                    else ->
                        red
                }

            paint.textSize = dp(52).toFloat()
            paint.typeface =
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "0",
                x,
                y + dp(20),
                paint
            )

            paint.color = Color.BLACK

            canvas.drawCircle(
                x,
                y + dp(10),
                dp(7).toFloat(),
                paint
            )

            canvas.drawRect(
                x - dp(4),
                y + dp(10),
                x + dp(4),
                y + dp(27),
                paint
            )

            paint.textAlign = Paint.Align.LEFT
        }

        private fun drawKey(canvas: Canvas) {

            val x = keyX
            val y = keyY

            canvas.save()
            canvas.rotate(rotation, x, y)

            glowPaint.style = Paint.Style.FILL
            glowPaint.color =
                Color.argb(
                    if (aligned) 120 else 70,
                    255,
                    190,
                    50
                )

            glowPaint.maskFilter = BlurMaskFilter(
                dp(if (aligned) 25 else 13).toFloat(),
                BlurMaskFilter.Blur.NORMAL
            )

            canvas.drawCircle(
                x,
                y,
                dp(45).toFloat(),
                glowPaint
            )

            glowPaint.maskFilter = null

            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(255, 190, 45)

            canvas.drawCircle(
                x - dp(30),
                y,
                dp(24).toFloat(),
                paint
            )

            paint.color = Color.rgb(100, 60, 10)

            canvas.drawCircle(
                x - dp(30),
                y,
                dp(9).toFloat(),
                paint
            )

            val shaft = RectF(
                x - dp(12),
                y - dp(7),
                x + dp(70),
                y + dp(7)
            )

            val goldGradient = LinearGradient(
                shaft.left,
                shaft.top,
                shaft.right,
                shaft.bottom,
                Color.rgb(255, 225, 110),
                Color.rgb(185, 115, 15),
                Shader.TileMode.CLAMP
            )

            paint.shader = goldGradient

            canvas.drawRoundRect(
                shaft,
                dp(6).toFloat(),
                dp(6).toFloat(),
                paint
            )

            paint.shader = null

            paint.color = Color.rgb(220, 150, 30)

            canvas.drawRect(
                x + dp(43),
                y + dp(5),
                x + dp(54),
                y + dp(24),
                paint
            )

            canvas.drawRect(
                x + dp(57),
                y + dp(5),
                x + dp(68),
                y + dp(18),
                paint
            )

            paint.color = white
            paint.textSize = dp(28).toFloat()
            paint.typeface =
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textAlign = Paint.Align.CENTER

            canvas.drawText(
                "1",
                x - dp(30),
                y + dp(9),
                paint
            )

            paint.textAlign = Paint.Align.LEFT

            canvas.restore()
        }

        private fun drawEnergyParticles(canvas: Canvas) {

            val lockCx = lockX
            val lockCy = lockY

            for (p in particles) {

                var px = p.x * width
                var py = p.y * height

                // Ambient movement.
                val movement =
                    sin(
                        p.phase + energy * 5f
                    ).toFloat() * dp(7)

                px += movement

                // During mechanical unlock, particles begin
                // converging toward the lock mechanism.
                if (unlockProgress > 0f) {

                    val converge =
                        unlockProgress * 0.55f

                    px +=
                        (lockCx - px) * converge

                    py +=
                        (lockCy - py) * converge
                }

                val pulse =
                    abs(
                        sin(
                            p.phase +
                                rotation * 0.02f +
                                unlockProgress * 8f
                        )
                    )

                val alpha =
                    (
                        35 +
                            75 * pulse +
                            45 * unlockProgress
                        ).toInt()
                        .coerceIn(25, 190)

                paint.style = Paint.Style.FILL

                // Negative energy transitions into
                // cyan positive energy.
                val red =
                    if (energy > 0.35f) 40 else 255

                val green =
                    if (energy > 0.35f) 230 else 55

                val blue =
                    if (energy > 0.35f) 255 else 100

                paint.color =
                    Color.argb(
                        alpha,
                        red,
                        green,
                        blue
                    )

                val particleSize =
                    p.size.toInt()
                        .coerceAtLeast(1)

                canvas.drawCircle(
                    px,
                    py,
                    dp(particleSize).toFloat(),
                    paint
                )
            }

            // Final cyan energy burst at the lock.
            if (unlockProgress > 0.72f) {

                val burst =
                    (unlockProgress - 0.72f) /
                        0.28f

                val radius =
                    dp(25 + (burst * 75).toInt())
                        .toFloat()

                val burstAlpha =
                    (90 * (1f - burst))
                        .toInt()
                        .coerceIn(0, 90)

                glowPaint.style = Paint.Style.STROKE
                glowPaint.strokeWidth = dp(3).toFloat()
                glowPaint.color =
                    Color.argb(
                        burstAlpha,
                        40,
                        230,
                        255
                    )

                glowPaint.maskFilter =
                    BlurMaskFilter(
                        dp(10).toFloat(),
                        BlurMaskFilter.Blur.NORMAL
                    )

                canvas.drawCircle(
                    lockCx,
                    lockCy,
                    radius,
                    glowPaint
                )

                glowPaint.maskFilter = null
                glowPaint.style = Paint.Style.FILL
            }
        }

        private fun drawGateway(canvas: Canvas) {
            val w = width.toFloat()
            val h = height.toFloat()
            val cx = w * 0.5f
            val cy = h * 0.52f
            val p = gatewayProgress.coerceIn(0f, 1f)

            fun ease(v: Float): Float {
                val x = v.coerceIn(0f, 1f)
                return x * x * (3f - 2f * x)
            }

            // 🚪 GDMIE DOOR CINEMATIC
            // Portal removed. Door appears after the mechanical unlock.

            val appear = ease(p / 0.22f)
            val open = ease((p - 0.18f) / 0.62f)
            val light = ease((p - 0.55f) / 0.35f)
            val fade = ease((p - 0.88f) / 0.12f)

            // Dark cinematic transition layer.
            paint.shader = null
            paint.style = Paint.Style.FILL
            paint.color = Color.argb(
                (150f * appear).toInt().coerceIn(0, 150),
                0,
                2,
                10
            )
            canvas.drawRect(0f, 0f, w, h, paint)

            // ✨ Light behind the GDMIE door.
            if (light > 0f) {
                glowPaint.shader = RadialGradient(
                    cx,
                    cy,
                    h * 0.42f,
                    intArrayOf(
                        Color.argb(
                            (230f * light).toInt(),
                            255,
                            255,
                            255
                        ),
                        Color.argb(
                            (150f * light).toInt(),
                            0,
                            225,
                            255
                        ),
                        Color.argb(
                            (90f * light).toInt(),
                            120,
                            60,
                            255
                        ),
                        Color.TRANSPARENT
                    ),
                    null,
                    Shader.TileMode.CLAMP
                )

                canvas.drawCircle(
                    cx,
                    cy,
                    h * 0.42f,
                    glowPaint
                )

                glowPaint.shader = null
            }

            if (appear > 0f) {
                val doorW = w * 0.52f
                val doorH = h * 0.62f

                val left = cx - doorW / 2f
                val top = cy - doorH / 2f
                val right = cx + doorW / 2f
                val bottom = cy + doorH / 2f

                val srcW = brainDoorBitmap.width
                val srcH = brainDoorBitmap.height

                val halfSrc = srcW / 2

                // Door separation.
                val separation = doorW * 0.46f * open

                val leftDest = RectF(
                    left - separation,
                    top,
                    cx - separation * 0.12f,
                    bottom
                )

                val rightDest = RectF(
                    cx + separation * 0.12f,
                    top,
                    right + separation,
                    bottom
                )

                val leftSrc = Rect(
                    0,
                    0,
                    halfSrc,
                    srcH
                )

                val rightSrc = Rect(
                    halfSrc,
                    0,
                    srcW,
                    srcH
                )

                paint.shader = null
                paint.style = Paint.Style.FILL
                paint.alpha =
                    (255f * appear).toInt().coerceIn(0, 255)

                canvas.drawBitmap(
                    brainDoorBitmap,
                    leftSrc,
                    leftDest,
                    paint
                )

                canvas.drawBitmap(
                    brainDoorBitmap,
                    rightSrc,
                    rightDest,
                    paint
                )

                paint.alpha = 255

                // Cyan door-frame glow.
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = dp(3).toFloat()
                paint.color = Color.argb(
                    (230f * appear).toInt(),
                    0,
                    225,
                    255
                )

                canvas.drawRoundRect(
                    RectF(
                        left - separation,
                        top,
                        right + separation,
                        bottom
                    ),
                    dp(18).toFloat(),
                    dp(18).toFloat(),
                    paint
                )

                // Moving energy line at the door opening.
                if (open > 0f) {
                    paint.strokeWidth = dp(4).toFloat()
                    paint.color = Color.argb(
                        (220f * open).toInt(),
                        255,
                        235,
                        130
                    )

                    canvas.drawLine(
                        cx,
                        top + dp(12),
                        cx,
                        bottom - dp(12),
                        paint
                    )
                }

                // Final white/cyan activation flash.
                if (fade > 0f) {
                    paint.style = Paint.Style.FILL
                    paint.color = Color.argb(
                        (190f * fade).toInt().coerceIn(0, 190),
                        220,
                        250,
                        255
                    )
                    canvas.drawRect(
                        0f,
                        0f,
                        w,
                        h,
                        paint
                    )
                }
            }
        }

        private fun animateKeyBack() {

            val startX = keyX
            val startY = keyY

            val targetX = width * 0.25f
            val targetY = height * 0.53f

            ValueAnimator.ofFloat(0f, 1f).apply {

                duration = 500

                addUpdateListener {

                    val t = it.animatedValue as Float

                    keyX =
                        startX + (targetX - startX) * t

                    keyY =
                        startY + (targetY - startY) * t

                    invalidate()
                }

                start()
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {

            if (unlocked || unlocking) return true

            when (event.actionMasked) {

                MotionEvent.ACTION_DOWN -> {

                    val distance = hypot(
                        (event.x - keyX).toDouble(),
                        (event.y - keyY).toDouble()
                    )

                    if (distance < dp(85)) {

                        dragging = true

                        dragOffsetX = event.x - keyX
                        dragOffsetY = event.y - keyY

                        parent?.requestDisallowInterceptTouchEvent(true)

                        return true
                    }
                }

                MotionEvent.ACTION_MOVE -> {

                    if (dragging) {

                        keyX = event.x - dragOffsetX
                        keyY = event.y - dragOffsetY

                        val distance = hypot(
                            (keyX - lockX).toDouble(),
                            (keyY - lockY).toDouble()
                        )

                        aligned = distance < dp(75)

                        energy =
                            if (aligned) 1f else 0.18f

                        invalidate()
                        return true
                    }
                }

                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL -> {

                    if (dragging) {

                        dragging = false

                        val distance = hypot(
                            (keyX - lockX).toDouble(),
                            (keyY - lockY).toDouble()
                        )

                        if (distance < dp(75)) {

                            keyX = lockX
                            keyY = lockY

                            aligned = true
                            energy = 1f

                            invalidate()

                            postDelayed({
                                onUnlock?.invoke()
                            }, 250)

                        } else {

                            aligned = false
                            energy = 0.1f

                            animateKeyBack()
                        }

                        parent?.requestDisallowInterceptTouchEvent(false)

                        return true
                    }
                }
            }

            return true
        }

        fun playUnlockAnimation() {
            if (unlocking) return

            unlocking = true

            val animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 1200L

                addUpdateListener { animation ->
                    val v = animation.animatedValue as Float

                    unlockProgress = v

                    // Mechanical key movement / rotation.
                    rotation = v * 360f
                    shake = if (v < 0.75f) {
                        kotlin.math.sin(v * Math.PI * 18.0).toFloat() * (1f - v) * 3f
                    } else {
                        0f
                    }

                    // Energy builds during the mechanical unlock.
                    energy = when {
                        v < 0.65f -> v / 0.65f
                        else -> 1f
                    }

                    invalidate()
                }

                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        unlockProgress = 1f
                        energy = 1f
                        shake = 0f
                        rotation = 360f

                        invalidate()

                        // 🚪 After mechanical unlock → GDMIE DOOR.
                        postDelayed({
                            startGatewayAnimation()
                        }, 180L)
                    }
                })
            }

            animator.start()
        }

        private fun startGatewayAnimation() {

            gatewayAnimator?.cancel()

            gatewayProgress = 0f
            invalidate()

            gatewayAnimator = ValueAnimator.ofFloat(0f, 1f).apply {

                // Slow extraordinary gateway sequence.
                duration = 2100L

                addUpdateListener {
                    gatewayProgress =
                        (it.animatedValue as Float)
                            .coerceIn(0f, 1f)

                    invalidate()
                }

                addListener(
                    object : AnimatorListenerAdapter() {

                        override fun onAnimationEnd(
                            animation: Animator
                        ) {
                            gatewayProgress = 1f
                            invalidate()

                            // Gateway white-out complete.
                            onGatewayComplete?.invoke()
                        }
                    }
                )

                start()
            }
        }

        private fun startAmbientAnimation() {

            ValueAnimator.ofFloat(0f, 1f).apply {

                duration = 3600
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE

                addUpdateListener {

                    if (!dragging && !unlocking) {

                        rotation += 0.12f

                        energy =
                            0.08f +
                            0.08f *
                            (it.animatedValue as Float)

                        invalidate()
                    }
                }

                start()
            }
        }

    }

    private class Particle(
        var x: Float,
        var y: Float,
        var size: Float,
        var phase: Float
    )

    private fun rounded(
        fill: Int,
        radius: Int,
        stroke: Int,
        strokeWidth: Int
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(fill)
            cornerRadius = dp(radius).toFloat()
            setStroke(dp(strokeWidth), stroke)
        }
    }

    private fun dp(value: Int): Int {
        return (
            value *
                resources.displayMetrics.density
            ).toInt()
    }
}
