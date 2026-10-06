package com.gdmie

import android.app.Activity
import android.content.Context
import android.os.Bundle

open class GDMIEBaseActivity : Activity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.apply(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
