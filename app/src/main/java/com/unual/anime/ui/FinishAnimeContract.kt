package com.unual.anime.ui

import com.unual.anime.base.IBaseView
import com.unual.anime.data.Anime
import com.unual.anime.data.RetrofitManager

/**
 * Created by Administrator on 2018/6/20.
 */
interface IFinishAnimeContract {
    interface IFinishAnimeView : IBaseView {
        fun onLoadAnimeList(list: List<Anime>)
    }

    interface IFinishAnimePresenter {
        fun loadAnimeList(page: Int, limit: Int)
    }
}

class FinishAnimePresenter : IFinishAnimeContract.IFinishAnimePresenter {
    override fun loadAnimeList(page: Int, limit: Int) {
        RetrofitManager.instance.getAnimeService()
                .loadAnimeList(page, limit)
                .subscribe()

    }
}