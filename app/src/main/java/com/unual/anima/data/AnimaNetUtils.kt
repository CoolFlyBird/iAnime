package com.unual.anima.data

import android.util.Log
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

/**
 * Created by Administrator on 2018/5/29.
 */
class Repository {
    private var client: OkHttpClient

    init {
        var builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            val request = authorised.build()
            chain.proceed(request)
        }
        val loginInterceptor = HttpLoggingInterceptor()
        loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loginInterceptor)
        client = builder.build()
    }

    companion object {
        val instance by lazy { Repository() }
    }

    fun loadPage(url: String, callback: (String) -> Unit) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map { pageUrl ->
                    try {
                        var respone = client.newCall(Request.Builder().url(pageUrl).build()).execute()
                        if (respone.isSuccessful) {
                            val result = respone.body()?.string() ?: ""
                            callback.invoke(result)
                        } else {
                            callback.invoke("")
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "Exception ${e.message}")
                        callback.invoke("")
                    }
                }
                .subscribe()
    }
}