package com.unual.anime.data.function

import com.unual.anime.data.exception.ServerException
import com.unual.anime.data.entity.HttpResponse
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function

/**
 * Created by Administrator on 2018/6/20.
 */
class ServerResultFunction<T> : Function<HttpResponse<T>, T> {
    @Throws(Exception::class)
    override fun apply(@NonNull response: HttpResponse<T>): T? {
        if (!response.isSuccess()) {
            throw ServerException(response.code, response.msg)
        }
        return response.data
    }

}