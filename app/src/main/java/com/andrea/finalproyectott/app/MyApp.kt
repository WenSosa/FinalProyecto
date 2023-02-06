package com.andrea.finalproyectott.app

import android.app.Application
import com.andrea.finalproyectott.others.MySharedPreferences

val preferences: MySharedPreferences by lazy { MyApp.prefs!!}

class MyApp : Application(){
    companion object{
        var prefs : MySharedPreferences?=null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = MySharedPreferences(applicationContext)
    }
}