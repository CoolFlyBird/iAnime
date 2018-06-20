package com.unual.anime.data.observer

import com.trello.rxlifecycle2.LifecycleProvider
import com.unual.anime.data.function.HttpResultFunction
import com.unual.anime.data.function.ServerResultFunction
import com.unual.anime.data.request.HttpResponse
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 *
 * 适用Retrofit网络请求Observable(被监听者)
 * Created by Administrator on 2018/6/20.
 *
 */
class HttpRxObservable {
    fun <T> getObservable(apiObservable: Observable<HttpResponse<T>>, lifecycle: LifecycleProvider<T>?): Observable<T> {
        var observable: Observable<T> = if (lifecycle != null) {
            apiObservable
                    .map(ServerResultFunction())
                    .compose(lifecycle.bindToLifecycle())
                    .onErrorResumeNext(HttpResultFunction<T>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        } else {
            apiObservable
                    .map(ServerResultFunction())
                    .onErrorResumeNext(HttpResultFunction<T>())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
        return observable
    }
}