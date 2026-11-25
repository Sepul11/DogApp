package com.example.dogapp.ui.editarcita

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dogapp.databinding.FragmentEditarCitaBinding
import com.example.dogapp.viewmodel.CitaViewModel
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.dogapp.R

class EditarCitaFragment : Fragment() {

    private var _binding: FragmentEditarCitaBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditarCitaBinding.inflate(
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

        binding.lifecycleOwner = viewLifecycleOwner

        val citaId = arguments?.getInt("citaId", -1) ?: -1

        if (citaId <= 0) {
            findNavController().navigateUp()
            return
        }

        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actvRaza.threshold = 2

        configurarEdicion()

        citaViewModel.citaSeleccionada.observe(viewLifecycleOwner) { cita ->
            if (cita != null && savedInstanceState == null) {
                binding.etNombreMascota.setText(cita.nombreMascota)
                binding.actvRaza.setText(cita.raza, false)
                binding.etNombrePropietario.setText(cita.nombrePropietario)
                binding.etTelefono.setText(cita.telefono)
            }
        }

        citaViewModel.razas.observe(viewLifecycleOwner) { razas ->
            val adaptador = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                razas
            )
            binding.actvRaza.setAdapter(adaptador)
        }

        if (citaViewModel.citaSeleccionada.value == null) {
            citaViewModel.cargarCitaPorId(citaId)
        }

        if (citaViewModel.razas.value.isNullOrEmpty()) {
            citaViewModel.cargarRazas()
        }
    }

    private fun configurarEdicion() {
        binding.etNombreMascota.doOnTextChanged { _, _, _, _ ->
            validarFormulario()
        }

        binding.actvRaza.doOnTextChanged { _, _, _, _ ->
            validarFormulario()
        }

        binding.etNombrePropietario.doOnTextChanged { _, _, _, _ ->
            validarFormulario()
        }

        binding.etTelefono.doOnTextChanged { _, _, _, _ ->
            validarFormulario()
        }

        binding.btnEditarCita.setOnClickListener {
            citaViewModel.actualizarCita(
                nombreMascota = binding.etNombreMascota.text.toString(),
                raza = binding.actvRaza.text.toString(),
                nombrePropietario = binding.etNombrePropietario.text.toString(),
                telefono = binding.etTelefono.text.toString()
            )
        }

        citaViewModel.guardando.observe(viewLifecycleOwner) { guardando ->
            val habilitado = guardando != true

            binding.etNombreMascota.isEnabled = habilitado
            binding.actvRaza.isEnabled = habilitado
            binding.etNombrePropietario.isEnabled = habilitado
            binding.etTelefono.isEnabled = habilitado

            validarFormulario()
        }

        citaViewModel.citaActualizada.observe(viewLifecycleOwner) { actualizada ->
            if (actualizada == true) {
                citaViewModel.consumirCitaActualizada()

                Toast.makeText(
                    requireContext(),
                    R.string.cita_actualizada_correctamente,
                    Toast.LENGTH_SHORT
                ).show()

                findNavController().popBackStack(
                    R.id.homeFragment,
                    false
                )
            }
        }

        citaViewModel.errorEditarCita.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                citaViewModel.consumirErrorEditarCita()

                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        citaViewModel.errorNuevaCita.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                // cargarRazas() publica sus errores en este estado compartido.
                citaViewModel.consumirErrorNuevaCita()

                Toast.makeText(
                    requireContext(),
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        validarFormulario()
    }

    private fun validarFormulario() {
        val camposCompletos =
            !binding.etNombreMascota.text.isNullOrBlank() &&
                    !binding.actvRaza.text.isNullOrBlank() &&
                    !binding.etNombrePropietario.text.isNullOrBlank() &&
                    !binding.etTelefono.text.isNullOrBlank()

        binding.btnEditarCita.isEnabled =
            camposCompletos &&
                    citaViewModel.citaSeleccionada.value != null &&
                    citaViewModel.guardando.value != true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}