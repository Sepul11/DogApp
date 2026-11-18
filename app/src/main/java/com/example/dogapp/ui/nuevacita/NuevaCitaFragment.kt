
package com.example.dogapp.ui.nuevacita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.dogapp.R
import com.example.dogapp.data.remote.RetrofitClient
import kotlinx.coroutines.launch
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.dogapp.data.local.CitaEntity
import com.example.dogapp.data.local.DogAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class NuevaCitaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_nueva_cita,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val campoRaza = view.findViewById<AutoCompleteTextView>(
            R.id.actvRaza
        )

        campoRaza.threshold = 2

        cargarRazas(campoRaza)

        val campoSintomas = view.findViewById<AutoCompleteTextView>(
            R.id.actvSintomas
        )

        val listaSintomas = listOf(
            "Solo duerme",
            "No come",
            "Fractura extremidad",
            "Tiene pulgas",
            "Tiene garrapatas",
            "Bota demasiado pelo"
        )

        val adaptadorSintomas = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            listaSintomas
        )

        campoSintomas.setAdapter(adaptadorSintomas)
        campoSintomas.threshold = 0

        campoSintomas.setOnClickListener {
            campoSintomas.showDropDown()
        }

        val campoMascota = view.findViewById<TextInputEditText>(
            R.id.etNombreMascota
        )

        val campoPropietario = view.findViewById<TextInputEditText>(
            R.id.etNombrePropietario
        )

        val campoTelefono = view.findViewById<TextInputEditText>(
            R.id.etTelefono
        )

        val botonGuardar = view.findViewById<MaterialButton>(
            R.id.btnGuardarCita
        )

        fun validarCampos() {
            val mascotaCompleta =
                !campoMascota.text.isNullOrBlank()

            val razaCompleta =
                !campoRaza.text.isNullOrBlank()

            val propietarioCompleto =
                !campoPropietario.text.isNullOrBlank()

            val telefonoCompleto =
                !campoTelefono.text.isNullOrBlank()

            botonGuardar.isEnabled =
                mascotaCompleta &&
                        razaCompleta &&
                        propietarioCompleto &&
                        telefonoCompleto
        }

        val observadorCampos = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                validarCampos()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        campoMascota.addTextChangedListener(observadorCampos)
        campoRaza.addTextChangedListener(observadorCampos)
        campoPropietario.addTextChangedListener(observadorCampos)
        campoTelefono.addTextChangedListener(observadorCampos)

        validarCampos()

        // 9. Validar síntomas al presionar Guardar Cita


        botonGuardar.setOnClickListener {

            val sintomaSeleccionado =
                campoSintomas.text.toString().trim()

            if (sintomaSeleccionado.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Selecciona un síntoma",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                botonGuardar.isEnabled = false

                val nombreMascota =
                    campoMascota.text.toString().trim()

                val razaSeleccionada =
                    campoRaza.text.toString().trim()

                val nombrePropietario =
                    campoPropietario.text.toString().trim()

                val telefono =
                    campoTelefono.text.toString().trim()

                val contextoApp = requireContext().applicationContext

                viewLifecycleOwner.lifecycleScope.launch {

                    try {

                        // Obtener la imagen correspondiente a la raza
                        val partesRaza = razaSeleccionada
                            .lowercase()
                            .split(" ")
                            .filter { it.isNotBlank() }

                        val respuestaImagen = when {

                            partesRaza.size == 1 -> {
                                RetrofitClient.api.obtenerImagenRaza(
                                    partesRaza[0]
                                )
                            }

                            partesRaza.size == 2 -> {
                                RetrofitClient.api.obtenerImagenSubraza(
                                    partesRaza[0],
                                    partesRaza[1]
                                )
                            }

                            else -> null
                        }

                        val imagenUrl = if (
                            respuestaImagen?.status == "success"
                        ) {
                            respuestaImagen.message
                        } else {
                            ""
                        }

                        // Crear la cita con la URL de su imagen
                        val nuevaCita = CitaEntity(
                            nombreMascota = nombreMascota,
                            raza = razaSeleccionada,
                            nombrePropietario = nombrePropietario,
                            telefono = telefono,
                            sintoma = sintomaSeleccionado,
                            imagenUrl = imagenUrl
                        )

                        // Guardar en Room
                        withContext(Dispatchers.IO) {

                            val baseDatos =
                                DogAppDatabase.getDatabase(contextoApp)

                            baseDatos.citaDao().insertarCita(nuevaCita)
                        }

                        Toast.makeText(
                            requireContext(),
                            "Cita guardada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                    } catch (e: Exception) {

                        botonGuardar.isEnabled = true

                        Toast.makeText(
                            requireContext(),
                            "No se pudo guardar la cita",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }





    }

    private fun cargarRazas(
        campoRaza: AutoCompleteTextView
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val respuesta =
                    RetrofitClient.api.obtenerRazas()

                if (respuesta.status == "success") {

                    val razas = respuesta.message.flatMap {
                            (raza, subrazas) ->

                        if (subrazas.isEmpty()) {
                            listOf(raza)
                        } else {
                            subrazas.map { subraza ->
                                "$raza $subraza"
                            }
                        }
                    }.sorted()

                    val adaptador = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        razas
                    )

                    campoRaza.setAdapter(adaptador)
                }

            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    "No se pudieron cargar las razas",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
