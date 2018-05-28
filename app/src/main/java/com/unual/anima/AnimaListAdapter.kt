package com.unual.anima

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 * Created by unual on 2018/5/29.
 */
class AnimaListAdapter(res: Int, private val callback: (KeyValue) -> Unit) : BaseQuickAdapter<KeyValue, BaseViewHolder>(res) {

    override fun convert(helper: BaseViewHolder, item: KeyValue) {
        with(item) {
            helper.setText(R.id.name, key)
            helper.itemView.tag = value
            helper.itemView.setOnClickListener { view ->
                callback.invoke(item)
            }
        }
    }

}