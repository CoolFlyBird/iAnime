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
        get_week_anima()
    }

    // 追更动漫
    fun get_week_anima() {
        var week = ArrayList<WeekDayClass>()
        week.add(WeekDayClass.Mon)
        week.add(WeekDayClass.Tues)
        week.add(WeekDayClass.Wed)
        week.add(WeekDayClass.Thur)
        week.add(WeekDayClass.Fri)
        week.add(WeekDayClass.Sat)
        week.add(WeekDayClass.Sun)
        load_page("http://www.dilidili.wang/", { page ->
            get_oneday(page, week)
        })
    }

    // 每一天更新动漫列表
    fun get_oneday(page: String, week: List<WeekDayClass>) {
        var jxDocument = JXDocument.create(page)
        Observable.fromIterable(week)
                .subscribeOn(Schedulers.io())
                .map { day ->
                    var namePath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/figure/figcaption/p[1]/text()"
                    var urlPath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val urlResult = jxDocument.sel(urlPath)
                    var result = ArrayList<List<Any>>()
                    result.add(nameResult)
                    result.add(urlResult)
                    result
                }
                .map { results ->
                    for (i in 0..results.size) {
                        var name = results[0].toString()
                        var url = "http://www.dilidili.wang&{${results[0]}}"
                        get_one_anima(name, url)
                    }
                }
                .subscribe()

    }


    // 更新动漫，具体每一集
    fun get_one_anima(name: String, url: String) {
        load_page(url, { page ->
            Log.e("TAG", "${Thread.currentThread().name} ->${page}")
        })
    }

    // 更新动漫，具体地址
    fun get_anima_video(name: String, url: String) {

    }

    fun load_page(url: String, callback: (String) -> Unit) {
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
