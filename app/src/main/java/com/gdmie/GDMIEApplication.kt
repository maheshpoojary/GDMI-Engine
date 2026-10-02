package com.gdmie

import android.app.Application
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.gdmie.audio.GDMIEAudioManager

class GDMIEApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        GDMIEAudioManager.playContinuousTheme(this)

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrashLog(thread, throwable)
            } catch (_: Exception) {
                // Never allow the diagnostic logger to cause another crash.
            }

            previousHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun saveCrashLog(
        thread: Thread,
        throwable: Throwable
    ) {
        val time = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.US
        ).format(Date())

        val stack = StringWriter()
        throwable.printStackTrace(PrintWriter(stack))

        val report = buildString {
            appendLine("========== GDMIE CRASH ==========")
            appendLine("Time: $time")
            appendLine("Thread: ${thread.name}")
            appendLine("Exception: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message}")
            appendLine()
            appendLine("STACK TRACE:")
            appendLine(stack.toString())
            appendLine("=================================")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = contentResolver

            val values = ContentValues().apply {
                put(
                    MediaStore.Downloads.DISPLAY_NAME,
                    "GDMIE-crash-${System.currentTimeMillis()}.log"
                )
                put(
                    MediaStore.Downloads.MIME_TYPE,
                    "text/plain"
                )
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS
                )
            }

            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            ) ?: return

            resolver.openOutputStream(uri)?.use {
                it.write(report.toByteArray())
            }
        } else {
            val dir = getExternalFilesDir(null) ?: return
            File(
                dir,
                "GDMIE-crash-${System.currentTimeMillis()}.log"
            ).writeText(report)
        }
    }
}
