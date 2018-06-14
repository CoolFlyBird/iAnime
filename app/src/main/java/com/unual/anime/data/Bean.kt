package com.unual.anime.data

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

class TypeUrl(var type: Int, var url: String, var line: List<String> = ArrayList())

class Anima(var name: String, var url: String, var record: String = "") : Serializable

class AnimaInfo(var anima: Anima) : Serializable {
    var list: ArrayList<AnimaVideo> = ArrayList()

    class AnimaVideo(var videoName: String = "", var videoUrl: String = "", var checked: Boolean = false, var checkType: Int = -1
                     , var useWebPlayer: Boolean = false, var line: ArrayList<String> = ArrayList()) : Serializable

    fun addAnimaVideo(video: AnimaVideo) {
        list.add(video)
    }
}

data class Anime(var animeId: String = "", var animeName: String = "", var animeUrl: String = "", var animeImg: String = "") {
    var record = ""
}

data class AnimeVideo(var animeId: String = "", var videoId: String = "", var videoName: String = "", var videoPage: String = "", var videoUrl: String = "", var videoType: String = "")

