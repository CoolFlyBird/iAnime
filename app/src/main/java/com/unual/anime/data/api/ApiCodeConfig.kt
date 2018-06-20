package com.unual.anime.data.api

/**
 * Created by Administrator on 2018/6/20.
 */
public class ApiCodeConfig {
    companion object {
        const val SUCCESS = 1000
    }

    fun getApiErr(code: Int): String {
        var errStr = ""
        when (code) {
            SUCCESS -> errStr = "请求成功"
            else -> errStr = "网络错误"
        }
        return errStr
    }
}