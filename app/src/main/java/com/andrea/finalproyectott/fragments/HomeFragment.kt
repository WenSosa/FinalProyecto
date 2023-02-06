package com.andrea.finalproyectott.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.setTitle(R.string.home_fragment_string)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        setUpCurrentUser()
        setUpCurrentUserInfoUI()

        return view
    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInfoUI() {
        binding.textViewInfoEmail.text=currentUser.email
        binding.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: run { getString(R.string.info_no_name) }



        /*Picasso.get().load(R.drawable.ic_no_image_profile).resize(300, 300)
            .centerCrop().transform(CircleTransform()).into(_view.imageViewInfoAvatar)*/


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}