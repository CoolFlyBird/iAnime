package com.unual.anime.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.unual.anime.R
import com.unual.anime.adapter.DayAnimeAdapter
import com.unual.anime.base.BaseFragment
import com.unual.anime.data.Anima
import kotlinx.android.synthetic.main.fragment_day_anime.*
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by Administrator on 2018/6/19.
 */
class DayAnimeFragment : BaseFragment() {
    private var animas: ArrayList<Anima> = ArrayList()
    private var adapter: DayAnimeAdapter? = null

    override fun getLayoutId(): Int = R.layout.fragment_day_anime

    fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    fun setValue(data: List<Anima>) {
        animas.clear()
        animas.addAll(data)
        recycler?.adapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = DayAnimeAdapter(R.layout.item_anima_list, { item ->
            EventBus.getDefault().post(item)
        })
        adapter?.setNewData(animas)
        recycler?.adapter = adapter
    }
}