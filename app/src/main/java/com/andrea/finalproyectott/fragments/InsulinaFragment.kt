package com.andrea.finalproyectott.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.adapters.InsulinaAdapter
import com.andrea.finalproyectott.databinding.FragmentInsulinaBinding
import com.andrea.finalproyectott.models.Insulina
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class InsulinaFragment : Fragment() {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var insulinaDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : InsulinaAdapter
    private val listainsulina : ArrayList<Insulina> = ArrayList()

    private var insulinaSubscription: ListenerRegistration? = null

    private var _binding: FragmentInsulinaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,
                                      savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.insulina_fragment_string)
        _binding = FragmentInsulinaBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpInsulinaDB()
        setUpCurrentUser()

        setUpReciclerView()
        setUpInsulinaBtn()
        subscribeToInsulina()

        return view
    }

    fun setUpInsulinaDB(){
        insulinaDBRef =store.collection("Registro_insulina")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    fun setUpReciclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = InsulinaAdapter(listainsulina)
        binding.recyclerViewInsulina.setHasFixedSize(true)
        binding.recyclerViewInsulina.layoutManager = layoutManager
        binding.recyclerViewInsulina.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewInsulina.adapter =adapter
    }

    fun setUpInsulinaBtn(){
        binding.botonGuardarNivelInsulina.setOnClickListener(){
            val insulinaText = binding.editTextNivelInsulina.text.toString()
            if (insulinaText.isNotEmpty()){
                val nivel = Insulina(currentUser.uid, insulinaText, Date().toString())
                saveInsulina(nivel)
                binding.editTextNivelInsulina.setText("")
            }else{
                requireActivity().toast("Vacio")
            }
        }
    }

    private fun saveInsulina(insulina : Insulina){
        val newInsulina = HashMap<String, Any>()
        newInsulina["Userid"] = insulina.Userid
        newInsulina["Registro"] = insulina.Registro
        newInsulina["Fecha"] = insulina.Fecha
        insulinaDBRef.add(newInsulina)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    fun subscribeToInsulina(){
        insulinaSubscription = insulinaDBRef
            .whereEqualTo("Userid",currentUser.uid)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        //activity!!.toast(exception.message.toString())
                        return
                    }

                    snapshot?.let {
                        listainsulina.clear()
                        val insulina = it.toObjects(Insulina::class.java)
                        listainsulina.addAll(insulina)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewInsulina.smoothScrollToPosition(0)
                    }
                }
            })
    }

    override fun onDestroyView() {
        insulinaSubscription?.remove()
        super.onDestroyView()
    }

}
