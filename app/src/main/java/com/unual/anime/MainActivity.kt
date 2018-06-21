package com.unual.anime

import android.Manifest
import android.content.Intent
import android.os.Bundle
import com.unual.anime.base.BaseActivity
import com.unual.anime.base.BaseFragment
import com.unual.anime.data.entity.Anima
import com.unual.anime.utils.Constants
import com.unual.anime.ui.AnimeActivity
import com.unual.anime.ui.FinishAnimeFragment
import com.unual.anime.ui.WeekAnimeFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity : BaseActivity() {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private var finishAnimeFragment: BaseFragment? = null
    private var weekAnimeFragment: BaseFragment? = null
    private var current: BaseFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setContentView(R.layout.activity_main)
        checkPermissions()
        setSupportActionBar(mainToolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                    mainTitle.text = "完结"
                    supportFragmentManager.beginTransaction().hide(current).show(finishAnimeFragment).commit()
                    current = finishAnimeFragment
                    item.isChecked = true
                }
                R.id.navigation_update -> {
                    mainTitle.text = "更新"
                    supportFragmentManager.beginTransaction().hide(current).show(weekAnimeFragment).commit()
                    current = weekAnimeFragment
                    item.isChecked = true
                }
                R.id.navigation_drama -> {
                    mainTitle.text = "日剧"
                    item.isChecked = true
                }
            }
            drawerLayout.closeDrawers()
            true
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
        if (!EasyPermissions.hasPermissions(this, *parm)) {
            EasyPermissions.requestPermissions(this, getString(R.string.setting_request), Constants.REQUEST_CODE, *parm)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
