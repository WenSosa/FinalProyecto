package com.andrea.finalproyectott.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.DialogProfileBinding
import com.andrea.finalproyectott.databinding.FragmentNivelesGItemBinding
import com.andrea.finalproyectott.databinding.FragmentProfileItemBinding
import com.andrea.finalproyectott.inflate
import com.andrea.finalproyectott.models.Profile

class ProfileAdapter (private  val items: List<Profile>) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileAdapter.ViewHolder {
        val binding = FragmentProfileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size


    class ViewHolder(val binding: FragmentProfileItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(perfil: Profile) = with(itemView) {
            binding.textViewReciveNombre.text = perfil.Nombre
            binding.textViewReciveApelldos.text = perfil.Apellido
            binding.textViewReciveEdad.text = perfil.Edad
            binding.textViewReciveAltura.text = perfil.Altura
            binding.textViewRecivePeso.text = perfil.peso
        }
    }
}