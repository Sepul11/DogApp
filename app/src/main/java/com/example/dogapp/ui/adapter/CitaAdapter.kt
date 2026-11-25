package com.example.dogapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dogapp.databinding.ItemCitaBinding
import com.example.dogapp.model.Cita
import com.example.dogapp.ui.viewholder.CitaViewHolder

// HU-2: conecta la lista con tarjetas reciclables y entrega la selección al Fragment.
class CitaAdapter(
    private val alSeleccionarCita: (Int) -> Unit
) : RecyclerView.Adapter<CitaViewHolder>() {

    private var citas: List<Cita> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CitaViewHolder {
        val binding = ItemCitaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CitaViewHolder(binding, alSeleccionarCita)
    }

    override fun onBindViewHolder(
        holder: CitaViewHolder,
        position: Int
    ) {
        holder.mostrarCita(citas[position])
    }

    override fun getItemCount(): Int {
        return citas.size
    }

    // Sustituye los datos presentados; cambiar esta lista no escribe en Room.
    fun actualizarCitas(nuevasCitas: List<Cita>) {
        citas = nuevasCitas.toList()
        notifyDataSetChanged()
    }
}