package com.unual.anime.data

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/5/29.
 */
class Repository {
    private var httpClient: OkHttpClient
    private var retrofit: Retrofit? = null
    private val animeService by lazy { retrofit?.create(AnimeService::class.java) }


    init {
        var builder1 = OkHttpClient.Builder()
        builder1.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            val request = authorised.build()
            chain.proceed(request)
        }
//        val loginInterceptor = HttpLoggingInterceptor()
//        loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        builder.addInterceptor(loginInterceptor)
        httpClient = builder1.build()

        retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                // 自定义Gson解析器 拦截服务端返回的错误信息
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

    }

    companion object {
        val instance by lazy { Repository() }
    }

    fun loadPage(url: String, callback: (String) -> Unit) {
        Observable.just(url)
                .subscribeOn(Schedulers.io())
                .map { pageUrl ->
                    try {
                        var respone = httpClient.newCall(Request.Builder().url(pageUrl).build()).execute()
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


    fun loadAnime(id: Int, callback: (Anime) -> Unit) {
        var animeObservable = animeService?.loadAnime(id)
        animeObservable?.subscribe(object : Observer<Anime> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                callback.invoke(Anime())
            }

            override fun onNext(anim: Anime) {
                callback.invoke(anim)
            }

            override fun onComplete() {
            }
        })
    }

    fun loadAnimeList(page: Int, limit: Int, callback: (List<Anime>) -> Unit) {
        animeService?.loadAnime(page, limit)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : Observer<List<Anime>> {
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
        var animeObservable = animeService?.loadAnimeVideo(id, page, limit)
        animeObservable?.subscribe(object : Observer<List<AnimeVideo>> {
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

open class ResultObserver<T>(var t: () -> Unit) : Observer<T> {
    protected var mTag: String = ""//请求标识


    override fun onSubscribe(d: Disposable) {
        onStart(d)
    }

    override fun onNext(t: T) {
        onSuccess(t)
    }


    override fun onError(e: Throwable) {
        onError("${e.message}")
    }

    override fun onComplete() {
    }

    fun onStart(d: Disposable) {

    }

    fun onError(e: String) {

    }

    fun onSuccess(response: T) {

    }
}