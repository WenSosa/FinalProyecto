package com.andrea.finalproyectott.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentAlimentosItemBinding
import com.andrea.finalproyectott.databinding.FragmentInsulinaItemBinding
import com.andrea.finalproyectott.databinding.FragmentMedicinaItemBinding
import com.andrea.finalproyectott.inflate
import com.andrea.finalproyectott.models.Medicina

class MedicamentosAdapter(val items: List<Medicina>) : RecyclerView.Adapter<MedicamentosAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentosAdapter.ViewHolder {
        val binding = FragmentMedicinaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    class ViewHolder(val binding: FragmentMedicinaItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(medicina: Medicina) = with(itemView) {
            binding.textViewCantidad.text = medicina.Dosis
            binding.textViewNombreMedicamento.text = medicina.Nombre_medicamento
            binding.textViewFechaMedicamento.text = medicina.Fecha
        }
    }




}