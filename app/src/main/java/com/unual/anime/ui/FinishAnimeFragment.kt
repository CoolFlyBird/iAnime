package com.unual.anime.ui

import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.adapter.AnimeListAdapter
import com.unual.anime.base.BaseListFragment
import com.unual.anime.data.Anima
import com.unual.anime.data.Anime
import com.unual.anime.data.Constants
import com.unual.anime.data.Repository
import kotlinx.android.synthetic.main.fragment_finish_anime.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by unual on 2018/6/14.
 */
class FinishAnimeFragment : BaseListFragment<Anime>() {
    override fun getLayoutId(): Int = R.layout.fragment_finish_anime

    override fun getRecyclerView() = recycler

    override fun getSwipeRefreshLayout(): SwipeRefreshLayout {
        return refresh
    }

    override fun loadListData() {
        Log.e("TAG", "${page} - ${pageSize}")
        getAnimeList(page, pageSize, { list ->
            onSetLoadData(list)
        })
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


    // 动漫列表
    private fun getAnimeList(page: Int, limit: Int, callback: (List<Anime>) -> Unit) {
        Repository.instance.loadAnimeList(page, limit, callback, {})
    }
}