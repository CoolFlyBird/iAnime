package com.unual.anime.data.function;

import android.util.Log;

import com.unual.anime.data.exception.ExceptionEngine;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2018/6/20.
 */

public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        Log.e("TAG", "HttpResultFunction:" + throwable);
        return Observable.error(ExceptionEngine.Companion.handleException(throwable));
    }
}

