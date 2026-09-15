package com.waxd.pdfviewer

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Material You dynamic color intentionally not applied: the waxd palette
        // in styles.xml/Theme.kt is the fixed brand look, not system wallpaper colors.
    }
}
