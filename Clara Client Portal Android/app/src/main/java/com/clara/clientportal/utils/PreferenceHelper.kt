package com.clara.clientportal.utils

import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceHelper @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun setClientId(id: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_CLIENT_ID, id)
            apply()
        }
    }

    fun getClientId(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_CLIENT_ID, "") ?: ""
    }

    fun setClientGeneralDetails(clientDetails: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_CLIENT_DETAILS, clientDetails)
            apply()
        }
    }

    fun getClientDetails(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_CLIENT_DETAILS, "") ?: ""
    }

    fun setStatus(status: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_STATUS, status)
            apply()
        }
    }

    fun getClassId(): Int {
        return sharedPreferences.getInt(PREFERENCES_KEY_CLASS_ID, -1)
    }

    fun setClassId(id: Int) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putInt(PREFERENCES_KEY_CLASS_ID, id)
            apply()
        }
    }

    fun getStatus(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_STATUS, "") ?: ""
    }

    fun setClientUserId(userId: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        with(editor) {
            putString(PREFERENCES_KEY_CLIENT_USER_ID, userId)
            apply()
        }
    }

    fun getClientUserId(): String {
        return sharedPreferences.getString(PREFERENCES_KEY_CLIENT_USER_ID, "") ?: ""
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


    fun clearSharedPreference() {
        val editor = sharedPreferences.edit()
        editor.remove(PREFERENCES_KEY_CLIENT_ID)
        editor.remove(PREFERENCES_KEY_CLIENT_DETAILS)
        editor.remove(PREFERENCES_KEY_CLASS_ID)
        editor.remove(PREFERENCES_KEY_CLIENT_USER_ID)
        editor.remove(PREFERENCES_KEY_TABLET)
        editor.remove(PREFERENCES_KEY_FCM_TOKEN)
        editor.remove(PREFERENCES_KEY_NOTIFICATION_COUNT)
        editor.apply()
        editor.clear()
    }

    companion object {
        const val PREFERENCES_NAME = "client_portal"
        private const val PREFERENCES_KEY_CLIENT_ID = "client_id"
        private const val PREFERENCES_KEY_CLIENT_DETAILS = "client_details"
        private const val PREFERENCES_KEY_STATUS = "status_details"
        private const val PREFERENCES_KEY_CLASS_ID = "class_id"
        private const val PREFERENCES_KEY_CLIENT_USER_ID = "client_user_id"
        private const val PREFERENCES_KEY_TABLET = "tablet"
        private const val PREFERENCES_KEY_FCM_TOKEN = "fcm_token"
        private const val PREFERENCES_KEY_NOTIFICATION_COUNT = "notification_count"
    }
}