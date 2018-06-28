package com.unual.anime.ui

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.JsResult
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.unual.anime.R
import com.unual.anime.base.FullScreenActivity
import com.unual.anime.data.entity.AnimaInfo
import com.unual.anime.data.entity.UserAgent
import com.unual.anime.utils.Constants
import com.unual.anime.widget.VideoEnabledWebChromeClient
import kotlinx.android.synthetic.main.activity_example.*


/**
 * Created by unual on 2018/5/29.
 */
class WebPlayerActivity : FullScreenActivity() {
    private var webChromeClient: VideoEnabledWebChromeClient? = null
    private lateinit var animaVideo: AnimaInfo.AnimaVideo
    private var agentIndex = 0
    private var playerIndex = 0
    private var url: String = ""
//    private var webChromeClient: WebChromeClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        val loadingView = layoutInflater.inflate(R.layout.view_loading_video, null)
        webChromeClient = object : VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (UserAgent.values()[agentIndex].needRemove()) {
                    webView?.loadUrl("javascript:function removeAd(){var jsDivTagName = document.getElementsByTagName(\"img\");for (var m = 0; m < jsDivTagName.length; m++) {jsDivTagName[m].style.display=\"none\";}}removeAd();")
                }
                super.onProgressChanged(view, newProgress)
            }
        }
        webChromeClient!!.setOnToggledFullscreen { fullscreen ->
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
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setAppCacheEnabled(true)
        webView.webChromeClient = webChromeClient
        webView.webViewClient = InsideWebViewClient()
        animaVideo = intent.getSerializableExtra(Constants.KEY_INTENT) as AnimaInfo.AnimaVideo
        playerIndex = animaVideo.checkType
        url = animaVideo.videoUrl
        switchLine.setOnClickListener {
            playerIndex++
            if (playerIndex < -1) playerIndex = 0
            if (playerIndex > animaVideo.line.size) playerIndex = -1
            setUrl(playerIndex)
            loadUrl(agentIndex)
        }
        agentIndex = 0
        switchAgent.setOnClickListener {
            agentIndex++
            if (agentIndex >= UserAgent.values().size) {
                agentIndex = 0
            }
            setAgent(agentIndex)
            loadUrl(agentIndex)
        }
        setUrl(playerIndex)
        setAgent(agentIndex)
        loadUrl(agentIndex)
    }

    fun setUrl(index: Int) {
        if (index < animaVideo.line.size) {
            if (index == -1) {
                switchLine.text = "原线路"
                url = animaVideo.videoUrl
            } else {
                switchLine.text = "线路${(index + 1)}"
                url = animaVideo.line[index] + animaVideo.videoUrl
            }
        }
    }

    fun setAgent(index: Int) {
        var agent = UserAgent.values()[index]
        switchAgent.text = agent.key()
        webView.settings.userAgentString = agent.value()
    }

    fun loadUrl(index: Int) {
        var agent = UserAgent.values()[index]
        switchAgent.text = agent.key()
        webView.settings.userAgentString = agent.value()
        Log.e("TAG", "${agent.key()} - $url")
        webView.loadUrl(url)
    }

    private inner class InsideWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }
    }

}