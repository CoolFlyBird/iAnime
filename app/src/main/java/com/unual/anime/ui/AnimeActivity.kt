package com.unual.anime.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unual.anime.R
import com.unual.anime.adapter.AnimeVideosAdapter
import com.unual.anime.base.BaseActivity
import com.unual.anime.base.Utils
import com.unual.anime.data.*
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_anime.*
import java.util.*

/**
 * Created by Administrator on 2018/5/29.
 */

class AnimeActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var adapter: AnimeVideosAdapter
    lateinit var animaInfo: AnimaInfo
    lateinit var anima: Anima
    lateinit var playName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        anima = intent.getSerializableExtra(`Constant.kt`.KEY_INTENT) as Anima
        title = anima.name
        animaInfo = AnimaInfo(anima)
        refresh.setOnRefreshListener(this)
        adapter = AnimeVideosAdapter(R.layout.item_video_list, { animaVideo ->
            if (animaVideo.checked) {
                openVideo(animaVideo)
            } else {
                getAnimaVideo(animaVideo.videoUrl, { typeUrl ->
                    animaVideo.line.addAll(typeUrl.line)
                    handleTypeUrl(typeUrl, animaVideo, -1)
                })
            }
        })
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        onRefresh()
    }

    //打开视频
    private fun openVideo(animaVideo: AnimaInfo.AnimaVideo) {
        playName = animaVideo.videoName
        Log.e("TAG", "open in web${animaVideo.useWebPlayer} - ${animaVideo.videoUrl} is {$animaVideo.videoUrl.isEmpty()}")
        setValue(anima.name + `Constant.kt`.LAST, "${Utils.format(Date(), "MM.dd")}·${animaVideo.videoName}")
        if (animaVideo.useWebPlayer && !animaVideo.videoUrl.isEmpty()) {
            var intent = Intent(this, WebPlayerActivity::class.java)
            intent.putExtra(`Constant.kt`.KEY_INTENT, animaVideo)
            startActivity(intent)
        } else if (!animaVideo.videoUrl.isEmpty()) {
            var intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(`Constant.kt`.KEY_INTENT, animaVideo)
            intent.putExtra(`Constant.kt`.KEY_INTENT_EXT, animaInfo)
            startActivity(intent)
        }
    }

    //刷新页面
    override fun onRefresh() {
        refresh.isRefreshing = true
        Repository.instance.loadPage(animaInfo.anima.url, { htmlPage ->
            getAnimaPages(htmlPage, { list ->
                refresh.isRefreshing = false
                adapter.setNewData(list)
                adapter.data
                autoCheckVideoUrl(list)
            })
        })
    }

    //自动检查 并解析 有无链接
    private fun autoCheckVideoUrl(list: List<AnimaInfo.AnimaVideo>) {
        Observable.fromIterable(list)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map { animaVideo ->
                    getAnimaVideo(animaVideo.videoUrl, { typeUrl ->
                        animaVideo.line.addAll(typeUrl.line)
                        handleTypeUrl(typeUrl, animaVideo, list.indexOf(animaVideo))
                    })
                }.subscribe()
    }

    // 获取动漫全集列表
    private fun getAnimaPages(htmlPage: String, callback: (List<AnimaInfo.AnimaVideo>) -> Unit) {
        Observable.just(htmlPage)
                .subscribeOn(Schedulers.io())
                .map { htmlPage ->
                    parsePage2Animas(htmlPage)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    // 动漫全集列表
                    callback.invoke(list)
                }
    }

    // 获取播放链接（mp4 或者 另一个链接）
    private fun getAnimaVideo(pageUrl: String, callback: (TypeUrl) -> Unit) {
        Repository.instance.loadPage(pageUrl, { htmlPage ->
            Observable.just(htmlPage)
                    .subscribeOn(Schedulers.io())
                    .map { htmlPage ->
                        parsePage2Url(htmlPage)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { typeUrl ->
                        callback.invoke(typeUrl)
                    }
        })
    }

    // 解析动漫全集
    private fun parsePage2Animas(htmlPage: String): ArrayList<AnimaInfo.AnimaVideo> {
        val jxDocument = JXDocument.create(htmlPage)
        val namePath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/em/text()"
        val urlPath = "//div[@class=\"swiper-slide\"]/ul[@class=\"clear\"]/li/a/@href"
        var nameResult = jxDocument.sel(namePath)
        var urlResult = jxDocument.sel(urlPath)
        val result: ArrayList<AnimaInfo.AnimaVideo> = ArrayList()
        for (i in 0 until nameResult.size) {
            var name = nameResult[i].toString()
            for (ii in nameResult[i].toString().split(" ")) {
                if (animaInfo.anima.name.contains(ii)) {
                    name = name.replace(ii, "")
                }
            }
            name = name.trim()
            var url = urlResult[i].toString()
            Log.e("TAG", "$name -> $url")
            result.add(AnimaInfo.AnimaVideo(name, url))
        }
        Log.e("TAG", "result.size -> ${result.size}")
        if (result.size == 0) {
            val namePath = "//h3[@class=\"box area\"]/li/a/div/p/text()"
            val urlPath = "//h3[@class=\"box area\"]/li/a/@href"
            nameResult = jxDocument.sel(namePath)
            urlResult = jxDocument.sel(urlPath)
            for (i in 0 until urlResult.size) {
                var name = nameResult[2 * i].toString()
                for (ii in nameResult[2 * i].toString().split(" ")) {
                    if (animaInfo.anima.name.contains(ii)) {
                        name = name.replace(ii, "")
                    }
                }
                name = nameResult[2 * i + 1].toString().trim() + " " + name.trim()
                var url = urlResult[i].toString()
                Log.e("TAG", "$name -> $url")
                result.add(AnimaInfo.AnimaVideo(name, url))
            }
        }
        Log.e("TAG", "result.sizea -> ${result.size}")
        return result
    }

    //解析播放页面 -> 播放链接（mp4 或者 另一个链接）
    private fun parsePage2Url(htmlPage: String): TypeUrl {
        var url = ""
        var sourceUrl = ""
        Regex("""var sourceUrl =.+;""").findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
            sourceUrl = t.replace("var sourceUrl = ", "").replace(";", "").replace("\"", "").trim()
        }

        val line: ArrayList<String> = ArrayList()
        Regex("""var line = \[.+];""").findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
            //            Log.e("TAG", "line->$t")
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
        var type = -1
        if (sourceUrl.endsWith(".mp4")) {
            url = sourceUrl
            type = 0
        } else if (!sourceUrl.isEmpty()) {
            var array = sourceUrl.split("/")
            if (array.size >= 3) {
                var sourceUrlHost = array[2]
                var index = 99
                for (i in 0 until lib.size) {
                    for (j in 0 until lib[i].size) {
                        if (sourceUrlHost == lib[i][j]) {
                            index = i
                            type = i
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
        }
        url = url.trim()
        return TypeUrl(type, url, line)
    }

    //

    /**
     * 处理返回的链接
     * @param typeUrl 类型链接：类型0-> mp4,其他类型需要去对应页面解析到mp4
     * @param animaVideo 对应的对象：改UI用
     * @param index 位置：-1为点击位置，跳去视频播放页面。其他位置刷新对应UI
     */
    private fun handleTypeUrl(typeUrl: TypeUrl, animaVideo: AnimaInfo.AnimaVideo, index: Int) {
        if (typeUrl.url.isEmpty()) {
            animaVideo.videoUrl = ""
            animaVideo.checked = true
            animaVideo.checkType = typeUrl.type
        }
        when (typeUrl.type) {
            -1 -> {
                animaVideo.useWebPlayer = true
//                animaVideo.videoUrl = "http://jx.yylep.com/?url=${typeUrl.url}"
                animaVideo.videoUrl = typeUrl.url
                animaVideo.checked = true
                Log.e("TAG", "${animaVideo.videoUrl}")
                if (index == -1) openVideo(animaVideo)
                else adapter.notifyItemChanged(index)
            }
            0 -> {
                Log.e("TAG", "type:0 -> $index-${typeUrl.url}")
                animaVideo.videoUrl = typeUrl.url
                animaVideo.checked = true
                if (index == -1) openVideo(animaVideo)
                else adapter.notifyItemChanged(index)
            }
            1, 3 -> getUrlFromType1(typeUrl.url, { result ->
                Log.e("TAG", "type:1,3 -> $index-${typeUrl.url} $result")
                if (!result.isEmpty()) {
                    animaVideo.videoUrl = result
                    animaVideo.checked = true
                }
                if (index == -1) openVideo(animaVideo)
                else adapter.notifyItemChanged(index)
            })
            else -> {
                Log.e("TAG", "type:${typeUrl.type} -> $index-${typeUrl.url}")
                if (!typeUrl.url.isEmpty()) {
                    animaVideo.videoUrl = typeUrl.url
                    animaVideo.checked = true
                    animaVideo.useWebPlayer = true
                }
                if (index == -1) openVideo(animaVideo)
            }
        }
    }

    /*从播放另一个链接 获取视频 mp4*/
    private fun getUrlFromType1(pageUrl: String, callback: (String) -> Unit) = Repository.instance.loadPage(pageUrl, { htmlPage ->
        Observable.just(htmlPage)
                .subscribeOn(Schedulers.io())
                .map { htmlPage ->
                    var url = ""
                    val regexUrl = """var url=.+?;"""
                    Regex(regexUrl).findAll(htmlPage).toList().flatMap(MatchResult::groupValues).forEach { t ->
                        var result = t.replace("var url=", "").replace(";", "").replace("'", "")
                        url = result
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