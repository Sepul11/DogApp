
package com.example.dogapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CitaDao {

    // Guardar una nueva cita
    @Insert
    suspend fun insertarCita(cita: CitaEntity)

    // Consultar todas las citas registradas
    @Query("SELECT * FROM citas ORDER BY id DESC")
    fun obtenerTodasLasCitas(): Flow<List<CitaEntity>>

    // Consultar una cita por su identificador
    @Query("SELECT * FROM citas WHERE id = :citaId LIMIT 1")
    suspend fun obtenerCitaPorId(citaId: Int): CitaEntity?
}
