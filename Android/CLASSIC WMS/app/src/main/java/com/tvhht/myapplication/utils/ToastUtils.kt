package com.tvhht.myapplication.utils


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.tvhht.myapplication.R


public class ToastUtils {


    companion object {


        public fun showToast(context: Context,message: String?) {
            val toast = Toast(context)
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.toast_layout, null)
            val tvMessage: TextView = view.findViewById(R.id.tvMessage)
            tvMessage.text = message
            toast.view = view
            toast.show()
        }
    }
}