package com.unual.anime.ui

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import com.unual.anime.R
import com.unual.anime.base.BaseFragment
import com.unual.anime.data.Anima
import com.unual.anime.data.Constants
import com.unual.anime.data.Repository
import com.unual.anime.data.WeekDayClass
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_week_anime.*
import java.util.*

/**
 * Created by unual on 2018/6/14.
 */
class WeekAnimeFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val fragments: ArrayList<DayAnimeFragment> = ArrayList()
    private var week = ArrayList<WeekDayClass>()

    init {
        week.clear()
        week.add(WeekDayClass.Mon)
        week.add(WeekDayClass.Tues)
        week.add(WeekDayClass.Wed)
        week.add(WeekDayClass.Thur)
        week.add(WeekDayClass.Fri)
        week.add(WeekDayClass.Sat)
        week.add(WeekDayClass.Sun)
    }

    override fun getLayoutId(): Int = R.layout.fragment_week_anime

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        for (i in 0 until week.size) {
            fragments.add(DayAnimeFragment())
        }
        viewPager.adapter = MyPagerAdapter(fragmentManager, fragments, week)
        tabLayout.setupWithViewPager(viewPager)
        refresh.setOnRefreshListener(this)
        onRefresh()
        selectToday()
    }

    private fun selectToday() {
        val c = Calendar.getInstance()
        c.time = Date()
        val weekday = c.get(Calendar.DAY_OF_WEEK)
        var index = weekday - 2
        if (index < 0) index = 6
        tabLayout.getTabAt(index)?.select()
    }

    override fun onRefresh() {
        refresh.isRefreshing = true
        getWeekAnima(week, { list ->
            refresh.isRefreshing = false
            for (i in 0 until list.size) {
                fragments[i].setValue(list[i])
            }
        })
    }

    // 追更动漫
    private fun getWeekAnima(week: List<WeekDayClass>, callback: (List<List<Anima>>) -> Unit) {
        Repository.instance.loadPage("http://www.dilidili.wang/", { htmlPage ->
            getOneDay(htmlPage, week, callback)
        }, {})
    }

    // 更新动漫列表
    private fun getOneDay(page: String, week: List<WeekDayClass>, callback: (List<List<Anima>>) -> Unit) {
        var jxDocument = JXDocument.create(page)
        Observable.fromIterable(week)
                .subscribeOn(Schedulers.io())
                .map { day ->
                    var namePath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/figure/figcaption/p[1]/text()"
                    var urlPath = "//ul[@class=\"wrp animate\"]/li[@class=\"${day.value()}\"]/div[@class=\"book small\"]/a/@href"
                    val nameResult = jxDocument.sel(namePath)
                    val urlResult = jxDocument.sel(urlPath)
                    var result: ArrayList<Anima> = ArrayList()
                    for (i in 0 until nameResult.size) {
                        var name = nameResult[i].toString()
                        var url = "http://www.dilidili.wang${urlResult[i]}"
                        var anima = Anima(name, url)
                        result.add(anima)
                    }
                    result
                }
                .map { list ->
                    for (anima in list) {
                        var value = getValue(anima.name + Constants.LAST)
                        if (!value.isEmpty()) {
                            anima.record = value
                        }
                    }
                    list
                }
                .collect(
                        // 动漫列表(一周列表) -> 动漫列表(一天列表)
                        { ArrayList<ArrayList<Anima>>() },
                        { list, item ->
                            list.add(item)
                        }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->
                    // 动漫列表(一周列表) contain 动漫列表(一天列表)
                    callback.invoke(list)
                }
    }


    class MyPagerAdapter(fm: FragmentManager?, private val fragments: ArrayList<DayAnimeFragment>, private val tabs: List<WeekDayClass>) : FragmentPagerAdapter(fm) {
        override fun getPageTitle(position: Int) = tabs[position].key()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount() = fragments.size
    }
}