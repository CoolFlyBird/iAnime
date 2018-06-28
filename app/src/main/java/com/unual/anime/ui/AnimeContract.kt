package com.unual.anime.ui

import com.unual.anime.base.BaseActivity
import com.unual.anime.base.BaseFragment
import com.unual.anime.base.BasePresenter
import com.unual.anime.base.IBaseView
import com.unual.anime.data.ApiService
import com.unual.anime.data.entity.Anime
import com.unual.anime.data.entity.AnimeVideo
import com.unual.anime.data.exception.ApiException
import com.unual.anime.data.observer.HttpRxObservable
import com.unual.anime.data.observer.HttpRxObserver
import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/6/20.
 */
interface IAnimeView : IBaseView {
    fun onLoadAnimeVideoList(list: List<AnimeVideo>)
}

interface IAnimePresenter {
    fun loadAnimeVideoList(id: Int, page: Int, limit: Int)
}

class AnimePresenter(var view: IAnimeView, var context: BaseActivity)
    : BasePresenter<IAnimeView, BaseActivity>(view, context), IAnimePresenter {
    override fun loadAnimeVideoList(id: Int, page: Int, limit: Int) {
        HttpRxObservable
                .getObservable(ApiService.instance.getAnimeService().loadAnimeVideo(id, page, limit), context)
                .subscribe(object : HttpRxObserver<ArrayList<AnimeVideo>>() {
                    override fun onStart(d: Disposable) {
                        mViewRef?.get()?.showLoading()
                    }

                    override fun onError(e: ApiException) {
                        mViewRef?.get()?.closeLoading()
                        mViewRef?.get()?.showToast(e.msg)
                    }

                    override fun onSuccess(list: ArrayList<AnimeVideo>) {
                        mViewRef?.get()?.closeLoading()
                        mViewRef?.get()?.onLoadAnimeVideoList(list)
                    }
                })
    }
}