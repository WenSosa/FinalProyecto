package com.andrea.finalproyectott.dialogues


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.DialogProfileBinding
import com.andrea.finalproyectott.toast
import com.google.firebase.auth.FirebaseAuth


class ProfileDialog : DialogFragment() {

    private var _binding: DialogProfileBinding? = null
    // This property is only valid between onCreateDialog and
    // onDestroyView.
    private val binding get() = _binding!!
    //val vista = activity!!.layoutInflater.inflate(R.layout.dialog_profile,null)
    private var entry: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_profile,null)
        _binding = DialogProfileBinding.inflate(LayoutInflater.from(context))
        entry =  view.findViewById(R.id.editTextName);

        return AlertDialog.Builder(context!!)
            .setTitle(getString(R.string.dialog_title))
            .setView(view)
            .setPositiveButton(getString(R.string.dialog_ok)){_,_->
                val textName = binding.editTextName.text.toString()
                val textApellido = binding.editTextApellidos.text.toString()
                val textEdad = binding.editTextEdad.text.toString()
                val textAltura = binding.editTextAltura.text.toString()
                val textPeso = binding.editTextPeso.text.toString()
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                //val profile = Profile(textName, textApellido, textEdad, textAltura, textPeso,userID)
                Log.d("ALO", textName +","+ textApellido+","+ textEdad+","+ textAltura+","+ textPeso+","+userID)
                //RxBus.publish(NewProfileEvent(profile))
            }
            .setNegativeButton(R.string.dialog_cancel){_,_->
                requireActivity().toast("Cancelado")
            }
            .create()
    }

}