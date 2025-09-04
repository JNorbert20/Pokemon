package com.example.pokemon.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokemon.data.local.dao.CaughtPokemonDao
import com.example.pokemon.data.local.entity.CaughtPokemonEntity

@Database(entities = [CaughtPokemonEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun caughtDao(): CaughtPokemonDao
}

