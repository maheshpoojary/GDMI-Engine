package com.gdmie

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = TextView(this)

        title.text = "GDMIE\nGeneral Decision & Mathematical Intelligence Engine"
        title.textSize = 24f
        title.setPadding(40, 80, 40, 40)

        setContentView(title)
    }
}
