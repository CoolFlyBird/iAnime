package com.unual.anime.base

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.unual.anime.data.Constants
import com.unual.anime.widget.DialogUtils
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by Administrator on 2018/5/29.
 */
open class BaseActivity : RxAppCompatActivity(), IBaseView {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val spf by lazy { getSharedPreferences(Constants.KEY_SPF, Context.MODE_PRIVATE) }
    private val loading by lazy { DialogUtils.getLoadingDialog(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setValue(key: String, value: String) {
        if (!EasyPermissions.hasPermissions(this, *parm)) return
        spf.edit().putString(key, value)?.commit()
    }

    fun getValue(key: String): String {
        if (!EasyPermissions.hasPermissions(this, *parm)) return ""
        return spf.getString(key, "")
    }

    override fun showLoading() {
        loading?.show()
    }

    override fun closeLoading() {
        loading?.dismiss()
    }

    override fun showToast(msg: String) {
        Log.e("TAG", "BaseActivity -> showToast($msg)")
    }


    override fun onDestroy() {
        super.onDestroy()
    }
}