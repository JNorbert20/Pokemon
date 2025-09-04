package com.example.pokemon.domain.model

data class Pokemon(
    val name: String,
    val imageUrl: String?,
    val weight: Int?,
    val height: Int?,
    val abilities: List<Ability> = emptyList(),
    val isCaught: Boolean = false,
    val type: String? = null
)

