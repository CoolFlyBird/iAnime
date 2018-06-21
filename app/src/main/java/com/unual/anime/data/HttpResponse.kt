package com.unual.anime.data

import com.unual.anime.data.api.ApiCodeConfig

/**
 * Created by Administrator on 2018/6/20.
 */
class HttpResponse<T> {
    var code: Int = 0
    var msg: String = ""
    var time: String = ""
    var data: T? = null

    fun isSuccess(): Boolean {
        return code == ApiCodeConfig.SUCCESS
    }

    override fun toString(): String {
        return "Response{code=$code, msg=$msg, time=$time, data=$data"
    }
}