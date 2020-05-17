package com.example.mmolinca20alumnes.veggiemetrics.adapters

import android.content.Context

class sharedprefs {
    // 1
    fun storeTutorialStatus1(context: Context, show: Boolean) {
        val preferences = context.getSharedPreferences("showTutorial1", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("show", show)
        editor.apply()
    }

    fun storeTutorialStatus2(context: Context, show: Boolean) {
        val preferences = context.getSharedPreferences("showTutorial2", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("show", show)
        editor.apply()
    }

    // 2
    fun getTutorialStatus1(context: Context): Boolean {
        val preferences = context.getSharedPreferences("showTutorial1", Context.MODE_PRIVATE)
        return preferences.getBoolean("show", false)
    }

    // 2
    fun getTutorialStatus2(context: Context): Boolean {
        val preferences = context.getSharedPreferences("showTutorial2", Context.MODE_PRIVATE)
        return preferences.getBoolean("show", false)
    }
}