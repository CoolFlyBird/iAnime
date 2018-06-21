package com.unual.anime.data.api

import android.util.Log
import com.google.gson.Gson
import com.unual.anime.data.exception.ClientHttpException
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.annotations.NonNull
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by unual on 2018/6/21.
 */
class PageService() {
    private var client: OkHttpClient

    init {
        client = getAgentHttpClient()
    }


    fun useAgent(useAgent: Boolean) {
        if (useAgent) {
            client = getAgentHttpClient()
        } else {
            client = getHttpClient()
        }
    }

    fun getAgentHttpClient(): OkHttpClient {
        var builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            val request = authorised.build()
            chain.proceed(request)
        }
        return builder.build()
    }

    fun getHttpClient(): OkHttpClient {
        var builder = OkHttpClient.Builder()
        return builder.build()
    }

    fun loadPage(url: String): Observable<String> {
        return Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map { url ->
                    var response = client.newCall(Request.Builder().url(url).build()).execute()
                    var result = response.body()?.string()
                    result
                }
    }
}