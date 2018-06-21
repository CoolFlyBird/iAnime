package com.unual.anime.data.api

import com.unual.anime.data.Anime
import com.unual.anime.data.AnimeVideo
import com.unual.anime.data.HttpResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by unual on 2018/6/14.
 */
interface AnimeService {

    @GET("anime/{id}")
    fun loadAnime(@Path("id") id: Int): Observable<Anime>

    @GET("anime/list")
    fun loadAnimeList(@Query("page") page: Int, @Query("limit") limit: Int): Observable<List<Anime>>

    @GET("anime/{id}/video")
    fun loadAnimeVideo(@Path("id") id: Int, @Query("page") page: Int, @Query("limit") limit: Int): Observable<List<AnimeVideo>>
}