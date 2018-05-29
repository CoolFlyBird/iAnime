package com.unual.anima.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Administrator on 2018/5/29.
 */
class MySwipeRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var preX = 0f
    private var preY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                preX = ev.x
                preY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                var dx = ev.x - preX
                var dy = ev.y - preY
                if (Math.abs(dx) > Math.abs(dy)) {
                    return false
                }
                preX = ev.x
                preY = ev.y
            }
            MotionEvent.ACTION_UP -> {
                preX = ev.x
                preY = ev.y
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

}