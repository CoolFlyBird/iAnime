package com.unual.anime.adapter.view

import com.chad.library.adapter.base.loadmore.LoadMoreView
import com.unual.anime.R

/**
 * Created by Administrator on 2018/6/19.
 */
class MLoadMoreView : LoadMoreView() {
    override fun getLayoutId(): Int {
        return R.layout.quick_view_load_more
    }

    override fun getLoadingViewId(): Int {
        return R.id.load_more_loading_view
    }

    override fun getLoadFailViewId(): Int {
        return R.id.load_more_load_fail_view
    }

    /**
     * isLoadEndGone()为true，可以返回0
     * isLoadEndGone()为false，不能返回0
     */
    override fun getLoadEndViewId(): Int {
        return R.id.load_more_load_end_view
    }
}