package com.unual.anime.ui

import com.unual.anime.base.BaseFragment
import com.unual.anime.base.BasePresenter
import com.unual.anime.base.IBaseView
import com.unual.anime.data.ApiService
import com.unual.anime.data.entity.Anime
import com.unual.anime.data.exception.ApiException
import com.unual.anime.data.observer.HttpRxObservable
import com.unual.anime.data.observer.HttpRxObserver
import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/6/20.
 */
interface IFinishAnimeView : IBaseView {
    fun onLoadAnimeList(list: List<Anime>)
}

interface IFinishAnimePresenter {
    fun loadAnimeList(filter: String, page: Int, limit: Int)
}

class FinishAnimePresenter(var view: IFinishAnimeView, var context: BaseFragment)
    : BasePresenter<IFinishAnimeView, BaseFragment>(view, context), IFinishAnimePresenter {
    override fun loadAnimeList(filter: String, page: Int, limit: Int) {
        HttpRxObservable
                .getObservable(ApiService.instance.getAnimeService().loadAnimeList(filter, page, limit), context)
                .subscribe(object : HttpRxObserver<ArrayList<Anime>>() {
                    override fun onStart(d: Disposable) {
                        mViewRef?.get()?.showLoading()
                    }

                    override fun onError(e: ApiException) {
                        mViewRef?.get()?.closeLoading()
                        mViewRef?.get()?.showToast(e.msg)
                    }

                    override fun onSuccess(list: ArrayList<Anime>) {
                        mViewRef?.get()?.closeLoading()
                        mViewRef?.get()?.onLoadAnimeList(list)
                    }
                })
    }
}