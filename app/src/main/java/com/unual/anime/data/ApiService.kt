package com.unual.anime.data

import com.unual.anime.data.api.AnimeService
import com.unual.anime.data.api.PageService
import com.unual.anime.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/6/20.
 */
class ApiService {
    private var animeService: AnimeService? = null

    companion object {
        val instance by lazy { ApiService() }
    }

    fun getPageService(): PageService {
        return PageService()
    }

    fun getAnimeService(): AnimeService {
        if (animeService == null) {
            val builder = OkHttpClient.Builder()
            val loginInterceptor = HttpLoggingInterceptor()
            loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loginInterceptor)
            val client = builder.build()
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()
            animeService = retrofit.create(AnimeService::class.java)
        }
        return animeService!!
    }
}