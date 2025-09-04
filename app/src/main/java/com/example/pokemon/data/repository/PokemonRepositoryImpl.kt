package com.example.pokemon.data.repository

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.repository.PokemonRepository

class PokemonRepositoryImpl : PokemonRepository {
    override suspend fun getTypes(): List<PokemonType> = emptyList()

    override suspend fun getPokemonsByType(typeName: String): List<Pokemon> = emptyList()

    override suspend fun searchPokemonsByName(query: String): List<Pokemon> = emptyList()

    override suspend fun getPokemonDetail(name: String): Pokemon =
        Pokemon(name = name, imageUrl = null, weight = null, height = null)

    override suspend fun toggleCatch(name: String): Boolean = false

    override suspend fun getCaughtPokemons(): List<Pokemon> = emptyList()
}

