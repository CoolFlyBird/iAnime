package com.unual.anima

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.unual.anima.base.FullScreenActivity
import com.unual.anima.data.AnimaInfo
import com.unual.anima.data.Constant
import com.unual.anima.widget.VideoEnabledWebChromeClient
import kotlinx.android.synthetic.main.activity_example.*

/**
 * Created by unual on 2018/5/29.
 */
class WebPlayerActivity : FullScreenActivity() {
    private var webChromeClient: VideoEnabledWebChromeClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        val loadingView = layoutInflater.inflate(R.layout.view_loading_video, null) // Your own view, read class comments
        webChromeClient = object : VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            override fun onProgressChanged(view: WebView, progress: Int) {
                // Your code...
            }
        }
        webChromeClient!!.setOnToggledFullscreen { fullscreen ->
            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            if (fullscreen) {
                val attrs = window.attributes
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                window.attributes = attrs
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
                }
            } else {
                val attrs = window.attributes
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                window.attributes = attrs
                if (android.os.Build.VERSION.SDK_INT >= 14) {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        }
        webView.webChromeClient = webChromeClient
        // Call private class InsideWebViewClient
        webView.webViewClient = InsideWebViewClient()
        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        val animaVideo = intent.getSerializableExtra(Constant.KEY_INTENT) as AnimaInfo.AnimaVideo
        var index = animaVideo.checkType
        switchButton.setOnClickListener {
            index++
            if (index < -1) index = 0
            if (index > animaVideo.line.size) index = -1
            if (index < animaVideo.line.size) {
                if (index == -1) {
                    switchButton.text = "原线路"
                    Log.e("TAG", "load url - >${animaVideo.videoUrl}")
                    webView.loadUrl(animaVideo.videoUrl)
                } else {
                    switchButton.text = "线路${(index + 1)}"
                    Log.e("TAG", "load url - >${animaVideo.line[index] + animaVideo.videoUrl}")
                    webView.loadUrl(animaVideo.line[index] + animaVideo.videoUrl)
                }
            }
        }
    }

    private inner class InsideWebViewClient : WebViewClient() {
        override// Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }
    }

//    override fun onBackPressed() {
//        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
//        if (!webChromeClient!!.onBackPressed()) {
//            if (webView!!.canGoBack()) {
//                webView!!.goBack()
//            } else {
//                // Standard back button implementation (for example this could close the app)
//                super.onBackPressed()
//            }
//        }
//    }

}