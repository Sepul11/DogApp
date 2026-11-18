
package com.example.dogapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CitaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DogAppDatabase : RoomDatabase() {

    abstract fun citaDao(): CitaDao

    companion object {

        @Volatile
        private var INSTANCE: DogAppDatabase? = null

        fun getDatabase(context: Context): DogAppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DogAppDatabase::class.java,
                    "dogapp_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
