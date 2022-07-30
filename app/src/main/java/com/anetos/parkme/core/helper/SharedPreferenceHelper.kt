package com.anetos.parkme.core.helper

import android.content.Context
import android.content.SharedPreferences
import com.anetos.parkme.Application
import com.anetos.parkme.core.helper.SharedPreferenceHelper.Companion.appPreferences
import com.anetos.parkme.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


/***
 *  created by Jaydeep Bhayani on 16/07/2022
 */
class SharedPreferenceHelper {

    fun getAppSharedPreferences(): SharedPreferences? {
        return Application.context.getSharedPreferences(appPreferences, Context.MODE_PRIVATE)
    }

    fun clearAppPreferences() {
        getAppSharedPreferences()?.edit()?.clear()?.apply()
    }

    fun save(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor? = getAppSharedPreferences()?.edit()
        editor?.putInt(KEY_NAME, value)
        editor?.apply()
    }

    fun saveString(KEY_NAME: String, value: String) {
        val editor: SharedPreferences.Editor? = getAppSharedPreferences()?.edit()
        editor?.putString(KEY_NAME, value)
        editor?.apply()
    }

    fun saveBoolean(KEY_NAME: String, value: Boolean) {
        val editor: SharedPreferences.Editor? = getAppSharedPreferences()?.edit()
        editor?.putBoolean(KEY_NAME, value)
        editor?.apply()
    }

    fun getValueInt(KEY_NAME: String): Int? = getAppSharedPreferences()?.getInt(KEY_NAME, 1)

    fun getValueBoolean(KEY_NAME: String): Boolean? = getAppSharedPreferences()?.getBoolean(KEY_NAME, true)

    fun getValueString(KEY_NAME: String): String? =  getAppSharedPreferences()?.getString(KEY_NAME, null)

    fun saveObjectToSharedPreference(
        serializedObjectKey: String,
        `object`: Any
    ) {
        val sharedPreferencesEditor =  getAppSharedPreferences()?.edit()
        val serializedObject = Gson().toJson(`object`)
        sharedPreferencesEditor?.putString(serializedObjectKey, serializedObject)
        sharedPreferencesEditor?.apply()
    }

    fun <T> getSavedObjectFromPreference(
        preferenceKey: String,
        classType: Type
    ): T? {
        if (getAppSharedPreferences()?.contains(preferenceKey) == true) {
            return Gson().fromJson( getAppSharedPreferences()?.getString(preferenceKey, ""), classType)
        }
        return null
    }

    fun saveAppData(key: String, value: Any) {
        val editor = getAppSharedPreferences()?.edit()
        when (value) {
            is String -> editor?.putString(key, value)
            is Boolean -> editor?.putBoolean(key, value)
            is Float -> editor?.putFloat(key, value)
            is Int -> editor?.putInt(key, value)
            is Long -> editor?.putLong(key, value)
        }
        editor?.apply()
    }

    fun removeAppKey(key: String) {
        val editor = getAppSharedPreferences()?.edit()
        editor?.remove(key)
        editor?.apply()
    }

    fun containsUserKey(key: String): Boolean {
        return getAppSharedPreferences()?.contains(key) ?: false
    }

    fun saveUser(user: User) {
        saveAppData(
            keyUserDetails, Gson().toJson(user).toString()
        )
    }

    fun getUser(): User {
        val str = getValueString(keyUserDetails)
        return Gson().fromJson(str, object : TypeToken<User>() {}.type)
    }

    companion object {
        private const val appPreferences = "app_preferences"
        const val keyFirebaseToken = "firebase_token"
        const val keyUserDetails = "user_details"
        private const val userPreferences = "user_preferences"
    }
}
