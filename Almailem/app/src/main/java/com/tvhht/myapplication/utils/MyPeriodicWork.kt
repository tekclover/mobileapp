package com.tvhht.myapplication.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.material.internal.ContextUtils.getActivity
import com.tvhht.myapplication.R
import com.tvhht.myapplication.login.LoginLiveData
import com.tvhht.myapplication.login.model.LoginModel
import com.tvhht.myapplication.quality.viewmodel.QualityLiveData
import com.tvhht.myapplication.utils.MyPeriodicWork
import kotlinx.android.synthetic.main.activity_quality_details_confirm.*
import java.util.*

class MyPeriodicWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    @SuppressLint("RestrictedApi")

    override fun doWork(): Result {
        Log.e(TAB, "PeriodicWork in BackGround")
        // Getting Data from MainActivity.
        val title = inputData.getString(EXTRA_TITLE)
        val text = inputData.getString(EXTRA_TEXT)


        if (NetworkUtils().haveNetworkConnection(applicationContext)) {
           //verifyTokenNew()



                val request = WMSApplication.getInstance().submitLocalRepository.qualityList()


                if (request.value!!.size > 0) {

                    Log.e(TAB, "PeriodicWok Size greater than 0")


                }
                else
                {
                    Log.e(TAB, "PeriodicWok Size 0")
                }


        }



        val output = Data.Builder()
            .putString(EXTRA_OUTPUT_MESSAGE, "Record pushed to Online Database")
            .build()

        return Result.success(output)
    }

    private fun sendNotification(title: String?, message: String?) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //If on Oreo then notification required a notification channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, "default")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
        notificationManager.notify(1, notification.build())
    }

    companion object {
        private val TAB = MyPeriodicWork::class.java.simpleName
        const val EXTRA_TITLE = "title"
        const val EXTRA_TEXT = "text"
        const val EXTRA_OUTPUT_MESSAGE = "output_message"
    }

    @SuppressLint("RestrictedApi")
    private fun verifyTokenNew() {
        val activity = getActivity(applicationContext) as AppCompatActivity


        var request = LoginModel(
            ApiConstant.apiName_transaction,
            ApiConstant.clientId,
            ApiConstant.clientSecretKey,
            ApiConstant.grantType,
            ApiConstant.apiName_name,
            ApiConstant.apiName_pass_key
        )
          val model = ViewModelProvider(Objects.requireNonNull(activity))[LoginLiveData::class.java]
        model.getLoginStatus(request).observe(Objects.requireNonNull(applicationContext as AppCompatActivity)) { asnList ->
            // update UI
            if (asnList.equals("ERROR")) {
                ToastUtils.showToast(applicationContext, "User not Available")
            } else {
                if (asnList.equals("FAILED")) {
                    ToastUtils.showToast(applicationContext, "No Data Available")
                } else {

                    createNewRecord()

                }
            }

        }


    }

    @SuppressLint("RestrictedApi")
    private fun createNewRecord() {
        val activity = getActivity(applicationContext) as AppCompatActivity

        val model = ViewModelProvider(Objects.requireNonNull(activity))[QualityLiveData::class.java]


        Handler().postDelayed({

            val request = WMSApplication.getInstance().submitLocalRepository.qualityList()


            if (request.value!!.size > 0) {
                model.createData(request.value!!).observe(Objects.requireNonNull(applicationContext as AppCompatActivity)) { vDataList ->
                    // update UI


                    if (vDataList != null && vDataList.isNotEmpty()) {
                        sendNotification("", "")
                    } else {
                        sendNotification("", "")
                    }
                }


            }

        }, 2000)


    }
}