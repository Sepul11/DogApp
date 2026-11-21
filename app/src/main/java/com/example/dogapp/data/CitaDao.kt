
package com.example.dogapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.dogapp.model.Cita

@Dao
interface CitaDao {

    @Query("SELECT * FROM citas ORDER BY id ASC")
    suspend fun obtenerCitas(): List<Cita>

    @Query("SELECT * FROM citas WHERE id = :citaId LIMIT 1")
    suspend fun obtenerCitaPorId(citaId: Int): Cita?

    @Insert
    suspend fun insertarCita(cita: Cita)
}
