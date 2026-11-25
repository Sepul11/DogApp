package com.example.dogapp.model

// HU-3/HU-5: Gson mapea las claves JSON message (mapa de razas/subrazas) y status.
data class DogBreedsResponse(
    val message: Map<String, List<String>>,
    val status: String
)