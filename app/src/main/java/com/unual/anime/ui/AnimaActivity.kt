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
import com.unual.anime.data.ApiService
import com.unual.anime.data.entity.Anima
import com.unual.anime.data.entity.AnimaInfo
import com.unual.anime.utils.Constants
import com.unual.anime.data.entity.TypeUrl
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.a_common_list.*
import java.util.*

/**
 * Created by Administrator on 2018/5/29.
 */

class AnimaActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var adapter: AnimeVideosAdapter
    lateinit var animaInfo: AnimaInfo
    lateinit var anima: Anima
    lateinit var playName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_common_list)
        anima = intent.getSerializableExtra(Constants.KEY_INTENT) as Anima
        title = anima.name
        animaInfo = AnimaInfo(anima)
        refresh.setOnRefreshListener(this)
        adapter = AnimeVideosAdapter(R.layout.item_video_list) { animaVideo ->
            if (animaVideo.checked) {
                openVideo(animaVideo)
            } else {
                getAnimeVideo(animaVideo.videoUrl) { typeUrl ->
                    animaVideo.line.addAll(typeUrl.line)
                    handleTypeUrl(typeUrl, animaVideo, -1)
                }
            }
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        onRefresh()
    }

    //打开视频
    private fun openVideo(animaVideo: AnimaInfo.AnimaVideo) {
        playName = animaVideo.videoName
        Log.e("TAG", "open in web${animaVideo.useWebPlayer} - ${animaVideo.videoUrl} is ${animaVideo.videoUrl.isEmpty()}")
        setValue(anima.name + Constants.LAST, "${Utils.format(Date(), "MM.dd")}·${animaVideo.videoName}")
        if (animaVideo.useWebPlayer && !animaVideo.videoUrl.isEmpty()) {
            var intent = Intent(this, WebPlayerActivity::class.java)
            intent.putExtra(Constants.KEY_INTENT, animaVideo)
            startActivity(intent)
        } else if (!animaVideo.videoUrl.isEmpty()) {
            var intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra(Constants.KEY_INTENT, animaVideo)
            intent.putExtra(Constants.KEY_INTENT_EXT, animaInfo)
            startActivity(intent)
        }
    }

    //刷新页面
    override fun onRefresh() {
        refresh.isRefreshing = true
        ApiService.instance.getPageService()
                .loadPage(animaInfo.anima.url)
                .map { htmlPage ->
                    getAnimePages(htmlPage) { list ->
                        refresh.isRefreshing = false
                        adapter.setNewData(list)
                        adapter.data
                        autoCheckVideoUrl(list)
                    }
                }.subscribe()
    }

    //自动检查 并解析 有无链接
    private fun autoCheckVideoUrl(list: List<AnimaInfo.AnimaVideo>) {
        Observable.fromIterable(list)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map { animaVideo ->
                    getAnimeVideo(animaVideo.videoUrl) { typeUrl ->
                        animaVideo.line.addAll(typeUrl.line)
                        handleTypeUrl(typeUrl, animaVideo, list.indexOf(animaVideo))
                    }
                }.subscribe()
    }

    // 获取动漫全集列表
    private fun getAnimePages(htmlPage: String, callback: (List<AnimaInfo.AnimaVideo>) -> Unit) {
        Observable.just(htmlPage)
                .subscribeOn(Schedulers.io())
                .map { htmlPage ->
                    parsePage2Anime(htmlPage)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    // 动漫全集列表
                    callback.invoke(list)
                }
    }

    // 获取播放链接（mp4 或者 另一个链接）
    private fun getAnimeVideo(pageUrl: String, callback: (TypeUrl) -> Unit) {
        ApiService.instance.getPageService()
                .loadPage(pageUrl)
                .map { htmlPage ->
                    parsePage2Url(htmlPage)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { typeUrl ->
                    callback.invoke(typeUrl)
                }
    }

    // 解析动漫全集
    private fun parsePage2Anime(htmlPage: String): ArrayList<AnimaInfo.AnimaVideo> {
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
            url = sourceUrl
            type = -1
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
            1, 3 -> {
            }
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
}