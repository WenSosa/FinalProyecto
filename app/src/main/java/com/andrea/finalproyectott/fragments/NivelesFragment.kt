package com.andrea.finalproyectott.fragments

import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.MainActivity
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.activities.GraficoNivelesActivity
import com.andrea.finalproyectott.adapters.NivelesGlucosaAdapter
import com.andrea.finalproyectott.databinding.FragmentInsulinaBinding
import com.andrea.finalproyectott.databinding.FragmentNivelesBinding
import com.andrea.finalproyectott.goToActivity
import com.andrea.finalproyectott.models.NivelGlucosa
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NivelesFragment : Fragment() {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var nivelesDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : NivelesGlucosaAdapter
    private val listaniveles : ArrayList<NivelGlucosa> = ArrayList()

    private var nivelesSubscription: ListenerRegistration? = null

    private var _binding: FragmentNivelesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.niveles_fragment_string)
        _binding = FragmentNivelesBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpProfileDB()
        setUpCurrentUser()

        setUpReciclerView()
        setUpNvBtn()
        setUpNvBtnGrp()
        subscribeToNiveles()


        return view
    }

    fun setUpProfileDB(){
        nivelesDBRef =store.collection("Niveles_glucosa")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    fun setUpReciclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = NivelesGlucosaAdapter(listaniveles)
        binding.recyclerViewNiveles.setHasFixedSize(true)
        binding.recyclerViewNiveles.layoutManager = layoutManager
        binding.recyclerViewNiveles.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewNiveles.adapter =adapter
    }

    private fun setUpNvBtn(){
        binding.botonGuardarNivel.setOnClickListener(){
            val nivelText = binding.editTextNivelGlucosa.text.toString()
            if (nivelText.isNotEmpty()){
                val nivel = NivelGlucosa(currentUser.uid, nivelText, Date().toString())
                saveNivel(nivel)
                binding.editTextNivelGlucosa.setText("")
            }else{
                requireActivity().toast("Vacio")
            }
        }
    }

    private fun setUpNvBtnGrp(){
        binding.botonVerGraficaNiveles.setOnClickListener(){

            val intent = Intent(activity, GraficoNivelesActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    private fun saveNivel(nivel : NivelGlucosa){
        val newNivel = HashMap<String, Any>()
        newNivel["Userid"] = nivel.Userid
        newNivel["Nivel"] = nivel.Nivel
        newNivel["Fecha"] = nivel.Fecha
        nivelesDBRef.add(newNivel)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    private fun subscribeToNiveles(){
        nivelesSubscription = nivelesDBRef
            .whereEqualTo("Userid",currentUser.uid)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        //activity!!.toast(exception.message.toString())
                        return
                    }

                    snapshot?.let {
                        listaniveles.clear()
                        val nivel = it.toObjects(NivelGlucosa::class.java)
                        listaniveles.addAll(nivel)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewNiveles.smoothScrollToPosition(0)
                    }
                }
            })
    }

    override fun onDestroyView() {
        nivelesSubscription?.remove()
        super.onDestroyView()
    }

}