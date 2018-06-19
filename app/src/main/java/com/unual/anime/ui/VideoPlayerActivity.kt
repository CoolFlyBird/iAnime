package com.unual.anime.ui

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.GSYVideoModel
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.unual.anime.R
import com.unual.anime.data.AnimaInfo
import com.unual.anime.data.Constants
import kotlinx.android.synthetic.main.activity_video_player.*
import pub.devrel.easypermissions.EasyPermissions
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/30.
 */
class VideoPlayerActivity : GSYBaseActivityDetail<ListGSYVideoPlayer>() {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val spf by lazy { getSharedPreferences(Constants.KEY_SPF, Context.MODE_PRIVATE) }
    private var animaVideo: AnimaInfo.AnimaVideo? = null
    private var animaInfo: AnimaInfo? = null

    val flag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    fun setValue(key: String, value: String) {
        if (!EasyPermissions.hasPermissions(this, *parm)) return
        spf.edit().putString(key, value)?.commit()
    }

    fun getValue(key: String): String {
        if (!EasyPermissions.hasPermissions(this, *parm)) return ""
        return spf.getString(key, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        initVideo()
        animaVideo = intent.getSerializableExtra(Constants.KEY_INTENT) as AnimaInfo.AnimaVideo
        animaInfo = intent.getSerializableExtra(Constants.KEY_INTENT_EXT) as AnimaInfo
        val urls = ArrayList<GSYVideoModel>()
        urls.add(GSYVideoModel(animaVideo?.videoUrl, animaVideo?.videoName))
        videoPlayer.setUp(urls, true, 0)
        resolveNormalVideoUI()

        videoPlayer.setIsTouchWiget(true)
        //关闭自动旋转
        videoPlayer.isRotateViewAuto = false
        videoPlayer.isLockLand = false
        videoPlayer.isShowFullAnimation = false
        videoPlayer.isNeedLockFull = true
        videoPlayer.setVideoAllCallBack(this)
        videoPlayer.setLockClickListener { _, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils.isEnable = !lock
            }
        }
        var time = 0L
        try {
            Log.e("TAG", "${animaInfo?.anima?.name + "_" + animaVideo?.videoName}")
            time = getValue(animaInfo?.anima?.name + "_" + animaVideo?.videoName).toLong()
        } catch (e: Exception) {
            Log.e("TAG", "Exception -> $time")
        }
        videoPlayer.seekOnStart = time

        loadCover(videoPlayer, animaVideo?.videoUrl ?: "")
//        next.setOnClickListener(View.OnClickListener { (detailPlayer.getCurrentPlayer() as ListGSYVideoPlayer).playNext() })
    }

    private fun resolveNormalVideoUI() {
        //增加title
        videoPlayer.titleTextView.visibility = View.VISIBLE
        videoPlayer.backButton.visibility = View.VISIBLE
    }

    private fun getCurPlay(): GSYVideoPlayer {
        return if (videoPlayer.fullWindowPlayer != null) {
            videoPlayer.fullWindowPlayer
        } else videoPlayer
    }


    private fun loadCover(videoPlayer: StandardGSYVideoPlayer, url: String) {
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(applicationContext)
                .setDefaultRequestOptions(
                        RequestOptions()
                                .frame(3000000)
                                .centerCrop())
                .load(url)
                .into(imageView)
        videoPlayer.thumbImageView = imageView
    }

    override fun clickForFullScreen() {
    }

    override fun getDetailOrientationRotateAuto(): Boolean {
        return true
    }

    override fun getGSYVideoPlayer(): ListGSYVideoPlayer {
        return videoPlayer
    }

    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder? {
        return null
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
        window.decorView.systemUiVisibility = flag
    }

    override fun onDestroy() {
        super.onDestroy()
        setValue(animaInfo?.anima?.name + "_" + animaVideo?.videoName, videoPlayer.gsyVideoManager.currentPosition.toString())
    }

}