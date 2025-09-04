package com.example.pokemon.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokemon.data.local.entity.CaughtPokemonEntity

@Dao
interface CaughtPokemonDao {
    @Query("SELECT * FROM caught_pokemon")
    suspend fun getAll(): List<CaughtPokemonEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CaughtPokemonEntity)

    @Delete
    suspend fun delete(entity: CaughtPokemonEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM caught_pokemon WHERE name = :name)")
    suspend fun isCaught(name: String): Boolean
}

