package com.gdmie

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object GDMIECloudProfile {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val db: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private const val USERS = "users"

    fun syncProfile(context: Context) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val data = hashMapOf(
            "uid" to uid,
            "displayName" to (
                user.displayName
                    ?: context.getSharedPreferences("GDMIE_ACCOUNT", Context.MODE_PRIVATE)
                        .getString("display_name", "GDMIE EXPLORER")
            ),
            "email" to (user.email ?: ""),
            "xp" to com.gdmie.game.GDMIEGameProgress.getXp(context),
            "level" to com.gdmie.game.GDMIEGameProgress.getLevel(context),
            "streak" to com.gdmie.game.GDMIEGameProgress.getStreak(context),
            "decisions" to com.gdmie.game.GDMIEGameProgress.getDecisions(context),
            "language" to LanguageManager.getLanguage(context)
        )

        db.collection(USERS)
            .document(uid)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    fun loadProfile(context: Context, onComplete: (() -> Unit)? = null) {
        val user = auth.currentUser ?: return

        db.collection(USERS)
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val prefs = context.getSharedPreferences(
                        "GDMIE_ACCOUNT",
                        Context.MODE_PRIVATE
                    )

                    val xp = (document.getLong("xp") ?: 0L).toInt()
                    val level = (document.getLong("level") ?: 1L).toInt()
                    val decisions = (document.getLong("decisions") ?: 0L).toInt()
                    val streak = (document.getLong("streak") ?: 0L).toInt()

                    com.gdmie.game.GDMIEGameProgress.restoreFromCloud(
                        context,
                        xp,
                        level,
                        decisions,
                        streak
                    )

                    document.getString("displayName")?.let {
                        prefs.edit().putString("display_name", it).apply()
                    }

                    document.getString("email")?.let {
                        prefs.edit().putString("email", it).apply()
                    }

                    document.getString("language")?.let {
                        LanguageManager.setLanguage(context, it)
                    }
                }

                onComplete?.invoke()
            }
            .addOnFailureListener {
                onComplete?.invoke()
            }
    }
}
