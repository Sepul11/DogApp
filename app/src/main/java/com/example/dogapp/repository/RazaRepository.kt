
package com.example.dogapp.repository

import com.example.dogapp.webservice.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RazaRepository {

    suspend fun obtenerRazas(): List<String> {
        return withContext(Dispatchers.IO) {
            val respuesta = RetrofitClient.api.obtenerRazas()

            if (respuesta.status != "success") {
                throw IllegalStateException(
                    "No se pudieron obtener las razas"
                )
            }

            respuesta.message.flatMap { (raza, subrazas) ->
                if (subrazas.isEmpty()) {
                    listOf(raza)
                } else {
                    subrazas.map { subraza ->
                        "$raza $subraza"
                    }
                }
            }.sorted()
        }
    }

    suspend fun obtenerImagenRaza(
        razaSeleccionada: String
    ): String {
        return withContext(Dispatchers.IO) {
            val partesRaza = razaSeleccionada
                .trim()
                .lowercase()
                .split(" ")
                .filter { it.isNotBlank() }

            val respuesta = when (partesRaza.size) {
                1 -> RetrofitClient.api.obtenerImagenRaza(
                    partesRaza[0]
                )

                2 -> RetrofitClient.api.obtenerImagenSubraza(
                    partesRaza[0],
                    partesRaza[1]
                )

                else -> throw IllegalArgumentException(
                    "La raza seleccionada no es válida"
                )
            }

            if (respuesta.status == "success") {
                respuesta.message
            } else {
                throw IllegalStateException(
                    "No se pudo obtener la imagen de la raza"
                )
            }
        }
    }
}
