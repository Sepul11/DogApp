package com.example.dogapp.webservice

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.dogapp.utils.Constants.BASE_URL

// HU-3/HU-5: object reutiliza cliente/servicio; Gson convierte JSON a modelos.
object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: DogApiService =
        retrofit.create(DogApiService::class.java)
}
