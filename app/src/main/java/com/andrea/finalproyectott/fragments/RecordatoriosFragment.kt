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
import com.andrea.finalproyectott.adapters.RecordatorioAdapter
import com.andrea.finalproyectott.databinding.FragmentRecordatoriosBinding
import com.andrea.finalproyectott.models.Recordatorio
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList

class RecordatoriosFragment : Fragment() {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var recordatorioDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : RecordatorioAdapter
    private val listarecordatorio : ArrayList<Recordatorio> = ArrayList()

    private var recordatorioSubscription: ListenerRegistration? = null


    private var _binding: FragmentRecordatoriosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentRecordatoriosBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpRecordatorioDB()
        setUpCurrentUser()

        setUpReciclerView()
        subscribeToRecordatorio()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding.btnAddEvent.setOnClickListener(){
            if (!binding.etTitle.getText().toString().isEmpty()  && !binding.etDesc.getText().toString().isEmpty()) {
                val intent = Intent(Intent.ACTION_INSERT)
                intent.setData(CalendarContract.Events.CONTENT_URI)
                intent.putExtra(CalendarContract.Events.TITLE,binding.etTitle.getText().toString())
                intent.putExtra(CalendarContract.Events.DESCRIPTION,binding.etDesc.getText().toString())
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION,"")
                intent.putExtra(CalendarContract.Events.ALL_DAY,"true")
                intent.putExtra(Intent.EXTRA_EMAIL,"")
                    startActivity(intent)

            }else{
                requireActivity().toast("Vacio")
            }
            val recordatorioText = binding.etTitle.text.toString()
            val recordatorioDesc = binding.etDesc.text.toString()
            if (recordatorioText.isNotEmpty()){
            val recordatorio = Recordatorio(currentUser.uid,recordatorioText, recordatorioDesc)
            saveRecordatorio(recordatorio)
            limpiarCampos()

            }else{
                requireActivity().toast("Añade datos a los campos")

            }
        }

        return view
    }


    private fun saveRecordatorio(recordatorio: Recordatorio) {
        val newRecordatorio = HashMap<String, Any>()
        newRecordatorio["Userid"] = recordatorio.Userid
        newRecordatorio["Nombre"] = recordatorio.Nombre
        newRecordatorio["Descripcion"] = recordatorio.Descripcion
        recordatorioDBRef.add(newRecordatorio)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
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

    private fun limpiarCampos() {
        binding.etTitle.setText("")
        binding.etDesc.setText("")

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
