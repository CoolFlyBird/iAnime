package com.unual.anima.base

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Administrator on 2018/5/30.
 */
@GlideModule
class MyAppGlideModule : AppGlideModule() {

}

object Utils {
    fun format(date: Date, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val df = SimpleDateFormat(pattern)//设置日期格式
        return df.format(date)
    }
}
