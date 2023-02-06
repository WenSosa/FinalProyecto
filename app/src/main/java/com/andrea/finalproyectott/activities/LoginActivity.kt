package com.andrea.finalproyectott.activities



import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrea.finalproyectott.*
import com.andrea.finalproyectott.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity(){

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            if (isValidEmail(email) &&  isValidPassword(password)){
                logInByEmail(email, password)
            }else{
                toast("Please make sure all the data is correct")
            }
        }

        binding.textViewForgotPassword.setOnClickListener{
            goToActivity<ForgotPasswordActivity>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        binding.buttonCreateAccount.setOnClickListener {
            goToActivity<SignUpActivity>()
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        }

        binding.editTextEmail.validate {
            binding.editTextEmail.error = if (isValidEmail(it)) null else "Email no valido"
        }
        binding.editTextPassword.validate {
            binding.editTextPassword.error = if (isValidPassword(it)) null else "La contraseña debe contener al menos 1 Letra Mayúscula, 1 minúscula, un carácter especial, y al menos 4 caracteres de longitud"
        }

    }

    private fun logInByEmail(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                if (mAuth.currentUser!!.isEmailVerified){
                    toast("El usuario inicia sesion")
                    goToActivity<MainActivity>(){
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }else{
                    toast("Debes confirmar el email primero")
                }
            }else{
                toast("Alguno de los datos ingresados es incorrecto")
            }
        }
    }
}