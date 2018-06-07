package com.unual.anima

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.LockClickListener
import com.shuyu.gsyvideoplayer.model.GSYVideoModel
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.unual.anima.data.Anima
import com.unual.anima.data.AnimaInfo
import com.unual.anima.data.Constant
import kotlinx.android.synthetic.main.activity_video_player.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/30.
 */
class VideoPlayerActivity : GSYBaseActivityDetail<ListGSYVideoPlayer>() {
    val flag = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        initVideo()
        var animaVideo = intent.getSerializableExtra(Constant.KEY_INTENT) as AnimaInfo.AnimaVideo
        val urls = ArrayList<GSYVideoModel>()
        urls.add(GSYVideoModel(animaVideo.videoUrl, animaVideo.videoName))
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
            time = intent.getStringExtra(Constant.KEY_INTENT_EXT).toLong()
        } catch (e: Exception) {
            Log.e("TAG", "Exception -> ${e.message}")
        }
        videoPlayer.gsyVideoManager.seekTo(time)

        loadCover(videoPlayer, animaVideo.videoUrl)
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
        Log.e("TAG", "current:" + videoPlayer.gsyVideoManager.currentPosition)
        val intent = Intent()
        intent.putExtra(Constant.KEY_INTENT, "${videoPlayer.gsyVideoManager.currentPosition}")
        setResult(Activity.RESULT_OK, intent)
    }
}