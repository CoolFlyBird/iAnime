package com.unual.anime.ui

import android.Manifest
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unual.anime.R
import com.unual.anime.adapter.AnimeListAdapter
import com.unual.anime.base.BaseFragment
import com.unual.anime.data.*
import kotlinx.android.synthetic.main.fragment_finish_anime.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by unual on 2018/6/14.
 */
class FinishAnimeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    lateinit var adapter: AnimeListAdapter
    var page = 0
    var limit = 100
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_finish_anime, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = AnimeListAdapter(R.layout.item_anime_list, { anime ->
            var value = getValue(anime.animeName + Constant.LAST)
            if (!value.isEmpty()) {
                anime.record = value
            }
            var anima = Anima(anime.animeName, anime.animeUrl, anime.record)
            EventBus.getDefault().post(anima)
        })
        recycler.adapter = adapter
        onRefresh()
    }


    override fun onRefresh() {
        refresh.isRefreshing = true
        getAnimeList(page, limit, { list ->
            refresh.isRefreshing = false
            adapter.setNewData(list)
        })
    }

    // 动漫列表
    private fun getAnimeList(page: Int, limit: Int, callback: (List<Anime>) -> Unit) {
        Repository.instance.loadAnimeList(page, limit, callback)
    }
}