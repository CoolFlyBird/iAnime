package com.unual.anime.data.observer

import android.text.TextUtils
import android.util.Log
import com.unual.anime.data.exception.ApiException
import com.unual.anime.data.exception.ExceptionEngine
import com.unual.anime.data.request.HttpRequestListener
import com.unual.anime.data.request.RxActionManagerImpl
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/6/20.
 */
abstract class HttpRxObserver<T> : Observer<T>, HttpRequestListener {
    var mTag: String = ""

    constructor() {
    }

    constructor(tag: String) {
        mTag = tag
    }

    override fun onError(e: Throwable) {
        RxActionManagerImpl.mInstance.remove(mTag)
        if (e is ApiException) {
            onError(e)
        } else {
            onError(ApiException(ExceptionEngine.UN_KNOWN_ERROR, e))
        }
    }

    override fun onComplete() {
    }

    override fun onNext(@NonNull t: T) {
        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.mInstance.remove(mTag)
        }
        onSuccess(t)
    }

    override fun onSubscribe(@NonNull d: Disposable) {
        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.mInstance.add(mTag, d)
        }
        onStart(d)
    }

    override fun cancel() {
        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.mInstance.cancel(mTag)
        }
    }

    fun isDisposed(): Boolean {
        return if (TextUtils.isEmpty(mTag)) {
            true
        } else RxActionManagerImpl.mInstance.isDisposed(mTag)
    }

    protected abstract fun onStart(d: Disposable)

    protected abstract fun onError(e: ApiException)

    protected abstract fun onSuccess(response: T)

}