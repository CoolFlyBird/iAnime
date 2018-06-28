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
import com.unual.anime.data.ApiService
import com.unual.anime.data.entity.Anima
import com.unual.anime.utils.Constants
import com.unual.anime.data.entity.WeekDay
import com.unual.jsoupxpath.JXDocument
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_week_anime.*
import java.util.*

/**
 * Created by unual on 2018/6/14.
 */
class WeekAnimeFragment : BaseFragment() {
    private val fragments: ArrayList<DayAnimeFragment> = ArrayList()
    private var week = ArrayList<WeekDay>()

    init {
        week.clear()
        week.add(WeekDay.Mon)
        week.add(WeekDay.Tues)
        week.add(WeekDay.Wed)
        week.add(WeekDay.Thur)
        week.add(WeekDay.Fri)
        week.add(WeekDay.Sat)
        week.add(WeekDay.Sun)
    }

    override fun getLayoutId(): Int = R.layout.fragment_week_anime

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        for (i in 0 until week.size) {
            var fragment = DayAnimeFragment()
            fragment.day = week[i]
            fragments.add(fragment)
        }
        viewPager.adapter = MyPagerAdapter(fragmentManager, fragments, week)
        tabLayout.setupWithViewPager(viewPager)
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


    class MyPagerAdapter(fm: FragmentManager?, private val fragments: ArrayList<DayAnimeFragment>, private val tabs: List<WeekDay>) : FragmentPagerAdapter(fm) {
        override fun getPageTitle(position: Int) = tabs[position].key()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount() = fragments.size
    }
}