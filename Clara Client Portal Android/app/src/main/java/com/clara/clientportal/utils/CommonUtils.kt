package com.clara.clientportal.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.util.Patterns
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import com.clara.clientportal.R
import com.clara.clientportal.enums.NotificationOrderType
import com.clara.clientportal.model.StatusResponse
import com.clara.clientportal.model.request.SetupServiceAuthTokenRequest
import com.clara.clientportal.ui.signin.SignInActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


class CommonUtils @Inject constructor(private val preferenceHelper: PreferenceHelper) {

    fun showToastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun expandView(view: View?) {
        view?.let { v ->
            val matchParentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
            val wrapContentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = v.measuredHeight
            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height =
                        if (interpolatedTime == 1f) ConstraintLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt()
                .toLong() // Expansion speed of 1dp/ms
            v.startAnimation(a)
        }
    }

    fun collapseView(view: View?) {
        view?.let { v ->
            val initialHeight = v.measuredHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }
            a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt()
                .toLong() // Collapse speed of 1dp/ms
            v.startAnimation(a)
        }
    }

    fun formatDate(date: String, inputDateFormat: String, outputDateFormat: String): String {
        val inputFormat = SimpleDateFormat(inputDateFormat, Locale.getDefault())
        val outputFormat = SimpleDateFormat(outputDateFormat, Locale.getDefault())
        var formatDate = ""
        try {
            val d = inputFormat.parse(date)
            d?.let {
                formatDate = outputFormat.format(it)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formatDate
    }

    fun getStatus(statusId: Int): String {
        try {
            val type = object : TypeToken<List<StatusResponse?>?>() {}.type
            val statusList: List<StatusResponse> =
                Gson().fromJson(preferenceHelper.getStatus(), type)
            if (statusList.isNotEmpty()) {
                val status = statusList.filter { statusId == it.statusId }
                return status[0].status ?: ""
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    fun authTokenRequest(apiName: String): SetupServiceAuthTokenRequest {
        return SetupServiceAuthTokenRequest(
            Constants.CLIENT_ID,
            Constants.CLIENT_SECRET_KEY,
            Constants.GRAND_TYPE,
            Constants.OAUTH_USER_NAME,
            Constants.OAUTH_PASSWORD,
            apiName
        )
    }

    fun getAppVersion(context: Context?): String {
        var version = ""
        context?.let {
            it.packageManager?.let { it1 ->
                try {
                    val packageInfo =
                        it1.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES)
                    version = packageInfo.versionName ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return version
    }

    fun formatDate(dateFormat: String): SimpleDateFormat {
        return SimpleDateFormat(dateFormat, Locale.getDefault())
    }

    fun isValidEmail(value: String): Boolean {
        var splitValue: Array<String>? = null
        if (value.contains("@")) splitValue =
            value.split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (Patterns.EMAIL_ADDRESS.matcher(value).matches()
                .not() || value.startsWith(".") || value.contains(
                "+"
            ) || splitValue?.first()?.endsWith(".") == true || value.contains("..")
        ) {
            return false
        }
        return true
    }
    fun showLogout(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.logout_title))
        builder.setMessage(context.resources.getString(R.string.logout_message))

        builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialogInterface, _ ->
            preferenceHelper.clearSharedPreference()
            dialogInterface.dismiss()
            context.startActivity(Intent(context, SignInActivity::class.java))
            finishAffinity(context as Activity)
        }

        builder.setNegativeButton(context.resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.color_devil_blue))
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context, R.color.color_devil_blue))
    }
    fun isTablet(tag: String?): Boolean {
        tag?.let {
            return it == Constants.TABLET
        }
        return false
    }
    fun getOrderType(): List<String> {
        val orderTypeList: ArrayList<String> = arrayListOf()
        orderTypeList.add(NotificationOrderType.MATTER.orderType)
        orderTypeList.add(NotificationOrderType.INITIAL.orderType)
        orderTypeList.add(NotificationOrderType.INVOICE.orderType)
        orderTypeList.add(NotificationOrderType.PAYMENT_PLAN.orderType)
        orderTypeList.add(NotificationOrderType.CHECKLIST.orderType)
        orderTypeList.add(NotificationOrderType.DOCUMENT_UPLOAD.orderType)
        orderTypeList.add(NotificationOrderType.RECEIPT.orderType)
        return orderTypeList
    }
}