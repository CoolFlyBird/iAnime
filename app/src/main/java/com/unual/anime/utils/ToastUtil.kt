package com.unual.anime.utils

import android.widget.Toast
import com.unual.anime.App

/**
 * Created by unual on 2018/6/21.
 */
object ToastUtil {
    private var toast: Toast? = null
    fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(App.context, msg, Toast.LENGTH_SHORT)
        }
        toast?.setText(msg)
        toast?.show()
    }
}