package com.andrea.finalproyectott.others

import android.content.Context

class MySharedPreferences (context : Context){
    private  val fileName = "mis_preferencias"
    private  val prefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE)

    var nombreLlamada : String?
        get() = prefs.getString("nombreLlamada","")
        set(value) = prefs.edit().putString("nombreLlamada",value).apply()
    var numeroLlamada : String?
        get() = prefs.getString("numeroLlamada","")
        set(value) = prefs.edit().putString("numeroLlamada",value).apply()
    var nombreMensaje : String?
        get() = prefs.getString("nombreMensaje","")
        set(value) = prefs.edit().putString("nombreMensaje",value).apply()
    var numeroMensaje : String?
        get() = prefs.getString("numeroMensaje","")
        set(value) = prefs.edit().putString("numeroMensaje",value).apply()

}