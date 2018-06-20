package com.unual.anime.data.request

import io.reactivex.disposables.Disposable

/**
 * Created by Administrator on 2018/6/20.
 */
interface RxActionManager<T> {
    /**
     * 添加
     * @param tag
     * @param disposable
     */
    fun add(tag: T, disposable: Disposable)

    /**
     * 移除
     * @param tag
     */
    fun remove(tag: T)

    /**
     * 取消
     *
     * @param tag
     */
    fun cancel(tag: T)
}
