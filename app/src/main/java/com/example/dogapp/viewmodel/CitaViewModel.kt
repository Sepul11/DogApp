
package com.example.dogapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dogapp.model.Cita
import com.example.dogapp.repository.CitaRepository
import com.example.dogapp.repository.RazaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CitaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val citaRepository = CitaRepository(application)
    private val razaRepository = RazaRepository()

    // HU 2: listado de citas
    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> = _citas

    // HU 3: listado de razas
    private val _razas = MutableLiveData<List<String>>()
    val razas: LiveData<List<String>> = _razas

    // HU 3: estado del guardado
    private val _citaGuardada = MutableLiveData<Boolean>()
    val citaGuardada: LiveData<Boolean> = _citaGuardada

    private val _errorNuevaCita = MutableLiveData<String?>()
    val errorNuevaCita: LiveData<String?> = _errorNuevaCita

    fun cargarCitas() {
        viewModelScope.launch {
            try {
                _citas.value = citaRepository.obtenerCitas()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al consultar las citas", e)
            }
        }
    }

    fun cargarRazas() {
        viewModelScope.launch {
            try {
                _razas.value = razaRepository.obtenerRazas()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al cargar las razas", e)
                _errorNuevaCita.value = "No se pudieron cargar las razas"
            }
        }
    }

    fun guardarCita(cita: Cita) {
        viewModelScope.launch {
            _citaGuardada.value = false
            _errorNuevaCita.value = null

            try {
                // Consultar la imagen antes de guardar
                val imagenUrl = try {
                    razaRepository.obtenerImagenRaza(cita.raza)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(
                        "CitaViewModel",
                        "No se pudo obtener la imagen de la raza",
                        e
                    )
                    ""
                }

                // Crear una copia con la imagen obtenida
                val citaConImagen = cita.copy(
                    imagenUrl = imagenUrl
                )

                // Guardar mediante el repositorio compartido
                citaRepository.insertarCita(citaConImagen)

                _citaGuardada.value = true

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al guardar la cita", e)
                _errorNuevaCita.value = "No se pudo guardar la cita"
            }
        }
    }

    fun consumirCitaGuardada() {
        _citaGuardada.value = false
    }

    fun consumirErrorNuevaCita() {
        _errorNuevaCita.value = null
    }
}
