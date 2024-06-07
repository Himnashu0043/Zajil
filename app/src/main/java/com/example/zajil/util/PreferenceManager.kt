package com.example.zajil.util

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class PreferenceManager(application: Application) {

    companion object {
        private const val PREF_NAME = "ZazelPreferences"
    }

    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        sharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    var SELECTED_LANGUAGE
        get() = sharedPreferences.getString("SELECTED_LANGUAGE", "en") ?: "en"
        set(value) {
            editor.putString("SELECTED_LANGUAGE", value).apply()
        }
    var LATITUDE
        get() = (sharedPreferences.getString("LATITUDE", "0.0") ?: "0.0").toDouble()
        set(value) {
            editor.putString("LATITUDE", value.toString()).apply()
        }

    var LONGITUDE
        get() = (sharedPreferences.getString("LONGITUDE", "0.0") ?: "0.0").toDouble()
        set(value) {
            editor.putString("LONGITUDE", value.toString()).apply()
        }

    var user
        get() = kotlin.run {
            val userString = sharedPreferences.getString(Constants.USER, "") ?: ""
            if (userString.isEmpty())
                null
            else Gson().fromJson(userString, UserData::class.java)
        }
        set(value) {
            editor.putString(Constants.USER, Gson().toJson(value)).apply()
        }

    var token
        get() = sharedPreferences.getString(Constants.TOKEN, "") ?: ""
        set(value) {
            editor.putString(Constants.TOKEN, value).apply()
        }

    var deviceToken
        get() = sharedPreferences.getString(Constants.DEVICE_TOKEN, "") ?: ""
        set(value) {
            editor.putString(Constants.DEVICE_TOKEN, value).apply()
        }



    fun logout() {
        val lang = SELECTED_LANGUAGE
        editor.clear()
        editor.apply()
        SELECTED_LANGUAGE = lang
    }
}