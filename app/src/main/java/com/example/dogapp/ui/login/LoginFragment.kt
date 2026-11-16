package com.example.dogapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.dogapp.R
import com.example.dogapp.databinding.FragmentLoginBinding
import com.example.dogapp.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(requireContext()),
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.finalizarAutenticacion(true)
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    viewModel.finalizarAutenticacion(false)

                    val cancelado =
                        errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                                errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                                errorCode == BiometricPrompt.ERROR_CANCELED

                    if (!cancelado && isAdded) {
                        Toast.makeText(
                            requireContext(),
                            errString,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // Android muestra el fallo y permite volver a intentar.
                    // El diálogo sigue abierto; no habilitamos otro intento.
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.fingerprintAnimation.setOnClickListener {
            autenticar()
        }

        viewModel.autenticado.observe(viewLifecycleOwner) { correcto ->
            if (correcto) {
                viewModel.consumirIngreso()

                findNavController().navigate(
                    R.id.homeFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.loginFragment) {
                            inclusive = true
                        }
                    }
                )
            }
        }
    }

    private fun autenticar() {
        if (viewModel.autenticando.value == true) return

        val autenticadores =
            BiometricManager.Authenticators.BIOMETRIC_STRONG

        val disponibilidad = BiometricManager.from(requireContext())
            .canAuthenticate(autenticadores)

        if (disponibilidad != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(
                requireContext(),
                R.string.biometric_unavailable,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.biometric_title))
            .setSubtitle(getString(R.string.biometric_subtitle))
            .setNegativeButtonText(getString(R.string.biometric_cancel))
            .setAllowedAuthenticators(autenticadores)
            .build()

        viewModel.iniciarAutenticacion()
        biometricPrompt.authenticate(promptInfo)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}