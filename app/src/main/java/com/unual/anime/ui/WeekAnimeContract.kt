package com.unual.anime.ui

import android.util.Log
import com.unual.anime.base.BaseFragment
import com.unual.anime.base.BasePresenter
import com.unual.anime.base.IBaseView
import com.unual.anime.data.ApiService
import com.unual.anime.data.entity.Anima
import com.unual.anime.data.entity.Anime
import com.unual.anime.data.entity.WeekDay
import com.unual.anime.data.exception.ApiException
import com.unual.anime.data.exception.ClientHttpException
import com.unual.anime.data.observer.HttpRxObservable
import com.unual.anime.data.observer.HttpRxObserver
import com.unual.anime.utils.Constants
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import java.util.ArrayList

/**
 * Created by unual on 2018/6/21.
 */
interface IWeekAnimeView : IBaseView {
    fun onAnimeLoad(list: ArrayList<Anime>)
}

interface IWeekAnimePresenter {
    fun loadDayAnime(day: WeekDay)
}

class WeekAnimePresenter(var view: IWeekAnimeView, var context: BaseFragment) : BasePresenter<IWeekAnimeView, BaseFragment>(view, context), IWeekAnimePresenter {
    override fun loadDayAnime(day: WeekDay) {
        HttpRxObservable.getPageObservable(
                ApiService.instance.getPageService().loadPage(Constants.PAGE_URL), context)
                .map { page ->
                    var jxDocument = JXDocument.create(page.toString())
                    var namePath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/figure/figcaption/p[1]/text()"
                    var imgPath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/figure/img/@src"
                    var urlPath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val imgResult = jxDocument.sel(imgPath)
                    val urlResult = jxDocument.sel(urlPath)
                    var result: ArrayList<Anime> = ArrayList()
                    for (i in 0 until nameResult.size) {
                        var name = nameResult[i].toString()
                        var url = if (urlResult[i].toString().contains("www.dilidili.wang")) {
                            "${urlResult[i]}"
                        } else {
                            "http://www.dilidili.wang${urlResult[i]}"
                        }

                        var img = if (imgResult[i].toString().contains("www.dilidili.wang")) {
                            "${imgResult[i]}"
                        } else {
                            "http://www.dilidili.wang${imgResult[i]}"
                        }
                        var anime = Anime(0, name, url, img)
                        result.add(anime)
                    }
                    result
                }
                .map { list ->
                    for (anime in list) {
                        var value = context.getValue(anime.animeName + Constants.LAST)
                        if (!value.isEmpty()) {
                            anime.record = value
                        }
                    }
                    list
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    mViewRef?.get()?.closeLoading()
                    mViewRef?.get()?.onAnimeLoad(list)
                }
    }
}
