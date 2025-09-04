package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class GetTypesUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(): List<PokemonType> = repository.getTypes()
}

