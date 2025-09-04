package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class GetPokemonsByTypeUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(typeName: String): List<Pokemon> =
        repository.getPokemonsByType(typeName)
}

