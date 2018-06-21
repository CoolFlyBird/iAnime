package com.unual.anime.data.exception

/**
 * Created by unual on 2018/6/22.
 */
class ClientHttpException : RuntimeException {
    var msg: String = ""//错误信息
    var code: Int = 0//错误码

    constructor(code: Int, msg: String) {
        this.code = code
        this.msg = msg
    }
}