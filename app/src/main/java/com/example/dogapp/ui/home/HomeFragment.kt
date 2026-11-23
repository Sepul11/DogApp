package com.example.dogapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dogapp.databinding.FragmentHomeBinding
import androidx.navigation.fragment.findNavController
import com.example.dogapp.R
import com.example.dogapp.ui.adapter.CitaAdapter
import androidx.fragment.app.viewModels
import com.example.dogapp.viewmodel.CitaViewModel
import androidx.core.view.isVisible

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(
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

        val citaAdapter = CitaAdapter { citaId ->
            val argumentos = Bundle().apply {
                putInt("citaId", citaId)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_detalleFragment,
                argumentos
            )
        }

        binding.rvCitas.adapter = citaAdapter
        binding.lifecycleOwner = viewLifecycleOwner

        citaViewModel.citas.observe(viewLifecycleOwner) { citas ->
            citaAdapter.actualizarCitas(citas)
            actualizarEstadoCitas()
        }

        citaViewModel.cargandoCitas.observe(viewLifecycleOwner) {
            actualizarEstadoCitas()
        }

        citaViewModel.errorCitas.observe(viewLifecycleOwner) {
            actualizarEstadoCitas()
        }

        binding.btnReintentarCitas.setOnClickListener {
            citaViewModel.cargarCitas()
        }

        binding.fabNuevaCita.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_nuevaCitaFragment
            )
        }
    }

    private fun actualizarEstadoCitas() {
        val cargando = citaViewModel.cargandoCitas.value == true
        val error = citaViewModel.errorCitas.value
        val citas = citaViewModel.citas.value

        val mostrarError = !cargando && error != null

        val mostrarResultado =
            !cargando && error == null && citas != null

        binding.progressCitas.isVisible = cargando

        binding.contenedorErrorCitas.isVisible = mostrarError
        binding.tvErrorCitas.text = error

        binding.tvCitasVacias.isVisible =
            mostrarResultado && citas.isNullOrEmpty()

        binding.rvCitas.isVisible =
            mostrarResultado && !citas.isNullOrEmpty()
    }

    override fun onStart() {
        super.onStart()
        citaViewModel.cargarCitas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}