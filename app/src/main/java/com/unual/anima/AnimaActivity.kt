package com.unual.anima

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        title = anima.name
        animaInfo = AnimaInfo(anima)
        refresh.setOnRefreshListener(this)
        adapter = AnimaVideosAdapter(R.layout.item_anima_list, { animaVideo ->
            getAnimaVideo(animaVideo.videoUrl, { url ->
                Log.e("TAG", "page ->${animaVideo.videoUrl}")
                var intent = Intent(this, WebPlayerActivity::class.java)
                if (!url.isEmpty()) {
                    animaVideo.videoUrl = url
                    Log.e("TAG", "load ->${animaVideo.videoUrl}")

                    if (url.endsWith(".mp4")) {
                        intent = Intent(this, VideoPlayerActivity::class.java)
                    }
                    intent.putExtra(Constant.KEY_INTENT, animaVideo)
                    startActivity(intent)
                }
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
                        var url = ""

                        var sourceUrl = ""
                        Regex("""var sourceUrl =.+;""").findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
                            sourceUrl = t.replace("var sourceUrl = ", "").replace(";", "").replace("\"", "").trim()
                        }

                        val line: ArrayList<String> = ArrayList()
                        Regex("""var line = \[.+];""").findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
                            var result = t.replace("var line = ", "").replace(";", "").replace("\"", "").replace("[", "").replace("]", "").trim()
                            var array = result.split(",")
                            line += array
                        }

                        var lib: ArrayList<ArrayList<String>> = ArrayList()
                        Regex("""var sourceLib =[\s\S]*?\n\]""").findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
                            var result = t.replace("var sourceLib =", "")
                            val comment = """//[\s\S]*?\n"""//去掉以"//"开头的注释
                            Regex(comment).findAll(t).toList().flatMap(MatchResult::groupValues).forEach { a ->
                                result = result.replace(a, "")
                            }
                            result = result.trim()
                            val entity = Gson().fromJson<List<List<String>>>(result, object : TypeToken<List<List<String>>>() {}.type)
                            for (group in entity) {
                                var g: ArrayList<String> = ArrayList()
                                lib.add(g)
                                if (group != null && !group.isEmpty()) {
                                    for (i in group) {
                                        g.add(i)
                                    }
                                } else {
                                    g.add("")
                                }
                            }
                        }

                        if (sourceUrl.endsWith(".mp4")) {
                            url = sourceUrl
                        } else {
                            var sourceUrlHost = sourceUrl.split("/")[2]
                            var index = 99
                            for (i in 0 until lib.size) {
                                for (j in 0 until lib[i].size) {
                                    if (sourceUrlHost == lib[i][j]) {
                                        index = i
                                        break
                                    }
                                }
                            }
                            if (index != 99) {
                                url = line[index] + sourceUrl
                            } else {
                                url = sourceUrl
                            }
                        }
                        url.trim()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { url ->
                        // 动漫全集列表
                        callback.invoke(url)
                    }
        })
    }
}