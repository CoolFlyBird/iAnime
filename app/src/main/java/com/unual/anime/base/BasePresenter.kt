package com.unual.anime.base

import com.bumptech.glide.manager.LifecycleListener
import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * Created by Administrator on 2018/6/20.
 */
class BasePresenter<V, T>(view: V, context: T) : LifecycleListener {
    protected var mContextRef: Reference<T>? = null
    protected var mViewRef: Reference<V>? = null

    init {
        attachContext(context)
        attachView(view)
        setListener()
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    private fun attachContext(context: T) {
        mContextRef = WeakReference(context)
    }

    private fun attachView(view: V) {
        mViewRef = WeakReference(view)
    }

    fun setListener() {
        var context = mContextRef?.get()
        if (context != null) {
        }
    }
}