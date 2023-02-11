package com.andrea.finalproyectott.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.adapters.InsulinaAdapter
import com.andrea.finalproyectott.app.preferences
import com.andrea.finalproyectott.databinding.FragmentAlertarBinding
import com.andrea.finalproyectott.models.Contacto
import com.andrea.finalproyectott.models.Insulina
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AlertarFragment : Fragment() {


    private val store : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var contactoDBRef: CollectionReference
    private lateinit var contactoDBRef2: CollectionReference

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser


    var CODE = 0
    val REQUEST_READ_CONTACTS = 3
    val PICK_CONTACT_REQUEST = 4
    private var _binding: FragmentAlertarBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.alertar_fragment_string)
        _binding = FragmentAlertarBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpContactoDB()
        setUpCurrentUser()
        inicializarContacto()

        setUpLlamadaBtn()
        setUpMensajeBtn()

        return view
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

                        binding.textViewNombreMensaje.text = listacontactoM[listacontactoM.size-1].Nombre
                        binding.textViewNumero2.text = listacontactoM[listacontactoM.size-1].Numero

                        Log.d("ALO", "DocumentSnapshot data: ${listacontactoM[0].Nombre}")
                        Log.d("ALO", "DocumentSnapshot data: ${listacontactoM[0].Numero}")

                    }else{
                        Log.d("ALO", "No such document")
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
                    val contactoLl = document.toObjects(Contacto::class.java)
                    listacontactoLl.addAll(contactoLl)
                    if (listacontactoLl.size > 0){

                        binding.textViewNombreLlamada.text = listacontactoLl[0].Nombre
                        binding.textViewNumero.text = listacontactoLl[0].Numero

                        Log.d("ALO", "DocumentSnapshot data: ${listacontactoLl[listacontactoLl.size-1].Nombre}")
                        Log.d("ALO", "DocumentSnapshot data: ${listacontactoLl[listacontactoLl.size-1].Numero}")

                    }else{
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



    fun setUpContactoDB(){
        contactoDBRef =store.collection("Contacto-Llamada")
        contactoDBRef2 =store.collection("Contacto-Mensaje")
    }

    fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpMensajeBtn() {
        binding.imageViewEditar2.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(view!!.context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                CODE=1
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.READ_CONTACTS),REQUEST_READ_CONTACTS) }
            }else{
                CODE=1
                seleccionaContacto()
            }
        }
    }

    private fun setUpLlamadaBtn() {
        binding.imageViewEditar.setOnClickListener(){
            if (ActivityCompat.checkSelfPermission(view!!.context,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                CODE=2
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.READ_CONTACTS),REQUEST_READ_CONTACTS) }
            }else{
                CODE=2
                seleccionaContacto()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode==REQUEST_READ_CONTACTS)seleccionaContacto()
    }

    private fun seleccionaContacto() {
        val i = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(i,PICK_CONTACT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_CONTACT_REQUEST){
                val contactUri = data!!.data
                if (contactUri != null) {
                    renderContact(contactUri)

                }
            }
        }
    }

    private fun renderContact(contactUri: Uri) {
        if (CODE==1){
            binding.textViewNombreMensaje.text = getName(contactUri)
            binding.textViewNumero2.text = getPhone(contactUri)
            var contactoMensaje = Contacto(currentUser.uid,getPhone(contactUri) as String,getName(contactUri) as String)
            saveContactoM(contactoMensaje)
        }else if (CODE==2){
            binding.textViewNombreLlamada.text = getName(contactUri)
            binding.textViewNumero.text = getPhone(contactUri)
            var contactoLlamada = Contacto(Userid=currentUser.uid,getPhone(contactUri) as String,getName(contactUri) as String)
            saveContactoLl(contactoLlamada)
        }
    }

    private fun saveContactoM(contacto: Contacto){
        val newContacto = HashMap<String, Any>()
        newContacto["Userid"] = contacto.Userid
        newContacto["Numero"] = contacto.Numero
        newContacto["Nombre"] = contacto.Nombre
        contactoDBRef2.add(newContacto)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    private fun saveContactoLl(contacto: Contacto){
        val newContacto = HashMap<String, Any>()
        newContacto["Userid"] = contacto.Userid
        newContacto["Numero"] = contacto.Numero
        newContacto["Nombre"] = contacto.Nombre
        contactoDBRef.add(newContacto)
            .addOnCompleteListener{
                requireActivity().toast("Añadido")
            }
            .addOnFailureListener{
                requireActivity().toast("Error")
            }
    }

    private fun getPhone(contactUri: Uri): String? {
        var id: String? = null
        var phone: String? = null

        val contactCursor: Cursor = requireActivity().contentResolver.query(
            contactUri, arrayOf(ContactsContract.Contacts._ID),
            null, null, null
        )!!

        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0)
        }
        contactCursor.close()

        val selectionArgs = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                ContactsContract.CommonDataKinds.Phone.TYPE + "= " +
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE

        val phoneCursor: Cursor = requireActivity().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            selectionArgs, arrayOf(id),
            null
        )!!
        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0)
        }
        phoneCursor.close()

        return phone
    }

    private fun getName(contactUri: Uri): CharSequence? {
        var name: String? = null
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val c: Cursor = contentResolver.query(
            contactUri, arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null
        )!!
        if (c.moveToFirst()) {
            name = c.getString(0)
        }
        c.close()

        return name
    }

}