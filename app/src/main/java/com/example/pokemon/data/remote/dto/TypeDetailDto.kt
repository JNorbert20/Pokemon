package com.example.pokemon.data.remote.dto

data class TypeDetailDto(
    val pokemon: List<PokemonSlot>
)

data class PokemonSlot(
    val pokemon: NamedApiResource
)

