package com.example.dogapp.webservice

import com.example.dogapp.model.DogBreedsResponse
import com.example.dogapp.model.DogImageResponse
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.dogapp.utils.Constants.END_POINT_RAZAS
import com.example.dogapp.utils.Constants.END_POINT_IMAGEN_RAZA
import com.example.dogapp.utils.Constants.END_POINT_IMAGEN_SUBRAZA

interface DogApiService {

    @GET(END_POINT_RAZAS)
    suspend fun obtenerRazas(): DogBreedsResponse

    @GET(END_POINT_IMAGEN_RAZA)
    suspend fun obtenerImagenRaza(
        @Path("raza") raza: String
    ): DogImageResponse

    @GET(END_POINT_IMAGEN_SUBRAZA)
    suspend fun obtenerImagenSubraza(
        @Path("raza") raza: String,
        @Path("subraza") subraza: String
    ): DogImageResponse
}