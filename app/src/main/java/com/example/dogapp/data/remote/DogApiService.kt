
package com.example.dogapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {

    // Obtener todas las razas disponibles
    @GET("api/breeds/list/all")
    suspend fun obtenerRazas(): DogBreedsResponse

    // Obtener una imagen aleatoria de una raza
    @GET("api/breed/{raza}/images/random")
    suspend fun obtenerImagenRaza(
        @Path("raza") raza: String
    ): DogImageResponse

    // Obtener una imagen de una subraza
    @GET("api/breed/{raza}/{subraza}/images/random")
    suspend fun obtenerImagenSubraza(
        @Path("raza") raza: String,
        @Path("subraza") subraza: String
    ): DogImageResponse
}
