package com.unual.anime.base

import com.bumptech.glide.manager.LifecycleListener
import java.lang.ref.Reference
import java.lang.ref.WeakReference


/**
 * Created by Administrator on 2018/6/20.
 */
open class BasePresenter<V, T>(view: V, context: T) : LifecycleListener {
    protected var mContextRef: Reference<T>? = null
    protected var mViewRef: Reference<V>? = null

    init {
        attachContext(context)
        attachView(view)
    }

    private fun attachContext(context: T) {
        mContextRef = WeakReference(context)
    }

    private fun attachView(view: V) {
        mViewRef = WeakReference(view)
    }

    fun isViewAttached(): Boolean {
        return mViewRef?.get() != null
    }

    fun isContextAttached(): Boolean {
        return mContextRef?.get() != null
    }

    private fun detachView() {
        if (isViewAttached()) {
            mViewRef?.clear()
        }
    }

    private fun detachContext() {
        if (isContextAttached()) {
            mContextRef?.clear()
        }
    }


    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
        detachView()
        detachContext()
    }

}