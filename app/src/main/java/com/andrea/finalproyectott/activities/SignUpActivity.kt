package com.andrea.finalproyectott.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andrea.finalproyectott.*
import com.andrea.finalproyectott.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.ButtonGoLogIn.setOnClickListener {
            goToActivity<LoginActivity>{
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }

        binding.ButtonSignUp.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            if (isValidEmail(email) &&  isValidPassword(password) && isValidConfirmPassword(password,confirmPassword)){
                signUpByEmail(email, password)
            }else{
                toast("Revisa que los datos sean correctos")
            }

        }

        binding.editTextEmail.validate {
            binding.editTextEmail.error = if (isValidEmail(it)) null else "Correo Invalido"
        }
        binding.editTextPassword.validate {
            binding.editTextPassword.error = if (isValidPassword(it)) null else "La contraseña debe contener al menos 1 Letra Mayúscula, 1 minúscula, un carácter especial, y al menos 4 caracteres de longitud"
        }
        binding.editTextConfirmPassword.validate {
            binding.editTextConfirmPassword.error = if (isValidConfirmPassword(binding.editTextPassword.text.toString() ,it)) null else "Revisa la contraseña"
        }
    }

    private fun signUpByEmail (email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                mAuth.currentUser!!.sendEmailVerification().addOnCompleteListener(this){
                    toast("Confirma tu registo en el correo que enviamos para poder iniciar sesion")
                    goToActivity<LoginActivity>{
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                }
            } else {
                toast("Error inesperado ocurrio")
            }
        }
    }

}