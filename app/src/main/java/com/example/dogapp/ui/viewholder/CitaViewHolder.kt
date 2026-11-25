package com.example.dogapp.ui.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.example.dogapp.databinding.ItemCitaBinding
import com.example.dogapp.model.Cita
import com.bumptech.glide.Glide

// HU-2: presenta una cita en item_cita; no consulta Room ni decide la navegación.
class CitaViewHolder(
    private val binding: ItemCitaBinding,
    private val alSeleccionarCita: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun mostrarCita(cita: Cita) {
        binding.tvNombreMascota.text = cita.nombreMascota
        binding.tvSintoma.text = cita.sintoma
        binding.tvTurno.text = "#${cita.id}"
        // Glide presenta la URL persistida, sin pedir otra imagen aleatoria a la API.
        Glide.with(binding.root)
            .load(cita.imagenUrl)
            .into(binding.ivMascota)

        // Envía el ID de Room, no la posición cambiante de la tarjeta.
        binding.cardCita.setOnClickListener {
            alSeleccionarCita(cita.id)
        }
    }
}