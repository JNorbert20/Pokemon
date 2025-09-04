package com.example.pokemon.data.remote.dto

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val abilities: List<AbilityDto>,
    val sprites: SpritesDto
)

data class AbilityDto(
    val is_hidden: Boolean,
    val ability: NamedApiResource
)

data class SpritesDto(
    val front_default: String?
)

