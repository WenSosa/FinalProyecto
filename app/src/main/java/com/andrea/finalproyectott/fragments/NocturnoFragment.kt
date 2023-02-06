package com.andrea.finalproyectott.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentNocturnoBinding
import com.andrea.finalproyectott.toast

class NocturnoFragment : Fragment() {

    private lateinit var _view: View
    val numero_telefono= "5516451399"
    val REQUEST_PHONE_CALL = 1
    val REQUEST_SEND_SMS = 2

    private var _binding: FragmentNocturnoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.nocturno_fragment_string)
        _view = inflater.inflate(R.layout.fragment_nocturno, container, false)

        _binding = FragmentNocturnoBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpLlamadaBtn()
        setUpSMSBtn()
        setUpTempHumBtn()
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
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:"+numero_telefono)
        startActivity(callIntent)
    }

    private fun sendSMS() {
        val Number = "5516451399"
        val text = "Mestoymuriendo vengan por mi"
        SmsManager.getDefault().sendTextMessage(Number,null,text,null,null)
        getActivity()?.toast("Mensaje de texto enviado")
    }


}