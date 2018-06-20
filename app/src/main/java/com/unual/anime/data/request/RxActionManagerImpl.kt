package com.unual.anime.data.request

import android.annotation.TargetApi
import android.os.Build
import android.support.v4.util.ArrayMap
import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/6/20.
 */
class RxActionManagerImpl {
    private var mMaps: ArrayMap<Any, Disposable> = ArrayMap()//处理,请求列表

    companion object {
        val mInstance by lazy { RxActionManagerImpl() }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun add(tag: Any, disposable: Disposable) {
        mMaps[tag] = disposable
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun remove(tag: Any) {
        if (!mMaps.isEmpty) {
            mMaps.remove(tag)
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun cancel(tag: Any) {
        if (mMaps.isEmpty) {
            return
        }
        if (mMaps[tag] == null) {
            return
        }
        var value = mMaps[tag]
        if (value != null && !value.isDisposed) {
            value.dispose()
        }
        mMaps.remove(tag)
    }

    /**
     * 判断是否取消了请求
     *
     * @param tag
     * @return
     */
    fun isDisposed(tag: Any): Boolean {
        return if (mMaps.isEmpty || mMaps[tag] == null) true else mMaps[tag]!!.isDisposed
    }
}
