package com.clara.timekeeping.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import com.clara.timekeeping.R
import com.clara.timekeeping.model.request.AuthTokenRequest
import com.clara.timekeeping.ui.login.LoginActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit
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

    fun convertStringDateToDate(stringDate: String, dateFormat: String): Date? {
        val inputFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
        try {
            inputFormat.parse(stringDate)?.let {
                return it
            }
        } catch (e: ParseException) {
            return null
        }
        return null
    }

    fun showLogout(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.resources.getString(R.string.logout_title))
        builder.setMessage(context.resources.getString(R.string.logout_message))

        builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialogInterface, _ ->
            preferenceHelper.clearSharedPreference()
            dialogInterface.dismiss()
            context.startActivity(Intent(context, LoginActivity::class.java))
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

    fun authTokenRequest(apiName: String): AuthTokenRequest {
        return AuthTokenRequest(
            Constants.CLIENT_ID,
            Constants.CLIENT_SECRET_KEY,
            Constants.PASSWORD,
            Constants.OAUTH_USER_NAME,
            Constants.OAUTH_PASSWORD,
            apiName
        )
    }

    fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    fun getDateTime(format: String): String? {
        return try {
            val dateFormat = SimpleDateFormat(format, Locale.getDefault()).apply {
                timeZone = TimeZone.getDefault()
            }
            dateFormat.format(Date())
        } catch (e: Exception) {
            ""
        }
    }

    fun hideKeyboard(context: Context?, view: View?) {
        context?.let {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view?.let { v ->
                inputMethodManager.let {
                    if (it.isAcceptingText) {
                        it.hideSoftInputFromWindow(
                            v.windowToken,
                            0
                        )
                    }
                }
            }
        }
    }

    fun enableDisableViews(viewGroup: ViewGroup, isEnable: Boolean) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val child = viewGroup.getChildAt(i)

            child.isEnabled = isEnable

            if (child is ViewGroup) {
                enableDisableViews(child, isEnable)
            }
        }
    }
}