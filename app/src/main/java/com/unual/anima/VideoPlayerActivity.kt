package com.unual.anima

import android.os.Bundle
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
import com.unual.anima.data.AnimaInfo
import com.unual.anima.data.Constant
import kotlinx.android.synthetic.main.activity_video_player.*
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/30.
 */
class VideoPlayerActivity : GSYBaseActivityDetail<ListGSYVideoPlayer>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        initVideo()
        var animaVideo = intent.getSerializableExtra(Constant.KEY_INTENT) as AnimaInfo.AnimaVideo
        val urls = ArrayList<GSYVideoModel>()
        urls.add(GSYVideoModel(animaVideo.videoUrl, animaVideo.videoName))
        videoPlayer.setUp(urls, true, 0)
        loadCover(videoPlayer, animaVideo.videoUrl)

        resolveNormalVideoUI()

        videoPlayer.setIsTouchWiget(true)
        //关闭自动旋转
        videoPlayer.isRotateViewAuto = false
        videoPlayer.isLockLand = false
        videoPlayer.isShowFullAnimation = false
        videoPlayer.isNeedLockFull = true
        videoPlayer.setVideoAllCallBack(this)
        videoPlayer.setLockClickListener(LockClickListener { view, lock ->
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils.isEnable = !lock
            }
        })
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
}