package com.andrea.finalproyectott.activities

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.models.Insulina

class GraficosActivity : AppCompatActivity() {
//https://medium.com/@yilmazvolkan/kotlinlinecharts-c2a730226ff1

    val arreglo = ArrayList<Insulina>()
    val xAxisLabel  = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficos)
    }
}
