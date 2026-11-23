package com.example.myappnavigation.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myappnavigation.data.local.dao.SesionDao
import com.example.myappnavigation.data.local.dao.VueloDao
import com.example.myappnavigation.data.local.entity.SesionEntity
import com.example.myappnavigation.data.local.entity.VueloEntity


@Database(entities = [SesionEntity::class, VueloEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sesionDao(): SesionDao

    abstract fun vueloDao(): VueloDao

    companion object {
        const val NOMBRE = "capebe_database.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOMBRE
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Esquema local Room sincronizado con entidades de sesion y vuelos
    }
}
