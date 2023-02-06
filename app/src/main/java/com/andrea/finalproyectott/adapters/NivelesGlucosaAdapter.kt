package com.andrea.finalproyectott.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.R
import com.andrea.finalproyectott.databinding.FragmentMedicinaItemBinding
import com.andrea.finalproyectott.databinding.FragmentNivelesGItemBinding
import com.andrea.finalproyectott.inflate
import com.andrea.finalproyectott.models.NivelGlucosa

class NivelesGlucosaAdapter ( val items: List<NivelGlucosa>) : RecyclerView.Adapter<NivelesGlucosaAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NivelesGlucosaAdapter.ViewHolder {
    val binding = FragmentNivelesGItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    return ViewHolder(binding)
}
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    class ViewHolder(val binding: FragmentNivelesGItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(nivelesGlucosa: NivelGlucosa) = with(itemView) {
            binding.textViewFecha.text = nivelesGlucosa.Fecha
            binding.textViewNivel.text = nivelesGlucosa.Nivel
        }
    }


}