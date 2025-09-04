package com.example.pokemon.domain.usecase

import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class ToggleCatchUseCase @Inject constructor(
    private val repository: PokemonRepository
) {
    suspend operator fun invoke(name: String): Boolean = repository.toggleCatch(name)
}

