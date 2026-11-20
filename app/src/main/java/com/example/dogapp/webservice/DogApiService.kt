package com.example.dogapp.webservice

import com.example.dogapp.model.DogBreedsResponse
import com.example.dogapp.model.DogImageResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DogApiService {

    @GET("api/breeds/list/all")
    suspend fun obtenerRazas(): DogBreedsResponse

    @GET("api/breed/{raza}/images/random")
    suspend fun obtenerImagenRaza(
        @Path("raza") raza: String
    ): DogImageResponse

    @GET("api/breed/{raza}/{subraza}/images/random")
    suspend fun obtenerImagenSubraza(
        @Path("raza") raza: String,
        @Path("subraza") subraza: String
    ): DogImageResponse
}