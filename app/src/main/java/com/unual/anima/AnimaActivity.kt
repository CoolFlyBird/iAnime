package com.unual.anima

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Window
import com.unual.anima.adapter.AnimaVideosAdapter
import com.unual.anima.base.BaseActivity
import com.unual.anima.data.*
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_anima.*
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/29.
 */

class AnimaActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var adapter: AnimaVideosAdapter
    var animaInfo: AnimaInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anima)
        val anima = intent.getSerializableExtra(Constant.KEY_INTENT) as Anima
        if (anima != null) {
            animaInfo = AnimaInfo(anima)
        }
        refresh.setOnRefreshListener(this)
        adapter = AnimaVideosAdapter(com.unual.anima.R.layout.item_anima_list, { animaVideo ->
            getAnimaVideo(animaVideo.videoUrl, { url ->
                Log.e("TAG", "page ->${animaVideo.videoUrl}")
                var intent = Intent(this, WebPlayerActivity::class.java)
                if (!url.isEmpty()) {
                    animaVideo.videoUrl = url
                }

                if (url.endsWith(".mp4")) {
                    intent = Intent(this, VideoPlayerActivity::class.java)
                }
                Log.e("TAG", "load ->${animaVideo.videoUrl}")
//                var intent = Intent(this, WebPlayerActivity::class.java)
                intent!!.putExtra(Constant.KEY_INTENT, animaVideo)
                startActivity(intent)
            })

        })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter;
        onRefresh()
    }

    override fun onRefresh() {
        refresh.isRefreshing = true
        Repository.instance.loadPage(animaInfo?.anima?.url ?: "", { htmlPage ->
            getAnimaPages(htmlPage, { list ->
                refresh.isRefreshing = false
                adapter.setNewData(list)
            })
        })
    }

    // 更新动漫列表
    private fun getAnimaPages(htmlPage: String, callback: (List<AnimaInfo.AnimaVideo>) -> Unit) {
        Observable.just(JXDocument.create(htmlPage))
                .subscribeOn(Schedulers.io())
                .map { jxDocument ->
                    var namePath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/em/text()"
                    var urlPath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val urlResult = jxDocument.sel(urlPath)
                    val result: ArrayList<AnimaInfo.AnimaVideo> = ArrayList()
                    for (i in 0 until nameResult.size) {
                        var name = nameResult[i].toString().replace(animaInfo?.anima?.name
                                ?: "", "")
                        var url = "${urlResult[i]}"
                        result.add(AnimaInfo.AnimaVideo(name, url))
                    }
                    result
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    // 动漫全集列表
                    callback.invoke(list)
                }
    }

    private fun getAnimaVideo(pageUrl: String, callback: (String) -> Unit) {
        Repository.instance.loadPage(pageUrl, { htmlPage ->
            Observable.just(htmlPage)
                    .subscribeOn(Schedulers.io())
                    .map { htmlPage ->
                        val numPattern = "var sourceUrl =.+;".toRegex()
                        var url = ""
                        for (matchResult in numPattern.findAll(htmlPage)) {
                            url = matchResult.value
                            println()
                        }
                        var lines = arrayOf("http://jx.yylep.com/?url=", "http://api.wlzhan.com/sudu/?url=", "https://api.flvsp.com/?url=", " ", "http://wanhao.maoyun.cloud/1/mdparse/index.php?id=")
//                        [ ],//line1
//                        ["www.bilibili.com","bangumi.bilibili.com"],//line2
//                        ["my.tv.sohu.com","tv.sohu.com","www.mgtv.com"],//line3
//                        ["www.ikanfan.cn", "xin.tcpspc.com","110.80.136.9:2100", "acvideo.fun:2100","moe.kirikiri.tv","www.yylep.com","moe.kirikiri.cc","107.jp255.com","tbm.alicdn.com","jx.itaoju.top"],//line4
//                        ["v.pptv.com"],//line5
                        url = url.replace("var sourceUrl = \"", "").replace("\";", "")
                        if (!url.endsWith(".mp4")) {
                            url = lines[0].toString() + url
                        }
                        url
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { url ->
                        // 动漫全集列表
                        callback.invoke(url)
                    }
        })
    }
}