package com.clara.timekeeping.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.clara.timekeeping.utils.CommonUtils
import com.clara.timekeeping.utils.PreferenceHelper
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {
    @Provides
    fun provideCommonUtils(preferenceHelper: PreferenceHelper): CommonUtils {
        return CommonUtils(preferenceHelper)
    }

    @Provides
    fun isNetworkConnected(@ApplicationContext context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                @Suppress("DEPRECATION")
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    @Provides
    fun provideCoroutineScope(): CoroutineScope {
        val job = Job()
        val coroutine = job + Dispatchers.IO
        return CoroutineScope(coroutine)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}