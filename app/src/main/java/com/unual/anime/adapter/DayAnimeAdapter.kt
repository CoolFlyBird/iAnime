package com.unual.anime.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.data.entity.Anima
import com.unual.anime.data.entity.AnimeVideo

/**
 * Created by unual on 2018/5/29.
 */
class DayAnimeAdapter(res: Int, private val callback: (AnimeVideo) -> Unit) : BaseQuickAdapter<AnimeVideo, BaseViewHolder>(res) {

    override fun convert(helper: BaseViewHolder, item: AnimeVideo) {
        with(item) {
            helper.setText(R.id.name, videoName)
            helper.setText(R.id.record, record)
            helper.itemView.tag = videoUrl
            helper.itemView.setOnClickListener { _ ->
                callback.invoke(item)
            }
        }
    }

}