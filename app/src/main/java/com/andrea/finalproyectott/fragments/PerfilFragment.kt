package com.andrea.finalproyectott.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.utils.RxBus
import com.andrea.finalproyectott.adapters.ProfileAdapter
import com.andrea.finalproyectott.databinding.FragmentPerfilBinding
import com.andrea.finalproyectott.models.NewProfileEvent
import com.andrea.finalproyectott.models.Profile
import com.andrea.finalproyectott.models.Sugerencias
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.disposables.Disposable
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PerfilFragment : Fragment() {


    private lateinit var adapter : ProfileAdapter
    private val profileList : ArrayList<Profile> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var profileDBRef: CollectionReference

    private var profileSubscription: ListenerRegistration? = null
    private lateinit var profileBusListener :  Disposable //No se que le movi xD

    private lateinit var mensaje :String

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.perfil_fragment_string)
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpProfileDB()
        setUpCurrentUser()

        setUpReciclerView()

        subscribeToProfiles()
        subscribeToNewProfiles()
        inicializarContacto(view)
        return view
    }

    fun setUpProfileDB(){
        profileDBRef =store.collection("profile")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
        //Log.d("ALO",currentUser.uid)
    }

    fun setUpReciclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = ProfileAdapter(profileList)
        binding.recyclerViewProfile.setHasFixedSize(true)
        binding.recyclerViewProfile.layoutManager = layoutManager
        binding.recyclerViewProfile.itemAnimator = DefaultItemAnimator()
        binding.recyclerViewProfile.adapter =adapter
    }

    private fun saveProfile(profile: Profile){
        val newProfile = HashMap<String, Any>()
        newProfile["Nombre"] =profile.Nombre
        newProfile["Apellido"] =profile.Apellido
        newProfile["Edad"] =profile.Edad
        newProfile["Altura"] =profile.Altura
        newProfile["peso"] =profile.peso
        newProfile["userID"] =profile.userID

        profileDBRef.add(newProfile)
            .addOnCompleteListener{
                requireActivity().toast("Datos Actualizados")
            }
            .addOnFailureListener{
                requireActivity().toast("Error, intenta de nuevo")
            }
    }

    private fun inicializarContacto(view: View) {
        val db = Firebase.firestore

        val docRef = db.collection("Sugerencias")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val lista: ArrayList<Sugerencias> = ArrayList()
                    val sugerencia = document.toObjects(Sugerencias::class.java)
                    lista.addAll(sugerencia)
                    if (lista.size > 0) {
                        val rnds = (0..lista.size-1).random()
                        Log.d("ALO", "a: "+lista[rnds].Mensaje)
                        basicAlert(view,lista[rnds].Mensaje)
                    } else {
                        Log.d("ALO", "No such document")
                    }
                } else {
                    Log.d("ALO", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ALO", "get failed with ", exception)
            }
    }
    val positiveButtonClick = { dialog: DialogInterface, which: Int ->

    }

    fun basicAlert(view: View, mensaje: String){

        val builder = AlertDialog.Builder(view.context)

        with(builder)
        {
            setTitle("Recomendaciones")
            setMessage(mensaje)
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            show()
        }


    }

    private fun subscribeToProfiles(){
        //val query = profileDBRef.whereEqualTo("userID", currentUser.uid)

        profileSubscription = profileDBRef
            .whereEqualTo("userID", currentUser.uid)
            .orderBy("Nombre", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
                override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                    exception?.let {
                        activity!!.toast(exception.message.toString())
                        return
                    }

                    snapshot?.let {
                        profileList.clear()
                        val profile = it.toObjects(Profile::class.java)
                        profileList.addAll(profile)
                        adapter.notifyDataSetChanged()
                        binding.recyclerViewProfile.smoothScrollToPosition(0)
                    }
                }
            })
    }

    private fun subscribeToNewProfiles(){
        profileBusListener = RxBus.listen(NewProfileEvent::class.java).subscribe({
            saveProfile(it.profile)
        })
    }

    override fun onDestroyView() {
        profileBusListener.dispose()
        profileSubscription?.remove()
        super.onDestroyView()
    }

}
