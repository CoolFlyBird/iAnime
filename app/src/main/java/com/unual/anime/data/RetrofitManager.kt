package com.unual.anime.data

import com.unual.anime.data.api.AnimeService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Administrator on 2018/6/20.
 */
class RetrofitManager {
    private var animeService: AnimeService? = null

    companion object {
        val instance by lazy { RetrofitManager() }
    }

    fun getAgentHttpClient(): OkHttpClient {
        var builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val originalRequest = chain.request()
            val authorised = originalRequest.newBuilder().header("user-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
            val request = authorised.build()
            chain.proceed(request)
        }
//        val loginInterceptor = HttpLoggingInterceptor()
//        loginInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        builder.addInterceptor(loginInterceptor)
        return builder.build()
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