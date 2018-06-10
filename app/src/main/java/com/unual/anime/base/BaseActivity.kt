package com.unual.anime.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.unual.anime.AnimeActivity
import com.unual.anime.data.Anima
import com.unual.anime.data.Constant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by Administrator on 2018/5/29.
 */
open class BaseActivity : AppCompatActivity() {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val spf by lazy { getSharedPreferences(Constant.KEY_SPF, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    fun setValue(key: String, value: String) {
        if (!EasyPermissions.hasPermissions(this, *parm)) return
        spf.edit().putString(key, value)?.commit()
    }

    fun getValue(key: String): String {
        if (!EasyPermissions.hasPermissions(this, *parm)) return ""
        return spf.getString(key, "")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(anima: Anima) {
        val intent = Intent(this, AnimeActivity::class.java)
        intent.putExtra(Constant.KEY_INTENT, anima)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}