package com.gdmie.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this).apply {
            text = "GDMIE\n\nGeneral Decision & Mathematical Intelligence Engine"
            textSize = 24f
            setPadding(32, 64, 32, 32)
        }

        setContentView(title)
    }
}
