package com.unual.anime.data.exception

/**
 * Created by Administrator on 2018/6/20.
 */
class ServerException : RuntimeException {
    var code: Int = 0//错误码
    var msg: String = ""//错误信息

    constructor(code: Int, msg: String) {
        this.code = code
        this.msg = msg
    }
}