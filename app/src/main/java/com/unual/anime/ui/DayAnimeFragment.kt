package com.unual.anime.ui

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.adapter.AnimeListAdapter
import com.unual.anime.base.BaseListFragment
import com.unual.anime.data.entity.Anima
import com.unual.anime.data.entity.Anime
import com.unual.anime.data.entity.WeekDay
import com.unual.anime.utils.Constants
import kotlinx.android.synthetic.main.a_common_list.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * Created by unual on 2018/6/22.
 */
class DayAnimeFragment : BaseListFragment<Anime>(), IWeekAnimeView {

    val presenter by lazy { WeekAnimePresenter(this, this) }
    var day: WeekDay = WeekDay.Mon
    override fun getLayoutId() = R.layout.a_common_list

    override fun getRecyclerView() = recycler

    override fun getSwipeRefreshLayout() = refresh

    override fun initView(view: View) {
        super.initView(view)
    }

    override fun showLoading() {
    }

    override fun closeLoading() {
    }

    override fun loadListData() {
        if (day != null) {
            presenter.loadDayAnime(day)
        }
    }

    override fun onAnimeLoad(list: ArrayList<Anime>) {
        onSetLoadData(list)
    }


    override fun bindAdapter(): BaseQuickAdapter<Anime, BaseViewHolder> {
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