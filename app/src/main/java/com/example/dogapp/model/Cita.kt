package com.example.dogapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Compartido HU-2 a HU-5: una instancia representa una fila; el ID identifica la cita.
@Entity(tableName = "citas")
data class Cita(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombreMascota: String,
    val raza: String,
    val nombrePropietario: String,
    // Texto para conservar ceros iniciales.
    val telefono: String,
    val sintoma: String,
    // URL persistida para mostrar la misma imagen en Home y Detalle.
    val imagenUrl: String
)