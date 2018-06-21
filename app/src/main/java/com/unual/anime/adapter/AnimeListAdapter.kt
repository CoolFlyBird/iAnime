package com.unual.anime.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.unual.anime.R
import com.unual.anime.data.entity.Anime
import kotlinx.android.synthetic.main.item_anime_list.view.*

/**
 * Created by unual on 2018/5/29.
 */
class AnimeListAdapter(res: Int, private val callback: (Anime) -> Unit) : BaseQuickAdapter<Anime, BaseViewHolder>(res) {

    override fun convert(helper: BaseViewHolder, item: Anime) {
        with(item) {
            helper.setText(R.id.name, animeName)
            helper.setText(R.id.record, record)
            Glide.with(mContext).load(animeImg).into(helper.itemView.img)
            helper.itemView.tag = animeUrl
            helper.itemView.setOnClickListener { _ ->
                callback.invoke(item)
            }
        }
    }

}