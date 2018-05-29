package com.unual.anima

import android.os.Bundle
import android.util.Log
import com.unual.anima.base.BaseActivity
import com.unual.anima.data.*
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

/**
 * Created by Administrator on 2018/5/29.
 */

class AnimaActivity : BaseActivity() {
    var animaInfo: AnimaInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anima)
        val anima = intent.getSerializableExtra(Constant.KEY_INTENT) as Anima
        if (anima != null) {
            animaInfo = AnimaInfo(anima.name)
            Log.e("TAG", "${anima.name}:")
            Repository.instance.loadPage(anima.url, { htmlPage ->
                getAnimaVideos(htmlPage, { list ->
                    for (i in list) {
//                        Log.e("TAG", "    ${i.videoName} -> ${i.videoUrl}")
                    }
                })
            })
        }
    }

    // 更新动漫列表
    private fun getAnimaVideos(htmlPage: String, callback: (List<AnimaInfo.AnimaVideo>) -> Unit) {
        Observable.just(JXDocument.create(htmlPage))
                .subscribeOn(Schedulers.io())
                .map { jxDocument ->
                    var namePath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/em/text()"
                    var urlPath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val urlResult = jxDocument.sel(urlPath)
                    val result: ArrayList<AnimaInfo.AnimaVideo> = ArrayList()
                    for (i in 0 until nameResult.size) {
                        Log.e("TAG", "    ${nameResult[i]} -> ${nameResult[i].toString().contains(animaInfo?.name
                                ?: "")}")
                        var name = nameResult[i].toString().replace(animaInfo?.name ?: "", "")
                        var url = "${urlResult[i]}"
                        result.add(AnimaInfo.AnimaVideo(name, url))
                    }
                    result
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    // 动漫列表(一周列表) contain 动漫列表(一天列表)
                    callback.invoke(list)
                }
    }
}