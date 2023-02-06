package com.andrea.finalproyectott.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrea.finalproyectott.databinding.FragmentRecordatorioItemBinding
import com.andrea.finalproyectott.models.Recordatorio

class RecordatorioAdapter ( val items: List<Recordatorio>) : RecyclerView.Adapter<RecordatorioAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordatorioAdapter.ViewHolder {
        val binding = FragmentRecordatorioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    class ViewHolder(val binding: FragmentRecordatorioItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recordatorio: Recordatorio) = with(itemView) {
            binding.textViewRecordatorio.text = recordatorio.Nombre
            binding.textViewDescripcion.text = recordatorio.Descripcion
        }
    }

}