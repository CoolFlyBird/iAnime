package com.unual.anime.base

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.adapter.view.MLoadMoreView
import com.unual.anime.interf.RecyclerViewLoadListListener

/**
 * Created by Administrator on 2018/6/19.
 */
abstract class BaseListFragment<DATA> : BaseFragment(),
        RecyclerViewLoadListListener<DATA>,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener,
        BaseQuickAdapter.OnItemClickListener {
    private lateinit var mAdapter: BaseQuickAdapter<DATA, BaseViewHolder>
    private var isRefresh = true
    protected var page = 0
    protected val pageSize = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerRefreshAdapter()
    }

    override fun initData() {

    }

    // 加载成功设置adapter
    private fun setAdapter() {
        mAdapter = bindAdapter()
        recyclerView.adapter = mAdapter
        if (isOpenEmptyView()) {
            // 设置空白布局
//            mAdapter?.emptyView = MEmptyView(this).view
        }
        mAdapter.onItemClickListener = this
        // 加载更多
        if (isOpenLoadMore) {
            mAdapter.setOnLoadMoreListener(this, recyclerView)
            // 设置加载状态布局
            mAdapter.setLoadMoreView(MLoadMoreView())
        }
    }

    fun authRefresh() {
        if (swipeRefreshLayout == null) return
        swipeRefreshLayout.post {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.isRefreshing = true
            }
            isRefresh = true
            page = 0
            loadListData()
        }
    }

    fun clearAdapterData() {
        mAdapter.setNewData(null)
    }

    override fun getAdapter(): BaseQuickAdapter<DATA, BaseViewHolder> {
        return mAdapter
    }


    override fun getLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context)
    }

    override fun isOpenAuthRefresh(): Boolean {
        return true
    }

    override fun isOpenLoadMore(): Boolean {
        return true
    }

    fun isOpenEmptyView(): Boolean {
        return true
    }

    override fun startRefresh() {
        authRefresh()
    }

    override fun onSetLoadData(data: List<DATA>?) {
        // 请求数据完成
        if (swipeRefreshLayout == null) return
        swipeRefreshLayout.isRefreshing = false
        mAdapter.setEnableLoadMore(true)
        val size = data?.size ?: 0
        if (isRefresh) {
            mAdapter.setNewData(data)
            isRefresh = false
        } else {
            if (size > 0) {
                mAdapter.addData(data!!)
            }
        }
        if (size < pageSize) {
            // 没有更多了
            mAdapter.loadMoreEnd()
        } else {
            // 本次加载更多完成
            mAdapter.loadMoreComplete()
        }
    }

    override fun onLoadError() {
        // 请求数据失败
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.isRefreshing = false
        }
        mAdapter.setEnableLoadMore(true)
        mAdapter.loadMoreFail()
    }

    // ========================================================================================== //

    override fun onRefresh() {
        if (isOpenLoadMore) {
            mAdapter.setEnableLoadMore(false)
        }
        isRefresh = true
        page = 0
        loadListData()
    }

    override fun onLoadMoreRequested() {
        page++
        mAdapter.setEnableLoadMore(true)
        isRefresh = false
        loadListData()
    }

    override fun initRecyclerRefreshAdapter() {
        swipeRefreshLayout.setOnRefreshListener(this)
        recyclerView.layoutManager = getLayoutManager()
        setAdapter()
        // 自动刷新
        if (isOpenAuthRefresh) {
            authRefresh()
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
    }

    protected abstract fun bindAdapter(): BaseQuickAdapter<DATA, BaseViewHolder>

}