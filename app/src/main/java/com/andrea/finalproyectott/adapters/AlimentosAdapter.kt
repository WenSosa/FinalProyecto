package com.andrea.finalproyectott.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.databinding.FragmentAlimentosItemBinding
import com.andrea.finalproyectott.listeners.RecyclerAlimentosListener
import com.andrea.finalproyectott.models.Alimentos

class AlimentosAdapter (private  val items: List<Alimentos>, private var listener: RecyclerAlimentosListener) : RecyclerView.Adapter<AlimentosAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentAlimentosItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)
    override fun getItemCount() = items.size

    class ViewHolder(val binding: FragmentAlimentosItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(alimentos: Alimentos, listener: RecyclerAlimentosListener) = with(itemView) {
            binding.textViewNombre.text = alimentos.Nombre
            binding.textViewCalorias.text = alimentos.Calorias

            setOnClickListener{listener.onClick(alimentos,adapterPosition)}
            binding.iconoAdd.setOnClickListener{listener.onSelect(alimentos,adapterPosition)}


        }
    }

}
