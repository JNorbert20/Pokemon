package com.example.pokemon.domain.repository

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.model.PokemonType

interface PokemonRepository {
    suspend fun getTypes(): List<PokemonType>
    suspend fun getPokemonsByType(typeName: String): List<Pokemon>
    suspend fun searchPokemonsByName(query: String): List<Pokemon>
    suspend fun getPokemonDetail(name: String): Pokemon
    suspend fun toggleCatch(name: String): Boolean
    suspend fun getCaughtPokemons(): List<Pokemon>
}

