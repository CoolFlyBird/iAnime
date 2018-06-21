package com.unual.anime.ui

import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.adapter.AnimeListAdapter
import com.unual.anime.base.BaseListFragment
import com.unual.anime.data.Anima
import com.unual.anime.data.Anime
import com.unual.anime.data.Constants
import com.unual.anime.widget.DialogUtils
import kotlinx.android.synthetic.main.fragment_finish_anime.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by unual on 2018/6/14.
 */
class FinishAnimeFragment : BaseListFragment<Anime>(), IFinishAnimeView {
    lateinit var presenter: IFinishAnimePresenter

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
        Log.e("TAG", "loadListData:${page} - ${pageSize}")
        presenter.loadAnimeList(page, pageSize)
    }

    override fun bindAdapter(): BaseQuickAdapter<Anime, BaseViewHolder> {
        Log.e("TAG", "bindAdapter")
        return AnimeListAdapter(R.layout.item_anime_list, { anime ->
            var value = getValue(anime.animeName + Constants.LAST)
            if (!value.isEmpty()) {
                anime.record = value
            }
            var anima = Anima(anime.animeName, anime.animeUrl, anime.record)
            EventBus.getDefault().post(anima)
        })
    }
}