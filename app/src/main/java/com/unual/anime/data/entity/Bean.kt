package com.unual.anime.data.entity

import java.io.Serializable

/**
 * Created by unual on 2018/5/28.
 */
enum class WeekDay {
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

enum class UserAgent {
    Chrome {
        override fun key() = "Chrome"
        override fun value() = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.79 Safari/537.36"
        override fun needRemove() = false

    },
    Android {
        override fun key() = "Android"
        override fun value() = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
        override fun needRemove() = true
    };

    abstract fun key(): String
    abstract fun value(): String
    abstract fun needRemove(): Boolean
}


class TypeUrl(var type: Int, var url: String, var line: List<String> = ArrayList())

class Anima(var name: String = "", var url: String = "", var record: String = "") : Serializable

class AnimaInfo(var anima: Anima) : Serializable {
    var list: ArrayList<AnimaVideo> = ArrayList()

    class AnimaVideo(var videoName: String = "", var videoUrl: String = "", var checked: Boolean = false, var checkType: Int = -1
                     , var useWebPlayer: Boolean = false, var line: ArrayList<String> = ArrayList()) : Serializable

    fun addAnimaVideo(video: AnimaVideo) {
        list.add(video)
    }
}

data class Anime(var animeId: Int, var animeName: String = "", var animeUrl: String = "", var animeImg: String = "") : Serializable {
    var record = ""
}

data class AnimeVideo(var animeId: String = "", var videoId: String = "", var videoName: String = "", var videoPage: String = "", var videoUrl: String = "", var videoType: String = "") {
    var record = ""
}

