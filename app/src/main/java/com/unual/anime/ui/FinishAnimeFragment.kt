package com.unual.anime.ui

import android.content.Intent
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.adapter.AnimeListAdapter
import com.unual.anime.base.BaseListFragment
import com.unual.anime.data.entity.Anima
import com.unual.anime.data.entity.Anime
import com.unual.anime.utils.Constants
import kotlinx.android.synthetic.main.fragment_finish_anime.*
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates

/**
 * Created by unual on 2018/6/14.
 */
class FinishAnimeFragment : BaseListFragment<Anime>(), IFinishAnimeView {
    private lateinit var presenter: IFinishAnimePresenter
    var isTV = false

    var filter: String by Delegates.observable("") { _, old, new ->
        println("$old - $new -> $filter")
        onRefresh()
    }


    override fun getLayoutId(): Int = R.layout.fragment_finish_anime

    override fun getRecyclerView() = recycler

    override fun initView(view: View) {
        super.initView(view)
        presenter = FinishAnimePresenter(this, this)
    }

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout {
        return refresh
    }

    override fun onLoadAnimeList(list: List<Anime>) {
        onSetLoadData(list)
    }

    override fun loadListData() {
        Log.e("TAG", "isTV:$isTV")
        if (!isTV) {
            presenter.loadAnimeList(filter, page, pageSize)
        } else {
            presenter.loadTVList(filter, page, pageSize)
        }
    }

    override fun showLoading() {

    }

    override fun closeLoading() {
        refresh.isRefreshing = false
    }

    override fun bindAdapter(): BaseQuickAdapter<Anime, BaseViewHolder> {
        return AnimeListAdapter(R.layout.item_anime_list) { anime ->
            var value = getValue(anime.animeName + Constants.LAST)
            if (!value.isEmpty()) {
                anime.record = value
            }
            if (!isTV) {
                EventBus.getDefault().post(anime)
            } else {
                var anima = Anima(anime.animeName, anime.animeUrl, "")
                EventBus.getDefault().post(anima)
            }
        }
    }
}