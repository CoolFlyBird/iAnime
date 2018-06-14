package com.unual.anime.exception

import kotlin.Exception

/**
 * Created by Administrator on 2018/6/14.
 */
class ApiException(var code: Int, var msg: String?) : Exception() {
}
