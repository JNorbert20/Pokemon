package com.example.pokemon.data.remote.dto

data class NamedApiResource(
    val name: String,
    val url: String
)

data class TypeListDto(
    val count: Int,
    val results: List<NamedApiResource>
)

