package com.unual.anime.base

/**
 * Created by Administrator on 2018/6/20.
 */
interface IBaseView {
    fun showLoading()
    fun closeLoading()
    fun showToast(msg: String)
}