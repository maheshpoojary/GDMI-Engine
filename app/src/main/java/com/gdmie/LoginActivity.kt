package com.gdmie

import com.gdmie.audio.GDMIEAudioManager
import com.google.firebase.auth.FirebaseAuth

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast

class LoginActivity : GDMIEBaseActivity() {
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val bg = Color.rgb(2, 5, 15)
    private val surface = Color.rgb(9, 17, 34)
    private val surface2 = Color.rgb(13, 25, 48)
    private val cyan = Color.rgb(35, 205, 255)
    private val blue = Color.rgb(45, 145, 255)
    private val purple = Color.rgb(155, 90, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 188)
    private val dim = Color.rgb(90, 112, 140)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = Color.rgb(5, 7, 18)

        val loginRoot = createLogin()
        setContentView(loginRoot)
        animateCinematic(loginRoot)
    }

    private fun animateCinematic(rootView: View) {
        val root = rootView as? ViewGroup ?: return

        // 🌌 Cinematic entrance
        root.alpha = 0f
        root.scaleX = 1.015f
        root.scaleY = 1.015f

        root.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300L)
            .setInterpolator(android.view.animation.DecelerateInterpolator(1.8f))
            .start()

        for (i in 0 until root.childCount) {
            val child = root.getChildAt(i)

            child.alpha = 0f
            child.translationY = dp(34).toFloat()
            child.scaleX = 0.97f
            child.scaleY = 0.97f

            child.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setStartDelay(40L + (i * 35L))
                .setDuration(280L)
                .setInterpolator(android.view.animation.DecelerateInterpolator(1.6f))
                .start()

            if (child is ViewGroup) {
                for (j in 0 until child.childCount) {
                    val inner = child.getChildAt(j)

                    inner.alpha = 0f
                    inner.translationY = dp(16).toFloat()
                    inner.scaleX = 0.985f
                    inner.scaleY = 0.985f

                    inner.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setStartDelay(70L + (i * 35L) + (j * 25L))
                        .setDuration(220L)
                        .setInterpolator(android.view.animation.DecelerateInterpolator(1.5f))
                        .start()
                }
            }
        }

        // ✨ GDMIE brand pulse after entrance
        val logo = root.getChildAt(0)
        logo.postDelayed({
            logo.animate()
                .scaleX(1.035f)
                .scaleY(1.035f)
                .setDuration(260L)
                .setInterpolator(android.view.animation.AccelerateDecelerateInterpolator())
                .withEndAction {
                    logo.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(420L)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .start()
                }
                .start()
        }, 400L)
    }

    private fun createLogin(): View {

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(dp(22), dp(20), dp(22), dp(18))

            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(2, 5, 15),
                    Color.rgb(6, 18, 42),
                    Color.rgb(20, 7, 42),
                    Color.rgb(3, 6, 18)
                )
            )
        }

        // ───────── TOP BRAND ─────────

        val logo = TextView(this).apply {
            text = "GDMIE"
            textSize = 40f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.10f
            setShadowLayer(
                dp(9).toFloat(),
                0f,
                0f,
                Color.rgb(45, 175, 255)
            )
        }

        root.addView(
            logo,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(58)
            )
        )

        val title = TextView(this).apply {
            text = "WELCOME TO GDMIE"
            textSize = 20f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.04f
        }

        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Decision intelligence.\nLearn  •  Analyze  •  Grow."
            textSize = 12.5f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setLineSpacing(dp(4).toFloat(), 1f)
            setPadding(0, dp(7), 0, dp(12))
        }

        root.addView(subtitle)

        // ───────── AI COMPANION CARD ─────────

        val companion = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(12), dp(10), dp(14), dp(10))

            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.rgb(10, 25, 49),
                    Color.rgb(19, 17, 47)
                )
            ).apply {
                cornerRadius = dp(18).toFloat()
                setStroke(dp(1), Color.rgb(35, 125, 190))
            }

            elevation = dp(4).toFloat()
        }

        val mascot = ImageView(this).apply {
            setImageResource(com.gdmie.R.drawable.mascot)
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = GradientDrawable().apply {
                setColor(Color.rgb(5, 16, 32))
                cornerRadius = dp(14).toFloat()
                setStroke(dp(1), cyan)
            }
            clipToOutline = true
        }

        companion.addView(
            mascot,
            LinearLayout.LayoutParams(
                dp(86),
                dp(86)
            )
        )

        val companionText = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(13), 0, 0, 0)
        }

        val companionTitle = TextView(this).apply {
            text = "YOUR AI COMPANION"
            textSize = 10f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            letterSpacing = 0.05f
        }

        companionText.addView(companionTitle)

        val companionMain = TextView(this).apply {
            text = "Think smarter.\nAnalyze deeper."
            textSize = 17f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            setPadding(0, dp(5), 0, dp(3))
            setLineSpacing(dp(2).toFloat(), 1f)
        }

        companionText.addView(companionMain)

        val companionSub = TextView(this).apply {
            text = "Every decision helps you learn."
            textSize = 10f
            setTextColor(muted)
        }

        companionText.addView(companionSub)

        companion.addView(
            companionText,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(
            companion,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(108)
            )
        )

        addSpace(root, 16)

        // ───────── LOGIN CARD ─────────

        val loginCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))

            background = GradientDrawable().apply {
                setColor(Color.rgb(8, 16, 32))
                cornerRadius = dp(22).toFloat()
                setStroke(dp(1), Color.rgb(45, 105, 170))
            }

            elevation = dp(5).toFloat()
        }

        val cardTitle = TextView(this).apply {
            text = "CHOOSE HOW TO CONTINUE"
            textSize = 10f
            setTextColor(dim)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.08f
            setPadding(0, 0, 0, dp(11))
        }

        loginCard.addView(cardTitle)

        // Email
        loginCard.addView(
            loginButton(
                "✉",
                "CONTINUE WITH EMAIL",
                surface2,
                false
            ) {
                showEmailAuthDialog()
            }
        )

        root.addView(
            loginCard,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        addSpace(root, 13)

        // ───────── OR ─────────

        val divider = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        divider.addView(
            lineView(),
            LinearLayout.LayoutParams(
                0,
                dp(1),
                1f
            )
        )

        val orText = TextView(this).apply {
            text = "  OR  "
            textSize = 10f
            setTextColor(muted)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        divider.addView(orText)

        divider.addView(
            lineView(),
            LinearLayout.LayoutParams(
                0,
                dp(1),
                1f
            )
        )

        root.addView(
            divider,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(20)
            )
        )

        addSpace(root, 8)

        // ───────── GUEST CTA ─────────

        val guest = Button(this).apply {
            text = "PLAY AS GUEST    →"
            textSize = 14f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            isAllCaps = false
            letterSpacing = 0.04f
            includeFontPadding = false

            background = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.rgb(125, 65, 235),
                    Color.rgb(175, 80, 255)
                )
            ).apply {
                cornerRadius = dp(17).toFloat()
                setStroke(dp(1), Color.rgb(95, 220, 255))
            }

            elevation = dp(6).toFloat()

            setOnClickListener {
                isEnabled = false

                getSharedPreferences("GDMIE_ACCOUNT", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .putString("login_method", "GUEST MODE")
                    .putBoolean("guest_mode", true)
                    .apply()

                GDMIEAudioManager.playSfx(
                    this@LoginActivity,
                    R.raw.gdmie_login_guest
                )

                animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(110L)
                    .withEndAction {

                        val screen =
                            this@LoginActivity
                                .findViewById<ViewGroup>(android.R.id.content)

                        screen.animate()
                            .alpha(0f)
                            .translationY(-dp(18).toFloat())
                            .scaleX(1.025f)
                            .scaleY(1.025f)
                            .setDuration(420L)
                            .setInterpolator(
                                android.view.animation.AccelerateInterpolator(1.2f)
                            )
                            .withEndAction {

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        GuestWelcomeActivity::class.java
                                    )
                                )

                                overridePendingTransition(0, 0)
                                finish()
                            }
                            .start()
                    }
                    .start()
            }
        }

        root.addView(
            guest,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(56)
            )
        )

        val guestNote = TextView(this).apply {
            text = "Guest Mode  •  No registration required"
            textSize = 9.5f
            setTextColor(muted)
            gravity = Gravity.CENTER
            setPadding(0, dp(9), 0, 0)
        }

        root.addView(guestNote)

        // ───────── FOOTER ─────────

        val footer = TextView(this).apply {
            text = "EDUCATIONAL SIMULATION  •  SMARTER DECISIONS"
            textSize = 8.5f
            setTextColor(Color.rgb(82, 105, 135))
            gravity = Gravity.CENTER
            letterSpacing = 0.03f
        }

        root.addView(
            footer,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        return root
    }

    private fun loginButton(
        icon: String,
        textValue: String,
        fillColor: Int,
        primary: Boolean,
        action: () -> Unit
    ): LinearLayout {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            setPadding(dp(12), 0, dp(12), 0)

            background = GradientDrawable().apply {
                setColor(fillColor)
                cornerRadius = dp(15).toFloat()
                setStroke(
                    dp(1),
                    if (primary) cyan else Color.rgb(40, 85, 130)
                )
            }

            elevation = if (primary) dp(4).toFloat() else dp(2).toFloat()

            setOnClickListener {
                  GDMIEAudioManager.playUiClick(this@LoginActivity)
                animate()
                    .scaleX(0.98f)
                    .scaleY(0.98f)
                    .setDuration(60)
                    .withEndAction {
                        animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)
                            .withEndAction { action() }
                            .start()
                    }
                    .start()
            }
        }

        val iconView = TextView(this).apply {
            text = icon
            textSize = 16f
            setTextColor(
                if (primary) white else cyan
            )
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        row.addView(
            iconView,
            LinearLayout.LayoutParams(
                dp(38),
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        val label = TextView(this).apply {
            text = textValue
            textSize = 12.5f
            setTextColor(white)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            letterSpacing = 0.025f
        }

        row.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
        )

        val arrow = TextView(this).apply {
            text = "›"
            textSize = 22f
            setTextColor(
                if (primary) white else cyan
            )
            gravity = Gravity.CENTER
        }

        row.addView(
            arrow,
            LinearLayout.LayoutParams(
                dp(30),
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        row.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(50)
        )

        return row
    }

    private fun lineView(): View =
        View(this).apply {
            setBackgroundColor(Color.rgb(55, 82, 115))
        }

    private fun addSpace(root: LinearLayout, height: Int) {
        root.addView(
            Space(this),
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }

    private fun addCardSpace(card: LinearLayout, height: Int) {
        card.addView(
            Space(this),
            LinearLayout.LayoutParams(
                1,
                dp(height)
            )
        )
    }

    private fun showEmailAuthDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(8), dp(24), dp(8))
        }

        val emailInput = EditText(this).apply {
            hint = "Email address"
            textSize = 16f
            setSingleLine(true)
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(dp(4), 0, dp(4), 0)
        }

        val passwordInput = EditText(this).apply {
            hint = "Password"
            textSize = 16f
            setSingleLine(true)
            inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod =
                android.text.method.PasswordTransformationMethod.getInstance()
            setPadding(dp(4), 0, dp(4), 0)
        }

        val passwordToggle = TextView(this).apply {
            text = "SHOW PASSWORD"
            textSize = 11f
            setTextColor(cyan)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(2), 0, dp(8))

            setOnClickListener {
                val showing = passwordInput.transformationMethod == null

                passwordInput.transformationMethod =
                    if (showing) {
                        android.text.method.PasswordTransformationMethod.getInstance()
                    } else {
                        null
                    }

                text = if (showing) "SHOW PASSWORD" else "HIDE PASSWORD"

                passwordInput.setSelection(
                    passwordInput.text.length
                )
            }
        }

        container.addView(
            emailInput,
            LinearLayout.LayoutParams(-1, dp(54)).apply {
                bottomMargin = dp(10)
            }
        )

        container.addView(
            passwordInput,
            LinearLayout.LayoutParams(-1, dp(54))
        )

        container.addView(
            passwordToggle,
            LinearLayout.LayoutParams(-1, dp(36))
        )

        AlertDialog.Builder(this)
            .setTitle("GDMIE ACCOUNT")
            .setMessage("Secure access with your email and password.")
            .setView(container)
            .setPositiveButton("LOGIN") { _, _ ->
                loginWithEmail(
                    emailInput.text.toString().trim(),
                    passwordInput.text.toString()
                )
            }
            .setNeutralButton("CREATE ACCOUNT") { _, _ ->
                createAccountWithEmail(
                    emailInput.text.toString().trim(),
                    passwordInput.text.toString()
                )
            }
            .setNegativeButton("FORGOT PASSWORD") { _, _ ->
                sendPasswordReset(
                    emailInput.text.toString().trim()
                )
            }
            .show()
    }

    private fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                saveFirebaseAccount(user?.displayName, user?.email)
                Toast.makeText(this, "ACCESS GRANTED", Toast.LENGTH_SHORT).show()
                openHomeAfterAuth()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    error.message ?: "Login failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun createAccountWithEmail(email: String, password: String) {
        if (email.isBlank() || password.length < 6) {
            Toast.makeText(
                this,
                "Use a valid email and password of at least 6 characters",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                saveFirebaseAccount(user?.displayName, user?.email)
                Toast.makeText(this, "ACCOUNT CREATED", Toast.LENGTH_SHORT).show()
                openHomeAfterAuth()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    error.message ?: "Account creation failed",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun openHomeAfterAuth() {
        GDMIECloudProfile.loadProfile(this) {
            GDMIECloudProfile.syncProfile(this)

            startActivity(
                Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            )
            overridePendingTransition(0, 0)
            finish()
        }
    }

    private fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            Toast.makeText(
                this,
                "Enter your email first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "PASSWORD RESET EMAIL SENT\nCheck your Gmail Inbox or Spam folder for the reset link.",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { error ->
                Toast.makeText(
                    this,
                    error.message ?: "Unable to send reset email",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun saveFirebaseAccount(displayName: String?, email: String?) {
        getSharedPreferences("GDMIE_ACCOUNT", MODE_PRIVATE)
            .edit()
            .putString("display_name", displayName ?: "GDMIE EXPLORER")
            .putString("email", email ?: "")
            .putString("login_method", "EMAIL")
            .putBoolean("guest_mode", false)
            .apply()
    }

    private fun showComingSoon() {
        Toast.makeText(
            this,
            "Sign-in will be connected later",
            Toast.LENGTH_SHORT
        ).show()
    }
}
