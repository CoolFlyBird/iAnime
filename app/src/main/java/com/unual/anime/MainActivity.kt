package com.unual.anime

import android.Manifest
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.unual.anime.adapter.DayAnimeAdapter
import com.unual.anime.base.BaseActivity
import com.unual.anime.data.Anima
import com.unual.anime.data.Constant
import com.unual.anime.data.Repository
import com.unual.anime.data.WeekDayClass
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_day_anime.*
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private val fragments: ArrayList<DayAnimasFragment> = ArrayList()
    private var week = ArrayList<WeekDayClass>()

    init {
        week.add(WeekDayClass.Mon)
        week.add(WeekDayClass.Tues)
        week.add(WeekDayClass.Wed)
        week.add(WeekDayClass.Thur)
        week.add(WeekDayClass.Fri)
        week.add(WeekDayClass.Sat)
        week.add(WeekDayClass.Sun)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
        setContentView(R.layout.activity_main)
        for (i in 0 until week.size) {
            fragments.add(DayAnimasFragment())
        }
        viewPager.adapter = MyPagerAdapter(supportFragmentManager, fragments, week)
        tabLayout.setupWithViewPager(viewPager)
        refresh.setOnRefreshListener(this)
        onRefresh()
        selectToday()
    }

    @AfterPermissionGranted(Constant.REQUEST_CODE)
    fun checkPermissions() {
        if (!EasyPermissions.hasPermissions(this, *parm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.setting_request), Constant.REQUEST_CODE, *parm)
        }
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
        })
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
                        var value = getValue(anima.name + Constant.LAST)
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


    class MyPagerAdapter(fm: FragmentManager, private val fragments: ArrayList<DayAnimasFragment>, private val tabs: List<WeekDayClass>) : FragmentPagerAdapter(fm) {
        override fun getPageTitle(position: Int) = tabs[position].key()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount() = fragments.size
    }

    class DayAnimasFragment : Fragment() {
        fun Fragment.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
            Toast.makeText(context, message, duration).show()
        }

        private var animas: ArrayList<Anima> = ArrayList()
        private var adapter: DayAnimeAdapter? = null

        fun setValue(data: List<Anima>) {
            animas.clear()
            animas.addAll(data)
            recycler?.adapter?.notifyDataSetChanged()
        }


        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_day_anime, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            recycler?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = DayAnimeAdapter(R.layout.item_anime_list, { item ->
                EventBus.getDefault().post(item)
            })
            adapter?.setNewData(animas)
            recycler?.adapter = adapter
        }

    }

}
