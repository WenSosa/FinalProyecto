package com.andrea.finalproyectott.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.adapters.RecordatorioAdapter
import com.andrea.finalproyectott.databinding.FragmentRecordatoriosBinding
import com.andrea.finalproyectott.databinding.FragmentRecordatoriosCuidadorBinding
import com.andrea.finalproyectott.models.Recordatorio
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

class RecordatoriosCuidadorFragment : Fragment() {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recordatorioDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : RecordatorioAdapter
    private val listarecordatorio : ArrayList<Recordatorio> = ArrayList()

    private var recordatorioSubscription: ListenerRegistration? = null


    private var _binding: FragmentRecordatoriosCuidadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentRecordatoriosCuidadorBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpRecordatorioDB()
        setUpCurrentUser()

        setUpReciclerView()
        subscribeToRecordatorio()


        return view
    }

    fun setUpRecordatorioDB(){
        recordatorioDBRef =store.collection("Recordatorio")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    fun setUpReciclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = RecordatorioAdapter(listarecordatorio)
        binding.recyclerViewRecordatorio.setHasFixedSize(true)
        binding.recyclerViewRecordatorio.layoutManager = layoutManager
        binding.recyclerViewRecordatorio.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewRecordatorio.adapter =adapter
    }

    fun subscribeToRecordatorio(){
        recordatorioSubscription = recordatorioDBRef
            .whereEqualTo("Userid",currentUser.uid)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        //activity!!.toast(exception.message.toString())
                        return
                    }

                    snapshot?.let {
                        listarecordatorio.clear()
                        val recordatorio = it.toObjects(Recordatorio::class.java)
                        listarecordatorio.addAll(recordatorio)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewRecordatorio.smoothScrollToPosition(0)
                    }
                }
            })
    }

    override fun onDestroyView() {
        recordatorioSubscription?.remove()
        super.onDestroyView()
    }


}