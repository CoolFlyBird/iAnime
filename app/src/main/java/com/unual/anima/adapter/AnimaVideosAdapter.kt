package com.unual.anima.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anima.R
import com.unual.anima.data.AnimaInfo

/**
 * Created by unual on 2018/5/29.
 */
class AnimaVideosAdapter(res: Int, private val callback: (AnimaInfo.AnimaVideo) -> Unit) : BaseQuickAdapter<AnimaInfo.AnimaVideo, BaseViewHolder>(res) {

    override fun convert(helper: BaseViewHolder, item: AnimaInfo.AnimaVideo) {
        with(item) {
            helper.setText(R.id.name, videoName)
            helper.itemView.tag = videoUrl
            if (item.checked && videoUrl.isEmpty()) {
                helper.setText(R.id.check, "无链接")
            } else if (item.checked && !videoUrl.isEmpty()) {
                helper.setText(R.id.check, "已解析")
            }
            helper.itemView.setOnClickListener { _ ->
                callback.invoke(item)
            }
        }
    }

}