package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class GetCaughtPokemonsUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(): List<Pokemon> = repository.getCaughtPokemons()
}

