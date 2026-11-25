package com.example.dogapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dogapp.model.Cita
import com.example.dogapp.utils.Constants.NAME_BD

// Compartido HU-2 a HU-5: declara las entidades y la versión del esquema local.
@Database(
    entities = [Cita::class],
    version = 1,
    exportSchema = false
)
abstract class DogAppDB : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {
        fun getDatabase(context: Context): DogAppDB {
            // Usa contexto de aplicación; construye una instancia sobre el mismo archivo, no un singleton.
            return Room.databaseBuilder(
                context.applicationContext,
                DogAppDB::class.java,
                NAME_BD
            ).build()
        }
    }
}