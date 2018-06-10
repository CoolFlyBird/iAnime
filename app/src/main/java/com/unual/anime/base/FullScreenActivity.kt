package com.unual.anime.base

import android.view.MotionEvent
import android.os.Bundle
import android.view.View


/**
 * Created by unual on 2018/5/30.
 */
open class FullScreenActivity : BaseActivity() {
    val flag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (window.decorView.systemUiVisibility != flag) {
            fullScreen()
            return true
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && (window.decorView.systemUiVisibility != flag)) {
            fullScreen()
        }
    }

    private fun fullScreen() {
        //1显示导航栏
        //5894全屏
        window.decorView.systemUiVisibility = flag
    }

}