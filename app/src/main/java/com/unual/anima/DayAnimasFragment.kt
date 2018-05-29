package com.unual.anima

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.unual.anima.adapter.DayAnimasAdapter
import com.unual.anima.data.Anima
import kotlinx.android.synthetic.main.fragment_day_anima.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by unual on 2018/5/29.
 */
class DayAnimasFragment : Fragment() {
    fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private var animas: ArrayList<Anima> = ArrayList()
    private var adapter: DayAnimasAdapter? = null

    fun setValue(data: List<Anima>) {
        animas.clear()
        animas.addAll(data)
        recycler?.adapter?.notifyDataSetChanged()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_day_anima, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = DayAnimasAdapter(R.layout.item_anima_list, { item ->
            EventBus.getDefault().post(item)
        })
        adapter?.setNewData(animas)
        recycler?.adapter = adapter
    }

}