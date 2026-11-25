
package com.example.dogapp.ui.nuevacita

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.databinding.FragmentNuevaCitaBinding
import com.example.dogapp.model.Cita
import com.example.dogapp.viewmodel.CitaViewModel
import com.example.dogapp.R

// HU-3: recoge campos y observa resultados; el ViewModel coordina API y guardado.
class NuevaCitaFragment : Fragment() {

    // El binding generado conecta Kotlin con el XML; solo se usa mientras existe la vista.
    private var _binding: FragmentNuevaCitaBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaCitaBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        configurarSintomas()
        configurarValidaciones()
        configurarObservadores()
        configurarBotones()

        citaViewModel.cargarRazas()
    }

    // Catalogo local de recursos; solo las razas se consultan por API.
    private fun configurarSintomas() {
        val sintomas = resources.getStringArray(R.array.sintomas)

        val adaptador = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            sintomas
        )

        binding.actvSintomas.setAdapter(adaptador)
        binding.actvSintomas.threshold = 0

        binding.actvSintomas.setOnClickListener {
            binding.actvSintomas.showDropDown()
        }
    }

    // La UI comprueba campos presentes; el ViewModel valida la raza antes de persistir.
    private fun configurarValidaciones() {
        binding.actvRaza.threshold = 2

        val observadorCampos = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                validarCampos()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        binding.etNombreMascota.addTextChangedListener(observadorCampos)
        binding.actvRaza.addTextChangedListener(observadorCampos)
        binding.etNombrePropietario.addTextChangedListener(observadorCampos)
        binding.etTelefono.addTextChangedListener(observadorCampos)

        validarCampos()
    }

    private fun validarCampos() {
        val camposCompletos =
            !binding.etNombreMascota.text.isNullOrBlank() &&
                    !binding.actvRaza.text.isNullOrBlank() &&
                    !binding.etNombrePropietario.text.isNullOrBlank() &&
                    !binding.etTelefono.text.isNullOrBlank()

        binding.btnGuardarCita.isEnabled =
            camposCompletos && citaViewModel.guardando.value != true
    }

    private fun configurarObservadores() {
        citaViewModel.guardando.observe(viewLifecycleOwner) {
            validarCampos()
        }
        citaViewModel.razas.observe(viewLifecycleOwner) { razas ->
            val adaptador = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                razas
            )

            binding.actvRaza.setAdapter(adaptador)
        }

        // Solo el resultado de Room permite anunciar éxito y volver a Home.
        citaViewModel.citaGuardada.observe(viewLifecycleOwner) { guardada ->
            if (guardada == true) {

                Toast.makeText(
                    requireContext(),
                    getString(R.string.cita_guardada_correctamente),
                    Toast.LENGTH_SHORT
                ).show()

                citaViewModel.consumirCitaGuardada()

                findNavController().popBackStack()
            }
        }

        citaViewModel.errorNuevaCita.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                validarCampos()

                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_SHORT
                ).show()

                citaViewModel.consumirErrorNuevaCita()
            }
        }
    }

    private fun configurarBotones() {
        binding.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGuardarCita.setOnClickListener {
            guardarCita()
        }
    }

    private fun guardarCita() {

        if (citaViewModel.guardando.value == true) return

        val sintoma = binding.actvSintomas.text
            .toString()
            .trim()

        if (sintoma.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.selecciona_sintoma),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // La URL vacía es provisional: el ViewModel debe obtenerla antes de insertar.
        val nuevaCita = Cita(
            nombreMascota = binding.etNombreMascota.text
                .toString().trim(),
            raza = binding.actvRaza.text
                .toString().trim(),
            nombrePropietario = binding.etNombrePropietario.text
                .toString().trim(),
            telefono = binding.etTelefono.text
                .toString().trim(),
            sintoma = sintoma,
            imagenUrl = ""
        )

        citaViewModel.guardarCita(nuevaCita)
    }

    // El Fragment puede seguir en la pila sin su vista; libera el binding para no retenerla.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
