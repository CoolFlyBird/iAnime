package com.unual.anima.data

import java.io.Serializable

/**
 * Created by unual on 2018/5/28.
 */
enum class WeekDayClass {
    Mon {
        override fun key() = "周一"
        override fun value() = "elmnt-one"
    },
    Tues {
        override fun key() = "周二"
        override fun value() = "elmnt-two"
    },
    Wed {
        override fun key() = "周三"
        override fun value() = "elmnt-three"
    },
    Thur {
        override fun key() = "周四"
        override fun value() = "elmnt-four"
    },
    Fri {
        override fun key() = "周五"
        override fun value() = "elmnt-five"
    },
    Sat {
        override fun key() = "周六"
        override fun value() = "elmnt-six"
    },
    Sun {
        override fun key() = "周日"
        override fun value() = "elmnt-seven"
    };

    abstract fun key(): String
    abstract fun value(): String
}

class Anima(var name: String, var url: String) : Serializable

class AnimaInfo(var name: String) {
    var list: ArrayList<AnimaVideo> = ArrayList()

    class AnimaVideo(var videoName: String, var videoUrl: String)

    fun addAnimaVideo(video: AnimaVideo) {
        list.add(video)
    }
}

