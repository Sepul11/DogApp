
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

// Compartido HU-2 a HU-5: coordina Repository y publica estado, sin conservar vistas.
// MutableLiveData privado permite escribir aquí; la UI observa LiveData y solicita acciones.
// viewModelScope inicia corrutinas ligadas al ViewModel y las cancela al destruirse.
class CitaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val citaRepository = CitaRepository(application)
    private val razaRepository = RazaRepository()

    // HU-2: lista y estados de Home.
    private val _citas = MutableLiveData<List<Cita>>()

    val citas: LiveData<List<Cita>> = _citas

    private val _cargandoCitas = MutableLiveData(false)
    val cargandoCitas: LiveData<Boolean> = _cargandoCitas

    private val _errorCitas = MutableLiveData<String?>(null)
    val errorCitas: LiveData<String?> = _errorCitas

    // HU-3/HU-5: catálogo remoto para sugerencias y validación.
    private val _razas = MutableLiveData<List<String>>()
    val razas: LiveData<List<String>> = _razas

    // HU-3: resultado de creación y errores del formulario/catálogo.
    private val _citaGuardada = MutableLiveData<Boolean>()
    val citaGuardada: LiveData<Boolean> = _citaGuardada

    private val _errorNuevaCita = MutableLiveData<String?>()
    val errorNuevaCita: LiveData<String?> = _errorNuevaCita

    // HU-3/HU-5: evita guardados simultáneos dentro de esta instancia.
    private val _guardando = MutableLiveData(false)

    val guardando: LiveData<Boolean> = _guardando

    // HU-4/HU-5: registro seleccionado por ID.
    private val _citaSeleccionada = MutableLiveData<Cita?>()
    val citaSeleccionada: LiveData<Cita?> = _citaSeleccionada

    // HU-4: resultado del borrado.
    private val _citaEliminada = MutableLiveData<Boolean>()
    val citaEliminada: LiveData<Boolean> = _citaEliminada

    // HU-5: resultado de edición.
    private val _citaActualizada = MutableLiveData(false)
    val citaActualizada: LiveData<Boolean> = _citaActualizada

    private val _errorEditarCita = MutableLiveData<String?>(null)
    val errorEditarCita: LiveData<String?> = _errorEditarCita

    // HU-4/HU-5: espera el resultado del Repository y lo publica para detalle/edición.
    fun cargarCitaPorId(citaId: Int) {
        viewModelScope.launch {
            try {
                _citaSeleccionada.value = citaRepository.obtenerCitaPorId(citaId)
            } catch (e: CancellationException) {
                // La cancelación pertenece al ciclo de vida; no se convierte en un error de datos.
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al cargar el detalle de la cita", e)
            }
        }
    }

    // HU-4: publica éxito después de completar el DAO, no al pulsar el botón.
    fun eliminarCita(cita: Cita) {
        viewModelScope.launch {
            try {
                citaRepository.eliminarCita(cita)
                _citaEliminada.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al eliminar la cita", e)
                _citaEliminada.value = false
            }
        }
    }

    // HU-2: lectura puntual; finally libera la carga incluso ante fallo o cancelación.
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

    // HU-3/HU-5: carga el catálogo por Repository; la UI observa la lista resultante.
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

    // HU-3: valida catálogo, obtiene imagen y después inserta; un fallo conserva el formulario.
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
                // Libera el bloqueo también ante return@launch o cancelación.
                _guardando.value = false
            }
        }
    }

    // Limpia el resultado atendido para no repetir Toast y navegación.
    fun consumirCitaEliminada() {
        _citaEliminada.value = false
    }

    fun consumirCitaSeleccionada() {
        _citaSeleccionada.value = null
    }

    fun consumirCitaGuardada() {
        _citaGuardada.value = false
    }

    fun consumirErrorNuevaCita() {
        _errorNuevaCita.value = null
    }

    // HU-5: recibe campos editables y conserva identidad/síntoma del registro seleccionado.
    fun actualizarCita(
        nombreMascota: String,
        raza: String,
        nombrePropietario: String,
        telefono: String
    ) {
        if (_guardando.value == true) return

        _errorEditarCita.value = null
        _citaActualizada.value = false

        val original = _citaSeleccionada.value

        if (original == null) {
            _errorEditarCita.value = getApplication<Application>()
                .getString(R.string.error_cita_no_disponible)
            return
        }

        val nombre = nombreMascota.trim()
        val propietario = nombrePropietario.trim()
        val contacto = telefono.trim()

        if (
            nombre.isBlank() || nombre.length > 15 ||
            propietario.isBlank() || propietario.length > 30 ||
            contacto.isEmpty() || contacto.length > 10 ||
            contacto.any { it !in '0'..'9' }
        ) {
            _errorEditarCita.value = getApplication<Application>()
                .getString(R.string.error_campos_edicion)
            return
        }

        val razaValidada = _razas.value?.firstOrNull {
            it.equals(raza.trim(), ignoreCase = true)
        }

        if (razaValidada == null) {
            _errorEditarCita.value = getApplication<Application>()
                .getString(R.string.error_raza_edicion)
            return
        }

        _guardando.value = true

        viewModelScope.launch {
            try {
                val cambioRaza = !original.raza.equals(
                    razaValidada,
                    ignoreCase = true
                )

                val imagen = if (cambioRaza) {
                    try {
                        razaRepository.obtenerImagenRaza(razaValidada)
                            .trim()
                            .also { check(it.isNotEmpty()) }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w("CitaViewModel", "Error al obtener imagen", e)
                        _errorEditarCita.value = getApplication<Application>()
                            .getString(R.string.error_obtener_imagen)
                        return@launch
                    }
                } else {
                    original.imagenUrl
                }

                // copy conserva los campos no indicados: ID y síntoma no se reemplazan.
                val modificada = original.copy(
                    nombreMascota = nombre,
                    raza = razaValidada,
                    nombrePropietario = propietario,
                    telefono = contacto,
                    imagenUrl = imagen
                )

                val filasActualizadas =
                    citaRepository.actualizarCita(modificada)

                // Cero filas no equivale a éxito: la cita pudo dejar de existir.
                check(filasActualizadas == 1) {
                    "La cita no pudo actualizarse"
                }

                _citaSeleccionada.value = modificada
                _citaActualizada.value = true
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("CitaViewModel", "Error al actualizar la cita", e)
                _errorEditarCita.value = getApplication<Application>()
                    .getString(R.string.error_actualizar_cita)
            } finally {
                // Libera el bloqueo también ante return@launch o cancelación.
                _guardando.value = false
            }
        }
    }

    // La UI consume resultados atendidos para no repetir sus efectos al recrearse.
    fun consumirCitaActualizada() {
        _citaActualizada.value = false
    }

    fun consumirErrorEditarCita() {
        _errorEditarCita.value = null
    }
}
