package com.unual.anime.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.data.Anima

/**
 * Created by unual on 2018/5/29.
 */
class DayAnimeAdapter(res: Int, private val callback: (Anima) -> Unit) : BaseQuickAdapter<Anima, BaseViewHolder>(res) {

    override fun convert(helper: BaseViewHolder, item: Anima) {
        with(item) {
            helper.setText(R.id.name, name)
            helper.setText(R.id.record, record)
            helper.itemView.tag = url
            helper.itemView.setOnClickListener { _ ->
                callback.invoke(item)
            }
        }
    }

}