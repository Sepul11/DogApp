
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
import com.example.dogapp.R

class CitaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val citaRepository = CitaRepository(application)
    private val razaRepository = RazaRepository()

    // HU 2: listado de citas
    private val _citas = MutableLiveData<List<Cita>>()

    val citas: LiveData<List<Cita>> = _citas

    private val _cargandoCitas = MutableLiveData(false)
    val cargandoCitas: LiveData<Boolean> = _cargandoCitas

    private val _errorCitas = MutableLiveData<String?>(null)
    val errorCitas: LiveData<String?> = _errorCitas

    // HU 3: listado de razas
    private val _razas = MutableLiveData<List<String>>()
    val razas: LiveData<List<String>> = _razas

    // HU 3: estado del guardado
    private val _citaGuardada = MutableLiveData<Boolean>()
    val citaGuardada: LiveData<Boolean> = _citaGuardada

    private val _errorNuevaCita = MutableLiveData<String?>()
    val errorNuevaCita: LiveData<String?> = _errorNuevaCita

    private val _guardando = MutableLiveData(false)

    val guardando: LiveData<Boolean> = _guardando

    fun cargarCitas() {
        if (_cargandoCitas.value == true) return

        _cargandoCitas.value = true
        _errorCitas.value = null

        viewModelScope.launch {
            try {
                _citas.value = citaRepository.obtenerCitas()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al consultar las citas", e)

                _errorCitas.value = getApplication<Application>()
                    .getString(R.string.error_cargar_citas)
            } finally {
                _cargandoCitas.value = false
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
                _errorNuevaCita.value = getApplication<Application>()
                    .getString(R.string.error_cargar_razas)
            }
        }
    }

    fun guardarCita(cita: Cita) {

        if (_guardando.value == true) return

        val razasDisponibles = _razas.value

        if (razasDisponibles.isNullOrEmpty()) {
            _errorNuevaCita.value = getApplication<Application>()
                .getString(R.string.error_catalogo_no_disponible)
            return
        }

        val razaValidada = razasDisponibles.firstOrNull { raza ->
            raza.equals(cita.raza.trim(), ignoreCase = true)
        }

        if (razaValidada == null) {
            _errorNuevaCita.value = getApplication<Application>()
                .getString(R.string.error_raza_invalida)
            return
        }

        _guardando.value = true

        viewModelScope.launch {
            _citaGuardada.value = false
            _errorNuevaCita.value = null

            try {
                // Consultar la imagen antes de guardar
                val imagenUrl = try {
                    val url = razaRepository
                        .obtenerImagenRaza(razaValidada)
                        .trim()

                    check(url.isNotEmpty()) {
                        "La API devolvió una URL vacía"
                    }

                    url
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(
                        "CitaViewModel",
                        "No se pudo obtener la imagen de la raza",
                        e
                    )

                    _errorNuevaCita.value = getApplication<Application>()
                        .getString(R.string.error_obtener_imagen)

                    return@launch
                }

                // Crear una copia con la imagen obtenida
                val citaConImagen = cita.copy(
                    raza = razaValidada,
                    imagenUrl = imagenUrl
                )

                // Guardar mediante el repositorio compartido
                citaRepository.insertarCita(citaConImagen)

                _citaGuardada.value = true

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al guardar la cita", e)
                _errorNuevaCita.value = getApplication<Application>()
                    .getString(R.string.error_guardar_cita)
            } finally {
                _guardando.value = false
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
