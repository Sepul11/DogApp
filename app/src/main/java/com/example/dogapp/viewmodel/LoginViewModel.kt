package com.example.dogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _autenticando = MutableLiveData(false)
    val autenticando: LiveData<Boolean> = _autenticando

    private val _autenticado = MutableLiveData(false)
    val autenticado: LiveData<Boolean> = _autenticado

    fun iniciarAutenticacion() {
        _autenticando.value = true
    }

    fun finalizarAutenticacion(correcta: Boolean) {
        _autenticando.value = false
        _autenticado.value = correcta
    }

    fun consumirIngreso() {
        _autenticado.value = false
    }
}