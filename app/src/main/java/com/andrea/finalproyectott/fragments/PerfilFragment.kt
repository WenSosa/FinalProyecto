package com.andrea.finalproyectott.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.utils.RxBus
import com.andrea.finalproyectott.adapters.ProfileAdapter
import com.andrea.finalproyectott.databinding.FragmentPerfilBinding
import com.andrea.finalproyectott.dialogues.ProfileDialog
import com.andrea.finalproyectott.models.NewProfileEvent
import com.andrea.finalproyectott.models.Profile
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.reactivex.disposables.Disposable
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log

class PerfilFragment : Fragment() {

    private lateinit var _view : View

    private lateinit var adapter : ProfileAdapter
    private val profileList : ArrayList<Profile> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var profileDBRef: CollectionReference

    private var profileSubscription: ListenerRegistration? = null
    private lateinit var profileBusListener :  Disposable //No se que le movi xD

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
        //setUpProfileButton()

        subscribeToProfiles()
        subscribeToNewProfiles()
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


    /*fun setUpProfileButton(){
        binding.editProfile.setOnClickListener{ProfileDialog().show(
            fragmentManager!!,"")}
    }*/

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
