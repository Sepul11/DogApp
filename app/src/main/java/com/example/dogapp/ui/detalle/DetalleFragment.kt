package com.example.dogapp.ui.detalle

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.dogapp.R
import com.example.dogapp.databinding.FragmentDetalleBinding
import com.example.dogapp.model.Cita
import com.example.dogapp.viewmodel.CitaViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// HU-4: presenta una cita, solicita su borrado y abre HU-5 por ID.
class DetalleFragment : Fragment() {
    // El binding generado conecta Kotlin con el XML; solo se usa mientras existe la vista.
    private var _binding: FragmentDetalleBinding? = null
    private val binding get() = _binding!!

    private val citaViewModel: CitaViewModel by viewModels()
    private var citaActual: Cita? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el ID enviado desde HomeFragment
        val citaId = arguments?.getInt("citaId", -1) ?: -1

        setupListeners()
        setupObservers()

        if (citaId != -1) {
            citaViewModel.cargarCitaPorId(citaId)
        } else {
            Log.e("DetalleFragment", "No se proporcionó un citaId válido")
            findNavController().navigateUp()
        }
    }


    private fun setupListeners() {
        // Regresar a la pantalla anterior (Home)
        binding.btnVolver.setOnClickListener {
            findNavController().navigateUp()
        }

        // Navegar a Editar Cita pasando el ID
        binding.btnEditarCita.setOnClickListener {
            citaActual?.let { cita ->
                val bundle = Bundle().apply {
                    putInt("citaId", cita.id)
                }
                findNavController().navigate(
                    R.id.action_detalleFragment_to_editarCitaFragment,
                    bundle
                )
            }
        }

        // Eliminar cita previa confirmación
        binding.btnEliminarCita.setOnClickListener {
            citaActual?.let { cita ->
                mostrarDialogoConfirmacion(cita)
            }
        }
    }

    private fun setupObservers() {
        // Mostrar los datos de la cita cuando se carguen desde Room
        citaViewModel.citaSeleccionada.observe(viewLifecycleOwner) { cita ->
            if (cita != null) {
                citaActual = cita
                mostrarDetalleCita(cita)
            }
        }

        // Al confirmar eliminación en Room, muestra Toast y vuelve a Home
        citaViewModel.citaEliminada.observe(viewLifecycleOwner) { eliminada ->
            if (eliminada == true) {
                Toast.makeText(
                    requireContext(),
                    R.string.cita_eliminada,
                    Toast.LENGTH_SHORT
                ).show()
                citaViewModel.consumirCitaEliminada()
                findNavController().navigateUp()
            }
        }
    }

    private fun mostrarDetalleCita(cita: Cita) {

        // Todos los campos y la imagen pertenecen al registro consultado por ID.
        binding.tvTituloDetalle.text = cita.nombreMascota

        binding.tvTurnoDetalle.text = getString(R.string.label_turno_simple, cita.id)
        binding.tvRazaDetalle.text = cita.raza
        binding.tvSintomaDetalle.text = cita.sintoma
        binding.tvNombrePropietarioDetalle.text = getString(R.string.label_propietario_fmt, cita.nombrePropietario)
        binding.tvTelefonoDetalle.text = getString(R.string.label_telefono_fmt, cita.telefono)


        Glide.with(this)
            .load(cita.imagenUrl)
            .placeholder(R.drawable.dog_head)
            .error(R.drawable.dog_head)
            .into(binding.ivDetalleMascota)
    }

    // Confirmación opcional en HU-4: aceptar solicita el borrado sin navegar antes de completarlo.
    private fun mostrarDialogoConfirmacion(cita: Cita) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_eliminar_titulo)
            .setMessage(R.string.dialog_eliminar_mensaje)
            .setPositiveButton(R.string.dialog_eliminar_confirmar) { _, _ ->
                citaViewModel.eliminarCita(cita)
            }
            .setNegativeButton(R.string.dialog_eliminar_cancelar, null)
            .show()
    }

    // El Fragment puede seguir en la pila sin su vista; libera el binding para no retenerla.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
