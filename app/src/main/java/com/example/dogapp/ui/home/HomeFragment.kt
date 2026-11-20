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
        }

        binding.fabNuevaCita.setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_nuevaCitaFragment
            )
        }
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