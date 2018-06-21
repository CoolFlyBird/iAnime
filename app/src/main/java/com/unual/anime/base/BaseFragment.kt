package com.unual.anime.base

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxFragment
import com.unual.anime.data.Anima
import com.unual.anime.data.Constants
import com.unual.anime.ui.AnimeActivity
import com.unual.anime.widget.DialogUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by unual on 2018/6/14.
 */
open abstract class BaseFragment : RxFragment(), IBaseView {
    private val parm = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    private val spf by lazy { context?.getSharedPreferences(Constants.KEY_SPF, Context.MODE_PRIVATE) }
    private val loading by lazy { DialogUtils.getLoadingDialog(context) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var mRootView: View? = null
        if (mRootView != null) {
            val parent = mRootView.parent as ViewGroup
            parent.removeView(mRootView)
        } else {
            mRootView = inflater.inflate(getLayoutId(), container, false)
            initView(mRootView)
            initData()
        }
        return mRootView
    }

    fun setValue(key: String, value: String) {
        if (context == null) return
        if (!EasyPermissions.hasPermissions(context!!, *parm)) return
        spf?.edit()?.putString(key, value)?.commit()
    }

    fun getValue(key: String): String {
        if (context == null) return ""
        if (!EasyPermissions.hasPermissions(context!!, *parm)) return ""
        return spf?.getString(key, "") ?: ""
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(anima: Anima) {
        val intent = Intent(context, AnimeActivity::class.java)
        intent.putExtra(Constants.KEY_INTENT, anima)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    open fun initView(view: View) {

    }

    open fun initData() {
    }

    override fun showLoading() {
        loading?.show()
    }

    override fun closeLoading() {
        loading?.dismiss()
    }

    override fun showToast(msg: String) {
        Log.e("TAG", "BaseFragment -> showToast($msg)")
    }

    protected abstract fun getLayoutId(): Int
}