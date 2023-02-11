package com.andrea.finalproyectott.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.andrea.finalproyectott.MainActivity
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.ActivityGraficoNivelesBinding
import com.andrea.finalproyectott.goToActivity
import com.andrea.finalproyectott.models.NivelGlucosa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.github.farshidroohi.ChartEntity
import io.github.farshidroohi.LineChart
import java.util.*
import java.util.EventListener

class GraficoNivelesActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivityGraficoNivelesBinding
    private lateinit var nivelesDBRef: CollectionReference
    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var currentUser: FirebaseUser


    private val listaniveles : ArrayList<NivelGlucosa> = ArrayList()
    private val chartArrayN : ArrayList<Float> = ArrayList()

    private val chartArrayF : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraficoNivelesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUpCurrentUser()
        setUpProfileDB()
        subscribeToNiveles()


        binding.botonVolver.setOnClickListener {
            goToActivity<MainActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

    }

    fun setUpProfileDB(){
        nivelesDBRef =store.collection("Niveles_glucosa")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun subscribeToNiveles(){

        nivelesDBRef
            .whereEqualTo("Userid",currentUser.uid)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        return
                    }
                    snapshot?.let {
                        listaniveles.clear()
                        val nivel = it.toObjects(NivelGlucosa::class.java)
                        listaniveles.addAll(nivel)
                        val size = listaniveles.size.toInt()
                        var arr =FloatArray(size)
                        for(i in 0..size-1) {
                            chartArrayN.add(listaniveles[i].Nivel.toFloat())
                            arr[i] = listaniveles[i].Nivel.toFloat();
                            chartArrayF.add(listaniveles[i].Fecha)
                            Log.d("Alo",listaniveles[i].Nivel+","+listaniveles[i].Fecha)

                            val firstChartEntity = ChartEntity(Color.WHITE, arr)

                            val list = ArrayList<ChartEntity>().apply {
                                add(firstChartEntity)
                            }

                            val lineChart = findViewById<LineChart>(R.id.lineChart)
                            lineChart.setLegend(chartArrayF)
                            lineChart.setList(list)
                        }
                    }
                }
            })

    }

}


