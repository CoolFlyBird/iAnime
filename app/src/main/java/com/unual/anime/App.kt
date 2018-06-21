package com.unual.anime

import android.app.Application

/**
 * Created by unual on 2018/6/21.
 */
class App : Application() {
    companion object {
        lateinit var context: App
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}