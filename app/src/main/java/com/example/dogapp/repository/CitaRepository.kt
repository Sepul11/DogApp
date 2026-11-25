
package com.example.dogapp.repository

import android.content.Context
import com.example.dogapp.data.CitaDao
import com.example.dogapp.data.DogAppDB
import com.example.dogapp.model.Cita
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Compartido HU-2 a HU-5: acceso local del ViewModel al DAO.
// withContext(IO) sigue el patrón del Repository de clase 8; los errores llegan al ViewModel.
class CitaRepository(context: Context) {

    private val citaDao: CitaDao =
        DogAppDB.getDatabase(context.applicationContext).citaDao()

    suspend fun obtenerCitas(): List<Cita> {
        return withContext(Dispatchers.IO) {
            citaDao.obtenerCitas()
        }
    }

    suspend fun obtenerCitaPorId(citaId: Int): Cita? {
        return withContext(Dispatchers.IO) {
            citaDao.obtenerCitaPorId(citaId)
        }
    }

    suspend fun insertarCita(cita: Cita) {
        withContext(Dispatchers.IO) {
            citaDao.insertarCita(cita)
        }
    }

    suspend fun actualizarCita(cita: Cita): Int {
        return withContext(Dispatchers.IO) {
            citaDao.actualizarCita(cita)
        }
    }

    suspend fun eliminarCita(cita: Cita) {
        withContext(Dispatchers.IO) {
            citaDao.eliminarCita(cita)
        }
    }
}
