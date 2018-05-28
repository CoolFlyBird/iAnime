package com.unual.anima

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by unual on 2018/5/29.
 */
class AnimaListFragment : Fragment() {
    fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

    private var animas: ArrayList<KeyValue> = ArrayList()
    private var adapter: AnimaListAdapter? = null
    private var recycler: RecyclerView? = null
    fun setValue(data: List<KeyValue>) {
        for (i in data) {
            Log.e("TAG", "${i.key} - ${i.value}")
        }
        animas.clear()
        animas.addAll(data)
        recycler?.adapter?.notifyDataSetChanged()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_anima_list, container, false)
        recycler = view.findViewById(R.id.recycler)
        recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = AnimaListAdapter(R.layout.item_anima_list, { item ->
            toast(item.value)
        })
        adapter?.setNewData(animas)
        recycler?.adapter = adapter
        return view
    }

}