package com.andrea.finalproyectott.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.databinding.FragmentInsulinaBinding
import com.andrea.finalproyectott.databinding.FragmentInsulinaItemBinding
import com.andrea.finalproyectott.models.Insulina

class InsulinaAdapter ( val items: List<Insulina>) : RecyclerView.Adapter<InsulinaAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsulinaAdapter.ViewHolder {
        val binding = FragmentInsulinaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    class ViewHolder(val binding: FragmentInsulinaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(insulina: Insulina) = with(itemView) {
            binding.textViewFecha.text = insulina.Fecha
            binding.textViewInsulina.text = insulina.Registro
        }
    }

}