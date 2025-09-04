package com.example.pokemon.data.remote.api

import com.example.pokemon.data.remote.dto.PokemonDetailDto
import com.example.pokemon.data.remote.dto.TypeDetailDto
import com.example.pokemon.data.remote.dto.TypeListDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApi {
    @GET("type")
    suspend fun getTypes(): TypeListDto

    @GET("type/{name}")
    suspend fun getTypeDetail(@Path("name") name: String): TypeDetailDto
    
    @GET("pokemon?limit=2000")
    suspend fun getAllPokemon(): TypeListDto

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") name: String): PokemonDetailDto
}

