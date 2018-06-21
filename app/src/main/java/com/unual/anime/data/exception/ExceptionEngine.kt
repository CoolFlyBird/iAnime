package com.unual.anime.data.exception

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException

/**
 * Created by Administrator on 2018/6/20.
 */
class ExceptionEngine {
    companion object {
        const val UN_KNOWN_ERROR = 1000//未知错误
        const val ANALYTIC_SERVER_DATA_ERROR = 1001//解析(服务器)数据错误
        private const val ANALYTIC_CLIENT_DATA_ERROR = 1002//解析(客户端)数据错误
        private const val CONNECT_ERROR = 1003//网络连接错误
        private const val TIME_OUT_ERROR = 1004//网络连接超时
        fun handleException(e: Throwable): ApiException {
            val ex: ApiException
            if (e is HttpException) {             //HTTP错误
                ex = ApiException(e.code(), e)
                ex.msg = "网络不给力，请稍后再试"  //均视为网络错误
                return ex
            } else if (e is ClientHttpException) {//HTTP300以上 错误
                ex = ApiException(e.code, e)
                ex.msg = "网络错误"
                return ex
            } else if (e is ServerException) {    //服务器返回的错误
                ex = ApiException(e.code, e)
                ex.msg = e.msg
                return ex
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException || e is MalformedJsonException) {  //解析数据错误
                ex = ApiException(ANALYTIC_SERVER_DATA_ERROR, e)
                ex.msg = "数据解析失败"
                return ex
            } else if (e is ConnectException) {//连接网络错误
                ex = ApiException(CONNECT_ERROR, e)
                ex.msg = "连接服务器失败，请检查您的网络"
                return ex
            } else if (e is SocketTimeoutException) {//网络超时
                ex = ApiException(TIME_OUT_ERROR, e)
                ex.msg = "连接服务器超时，请检查您的网络"
                return ex
            } else {  //未知错误
                ex = ApiException(UN_KNOWN_ERROR, e)
                ex.msg = "网络不给力，请重试"
                return ex
            }
        }
    }
}