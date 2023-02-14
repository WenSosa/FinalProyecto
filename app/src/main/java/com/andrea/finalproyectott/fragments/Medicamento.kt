package com.andrea.finalproyectott.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.adapters.MedicamentosAdapter
import com.andrea.finalproyectott.databinding.FragmentMedicamentoBinding
import com.andrea.finalproyectott.models.Medicina
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Medicamento : Fragment()  {


    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var medicamentoDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : MedicamentosAdapter
    private val listamedicina : ArrayList<Medicina> = ArrayList()

    private var medicamentoSubscription: ListenerRegistration? = null

    private var _binding: FragmentMedicamentoBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.medicamento_fragment_string)
        _binding = FragmentMedicamentoBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpInsulinaDB()
        setUpCurrentUser()

        setUpReciclerView()
        setUpMedicamentoBtn()
        subscribeToMedicina()

        return view
    }

    fun setUpInsulinaDB(){
        medicamentoDBRef =store.collection("Registro_medicamento")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpReciclerView() {
        val layoutManager = LinearLayoutManager(context)
        adapter = MedicamentosAdapter(listamedicina)
        binding.recyclerViewMedicamento.setHasFixedSize(true)
        binding.recyclerViewMedicamento.layoutManager = layoutManager
        binding.recyclerViewMedicamento.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewMedicamento.adapter =adapter
    }

    fun setUpMedicamentoBtn(){
        binding.botonGuardarMedicacion.setOnClickListener(){
            val medicinaText = binding.editTextMedicamento.text.toString()
            val dosisIngerida = binding.editTextDosis.text.toString()
            if (medicinaText.isNotEmpty()){
                val nivel = Medicina(currentUser.uid, medicinaText,  dosisIngerida, Date().toString())
                saveMedicina(nivel)
                binding.editTextMedicamento.setText("")
                binding.editTextDosis.setText("")
            }else{
                requireActivity().toast("Vacio")
            }
        }
    }

    private fun saveMedicina(medicina : Medicina){
        val newMedicina = HashMap<String, Any>()
        newMedicina["Userid"] = medicina.Userid
        newMedicina["Nombre_medicamento"] = medicina.Nombre_medicamento
        //newMedicina["Tipo_medicamento"] = medicina.Tipo_medicamento
        newMedicina["Dosis"] = medicina.Dosis
        newMedicina["Fecha"] = medicina.Fecha
        medicamentoDBRef.add(newMedicina)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    private fun subscribeToMedicina() {
        medicamentoSubscription =medicamentoDBRef
            .whereEqualTo("Userid",currentUser.uid)
            .addSnapshotListener(object :EventListener,com.google.firebase.firestore.EventListener<QuerySnapshot>{
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        return
                    }
                    snapshot?.let {
                        listamedicina.clear()
                        val medicamento = it.toObjects(Medicina::class.java)
                        listamedicina.addAll(medicamento)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewMedicamento.smoothScrollToPosition(0)
                    }
                }
            })
    }



    override fun onDestroyView() {
        medicamentoSubscription?.remove()
        super.onDestroyView()
    }



}
