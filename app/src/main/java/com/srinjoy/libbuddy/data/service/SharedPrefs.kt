package com.srinjoy.libbuddy.data.service

import android.content.Context
import android.content.SharedPreferences
import com.srinjoy.libbuddy.core.Constants

class SharedPrefs(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(Constants.APP_PREFS, Context.MODE_PRIVATE)

    var isStudentLoggedIn: Boolean
        get() = preferences.getBoolean(Constants.STUDENT_LOGIN_KEY, false)
        set(value) = preferences.edit().putBoolean(Constants.STUDENT_LOGIN_KEY, value).apply()

    var isAdminLoggedIn: Boolean
        get() = preferences.getBoolean(Constants.ADMIN_LOGIN_KEY, false)
        set(value) = preferences.edit().putBoolean(Constants.ADMIN_LOGIN_KEY, value).apply()

    var token: String?
        get() = preferences.getString(Constants.TOKEN_KEY, null)
        set(value) = preferences.edit().putString(Constants.TOKEN_KEY, value).apply()

    var loadBooks: Boolean
        get() = preferences.getBoolean(Constants.LOAD_BOOKS_KEY, false)
        set(value) = preferences.edit().putBoolean(Constants.LOAD_BOOKS_KEY, value).apply()
}