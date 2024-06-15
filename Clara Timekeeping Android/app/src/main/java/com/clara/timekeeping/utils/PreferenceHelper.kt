package com.clara.timekeeping.utils

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun setUserId(id: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_USER_ID, id)
            apply()
        }
    }

    fun getUserId(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_USER_ID, "") ?: ""
    }

    fun setLoginDetails(loginDetails: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_LOGIN_DETAILS, loginDetails)
            apply()
        }
    }

    fun getLoginDetails(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_LOGIN_DETAILS, "") ?: ""
    }

    fun setTablet(isTablet: Boolean) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putBoolean(PREFERENCES_KEY_TABLET, isTablet)
            apply()
        }
    }

    fun isTablet(): Boolean {
        return sharedPreferences.getBoolean(PREFERENCES_KEY_TABLET, false)
    }

    fun setNotificationCount(count: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putInt(PREFERENCES_KEY_NOTIFICATION_COUNT, count)
            apply()
        }
    }

    fun getNotificationCount(): Int {
        return sharedPreferences.getInt(PREFERENCES_KEY_NOTIFICATION_COUNT, 0)
    }

    fun setFCMToken(token: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_FCM_TOKEN, token)
            apply()
        }
    }

    fun getFCMToken(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_FCM_TOKEN, "") ?: ""
    }

    fun clearSharedPreference() {
        val editor = sharedPreferences.edit()
        editor.remove(PREFERENCES_KEY_USER_ID)
        editor.remove(PREFERENCES_KEY_LOGIN_DETAILS)
        editor.remove(PREFERENCES_KEY_TABLET)
        editor.remove(PREFERENCES_KEY_NOTIFICATION_COUNT)
        editor.remove(PREFERENCES_KEY_FCM_TOKEN)
        editor.apply()
        editor.clear()
    }

    companion object {
        const val PREFERENCES_NAME = "time_ticket"
        private const val PREFERENCES_KEY_USER_ID = "user_id"
        private const val PREFERENCES_KEY_LOGIN_DETAILS = "login_details"
        private const val PREFERENCES_KEY_TABLET = "tablet"
        private const val PREFERENCES_KEY_NOTIFICATION_COUNT = "notification_count"
        private const val PREFERENCES_KEY_FCM_TOKEN = "fcm_token"
    }
}