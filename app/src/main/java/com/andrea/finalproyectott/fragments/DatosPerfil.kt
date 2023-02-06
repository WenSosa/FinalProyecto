package com.andrea.finalproyectott.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentDatosPerfilBinding
import com.andrea.finalproyectott.databinding.FragmentInsulinaBinding
import com.andrea.finalproyectott.models.Insulina
import com.andrea.finalproyectott.models.NewProfileEvent
import com.andrea.finalproyectott.models.Profile
import com.andrea.finalproyectott.toast
import com.andrea.finalproyectott.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

@SuppressLint("StaticFieldLeak")
private var _binding: FragmentDatosPerfilBinding? = null
private val binding get() = _binding!!

private lateinit var perfilDBRef: CollectionReference
private val store : FirebaseFirestore = FirebaseFirestore.getInstance()

private var DatosSubscription: ListenerRegistration? = null

class DatosPerfil : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDatosPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpProfileDB()
        setUpProfileBtn()

        return view
    }

    fun setUpProfileBtn(){
        binding.botonGuardarDatos.setOnClickListener(){
            val textName = binding.editTextName.text.toString()
            val textApellido = binding.editTextApellidos.text.toString()
            val textEdad = binding.editTextEdad.text.toString()
            val textAltura = binding.editTextAltura.text.toString()
            val textPeso = binding.editTextPeso.text.toString()
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            val profile = Profile(textName, textApellido, textEdad, textAltura, textPeso,userID)
            Log.d("ALO", textName +","+ textApellido+","+ textEdad+","+ textAltura+","+ textPeso+","+userID)
            RxBus.publish(NewProfileEvent(profile))
            savePerfil(profile)
            /*if (insulinaText.isNotEmpty()){
                val nivel = Insulina(currentUser.uid, insulinaText, Date().toString())
                saveInsulina(nivel)
                binding.editTextNivelInsulina.setText("")
            }else{
                requireActivity().toast("Vacio")
            }*/
        }
    }

    fun setUpProfileDB(){
        perfilDBRef =store.collection("profile")
    }

    private fun savePerfil(perfil: Profile){
        val newPerfil = HashMap<String, Any>()
        newPerfil["Nombre"] = perfil.Nombre
        newPerfil["Apellido"] = perfil.Apellido
        newPerfil["Edad"] = perfil.Edad
        newPerfil["Altura"] = perfil.Altura
        newPerfil["peso"] = perfil.peso
        newPerfil["userID"] = perfil.userID
        perfilDBRef.add(newPerfil)
            .addOnCompleteListener{
                requireActivity().toast("Guardado")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    override fun onDestroyView() {
        DatosSubscription?.remove()
        super.onDestroyView()
    }

}