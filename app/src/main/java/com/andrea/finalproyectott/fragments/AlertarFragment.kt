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
import com.andrea.finalproyectott.app.preferences
import com.andrea.finalproyectott.databinding.FragmentAlertarBinding

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class AlertarFragment : Fragment() {


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


        setUpLlamadaBtn()
        setUpMensajeBtn()

        return view
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
            //Aqui guardamos las Shared Preferences
            //preferences.nombreMensaje = getName(contactUri) as String
            //preferences.numeroMensaje = getPhone(contactUri) as String
        }else if (CODE==2){
            Log.d("HELP","Muere5-2")
            binding.textViewNombreLlamada.text = getName(contactUri)
            Log.d("HELP","Muere5-3")
            binding.textViewNumero.text = getPhone(contactUri)
            //Aqui guardamos las Shared Preferences
            //preferences.nombreLlamada = getName(contactUri) as String
            //preferences.numeroLlamada = getPhone(contactUri) as String
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