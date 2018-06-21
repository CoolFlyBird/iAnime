package com.unual.anime.widget

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import com.unual.anime.R


/**
 * Created by Administrator on 2018/6/21.
 */
object DialogUtils {
    fun getLoadingDialog(context: Context?): AlertDialog? {
        var dialog: AlertDialog? = null
        if (context != null) {
            var view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null)
            dialog = AlertDialog.Builder(context, R.style.AppTheme_Transparent)
                    .setCancelable(false)
                    .setView(view)
                    .create()
        }
        return dialog
    }
}