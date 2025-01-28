package com.example.tickticketing.utils

import android.app.Activity
import android.app.AlertDialog
import com.example.tickticketing.R

class Loader(val activity : Activity) {
    lateinit var alertDialog: AlertDialog

    fun show(){
        val builder = AlertDialog.Builder(activity)

        val dialogView = activity.layoutInflater.inflate(R.layout.loading,null)
        builder.setView(dialogView)
        builder.setCancelable(false)

        alertDialog = builder.create()
        alertDialog.show()

    }

    fun dismiss(){
        alertDialog.dismiss()
    }
}