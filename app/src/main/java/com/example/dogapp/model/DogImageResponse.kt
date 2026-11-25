
package com.example.dogapp.model

// HU-3/HU-5: message contiene la URL de imagen, no sus bytes; las claves coinciden con JSON.
data class DogImageResponse(
    val message: String,
    val status: String
)
