package com.gdmie
import com.gdmie.audio.GDMIEAudioManager

import com.gdmie.game.GDMIEGameProgress

import android.animation.ObjectAnimator

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*


private class GDMIEHomeWorldDrawable(
    private val cityMode: Boolean
) : Drawable() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun draw(canvas: Canvas) {
        val w = bounds.width().toFloat()
        val h = bounds.height().toFloat()

        if (w <= 0f || h <= 0f) return

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h,
            if (cityMode) Color.rgb(4, 13, 32)
            else Color.rgb(20, 10, 48),
            if (cityMode) Color.rgb(8, 32, 58)
            else Color.rgb(6, 10, 27),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        if (cityMode) {
            drawCity(canvas, w, h)
        } else {
            drawCosmic(canvas, w, h)
        }
    }

    private fun drawCosmic(canvas: Canvas, w: Float, h: Float) {
        canvas.save()

        paint.shader = LinearGradient(
            0f, 0f, 0f, h,
            Color.rgb(55, 22, 110),
            Color.rgb(3, 7, 24),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        // Nebula glow
        paint.color = Color.argb(24, 80, 170, 255)
        canvas.drawCircle(w * 0.48f, h * 0.28f, h * 0.42f, paint)

        paint.color = Color.argb(20, 210, 80, 255)
        canvas.drawCircle(w * 0.70f, h * 0.34f, h * 0.30f, paint)

        // Cinematic cosmic depth
        paint.shader = LinearGradient(
            0f,
            h * 0.18f,
            0f,
            h * 0.72f,
            Color.argb(55, 90, 140, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.12f, w, h * 0.78f, paint)
        paint.shader = null

        // Giant moon / planet
        paint.color = Color.argb(22, 120, 180, 255)
        canvas.drawCircle(w * 0.80f, h * 0.25f, h * 0.23f, paint)

        paint.color = Color.argb(45, 155, 90, 255)
        canvas.drawCircle(w * 0.80f, h * 0.25f, h * 0.16f, paint)

        paint.color = Color.argb(245, 235, 242, 255)
        canvas.drawCircle(w * 0.80f, h * 0.25f, h * 0.065f, paint)

        // Stars
        val stars = arrayOf(
            0.06f to 0.13f,
            0.14f to 0.29f,
            0.24f to 0.10f,
            0.36f to 0.22f,
            0.49f to 0.08f,
            0.60f to 0.25f,
            0.72f to 0.12f,
            0.91f to 0.10f,
            0.95f to 0.31f
        )

        for ((x, y) in stars) {
            paint.color = Color.argb(225, 230, 245, 255)
            canvas.drawCircle(w * x, h * y, 2.1f, paint)

            paint.color = Color.argb(65, 35, 205, 255)
            canvas.drawCircle(w * x, h * y, 7f, paint)
        }

        // Distant cinematic mountains
        val far = Path()
        far.moveTo(0f, h * 0.70f)
        far.lineTo(w * 0.12f, h * 0.51f)
        far.lineTo(w * 0.23f, h * 0.65f)
        far.lineTo(w * 0.39f, h * 0.38f)
        far.lineTo(w * 0.53f, h * 0.64f)
        far.lineTo(w * 0.68f, h * 0.46f)
        far.lineTo(w * 0.82f, h * 0.63f)
        far.lineTo(w, h * 0.49f)
        far.lineTo(w, h)
        far.lineTo(0f, h)
        far.close()

        paint.color = Color.rgb(35, 25, 70)
        canvas.drawPath(far, paint)

        // Snow / purple mountain highlights
        paint.color = Color.argb(115, 190, 170, 255)

        val peak1 = Path()
        peak1.moveTo(w * 0.30f, h * 0.57f)
        peak1.lineTo(w * 0.39f, h * 0.38f)
        peak1.lineTo(w * 0.46f, h * 0.57f)
        peak1.close()
        canvas.drawPath(peak1, paint)

        val peak2 = Path()
        peak2.moveTo(w * 0.60f, h * 0.59f)
        peak2.lineTo(w * 0.68f, h * 0.46f)
        peak2.lineTo(w * 0.75f, h * 0.60f)
        peak2.close()
        canvas.drawPath(peak2, paint)

        // Foreground mountain range
        val near = Path()
        near.moveTo(0f, h * 0.83f)
        near.lineTo(w * 0.19f, h * 0.61f)
        near.lineTo(w * 0.34f, h * 0.78f)
        near.lineTo(w * 0.50f, h * 0.56f)
        near.lineTo(w * 0.66f, h * 0.79f)
        near.lineTo(w * 0.82f, h * 0.62f)
        near.lineTo(w, h * 0.73f)
        near.lineTo(w, h)
        near.lineTo(0f, h)
        near.close()

        paint.color = Color.rgb(7, 8, 25)
        canvas.drawPath(near, paint)

        // Cinematic horizon glow
        paint.shader = LinearGradient(
            0f,
            h * 0.58f,
            0f,
            h * 0.92f,
            Color.argb(70, 155, 90, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.54f, w, h * 0.92f, paint)
        paint.shader = null

        // Purple horizon
        paint.shader = LinearGradient(
            0f, h * 0.60f, 0f, h,
            Color.argb(145, 155, 90, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.60f, w, h, paint)
        paint.shader = null

        canvas.restore()
    }

    private fun drawCity(canvas: Canvas, w: Float, h: Float) {
        canvas.save()

        paint.shader = LinearGradient(
            0f, 0f, 0f, h,
            Color.rgb(4, 24, 58),
            Color.rgb(2, 6, 20),
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        // Neon sky atmosphere
        paint.color = Color.argb(45, 35, 205, 255)
        canvas.drawCircle(w * 0.50f, h * 0.50f, h * 0.38f, paint)

        paint.color = Color.argb(38, 155, 90, 255)
        canvas.drawCircle(w * 0.72f, h * 0.42f, h * 0.28f, paint)

        // Cinematic city depth
        paint.shader = LinearGradient(
            0f,
            h * 0.10f,
            0f,
            h * 0.70f,
            Color.argb(48, 35, 205, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.08f, w, h * 0.72f, paint)
        paint.shader = null

        // Skyline — deliberately kept in lower third
        val buildings = arrayOf(
            floatArrayOf(0.00f, 0.68f, 0.11f),
            floatArrayOf(0.11f, 0.55f, 0.22f),
            floatArrayOf(0.22f, 0.64f, 0.34f),
            floatArrayOf(0.34f, 0.49f, 0.47f),
            floatArrayOf(0.47f, 0.59f, 0.59f),
            floatArrayOf(0.59f, 0.43f, 0.72f),
            floatArrayOf(0.72f, 0.53f, 0.84f),
            floatArrayOf(0.84f, 0.38f, 1.00f)
        )

        for ((index, b) in buildings.withIndex()) {
            val left = w * b[0]
            val top = h * b[1]
            val right = w * b[2]

            paint.color = Color.rgb(
                4,
                13 + index * 2,
                30 + index * 3
            )
            canvas.drawRect(left, top, right, h, paint)

            // Neon building edge
            paint.color =
                if (index % 2 == 0)
                    Color.argb(210, 35, 205, 255)
                else
                    Color.argb(205, 155, 90, 255)

            canvas.drawRect(
                left,
                top,
                left + 3f,
                h * 0.82f,
                paint
            )

            // Windows
            paint.color = Color.argb(190, 55, 220, 255)
            var y = top + 12f

            while (y < h * 0.82f - 10f) {
                canvas.drawRect(
                    left + 8f,
                    y,
                    minOf(right - 8f, left + 14f),
                    y + 3f,
                    paint
                )
                y += 17f
            }
        }

        // Central neon skyline glow
        paint.shader = LinearGradient(
            0f, h * 0.62f, 0f, h,
            Color.argb(135, 35, 205, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.62f, w, h, paint)
        paint.shader = null

        // Cinematic horizon glow
        paint.shader = LinearGradient(
            0f,
            h * 0.60f,
            0f,
            h * 0.92f,
            Color.argb(70, 35, 205, 255),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawRect(0f, h * 0.54f, w, h * 0.92f, paint)
        paint.shader = null

        // Futuristic horizon line
        paint.color = Color.argb(210, 35, 205, 255)
        canvas.drawRect(0f, h * 0.78f, w, h * 0.785f, paint)

        canvas.restore()
    }


    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int =
        android.graphics.PixelFormat.TRANSLUCENT
}

class MainActivity : Activity() {

    // ============================================================
    // GDMIE HOME V2
    // Premium dark / blue decision-intelligence dashboard
    // ============================================================

    private val bg = Color.rgb(3, 6, 14)
    private val surface = Color.rgb(8, 14, 27)
    private val surface2 = Color.rgb(12, 21, 40)
    private val surface3 = Color.rgb(18, 29, 55)

    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val gold = Color.rgb(255, 205, 70)
    private val green = Color.rgb(45, 225, 135)
    private val yellow = Color.rgb(255, 220, 70)

    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val dim = Color.rgb(82, 108, 135)
    private val border = Color.rgb(25, 57, 87)

    private var explorerLevelView: TextView? = null
    private var explorerXpView: TextView? = null
    private var explorerStreakView: TextView? = null

    // ============================================================
    // MASCOT STATE SYSTEM — STEP 6F
    // ============================================================
    private enum class MascotState {
        IDLE,
        THINKING,
        ANALYZING,
        WAITING,
        SUCCESS,
        LEARNING,
        LEVEL_UP
    }

    private var mascotState = MascotState.IDLE

    private fun setMascotState(state: MascotState) {
        mascotState = state
    }

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
            includeFontPadding = false
            letterSpacing = if (bold) 0.015f else 0.005f

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
            background = rounded(
                Color.argb(155, 8, 18, 38),
                20,
                Color.rgb(35, 120, 190),
                1
            )
            elevation = dp(4).toFloat()
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
                setPadding(0, dp(2), 0, dp(1))
            }
        )

        parent.addView(gap(10))
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
                if (selected) purple else surface2,
                18,
                if (selected) purple else border
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

            background = rounded(
                Color.argb(105, 18, 29, 55),
                18,
                Color.rgb(55, 145, 215),
                1
            )
            elevation = dp(7).toFloat()
            isClickable = true
            isFocusable = true

            setOnClickListener {
            GDMIEAudioManager.playUiClick(this@MainActivity)
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
                                action()
                            }
                            .start()
                    }
                    .start()
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
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(82)
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
            GDMIEAudioManager.playUiClick(this@MainActivity)
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

        val worldFrame = FrameLayout(this).apply {
            background = android.graphics.drawable.LayerDrawable(
                arrayOf(
                    GDMIEHomeWorldDrawable(
                        getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
                            .getString("home_mode", "COSMIC") == "CITY"
                    ),
                    rounded(
                        Color.argb(55, 8, 18, 38),
                        20,
                        Color.rgb(55, 120, 185),
                        1
                    )
                )
            )
        }

        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                dp(16),
                dp(18),
                dp(16),
                dp(85)
            )
            setBackgroundColor(Color.TRANSPARENT)
        }

        worldFrame.addView(
            scroll,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        // ========================================================
        // HEADER
        // ========================================================

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(4), dp(5), dp(4), dp(5))
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
                purple,
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
            text = "👤"
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(white)
            setShadowLayer(dp(8).toFloat(), 0f, 0f, cyan)
            contentDescription = "Login Profile"
            background = rounded(
                Color.argb(190, 6, 16, 38),
                21,
                cyan
            )
            isClickable = true
            isFocusable = true

            setOnClickListener {
            GDMIEAudioManager.playUiClick(this@MainActivity)
                animate()
                    .scaleX(0.92f)
                    .scaleY(0.92f)
                    .setDuration(70)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(90)
                            .withEndAction {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        ProfileActivity::class.java
                                    )
                                )
                            }
                            .start()
                    }
                    .start()
            }
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
                26f,
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
                setPadding(0, dp(6), 0, 0)
            }
        )

        root.addView(gap(16))

        // ========================================================
        // EXPLORER HUD
        // ========================================================

        val explorerHud = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))

            background = rounded(
                Color.argb(125, 8, 18, 38),
                20,
                Color.rgb(55, 145, 205),
                1
            )

            elevation = dp(7).toFloat()
        }

        val levelBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        levelBox.addView(
            tv(
                "LEVEL",
                8f,
                muted,
                true
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        explorerLevelView = tv(
            "%02d".format(GDMIEGameProgress.getLevel(this)),
            25f,
            cyan,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(2), 0, 0)
        }
        levelBox.addView(explorerLevelView)

        explorerHud.addView(
            levelBox,
            LinearLayout.LayoutParams(
                dp(62),
                dp(58)
            )
        )

        explorerHud.addView(
            View(this).apply {
                setBackgroundColor(Color.rgb(45, 70, 105))
            },
            LinearLayout.LayoutParams(
                dp(1),
                dp(42)
            )
        )

        val progressBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, dp(10), 0)
        }

        progressBox.addView(
            tv(
                "EXPLORER XP",
                8f,
                muted,
                true
            )
        )

        explorerXpView = tv(
            "${GDMIEGameProgress.xpIntoCurrentLevel(this)} / 100 XP",
            12f,
            white,
            true
        ).apply {
            setPadding(0, dp(4), 0, dp(5))
        }
        progressBox.addView(explorerXpView)

        val xpTrack = View(this).apply {
            background = rounded(
                Color.argb(95, 25, 40, 65),
                8,
                Color.rgb(35, 110, 165),
                1
            )
        }

        progressBox.addView(
            xpTrack,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(7)
            )
        )

        explorerHud.addView(
            progressBox,
            LinearLayout.LayoutParams(
                0,
                dp(58),
                1f
            )
        )

        val statsBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        statsBox.addView(
            tv(
                "STREAK",
                8f,
                muted,
                true
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        explorerStreakView = tv(
            "${GDMIEGameProgress.getStreak(this)}  🔥",
            15f,
            gold,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(4), 0, 0)
        }
        statsBox.addView(explorerStreakView)

        explorerHud.addView(
            statsBox,
            LinearLayout.LayoutParams(
                dp(68),
                dp(58)
            )
        )

        root.addView(
            explorerHud,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(84)
            )
        )

        root.addView(gap(18))

        // ========================================================
        // HOME MODE — COSMIC / CITY
        // ========================================================
        val homeModeCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = GradientDrawable().apply {
                setColor(Color.argb(115, 8, 18, 38))
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), border)
            }
            elevation = dp(5).toFloat()
        }

        homeModeCard.addView(
            tv("HOME MODE", 9f, muted, true)
        )

        val modeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, 0)
        }

        val modePrefs = getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
        var currentHomeMode =
            modePrefs.getString("home_mode", "COSMIC") ?: "COSMIC"


        // Live Hero refresh callback.
        var refreshHeroLive: (() -> Unit)? = null

        val cosmicButton = Button(this).apply {
            text = "🌌 COSMIC"
            textSize = 10f
            isAllCaps = false
            setTypeface(null, Typeface.BOLD)
        }

        val cityButton = Button(this).apply {
            text = "🌆 CITY"
            textSize = 10f
            isAllCaps = false
            setTypeface(null, Typeface.BOLD)
        }

        fun refreshHomeMode() {
            val cosmicSelected = currentHomeMode == "COSMIC"

            cosmicButton.background = rounded(
                if (cosmicSelected) Color.argb(145, 155, 90, 255)
                else Color.argb(95, 18, 29, 55),
                14,
                if (cosmicSelected) cyan else border,
                1
            )

            cityButton.background = rounded(
                if (!cosmicSelected) Color.argb(145, 45, 145, 255)
                else Color.argb(95, 18, 29, 55),
                14,
                if (!cosmicSelected) cyan else border,
                1
            )

            cosmicButton.setTextColor(white)
            cityButton.setTextColor(white)
        }

        cosmicButton.setOnClickListener {
        GDMIEAudioManager.playUiClick(this@MainActivity)
            currentHomeMode = "COSMIC"
            modePrefs.edit()
                .putString("home_mode", currentHomeMode)
                .apply()
            refreshHomeMode()
            refreshHeroLive?.invoke()
        }

        cityButton.setOnClickListener {
        GDMIEAudioManager.playUiClick(this@MainActivity)
            currentHomeMode = "CITY"
            modePrefs.edit()
                .putString("home_mode", currentHomeMode)
                .apply()
            refreshHomeMode()
            refreshHeroLive?.invoke()
        }

        modeRow.addView(
            cosmicButton,
            LinearLayout.LayoutParams(0, dp(44), 1f)
        )

        modeRow.addView(
            Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(dp(8), 1)
            }
        )

        modeRow.addView(
            cityButton,
            LinearLayout.LayoutParams(0, dp(44), 1f)
        )

        homeModeCard.addView(modeRow)
        refreshHomeMode()

        root.addView(
            homeModeCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(86)
            )
        )

        root.addView(gap(18))

        // QUICK DECISION ZONE
        // ========================================================

        val quickZone = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(15), dp(12), dp(12), dp(12))

            background = rounded(
                Color.argb(115, 12, 25, 49),
                20,
                Color.rgb(65, 145, 210),
                1
            )

            elevation = dp(8).toFloat()
        }

        val quickText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        quickText.addView(
            tv(
                "⚡ FAST DECISION",
                10f,
                cyan,
                true
            )
        )

        quickText.addView(
            tv(
                "Make a decision. Learn from the result.",
                11f,
                white,
                true
            ).apply {
                setPadding(0, dp(5), 0, 0)
            }
        )

        quickText.addView(
            tv(
                "Fast simulation • Instant analysis • Earn XP",
                8.5f,
                muted
            ).apply {
                setPadding(0, dp(4), 0, 0)
            }
        )

        quickZone.addView(
            quickText,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val quickButton = Button(this).apply {
            text = "START  →"
            textSize = 10f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            isAllCaps = false
            letterSpacing = 0.04f

            background = rounded(
                purple,
                14,
                cyan,
                1
            )

            elevation = dp(4).toFloat()

            setOnClickListener {
            GDMIEAudioManager.playUiClick(this@MainActivity)
                startActivity(
                    Intent(
                        this@MainActivity,
                        QuickDecisionActivity::class.java
                    )
                )
            }
        }

        quickZone.addView(
            quickButton,
            LinearLayout.LayoutParams(
                dp(92),
                dp(46)
            )
        )

        root.addView(
            quickZone,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(82)
            )
        )

        root.addView(gap(18))

        // ========================================================
        // GDMIE MASCOT HERO
        // =======================================================
        // ========================================================
        // GDMIE MASCOT HERO
        // ========================================================

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            background = GDMIEHomeWorldDrawable(
                getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
                    .getString("home_mode", "COSMIC") == "CITY"
            )
            elevation = dp(5).toFloat()
        }

        val mascot = ImageView(this).apply {
            setImageResource(R.drawable.mascot)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            contentDescription = "GDMIE AI Mascot"

            pivotX = dp(52).toFloat()
            pivotY = dp(62).toFloat()

            ObjectAnimator.ofFloat(
                this,
                "scaleX",
                1.0f,
                1.035f,
                1.0f
            ).apply {
                duration = 2800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
                start()
            }

            ObjectAnimator.ofFloat(
                this,
                "scaleY",
                1.0f,
                1.035f,
                1.0f
            ).apply {
                duration = 2800
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.RESTART
                start()
            }
        }

hero.addView(
            mascot,
            LinearLayout.LayoutParams(
                dp(105),
                dp(125)
            )
        )

        val homeMode =
            getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
                .getString("home_mode", "COSMIC") ?: "COSMIC"

        val heroAccent = if (homeMode == "CITY") blue else purple
        val heroLabel =
            if (homeMode == "CITY")
                "AI CITY DECISION COMPANION"
            else
                "COSMIC DECISION COMPANION"

        val heroTitle =
            if (homeMode == "CITY")
                "Navigate smarter.\nDecide faster."
            else
                "Think smarter.\nAnalyze deeper."

        hero.background = android.graphics.drawable.LayerDrawable(
            arrayOf(
                GDMIEHomeWorldDrawable(
                    homeMode == "CITY"
                ),
                rounded(
                    Color.argb(45, 8, 18, 38),
                    20,
                    heroAccent,
                    1
                )
            )
        )

        val heroText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), 0, 0, 0)
        }

        heroText.addView(
            tv(
                heroLabel,
                10f,
                cyan,
                true
            )
        )

        heroText.addView(
            tv(
                heroTitle,
                20f,
                white,
                true
            ).apply {
                setPadding(0, dp(7), 0, 0)
            }
        )

        heroText.addView(
            tv(
                if (homeMode == "CITY")
                    "Explore signals, compare paths and move with clarity."
                else
                    "Every analysis helps you learn, improve and grow.",
                10f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, 0)
            }
        )

        hero.addView(
            heroText,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )


          // Live Hero update when Home Mode changes.
          refreshHeroLive = {
              val liveMode =
                  modePrefs.getString("home_mode", "COSMIC") ?: "COSMIC"

              val liveCity = liveMode == "CITY"
              val liveAccent = if (liveCity) blue else purple

              val liveLabel =
                  if (liveCity)
                      "AI CITY DECISION COMPANION"
                  else
                      "COSMIC DECISION COMPANION"

              val liveTitle =
                  if (liveCity)
                      "Navigate smarter.\nDecide faster."
                  else
                      "Think smarter.\nAnalyze deeper."

              val liveDescription =
                  if (liveCity)
                      "Explore signals, compare paths and move with clarity."
                  else
                      "Every analysis helps you learn, improve and grow."

              worldFrame.background = GDMIEHomeWorldDrawable(liveCity)

              hero.background = android.graphics.drawable.LayerDrawable(
                  arrayOf(
                      GDMIEHomeWorldDrawable(liveCity),
                      rounded(
                          Color.argb(55, 8, 18, 38),
                          20,
                          liveAccent,
                          1
                      )
                  )
              )

              (heroText.getChildAt(0) as? TextView)?.text = liveLabel
              (heroText.getChildAt(1) as? TextView)?.text = liveTitle
              (heroText.getChildAt(2) as? TextView)?.text = liveDescription
          }

          refreshHeroLive?.invoke()
        root.addView(
            hero,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(150)
            )
        )

        root.addView(gap(16))
        // ========================================================
        // DECISION HUB
// ========================================================
sectionTitle(root, "DECISION HUB")

val decisionHub = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL
    gravity = Gravity.CENTER
}

fun decisionWorldCard(
    icon: String,
    title: String,
    subtitle: String,
    accent: Int,
    action: () -> Unit
): View {
    return LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(
            dp(8),
            dp(12),
            dp(8),
            dp(12)
        )
        background = rounded(
            Color.argb(105, 8, 18, 38),
            20,
            accent,
            1
        )
        elevation = dp(11).toFloat()
        isClickable = true
        isFocusable = true

        setOnClickListener {
        GDMIEAudioManager.playUiClick(this@MainActivity)
            animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70)
                .withEndAction {
                    animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(90)
                        .withEndAction {
                            action()
                        }
                        .start()
                }
                .start()
        }

        addView(
            tv(icon, 25f, accent, true).apply {
                gravity = Gravity.CENTER
            }
        )

        addView(
            tv(title, 11f, white, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(7), 0, 0)
            }
        )

        addView(
            tv(subtitle, 8f, muted).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(5), 0, 0)
            }
        )
    }
}

decisionHub.addView(
    decisionWorldCard(
        "⚡",
        "QUICK",
        "DECISION",
        cyan
    ) {
        startActivity(
            Intent(
                this@MainActivity,
                QuickDecisionFullActivity::class.java
            )
        )
    },
    LinearLayout.LayoutParams(
        0,
        dp(142),
        1f
    ).apply {
        setMargins(dp(3), 0, dp(3), 0)
    }
)

decisionHub.addView(
    decisionWorldCard(
        "🏆",
        "DAILY",
        "CHALLENGE",
        gold
    ) {
        startActivity(
            Intent(
                this@MainActivity,
                DecisionChallengeActivity::class.java
            )
        )
    },
    LinearLayout.LayoutParams(
        0,
        dp(142),
        1f
    ).apply {
        setMargins(dp(3), 0, dp(3), 0)
    }
)

        root.addView(decisionHub)
root.addView(gap(22))

        // ========================================================
        // LIVE ANALYSIS
        // ========================================================
        sectionTitle(root, "LIVE ANALYSIS")

        val liveAnalysisCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(10), dp(10), dp(10), dp(10))
            background = rounded(
                Color.argb(105, 8, 18, 38),
                20,
                Color.rgb(35, 145, 215),
                1
            )
            elevation = dp(9).toFloat()
            isClickable = true
            isFocusable = true
            setOnClickListener {
            GDMIEAudioManager.playUiClick(this@MainActivity)
                startActivity(
                    Intent(
                        this@MainActivity,
                        LiveAnalysisActivity::class.java
                    )
                )
            }
        }

        val liveHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(5), 0, dp(5), dp(6))
        }

        val liveHeaderLeft = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        liveHeaderLeft.addView(
            tv("▥", 22f, cyan, true).apply {
                setPadding(0, 0, dp(7), 0)
            }
        )

        val liveTitleBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        liveTitleBox.addView(
            tv("REAL-TIME ANALYSIS", 12f, white, true)
        )

        liveTitleBox.addView(
            tv("REAL-TIME INSIGHTS  •  WORLD SIGNALS", 8f, muted).apply {
                setPadding(0, dp(2), 0, 0)
            }
        )

        liveHeaderLeft.addView(liveTitleBox)

        liveHeader.addView(
            liveHeaderLeft,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        val liveStatus = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        liveStatus.addView(
            tv("●", 13f, green, true).apply {
                setPadding(0, 0, dp(4), 0)
            }
        )

        liveStatus.addView(
            tv("LIVE", 9f, green, true)
        )

liveHeader.addView(liveStatus)

        liveAnalysisCard.addView(liveHeader)

        val liveBody = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val globePanel = FrameLayout(this).apply {
            background = rounded(
                Color.argb(85, 8, 22, 48),
                16,
                Color.rgb(35, 145, 215),
                1
            )
            elevation = dp(4).toFloat()
        }

        val worldVisual = object : View(this) {

            private val worldPaint = android.graphics.Paint(
                android.graphics.Paint.ANTI_ALIAS_FLAG
            )

            private var phase = 0f

            override fun onDraw(canvas: android.graphics.Canvas) {
                super.onDraw(canvas)

                val cx = width / 2f
                val cy = height / 2f - dp(5).toFloat()
                val r = dp(27).toFloat()

                phase += 0.018f

                // Outer holographic pulse
                val pulse = (kotlin.math.sin(phase * 2.0) * 0.5 + 0.5).toFloat()

                worldPaint.style = android.graphics.Paint.Style.STROKE
                worldPaint.strokeWidth = dp(1).toFloat()
                worldPaint.color = android.graphics.Color.argb(
                    (45 + pulse * 65).toInt(),
                    35, 205, 255
                )
                canvas.drawCircle(
                    cx, cy,
                    r + dp(7).toFloat() + pulse * dp(5).toFloat(),
                    worldPaint
                )

                // Planet glow
                worldPaint.style = android.graphics.Paint.Style.FILL
                worldPaint.color = android.graphics.Color.argb(35, 35, 205, 255)
                canvas.drawCircle(cx, cy, r + dp(5).toFloat(), worldPaint)

                worldPaint.color = android.graphics.Color.argb(110, 15, 105, 170)
                canvas.drawCircle(cx, cy, r, worldPaint)

                // Planet outline
                worldPaint.style = android.graphics.Paint.Style.STROKE
                worldPaint.strokeWidth = dp(2).toFloat()
                worldPaint.color = cyan
                canvas.drawCircle(cx, cy, r, worldPaint)

                // Latitude
                worldPaint.strokeWidth = dp(1).toFloat()
                worldPaint.color = android.graphics.Color.argb(150, 70, 220, 255)

                val lat = android.graphics.RectF(
                    cx - r * 0.82f,
                    cy - r * 0.42f,
                    cx + r * 0.82f,
                    cy + r * 0.42f
                )
                canvas.drawOval(lat, worldPaint)

                // Longitude
                val lon = android.graphics.RectF(
                    cx - r * 0.42f,
                    cy - r,
                    cx + r * 0.42f,
                    cy + r
                )
                canvas.drawOval(lon, worldPaint)

                // Equator
                canvas.drawLine(
                    cx - r,
                    cy,
                    cx + r,
                    cy,
                    worldPaint
                )

                // Moving signal point
                val angle = phase
                val sx = cx + kotlin.math.cos(angle.toDouble()).toFloat() * r
                val sy = cy + kotlin.math.sin(angle.toDouble()).toFloat() * r

                worldPaint.style = android.graphics.Paint.Style.FILL
                worldPaint.color = android.graphics.Color.WHITE
                worldPaint.setShadowLayer(dp(8).toFloat(), 0f, 0f, cyan)
                canvas.drawCircle(sx, sy, dp(3).toFloat(), worldPaint)
                worldPaint.clearShadowLayer()

                // Orbit ring
                worldPaint.style = android.graphics.Paint.Style.STROKE
                worldPaint.strokeWidth = dp(1).toFloat()
                worldPaint.color = android.graphics.Color.argb(100, 35, 205, 255)

                val orbit = android.graphics.RectF(
                    cx - r - dp(9).toFloat(),
                    cy - dp(10).toFloat(),
                    cx + r + dp(9).toFloat(),
                    cy + dp(10).toFloat()
                )
                canvas.drawOval(orbit, worldPaint)

                postInvalidateOnAnimation()
            }
        }

        globePanel.addView(
            worldVisual,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        globePanel.addView(
            tv("WORLD", 7f, muted, true).apply {
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                setPadding(0, 0, 0, dp(6))
            },
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
        )

        liveBody.addView(
            globePanel,
            LinearLayout.LayoutParams(
                0,
                dp(112),
                0.85f
            ).apply {
                setMargins(0, 0, dp(8), 0)
            }
        )

        val metrics = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(7), dp(3), dp(7), dp(3))
            background = rounded(
                Color.argb(60, 8, 18, 38),
                14,
                Color.rgb(30, 90, 135),
                1
            )
        }

        fun metricRow(
            label: String,
            value: String,
            valueColor: Int
        ) {
            val row = LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }

            row.addView(
                tv(label, 7f, muted, true),
                LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )

            row.addView(
                tv(value, 9f, valueColor, true).apply {
                    gravity = Gravity.END
                }
            )

            metrics.addView(
                row,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            )
        }

        metricRow("MARKET", "+2.4%", green)
        metricRow("SENTIMENT", "-0.8%", Color.rgb(255, 85, 125))
        metricRow("RISK LEVEL", "HIGH", yellow)
        metricRow("OPPORTUNITY", "DETECTED", cyan)

        liveBody.addView(
            metrics,
            LinearLayout.LayoutParams(
                0,
                dp(112),
                1.15f
            ).apply {
                setMargins(0, 0, dp(8), 0)
            }
        )

        val worldPreview = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = android.graphics.drawable.LayerDrawable(
                arrayOf(
                    GDMIEHomeWorldDrawable(
                        getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
                            .getString("home_mode", "COSMIC") == "CITY"
                    ),
                    rounded(
                        Color.argb(55, 8, 18, 38),
                        16,
                        Color.rgb(45, 145, 215),
                        1
                    )
                )
            )
            setPadding(dp(5), dp(5), dp(5), dp(5))
        }

        worldPreview.addView(
            tv("▶", 28f, white, true).apply {
                gravity = Gravity.CENTER
                setShadowLayer(dp(8).toFloat(), 0f, 0f, cyan)
            }
        )

        worldPreview.addView(
            tv("WATCH THE WORLD\nIN MOTION", 7f, white, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, 0)
            }
        )

        liveBody.addView(
            worldPreview,
            LinearLayout.LayoutParams(
                0,
                dp(112),
                0.9f
            )
        )

        liveAnalysisCard.addView(
            liveBody,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(112)
            )
        )

        root.addView(liveAnalysisCard)
        root.addView(gap(22))

        // ========================================================
        // RUN GDMIE
        // ========================================================
        sectionTitle(root, "RUN GDMIE")

        val runGdmieCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))

            background = rounded(
                Color.argb(105, 8, 18, 38),
                20,
                purple,
                1
            )

            elevation = dp(10).toFloat()
            isClickable = true
            isFocusable = true

            setOnClickListener {
            GDMIEAudioManager.playUiClick(this@MainActivity)
                animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(70)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(90)
                            .withEndAction {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        GDMEngineBrainActivity::class.java
                                    )
                                )
                            }
                            .start()
                    }
                    .start()
            }
        }

        runGdmieCard.addView(
            tv("🧠", 30f, purple, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, 0, dp(12), 0)
            }
        )

        val runGdmieText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }

        runGdmieText.addView(
            tv("RUN GDMIE", 14f, white, true)
        )

        runGdmieText.addView(
            tv(
                "GDMIE BRAIN  •  DEEP MATHEMATICAL ANALYSIS",
                8f,
                muted
            ).apply {
                setPadding(0, dp(4), 0, 0)
            }
        )

        runGdmieCard.addView(
            runGdmieText,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        runGdmieCard.addView(
            tv("›", 28f, purple, true).apply {
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            runGdmieCard,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(78)
            )
        )

        root.addView(gap(22))

        // ECOSYSTEM
        // ========================================================

        // ========================================================
        // GDMIE ECOSYSTEM • DECISION UNIVERSE
        // ========================================================

        val ecosystemWorld = FrameLayout(this).apply {
            background = rounded(
                Color.argb(125, 5, 10, 28),
                28,
                purple,
                1
            )
            elevation = dp(12).toFloat()
            setPadding(dp(10), dp(12), dp(10), dp(12))
        }

        // --- Central locked GDMIE CORE ---
        val core = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(
                Color.argb(205, 8, 14, 34),
                70,
                purple,
                2
            )
            elevation = dp(18).toFloat()
            setPadding(dp(18), dp(14), dp(18), dp(14))
        }

        core.addView(
            tv("🔒", 24f, purple, true).apply {
                gravity = Gravity.CENTER
            }
        )

        core.addView(
            tv("🧠", 38f, purple, true).apply {
                gravity = Gravity.CENTER
                setShadowLayer(dp(12).toFloat(), 0f, 0f, purple)
            }
        )

        core.addView(
            tv("GDMIE CORE", 12f, white, true).apply {
                gravity = Gravity.CENTER
            }
        )

        core.addView(
            tv("LOCKED • INTELLIGENCE CORE", 7f, muted, false).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(3), 0, 0)
            }
        )

        ecosystemWorld.addView(
            core,
            FrameLayout.LayoutParams(
                dp(154),
                dp(142),
                Gravity.CENTER
            )
        )

        fun worldNode(
            icon: String,
            title: String,
            accent: Int,
            nodeGravity: Int
        ): View {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = rounded(
                    Color.argb(135, 7, 16, 36),
                    42,
                    accent,
                    1
                )
                elevation = dp(9).toFloat()
                setPadding(dp(10), dp(7), dp(10), dp(7))

                addView(
                    tv(icon, 27f, accent, true).apply {
                        gravity = Gravity.CENTER
                        setShadowLayer(dp(8).toFloat(), 0f, 0f, accent)
                    }
                )

                addView(
                    tv(title, 9f, white, true).apply {
                        gravity = Gravity.CENTER
                        setPadding(0, dp(2), 0, 0)
                    }
                )
            }
        }

        // ENERGY
        ecosystemWorld.addView(
            worldNode("⚡", "ENERGY", gold, Gravity.TOP),
            FrameLayout.LayoutParams(
                dp(112),
                dp(88),
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            ).apply {
                topMargin = dp(8)
            }
        )

        // SPACE
        ecosystemWorld.addView(
            worldNode("🌌", "SPACE", cyan, Gravity.START),
            FrameLayout.LayoutParams(
                dp(112),
                dp(88),
                Gravity.START or Gravity.CENTER_VERTICAL
            ).apply {
                leftMargin = dp(8)
            }
        )

        // EARTH
        ecosystemWorld.addView(
            worldNode("🌍", "EARTH", green, Gravity.END),
            FrameLayout.LayoutParams(
                dp(112),
                dp(88),
                Gravity.END or Gravity.CENTER_VERTICAL
            ).apply {
                rightMargin = dp(8)
            }
        )

        // TIME
        ecosystemWorld.addView(
            worldNode("⏳", "TIME", purple, Gravity.BOTTOM),
            FrameLayout.LayoutParams(
                dp(112),
                dp(88),
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ).apply {
                bottomMargin = dp(8)
            }
        )

        // Central energy pulse
        val pulse = View(this).apply {
            background = rounded(
                Color.argb(45, 120, 90, 255),
                200,
                purple,
                1
            )
            alpha = 0.35f
        }

        ecosystemWorld.addView(
            pulse,
            FrameLayout.LayoutParams(
                dp(190),
                dp(190),
                Gravity.CENTER
            )
        )

        // Keep the core visually above the pulse.
        core.bringToFront()

        root.addView(
            ecosystemWorld,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(300)
            )
        )

        root.addView(gap(14))


        // ========================================================
        // FOOTER
        // ========================================================

        root.addView(
            tv(
                "BETTER DECISIONS • A BRIGHTER TOMORROW",
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
                dp(6),
                dp(2),
                dp(6),
                dp(2)
            )

            background = rounded(
                Color.argb(190, 5, 12, 25),
                18,
                Color.rgb(45, 145, 205),
                1
            )

            elevation = dp(12).toFloat()
        }

        fun bottomNavItem(
            title: String,
            selected: Boolean,
            action: () -> Unit
        ) {
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(
                    dp(3),
                    dp(5),
                    dp(3),
                    dp(4)
                )

                background = if (selected) {
                    rounded(
                        Color.argb(80, 35, 205, 255),
                        14,
                        Color.rgb(35, 180, 230),
                        1
                    )
                } else {
                    android.graphics.drawable.ColorDrawable(Color.TRANSPARENT)
                }

                elevation = if (selected) {
                    dp(4).toFloat()
                } else {
                    0f
                }

                setOnClickListener {
                GDMIEAudioManager.playUiClick(this@MainActivity)
                    action()
                }
            }

            item.addView(
                tv(
                    if (selected) "●" else "○",
                    10f,
                    if (selected) cyan else dim,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                    setShadowLayer(
                        if (selected) dp(4).toFloat() else 0f,
                        0f,
                        0f,
                        cyan
                    )
                }
            )

            item.addView(
                tv(
                    title,
                    8f,
                    if (selected) white else muted,
                    selected
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(1), 0, 0)
                }
            )

            bottom.addView(
                item,
                LinearLayout.LayoutParams(
                    0,
                    dp(52),
                    1f
                ).apply {
                    setMargins(
                        dp(2),
                        0,
                        dp(2),
                        0
                    )
                }
            )
        }

        bottomNavItem(
            "Home",
            true
        ) {
            // Already on Home
        }

        bottomNavItem(
            "Challenge",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    DecisionChallengeActivity::class.java
                )
            )
        }

        bottomNavItem(
            "Analyze",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    LiveAnalysisActivity::class.java
                )
            )
        }

        bottomNavItem(
            "Ecosystem",
            false
        ) {
            // Ecosystem hub is currently represented on Home.
            // Dedicated Activity can be connected later.
        }

        bottomNavItem(
            "Profile",
            false
        ) {
            startActivity(
                Intent(
                    this@MainActivity,
                    ProfileActivity::class.java
                )
            )
        }

        val frame = FrameLayout(this)

        frame.addView(
            worldFrame,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        frame.addView(
            bottom,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(64),
                Gravity.BOTTOM
            ).apply {
                setMargins(
                    dp(10),
                    0,
                    dp(10),
                    dp(8)
                )
            }
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





        explorerLevelView?.text =
            "%02d".format(GDMIEGameProgress.getLevel(this))

        explorerXpView?.text =
            "${GDMIEGameProgress.xpIntoCurrentLevel(this)} / 100 XP"

        explorerStreakView?.text =
            "${GDMIEGameProgress.getStreak(this)}  🔥"
    }

    override fun onResume() {
        super.onResume()


        updateHomeOverview()
    }
}
