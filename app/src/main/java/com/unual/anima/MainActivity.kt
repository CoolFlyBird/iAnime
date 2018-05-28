package com.unual.anima

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private var client: OkHttpClient

    init {
        var builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            val request = authorised.build()
            chain.proceed(request)
        }
        client = builder.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getWeekAnima()
    }

    // 追更动漫
    fun getWeekAnima() {
        var week = ArrayList<WeekDayClass>()
        week.add(WeekDayClass.Mon)
        week.add(WeekDayClass.Tues)
        week.add(WeekDayClass.Wed)
        week.add(WeekDayClass.Thur)
        week.add(WeekDayClass.Fri)
        week.add(WeekDayClass.Sat)
        week.add(WeekDayClass.Sun)
        loadPage("http://www.dilidili.wang/", { page ->
            getOneDay(page, week)
        })
    }

    // 更新动漫列表
    fun getOneDay(page: String, week: List<WeekDayClass>) {
        var jxDocument = JXDocument.create(page)
        Observable.fromIterable(week)
                .subscribeOn(Schedulers.io())
                .map { day ->
                    var namePath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/figure/figcaption/p[1]/text()"
                    var urlPath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val urlResult = jxDocument.sel(urlPath)
                    var result: ArrayList<KeyValue> = ArrayList()
                    for (i in 0 until nameResult.size) {
                        var name = nameResult[i].toString()
                        var url = "http://www.dilidili.wang${urlResult[i]}"
                        var keyValue = KeyValue(name, url)
//                        Log.e("TAG", "${day.key()}->$name -> $url")
                        result.add(keyValue)
                    }
                    result
                }
                .collect(
                        // 动漫列表(一周列表) -> 动漫列表(一天列表)
                        { ArrayList<ArrayList<KeyValue>>() },
                        { list, item ->
                            list.add(item)
                        }
                )
                .map { list ->
                    for (animaDay in list) {//动漫列表(一天列表) in 动漫列表(一周列表)
                        for (anima in animaDay) {//
                            var name = anima.key//名字
                            var url = anima.value//链接
                            Log.e("TAG", "$name -> $url")
                        }
                        Log.e("TAG", "------")
                    }
                }
                .subscribe()
    }

    //处理动画
    private fun handlerAnimas(animas: ArrayList<ArrayList<KeyValue>>) {
    }

    // 具体动漫的每一集
    fun getOneAnima(name: String, url: String) {
        loadPage(url, { page ->
            Log.e("TAG", "${Thread.currentThread().name} ->${page}")
        })
    }

    // 更新动漫，具体地址
    fun getAnimaVideo(name: String, url: String) {

    }

    fun loadPage(url: String, callback: (String) -> Unit) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map { pageUrl ->
                    var respone = client.newCall(Request.Builder().url(pageUrl).build()).execute()
                    if (respone.isSuccessful) {
                        callback.invoke(respone.body().string())
                    } else {
                        callback.invoke("")
                    }
                }
                .subscribe()
    }

}
