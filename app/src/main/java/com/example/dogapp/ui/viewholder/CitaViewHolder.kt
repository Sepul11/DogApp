package com.example.dogapp.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.dogapp.databinding.ItemCitaBinding
import com.example.dogapp.model.Cita
import com.bumptech.glide.Glide

class CitaViewHolder(
    private val binding: ItemCitaBinding,
    private val alSeleccionarCita: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun mostrarCita(cita: Cita) {
        binding.tvNombreMascota.text = cita.nombreMascota
        binding.tvSintoma.text = cita.sintoma
        binding.tvTurno.text = "#${cita.id}"
        Glide.with(binding.root)
            .load(cita.imagenUrl)
            .into(binding.ivMascota)

        binding.cardCita.setOnClickListener {
            alSeleccionarCita(cita.id)
        }
    }
}