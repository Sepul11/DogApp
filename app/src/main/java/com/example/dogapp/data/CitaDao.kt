
package com.example.dogapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import com.example.dogapp.model.Cita

// Compartido HU-2 a HU-5: Room genera estas operaciones suspendidas.
// suspend permite esperar sin bloquear; por sí solo no selecciona otro hilo.
@Dao
interface CitaDao {

    // HU-2: consulta puntual; Home solicita otra lectura al regresar, no observa el DAO.
    @Query("SELECT * FROM citas ORDER BY id ASC")
    suspend fun obtenerCitas(): List<Cita>

    // HU-4/HU-5: busca por clave primaria; null indica que el registro no existe.
    @Query("SELECT * FROM citas WHERE id = :citaId LIMIT 1")
    suspend fun obtenerCitaPorId(citaId: Int): Cita?

    // HU-3: Room genera el ID; esta firma no lo devuelve ni modifica el objeto recibido.
    @Insert
    suspend fun insertarCita(cita: Cita)

    // HU-5: actualiza por ID y devuelve filas afectadas para comprobar el resultado.
    @Update
    suspend fun actualizarCita(cita: Cita): Int

    // HU-4: elimina por la clave primaria de la entidad recibida.
    @Delete
    suspend fun eliminarCita(cita: Cita)
}
