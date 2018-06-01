package com.unual.anima.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.unual.anima.AnimaActivity
import com.unual.anima.data.Anima
import com.unual.anima.data.Constant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2018/5/29.
 */
open class BaseActivity : AppCompatActivity() {
    private val spf by lazy { getSharedPreferences(Constant.KEY_SPF, Context.MODE_PRIVATE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    fun setValue(key: String, value: String) {
        spf.edit().putString(key, value)
    }

    fun getValue(key: String): String {
        return spf.getString(key, "")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(anima: Anima) {
        val intent = Intent(this, AnimaActivity::class.java)
        intent.putExtra(Constant.KEY_INTENT, anima)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}