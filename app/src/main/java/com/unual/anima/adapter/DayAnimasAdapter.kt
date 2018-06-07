package com.unual.anima.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anima.R
import com.unual.anima.data.Anima

/**
 * Created by unual on 2018/5/29.
 */
class DayAnimasAdapter(res: Int, private val callback: (Anima) -> Unit) : BaseQuickAdapter<Anima, BaseViewHolder>(res) {

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