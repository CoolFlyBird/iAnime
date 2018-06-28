package com.unual.anime

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.view.Menu
import com.unual.anime.base.BaseActivity
import com.unual.anime.base.BaseFragment
import com.unual.anime.data.entity.Anima
import com.unual.anime.ui.AnimeActivity
import com.unual.anime.ui.FinishAnimeFragment
import com.unual.anime.ui.WeekAnimeFragment
import com.unual.anime.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity() {
    private val param = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var finishAnimeFragment: FinishAnimeFragment? = null
    private var weekAnimeFragment: WeekAnimeFragment? = null
    private var current: BaseFragment? = null
    private var optionMenuOn = false  //标示是否要显示optionmenu
    private var aMenu: Menu? = null         //获取optionmenu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_main)
        checkPermissions()
        setSupportActionBar(mainToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        mainToolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(mainNavi)
        }

        weekAnimeFragment = WeekAnimeFragment()
        finishAnimeFragment = FinishAnimeFragment()

        supportFragmentManager.beginTransaction()
                .add(R.id.mainContent, weekAnimeFragment)
                .add(R.id.mainContent, finishAnimeFragment)
                .hide(weekAnimeFragment)
                .show(finishAnimeFragment)
                .commit()
        current = finishAnimeFragment

        mainNavi.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_finish -> {
                    mainTitle?.text = "完结"
                    supportFragmentManager.beginTransaction().hide(current).show(finishAnimeFragment).commit()
                    current = finishAnimeFragment
                    item.isChecked = true
                    optionMenuOn = true
                }
                R.id.navigation_update -> {
                    mainTitle?.text = "更新"
                    supportFragmentManager.beginTransaction().hide(current).show(weekAnimeFragment).commit()
                    current = weekAnimeFragment
                    item.isChecked = true
                    optionMenuOn = false
                }
                R.id.navigation_drama -> {
                    mainTitle?.text = "日剧"
                    item.isChecked = true
                    optionMenuOn = true
                }
            }
            checkOptionMenu()
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.item_menu_search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)//默认为true在框内，设置false则在框外
        searchView.isSubmitButtonEnabled = true
        searchView.isQueryRefinementEnabled = true
        searchView.onActionViewExpanded()// 当展开无输入内容的时候，没有关闭的图标
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                finishAnimeFragment?.filter = query
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                finishAnimeFragment?.filter = newText
                return true
            }
        })
        searchView.queryHint = "输入动漫..."
        aMenu = menu
        return true
    }

    private fun checkOptionMenu() {
        if (null != aMenu) {
            if (optionMenuOn) {
                for (i in 0 until aMenu!!.size()) {
                    aMenu!!.getItem(i)?.isVisible = true
                    aMenu!!.getItem(i)?.isEnabled = true
                }
            } else {
                for (i in 0 until aMenu!!.size()) {
                    aMenu!!.getItem(i)?.isVisible = false
                    aMenu!!.getItem(i)?.isEnabled = false
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(anima: Anima) {
        val intent = Intent(this, AnimeActivity::class.java)
        intent.putExtra(Constants.KEY_INTENT, anima)
        startActivity(intent)
    }

    @AfterPermissionGranted(Constants.REQUEST_CODE)
    fun checkPermissions() {
        if (!EasyPermissions.hasPermissions(this, *param)) {
            EasyPermissions.requestPermissions(this, getString(R.string.setting_request), Constants.REQUEST_CODE, *param)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
