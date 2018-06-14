package com.unual.anime.data

import android.util.Log
import com.unual.anime.data.retrofit.AnimeService
import com.unual.anime.exception.ApiException
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

/**
 * Created by Administrator on 2018/5/29.
 */
class Repository {
    private lateinit var animeService: AnimeService
    private lateinit var httpClient: OkHttpClient
    private lateinit var retrofit: Retrofit

    init {
        change(true, true)
    }

    companion object {
        val instance by lazy { Repository() }
    }

    fun change(useAgent: Boolean, enableLog: Boolean): Repository {
        var builder = OkHttpClient.Builder()
        if (useAgent) {
            builder.addInterceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
                val request = authorised.build()
                chain.proceed(request)
            }
        }
        if (enableLog) {
            val loginInterceptor = HttpLoggingInterceptor()
            loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loginInterceptor)
        }
        httpClient = builder.build()
        retrofit = Retrofit.Builder()
                .client(httpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        animeService = retrofit.create(AnimeService::class.java)
        return this
    }


    fun loadPage(url: String, onSuccess: (String) -> Unit, onFail: (Exception) -> Unit) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map { url ->
                    try {
                        var response = httpClient.newCall(Request.Builder().url(url).build()).execute()
                        if (response.isSuccessful) {
                            val result = response.body()?.string() ?: ""
                            onSuccess.invoke(result)
                        } else {
                            onFail.invoke(ApiException(response.code(), response.message()))
                        }
                    } catch (e: Exception) {
                        onFail.invoke(e)
                    }
                }
                .subscribe()
        loadAnime(1, { anime -> }, { anime -> }, { anime -> })
    }


    fun loadAnime(id: Int, callback1: (Anime) -> Unit, callback2: (Anime) -> Unit, callback3: (Anime) -> Unit) {
        var animeObservable = animeService.loadAnime(id)
        animeObservable.subscribe(object : Observer<Anime> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onError(e: Throwable) {
                callback1.invoke(Anime())
            }

            override fun onNext(anim: Anime) {
                callback2.invoke(anim)
            }

            override fun onComplete() {
            }
        })
    }

    fun loadAnimeList(page: Int, limit: Int, callback: (List<Anime>) -> Unit) {
        animeService.loadAnime(page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Anime>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        callback.invoke(ArrayList<Anime>())
                    }

                    override fun onNext(animList: List<Anime>) {
                        callback.invoke(animList)
                    }

                    override fun onComplete() {
                    }
                })
    }

    fun loadAnimeVideo(id: Int, page: Int, limit: Int, callback: (List<AnimeVideo>) -> Unit) {
        animeService.loadAnimeVideo(id, page, limit)
                .subscribe(object : Observer<List<AnimeVideo>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        callback.invoke(ArrayList<AnimeVideo>())
                    }

                    override fun onNext(animList: List<AnimeVideo>) {
                        callback.invoke(animList)
                    }

                    override fun onComplete() {
                    }
                })
    }
}