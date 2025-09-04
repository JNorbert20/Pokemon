package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class SearchPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(query: String): List<Pokemon> =
        repository.searchPokemonsByName(query)
}

