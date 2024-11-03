package com.example.pemesanantiket

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

class SessionManager(private val context: Context) {
    private val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = pref.edit()

    companion object {
        private const val PREF_NAME = "UserSessionPref"
        private const val IS_LOGIN = "IsLoggedIn"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_USERNAME = "username"
    }

    fun createLoginSession(email: String, name: String, username: String) {
        editor.apply {
            putBoolean(IS_LOGIN, true)
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            putString(KEY_USERNAME, username)
            apply()
        }
    }

    fun checkLogin() {
        if (!isLoggedIn) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    fun getUserDetails(): Map<String, String?> {
        return mapOf(
            KEY_EMAIL to pref.getString(KEY_EMAIL, null),
            KEY_NAME to pref.getString(KEY_NAME, null),
            KEY_USERNAME to pref.getString(KEY_USERNAME, null)
        )
    }

    fun logoutUser() {
        editor.clear()
        editor.apply()

        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    val isLoggedIn: Boolean
        get() = pref.getBoolean(IS_LOGIN, false)
}