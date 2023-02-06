package com.andrea.finalproyectott.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.adapters.AlimentosAdapter
import com.andrea.finalproyectott.databinding.FragmentAlimenticiaBinding
import com.andrea.finalproyectott.models.Alimentos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import java.util.EventListener
import kotlin.collections.ArrayList

class AlimenticiaFragment : Fragment() {

    private lateinit var _view : View

    private lateinit var adapter : AlimentosAdapter
    private val listaalimentos : ArrayList<Alimentos> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var alimentosDBRef: CollectionReference

    private var alimentosSubscription: ListenerRegistration? = null
    private var _binding: FragmentAlimenticiaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.alimentos_fragment_string)
        _binding = FragmentAlimenticiaBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpProfileDB()
        setUpCurrentUser()

        setUpReciclerView()
        subscribeToAlimentos()

        return view
    }

    fun setUpProfileDB(){
        alimentosDBRef =store.collection("Alimentos")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    fun setUpReciclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = AlimentosAdapter(listaalimentos)
        binding.recyclerViewAlimentos.setHasFixedSize(true)
        binding.recyclerViewAlimentos.layoutManager = layoutManager
        binding.recyclerViewAlimentos.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewAlimentos.adapter =adapter
    }

    private fun subscribeToAlimentos(){
        //val query = profileDBRef.whereEqualTo("userID", currentUser.uid)

        alimentosSubscription = alimentosDBRef
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        //activity!!.toast(exception.message.toString())
                        return
                    }

                    snapshot?.let {
                        listaalimentos.clear()
                        val alimento = it.toObjects(Alimentos::class.java)
                        listaalimentos.addAll(alimento)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewAlimentos.smoothScrollToPosition(0)
                    }
                }
            })
    }

    override fun onDestroyView() {
        alimentosSubscription?.remove()
        super.onDestroyView()
    }
}
