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
import com.andrea.finalproyectott.databinding.FragmentInsulinaCuidadorBinding
import com.andrea.finalproyectott.models.Insulina
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class InsulinaCuidadorFragment : Fragment() {

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var insulinaDBRef: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter : InsulinaAdapter
    private val listainsulina : ArrayList<Insulina> = ArrayList()

    private var insulinaSubscription: ListenerRegistration? = null

    private var _binding: FragmentInsulinaCuidadorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(        inflater: LayoutInflater, container: ViewGroup?,
                                      savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.insulina_fragment_string)
        _binding = FragmentInsulinaCuidadorBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpInsulinaDB()
        setUpCurrentUser()
        setUpReciclerView()
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
