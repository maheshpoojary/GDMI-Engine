package com.gdmie

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Switch
import com.gdmie.audio.GDMIEAudioManager
import com.gdmie.game.GDMIEGameProgress

class ProfileActivity : GDMIEBaseActivity() {

    private val bg = Color.rgb(2, 7, 18)
    private val card = Color.argb(175, 9, 25, 50)
    private val cyan = Color.rgb(35, 205, 255)
    private val purple = Color.rgb(165, 85, 255)
    private val gold = Color.rgb(255, 195, 45)
    private val green = Color.rgb(55, 225, 145)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 195)

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun tv(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    private fun glass(stroke: Int = Color.rgb(35, 150, 220)): GradientDrawable =
        GradientDrawable().apply {
            setColor(card)
            setStroke(dp(1), stroke)
            cornerRadius = dp(20).toFloat()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val xp = GDMIEGameProgress.getXp(this)
        val level = GDMIEGameProgress.getLevel(this)
        val decisions = GDMIEGameProgress.getDecisions(this)
        val streak = GDMIEGameProgress.getStreak(this)
        val levelXp = GDMIEGameProgress.xpIntoCurrentLevel(this)

        val homePrefs = getSharedPreferences("GDMIE_HOME", MODE_PRIVATE)
        val mode = homePrefs.getString("home_mode", "COSMIC") ?: "COSMIC"

        // AUDIO SETTINGS — persistent across app restarts
        val audioPrefs = getSharedPreferences("GDMIE_AUDIO", MODE_PRIVATE)
        val musicEnabled = audioPrefs.getBoolean("music_enabled", true)
        val sfxEnabled = audioPrefs.getBoolean("sfx_enabled", true)

        GDMIEAudioManager.setMusicEnabled(musicEnabled)
        GDMIEAudioManager.setSfxEnabled(sfxEnabled)

        // Future-ready account values.
        val accountPrefs =
            getSharedPreferences("GDMIE_ACCOUNT", MODE_PRIVATE)

        val displayName =
            accountPrefs.getString("display_name", null)
                ?: accountPrefs.getString("user_name", null)
                ?: "GDMIE EXPLORER"

        val email =
            accountPrefs.getString("email", null)
                ?: "Guest account"

        val loginMethod =
            accountPrefs.getString("login_method", null)
                ?: "GUEST MODE"

        val scroll = ScrollView(this).apply {
            setBackgroundColor(bg)
        }

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(28), dp(20), dp(28))
            setBackgroundColor(bg)
        }

        scroll.addView(root)

        // HEADER
        root.addView(
            tv("🧠  GDMIE", 30f, cyan, true),
            LinearLayout.LayoutParams(-1, dp(44))
        )

        root.addView(
            tv("YOUR PROFILE", 13f, purple, true),
            LinearLayout.LayoutParams(-1, dp(28))
        )

        // PROFILE HERO
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(20), dp(20), dp(20), dp(20))
            background = glass(cyan)
            elevation = dp(8).toFloat()
        }

        val avatar = tv("👤", 48f, white, true).apply {
            gravity = Gravity.CENTER
        }

        hero.addView(
            avatar,
            LinearLayout.LayoutParams(-1, dp(62))
        )

        hero.addView(
            tv(displayName, 23f, white, true).apply {
                gravity = Gravity.CENTER
            }
        )

        hero.addView(
            tv("GDMIE EXPLORER", 11f, cyan, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(4), 0, 0)
            }
        )

        hero.addView(
            tv("$loginMethod  •  $email", 9f, muted).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(7), 0, 0)
            }
        )

        root.addView(
            hero,
            LinearLayout.LayoutParams(-1, dp(210)).apply {
                topMargin = dp(14)
            }
        )

        // LEVEL / XP
        val progressCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = glass(purple)
        }

        progressCard.addView(
            tv("EXPLORER PROGRESS", 11f, cyan, true)
        )

        progressCard.addView(
            tv("LEVEL $level", 30f, white, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(8), 0, 0)
            }
        )

        progressCard.addView(
            tv("$levelXp / 100 XP   •   TOTAL $xp XP", 11f, muted, true).apply {
                gravity = Gravity.CENTER
            }
        )

        val progress = ProgressBar(
            this,
            null,
            android.R.attr.progressBarStyleHorizontal
        ).apply {
            max = 100
            progress = levelXp
            progressTintList = ColorStateList.valueOf(cyan)
        }

        progressCard.addView(
            progress,
            LinearLayout.LayoutParams(-1, dp(8)).apply {
                topMargin = dp(12)
            }
        )

        root.addView(
            progressCard,
            LinearLayout.LayoutParams(-1, dp(150)).apply {
                topMargin = dp(12)
            }
        )

        // STATS
        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        fun statCard(
            icon: String,
            value: String,
            label: String,
            color: Int
        ): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(8), dp(12), dp(8), dp(12))
                background = glass(color)

                addView(tv(icon, 27f, color, true))

                addView(
                    tv(value, 24f, white, true).apply {
                        gravity = Gravity.CENTER
                        setPadding(0, dp(5), 0, 0)
                    }
                )

                addView(
                    tv(label, 9f, muted, true).apply {
                        gravity = Gravity.CENTER
                        setPadding(0, dp(2), 0, 0)
                    }
                )
            }
        }

        stats.addView(
            statCard("🔥", streak.toString(), "STREAK", gold),
            LinearLayout.LayoutParams(0, dp(135), 1f).apply {
                marginEnd = dp(6)
            }
        )

        stats.addView(
            statCard("🧠", decisions.toString(), "DECISIONS", cyan),
            LinearLayout.LayoutParams(0, dp(135), 1f).apply {
                marginStart = dp(6)
            }
        )

        root.addView(
            stats,
            LinearLayout.LayoutParams(-1, dp(135)).apply {
                topMargin = dp(12)
            }
        )

        // WORLD
        val world = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = glass(
                if (mode == "CITY") purple else cyan
            )
        }

        world.addView(
            tv("CURRENT WORLD", 11f, cyan, true)
        )

        world.addView(
            tv(
                if (mode == "CITY")
                    "🏙️  CITY WORLD"
                else
                    "🌌  COSMIC WORLD",
                21f,
                white,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(10), 0, dp(4))
            }
        )

        world.addView(
            tv(
                "Explore  •  Decide  •  Learn  •  Evolve",
                10f,
                muted
            ).apply {
                gravity = Gravity.CENTER
            }
        )

        root.addView(
            world,
            LinearLayout.LayoutParams(-1, dp(130)).apply {
                topMargin = dp(12)
            }
        )

        // SYSTEM AUDIO
        val audioCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(14))
            background = glass(purple)
            elevation = dp(8).toFloat()
        }

        audioCard.addView(
            tv("SYSTEM AUDIO", 11f, purple, true)
        )

        audioCard.addView(
            tv(
                "Control GDMIE music and interaction sounds.",
                10f,
                muted
            ).apply {
                setPadding(0, dp(6), 0, dp(8))
            }
        )

        val musicSwitch = Switch(this).apply {
            text = "🎵  MUSIC"
            textSize = 14f
            setTextColor(white)
            isChecked = musicEnabled
        }

        musicSwitch.setOnCheckedChangeListener { _, checked ->
            audioPrefs.edit()
                .putBoolean("music_enabled", checked)
                .apply()

            GDMIEAudioManager.setMusicEnabled(checked)

            if (checked) {
                GDMIEAudioManager.playContinuousTheme(
                    this@ProfileActivity
                )
            }

            if (GDMIEAudioManager.isSfxEnabled()) {
                GDMIEAudioManager.playUiClick(this@ProfileActivity)
            }
        }

        audioCard.addView(
            musicSwitch,
            LinearLayout.LayoutParams(-1, dp(48))
        )

        val sfxSwitch = Switch(this).apply {
            text = "🔊  UI & EFFECT SOUNDS"
            textSize = 14f
            setTextColor(white)
            isChecked = sfxEnabled
        }

        sfxSwitch.setOnCheckedChangeListener { _, checked ->
            audioPrefs.edit()
                .putBoolean("sfx_enabled", checked)
                .apply()

            GDMIEAudioManager.setSfxEnabled(checked)

            if (checked) {
                GDMIEAudioManager.playUiClick(this@ProfileActivity)
            }
        }

        audioCard.addView(
            sfxSwitch,
            LinearLayout.LayoutParams(-1, dp(48))
        )

        root.addView(
            audioCard,
            LinearLayout.LayoutParams(-1, dp(175)).apply {
                topMargin = dp(12)
            }
        )

        // PRIVACY POLICY
        val privacyCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = glass(purple)
            elevation = dp(8).toFloat()
        }

        privacyCard.addView(
            tv("🔒  PRIVACY & DATA", 11f, cyan, true)
        )

        privacyCard.addView(
            tv(
                "Read how GDMIE handles your account, progress, ads and data.",
                12f,
                white,
                true
            ).apply {
                setPadding(0, dp(9), 0, dp(12))
            }
        )

        val privacyButton = tv(
            "VIEW PRIVACY POLICY  →",
            11f,
            white,
            true
        ).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(13), 0, dp(13))
            background = glass(cyan)
            setOnClickListener {
                startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://gdmie-d474a.web.app")
                    )
                )
            }
        }

        privacyCard.addView(
            privacyButton,
            LinearLayout.LayoutParams(-1, dp(50))
        )

        root.addView(
            privacyCard,
            LinearLayout.LayoutParams(-1, dp(174)).apply {
                topMargin = dp(12)
            }
        )

        if (loginMethod != "GUEST MODE") {
            val logoutButton = tv("LOG OUT  →", 13f, white, true).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(16), 0, dp(16))
                background = glass(cyan)
                setOnClickListener {
                    android.app.AlertDialog.Builder(this@ProfileActivity)
                        .setTitle("LOG OUT")
                        .setMessage("Sign out of this GDMIE account on this device?")
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("LOG OUT") { _, _ ->
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            getSharedPreferences("GDMIE_GAME_PROGRESS", MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply()
                            getSharedPreferences("GDMIE_ACCOUNT", MODE_PRIVATE).edit().clear().apply()
                            startActivity(android.content.Intent(this@ProfileActivity, LoginActivity::class.java))
                            finishAffinity()
                        }
                        .show()
                }
            }
            root.addView(logoutButton, LinearLayout.LayoutParams(-1, dp(58)).apply {
                topMargin = dp(14)
            })
        }
        if (loginMethod == "GUEST MODE") {
            val accountPrompt = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(18), dp(18), dp(18), dp(18))
                background = glass(purple)
                elevation = dp(8).toFloat()
            }

            accountPrompt.addView(
                tv("🔐  GDMIE ACCOUNT", 13f, cyan, true)
            )

            accountPrompt.addView(
                tv(
                    "Save your progress.\nKeep your XP, Level, Streak\nand decision history.",
                    12f,
                    white,
                    true
                ).apply {
                    setPadding(0, dp(10), 0, dp(12))
                }
            )

            val connectButton = tv(
                "CONTINUE WITH EMAIL  →",
                12f,
                white,
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(14), 0, dp(14))
                background = glass(cyan)
                setOnClickListener {
                    startActivity(
                        android.content.Intent(
                            this@ProfileActivity,
                            LoginActivity::class.java
                        )
                    )
                }
            }

            accountPrompt.addView(
                connectButton,
                LinearLayout.LayoutParams(-1, dp(52))
            )

            accountPrompt.addView(
                tv(
                    "NOT NOW",
                    10f,
                    muted,
                    true
                ).apply {
                    gravity = Gravity.CENTER
                    setPadding(0, dp(12), 0, 0)
                }
            )

            root.addView(
                accountPrompt,
                LinearLayout.LayoutParams(-1, dp(205)).apply {
                    topMargin = dp(12)
                }
            )
        }

        if (loginMethod != "GUEST MODE") {
            val deleteAccountButton = tv(
                "DELETE GDMIE ACCOUNT",
                11f,
                Color.rgb(255, 120, 120),
                true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(14), 0, dp(14))
                background = glass(Color.rgb(70, 25, 35))

                setOnClickListener {
                    val passwordInput = android.widget.EditText(this@ProfileActivity).apply {
                        hint = "Enter your password"
                        inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or
                            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        setPadding(dp(16), dp(10), dp(16), dp(10))
                    }

                    val box = LinearLayout(this@ProfileActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(dp(20), dp(4), dp(20), 0)
                        addView(
                            tv(
                                "This permanently deletes your GDMIE account and cloud profile.",
                                11f,
                                muted
                            ),
                            LinearLayout.LayoutParams(-1, dp(58))
                        )
                        addView(
                            passwordInput,
                            LinearLayout.LayoutParams(-1, dp(54))
                        )
                    }

                    android.app.AlertDialog.Builder(this@ProfileActivity)
                        .setTitle("DELETE GDMIE ACCOUNT")
                        .setView(box)
                        .setNegativeButton("CANCEL", null)
                        .setPositiveButton("DELETE") { _, _ ->
                            val password = passwordInput.text.toString()

                            if (password.isBlank()) {
                                android.widget.Toast.makeText(
                                    this@ProfileActivity,
                                    "Enter your password to continue.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@setPositiveButton
                            }

                            val auth =
                                com.google.firebase.auth.FirebaseAuth.getInstance()
                            val user = auth.currentUser

                            if (user == null || user.email.isNullOrBlank()) {
                                android.widget.Toast.makeText(
                                    this@ProfileActivity,
                                    "Account session not found.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@setPositiveButton
                            }

                            val email = user.email!!
                            val uid = user.uid

                            val credential =
                                com.google.firebase.auth.EmailAuthProvider
                                    .getCredential(email, password)

                            user.reauthenticate(credential)
                                .addOnSuccessListener {
                                    com.google.firebase.firestore.FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .delete()
                                        .addOnSuccessListener {
                                            user.delete()
                                                .addOnSuccessListener {
                                                    getSharedPreferences(
                                                        "GDMIE_GAME_PROGRESS",
                                                        MODE_PRIVATE
                                                    ).edit()
                                                        .clear()
                                                        .apply()

                                                    getSharedPreferences(
                                                        "GDMIE_ACCOUNT",
                                                        MODE_PRIVATE
                                                    ).edit()
                                                        .clear()
                                                        .apply()

                                                    android.widget.Toast.makeText(
                                                        this@ProfileActivity,
                                                        "GDMIE account deleted.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()

                                                    startActivity(
                                                        android.content.Intent(
                                                            this@ProfileActivity,
                                                            LoginActivity::class.java
                                                        )
                                                    )
                                                    finishAffinity()
                                                }
                                                .addOnFailureListener {
                                                    android.widget.Toast.makeText(
                                                        this@ProfileActivity,
                                                        "Account deletion failed. Please try again.",
                                                        android.widget.Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            android.widget.Toast.makeText(
                                                this@ProfileActivity,
                                                "Cloud profile could not be deleted.",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                                .addOnFailureListener {
                                    android.widget.Toast.makeText(
                                        this@ProfileActivity,
                                        "Incorrect password. Account was not deleted.",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .show()
                }
            }

            root.addView(
                deleteAccountButton,
                LinearLayout.LayoutParams(-1, dp(54)).apply {
                    topMargin = dp(10)
                }
            )
        }

        // ACCOUNT STATUS
        val account = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(18), dp(18), dp(18))
            background = glass(green)
        }

        account.addView(
            tv("ACCOUNT STATUS", 11f, cyan, true)
        )

        account.addView(
            tv(
                if (loginMethod == "GUEST MODE")
                    "🟢  EXPLORER SESSION"
                else
                    "🟢  ACCOUNT CONNECTED",
                15f,
                white,
                true
            ).apply {
                setPadding(0, dp(10), 0, dp(3))
            }
        )

        account.addView(
            tv(
                if (loginMethod == "GUEST MODE")
                    "Progress is stored on this device."
                else
                    "Your GDMIE profile is connected.",
                10f,
                muted
            )
        )

        root.addView(
            account,
            LinearLayout.LayoutParams(-1, dp(125)).apply {
                topMargin = dp(12)
            }
        )

        root.addView(
            tv(
                "GDMIE supports your thinking.\nYou make the final decision.",
                10f,
                muted
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(26), 0, dp(20))
            },
            LinearLayout.LayoutParams(-1, dp(70))
        )

        setContentView(scroll)
    }
}
