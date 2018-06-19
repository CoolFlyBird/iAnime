package com.unual.anime.interf;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2018/6/19.
 */

public interface RecyclerViewLoadListListener<DATA> {
    /**
     * 绑定activity
     * @return
     */
    void initRecyclerRefreshAdapter();
    RecyclerView getRecyclerView();
    SwipeRefreshLayout getSwipeRefreshLayout();
    BaseQuickAdapter<DATA,BaseViewHolder> getAdapter();
    RecyclerView.LayoutManager getLayoutManager();
    boolean isOpenAuthRefresh();
    boolean isOpenLoadMore();

    /**
     * 启动一次刷新操作
     */
    void startRefresh();
    /**
     * 加载数据
     */
    void loadListData();

    /**
     * 数据获取成功 去设置
     * @param data
     */
    void onSetLoadData(List<DATA> data);

    /**
     * 数据获取失败了
     */
    void onLoadError();
}