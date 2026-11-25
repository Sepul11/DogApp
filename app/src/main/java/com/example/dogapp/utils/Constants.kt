package com.example.dogapp.utils

// Compartido: nombre estable de Room y rutas de API usadas por HU-3/HU-5.
object Constants {
    const val NAME_BD = "dogapp_database"

    const val BASE_URL = "https://dog.ceo/"

    const val END_POINT_RAZAS = "api/breeds/list/all"
    const val END_POINT_IMAGEN_RAZA = "api/breed/{raza}/images/random"
    const val END_POINT_IMAGEN_SUBRAZA = "api/breed/{raza}/{subraza}/images/random"
}