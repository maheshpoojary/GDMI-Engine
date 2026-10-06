package com.gdmie.game

import android.content.Context

object GDMIEGameProgress {

    private const val PREFS = "GDMIE_GAME_PROGRESS"

    private const val XP = "xp"
    private const val LEVEL = "level"
    private const val DECISIONS = "decisions"
    private const val STREAK = "streak"

    fun addDecision(context: Context, resultXp: Int = 10): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val newXp = prefs.getInt(XP, 0) + resultXp
        val newDecisions = prefs.getInt(DECISIONS, 0) + 1
        val newLevel = calculateLevel(newXp)

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .putInt(DECISIONS, newDecisions)
            .apply()

        syncCloud(context)

        return resultXp
    }

    fun addAnalysisXp(context: Context, xp: Int = 5): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val newXp = prefs.getInt(XP, 0) + xp
        val newLevel = calculateLevel(newXp)

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .apply()

        syncCloud(context)

        return xp
    }

    data class AnalysisXpResult(
        val awardedXp: Int,
        val oldXp: Int,
        val newXp: Int,
        val oldLevel: Int,
        val newLevel: Int,
        val levelUp: Boolean
    )

    fun addAnalysisXpOnce(
        context: Context,
        analysisId: Int,
        xp: Int = 5
    ): AnalysisXpResult {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "analysis_rewarded_$analysisId"

        if (prefs.getBoolean(key, false)) {
            val currentXp = prefs.getInt(XP, 0)
            val currentLevel = calculateLevel(currentXp)

            return AnalysisXpResult(
                awardedXp = 0,
                oldXp = currentXp,
                newXp = currentXp,
                oldLevel = currentLevel,
                newLevel = currentLevel,
                levelUp = false
            )
        }

        val oldXp = prefs.getInt(XP, 0)
        val oldLevel = calculateLevel(oldXp)
        val newXp = oldXp + xp
        val newLevel = calculateLevel(newXp)
        val levelUp = didLevelUp(oldXp, newXp)

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .putBoolean(key, true)
            .apply()

        syncCloud(context)

        return AnalysisXpResult(
            awardedXp = xp,
            oldXp = oldXp,
            newXp = newXp,
            oldLevel = oldLevel,
            newLevel = newLevel,
            levelUp = levelUp
        )
    }

    // Rewarded Ad XP — one reward per unique completed ad
    fun addRewardedAdXpOnce(
        context: Context,
        rewardId: String,
        xp: Int = 10
    ): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "rewarded_ad_xp_$rewardId"

        if (prefs.getBoolean(key, false)) {
            return 0
        }

        val oldXp = prefs.getInt(XP, 0)
        val newXp = oldXp + xp
        val newLevel = calculateLevel(newXp)

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .putBoolean(key, true)
            .apply()

        syncCloud(context)

        return xp
    }

    fun addOutcomeXpOnce(
        context: Context,
        analysisId: Int,
        xp: Int = 5
    ): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = "outcome_rewarded_$analysisId"

        if (prefs.getBoolean(key, false)) {
            return 0
        }

        val newXp = prefs.getInt(XP, 0) + xp
        val newLevel = calculateLevel(newXp)

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .putBoolean(key, true)
            .apply()

        syncCloud(context)

        return xp
    }

    fun completeDailyChallenge(context: Context, xp: Int = 30): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        val today = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.US
        ).format(java.util.Date())

        val lastDailyDate = prefs.getString("daily_last_date", null)

        // Already completed today — no duplicate XP or streak.
        if (lastDailyDate == today) {
            return 0
        }

        val oldXp = prefs.getInt(XP, 0)
        val newXp = oldXp + xp
        val newLevel = calculateLevel(newXp)

        val oldStreak = prefs.getInt(STREAK, 0)

        val newStreak = if (lastDailyDate == null) {
            1
        } else {
            val format = java.text.SimpleDateFormat(
                "yyyy-MM-dd",
                java.util.Locale.US
            )

            val lastDate = try {
                format.parse(lastDailyDate)
            } catch (_: Exception) {
                null
            }

            val todayDate = try {
                format.parse(today)
            } catch (_: Exception) {
                null
            }

            if (lastDate != null && todayDate != null) {
                val diffDays =
                    (todayDate.time - lastDate.time) /
                    (24L * 60L * 60L * 1000L)

                if (diffDays == 1L) {
                    oldStreak + 1
                } else {
                    1
                }
            } else {
                1
            }
        }

        prefs.edit()
            .putInt(XP, newXp)
            .putInt(LEVEL, newLevel)
            .putInt(STREAK, newStreak)
            .putString("daily_last_date", today)
            .apply()

        syncCloud(context)

        return xp
    }

    fun saveDailyLearning(
        context: Context,
        decision: String,
        priority: String,
        result: String,
        awardedXp: Int,
        streak: Int
    ) {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val date = java.text.SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            java.util.Locale.US
        ).format(java.util.Date())

        val entry =
            "$date||$decision||$priority||$result||$awardedXp||$streak"

        val existing = prefs.getString("daily_learning_history", "") ?: ""

        val entries = existing
            .split("\n")
            .filter { it.isNotBlank() }
            .toMutableList()

        entries.add(0, entry)

        val limited = entries.take(30)

        prefs.edit()
            .putString(
                "daily_learning_history",
                limited.joinToString("\n")
            )
            .apply()
    }

    fun getDailyLearningHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        return (prefs.getString("daily_learning_history", "") ?: "")
            .split("\n")
            .filter { it.isNotBlank() }
    }

    fun addResultXp(context: Context, outcome: String): Int {
        val xp = when (outcome.uppercase()) {
            "WIN" -> 10
            "LOSS" -> 5
            "NEUTRAL" -> 5
            else -> 5
        }

        return addDecision(context, xp)
    }

    private fun syncCloud(context: Context) {
        com.gdmie.GDMIECloudProfile.syncProfile(context)
    }

    fun calculateLevel(xp: Int): Int =
        (xp / 100) + 1

    fun restoreFromCloud(
        context: Context,
        xp: Int,
        level: Int,
        decisions: Int,
        streak: Int
    ) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(XP, xp.coerceAtLeast(0))
            .putInt(LEVEL, level.coerceAtLeast(1))
            .putInt(DECISIONS, decisions.coerceAtLeast(0))
            .putInt(STREAK, streak.coerceAtLeast(0))
            .apply()
    }

    fun getXp(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(XP, 0)

    fun getLevel(context: Context): Int =
        calculateLevel(getXp(context))

    fun didLevelUp(oldXp: Int, newXp: Int): Boolean =
        calculateLevel(oldXp) < calculateLevel(newXp)


    fun getDecisions(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(DECISIONS, 0)

    fun getStreak(context: Context): Int =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(STREAK, 0)

    fun setStreak(context: Context, streak: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(STREAK, streak.coerceAtLeast(0))
            .apply()
        syncCloud(context)
    }

    fun xpIntoCurrentLevel(context: Context): Int =
        getXp(context) % 100

    fun xpToNextLevel(context: Context): Int =
        100 - xpIntoCurrentLevel(context)
}
