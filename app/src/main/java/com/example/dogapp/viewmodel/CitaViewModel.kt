package com.example.dogapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dogapp.model.Cita
import com.example.dogapp.repository.CitaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class CitaViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val citaRepository = CitaRepository(application)

    private val _citas = MutableLiveData<List<Cita>>()
    val citas: LiveData<List<Cita>> = _citas

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
}