package com.gdmie.audio
import com.gdmie.R

import android.content.Context
import android.media.MediaPlayer

object GDMIEAudioManager {

    private var musicPlayer: MediaPlayer? = null
    private var currentResId: Int = 0

    private var musicEnabled = true
    private var sfxEnabled = true

    private const val MUSIC_VOLUME = 0.55f
    private const val SFX_VOLUME = 0.75f

    fun playMusic(
        context: Context,
        resId: Int,
        loop: Boolean = true
    ) {
        refreshPreferences(context)
        if (!musicEnabled) return

        if (currentResId == resId &&
            musicPlayer?.isPlaying == true
        ) {
            return
        }

        stopMusic()

        musicPlayer = MediaPlayer.create(
            context.applicationContext,
            resId
        )?.apply {
            isLooping = loop
            setVolume(MUSIC_VOLUME, MUSIC_VOLUME)
            start()
        }

        currentResId = resId
    }

    fun playContinuousTheme(context: Context) {
        refreshPreferences(context)
        if (!musicEnabled) return

        if (currentResId == R.raw.gdmie_background_theme &&
            musicPlayer?.isPlaying == true
        ) {
            return
        }

        stopMusic()

        musicPlayer = MediaPlayer.create(
            context.applicationContext,
            R.raw.gdmie_background_theme
        )?.apply {
            isLooping = true
            setVolume(0.02f, 0.02f)
            start()
        }

        currentResId = R.raw.gdmie_background_theme
    }

    fun stopMusic() {
        musicPlayer?.let {
            try {
                if (it.isPlaying) it.stop()
            } catch (_: Exception) {
            }
            it.release()
        }

        musicPlayer = null
        currentResId = 0
    }

    fun pauseMusic() {
        try {
            if (musicPlayer?.isPlaying == true) {
                musicPlayer?.pause()
            }
        } catch (_: Exception) {
        }
    }

    fun resumeMusic() {
        if (!musicEnabled) return

        try {
            musicPlayer?.start()
        } catch (_: Exception) {
        }
    }

    fun playSfx(
        context: Context,
        resId: Int
    ) {
        refreshPreferences(context)
        if (!sfxEnabled) return

        MediaPlayer.create(
            context.applicationContext,
            resId
        )?.apply {
            setVolume(SFX_VOLUME, SFX_VOLUME)

            setOnCompletionListener { player ->
                player.release()
            }

            start()
        }
    }

    fun playUiClick(context: Context) {
        playSfx(context, R.raw.gdmie_ui_click)
    }

    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled

        if (!enabled) {
            stopMusic()
        }
    }

    fun setSfxEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun refreshPreferences(context: Context) {
        val prefs =
            context.getSharedPreferences("GDMIE_AUDIO", Context.MODE_PRIVATE)
        musicEnabled = prefs.getBoolean("music_enabled", true)
        sfxEnabled = prefs.getBoolean("sfx_enabled", true)
    }

    fun isMusicEnabled(): Boolean = musicEnabled

    fun isSfxEnabled(): Boolean = sfxEnabled

    fun release() {
        stopMusic()
    }
}
