package com.unual.anime.data.exception

class ApiException : RuntimeException {
    var msg: String = ""//错误信息
    var code: Int = 0//错误码

    constructor(code: Int, msg: String) {
        this.code = code
        this.msg = msg
    }

    constructor(code: Int, throwable: Throwable) : super(throwable) {
        this.code = code
    }
}