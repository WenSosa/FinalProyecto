package com.andrea.finalproyectott.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentNocturnoBinding
import com.andrea.finalproyectott.models.Contacto
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NocturnoFragment : Fragment() {

    private lateinit var _view: View
    lateinit var numero_telefonoM : String
    lateinit var numero_telefonoLl : String
    val REQUEST_PHONE_CALL = 1
    val REQUEST_SEND_SMS = 2

    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var contactoDBRef: CollectionReference
    private lateinit var contactoDBRef2: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private var _binding: FragmentNocturnoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.nocturno_fragment_string)
        _view = inflater.inflate(R.layout.fragment_nocturno, container, false)

        _binding = FragmentNocturnoBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpInsulinaDB()
        setUpCurrentUser()
        setUpLlamadaBtn()
        setUpSMSBtn()
        setUpTempHumBtn()
        inicializarContacto()
        return view
    }

    fun setUpTempHumBtn() {
        binding.botonGuardarTemHum.setOnClickListener(){
            val Temp= binding.editTextTemp.text.toString().toInt()
            val Hum= binding.editTextHumedad.text.toString().toInt()

            if(Temp>=50 && Hum>=50){
                getActivity()?.toast("Entra")
                startCall()
                sendSMS()
            }else{
                getActivity()?.toast("No entra")
            }
        }
    }
    fun setUpInsulinaDB(){
        contactoDBRef =store.collection("Contacto-Llamada")
        contactoDBRef2 =store.collection("Contacto-Mensaje")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun inicializarContacto() {
        val db = Firebase.firestore

        val docRef = db.collection("Contacto-Mensaje").whereEqualTo("Userid",currentUser.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val listacontactoM : ArrayList<Contacto> = ArrayList()
                    val contactoM = document.toObjects(Contacto::class.java)
                    listacontactoM.addAll(contactoM)
                    if (listacontactoM.size > 0){

                        numero_telefonoM = listacontactoM[listacontactoM.size-1].Numero

                    }else{
                        Log.d("ALO", "No such document")
                        numero_telefonoM = ""
                    }
                } else {
                    Log.d("ALO", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ALO", "get failed with ", exception)
            }

        val docRef2 = db.collection("Contacto-Llamada").whereEqualTo("Userid",currentUser.uid)
        docRef2.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val listacontactoLl : ArrayList<Contacto> = ArrayList()
                    val contactoM = document.toObjects(Contacto::class.java)
                    listacontactoLl.addAll(contactoM)
                    if (listacontactoLl.size > 0){

                        numero_telefonoLl = listacontactoLl[listacontactoLl.size-1].Numero

                    }else{
                        Log.d("ALO", "No such document")
                        numero_telefonoLl = ""
                    }
                } else {
                    Log.d("ALO", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ALO", "get failed with ", exception)
            }
    }

    fun setUpSMSBtn() {
        binding.buttonSMS.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(_view.context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.SEND_SMS),REQUEST_SEND_SMS) }
            }else{
                sendSMS()
            }
        }
    }
    fun setUpLlamadaBtn() {
        binding.buttonLlamada.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(_view.context, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL) }
            }else {
                startCall()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==REQUEST_PHONE_CALL)startCall()
        if (requestCode==REQUEST_SEND_SMS)sendSMS()
    }

    private fun startCall() {
        if (numero_telefonoLl==""){
            getActivity()?.toast("Numero de llamada no registrado")
        }else{
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:"+numero_telefonoLl)
            startActivity(callIntent)
        }

    }

    private fun sendSMS() {
        if (numero_telefonoM==""){
            getActivity()?.toast("Numero de mensaje no registrado")
        }else {
            val text = "Prueba: este es un mensaje de Alerta sobre tu contacto Paciente"
            SmsManager.getDefault().sendTextMessage(numero_telefonoM, null, text, null, null)
            getActivity()?.toast("Mensaje de texto enviado")
        }
    }


}