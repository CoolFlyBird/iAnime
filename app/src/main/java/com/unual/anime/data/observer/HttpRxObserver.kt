package com.unual.anime.data.observer

import com.unual.anime.data.exception.ApiException
import com.unual.anime.data.exception.ExceptionEngine
import com.unual.anime.data.request.RxActionManagerImpl

/**
 * Created by Administrator on 2018/6/20.
 */
abstract class HttpRxObserver<T> : HttpObserver<T> {
    constructor() : super() {
    }

    constructor(tag: String) : super(tag) {
    }

    override fun onError(e: Throwable) {
        RxActionManagerImpl.mInstance.remove(mTag)
        if (e is ApiException) {
            onError(e)
        } else {
            onError(ApiException(ExceptionEngine.UN_KNOWN_ERROR, e))
        }
    }
}