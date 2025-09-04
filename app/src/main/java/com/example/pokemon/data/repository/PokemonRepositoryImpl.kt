package com.example.pokemon.data.repository

import com.example.pokemon.data.local.dao.CaughtPokemonDao
import com.example.pokemon.data.local.entity.CaughtPokemonEntity
import com.example.pokemon.data.remote.api.PokeApi
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val caughtDao: CaughtPokemonDao
) : PokemonRepository {
    override suspend fun getTypes(): List<PokemonType> =
        api.getTypes().results.map { PokemonType(name = it.name) }

    override suspend fun getPokemonsByType(typeName: String): List<Pokemon> =
        api.getTypeDetail(typeName).pokemon.map { slot ->
            val name = slot.pokemon.name
            Pokemon(
                name = name,
                imageUrl = null,
                weight = null,
                height = null,
                type = typeName,
                isCaught = caughtDao.isCaught(name)
            )
        }

    override suspend fun searchPokemonsByName(query: String): List<Pokemon> =
        api.getAllPokemon().results
            .map { it.name }
            .filter { it.contains(query.trim(), ignoreCase = true) }
            .map { name ->
                Pokemon(
                    name = name,
                    imageUrl = null,
                    weight = null,
                    height = null,
                    isCaught = caughtDao.isCaught(name)
                )
            }

    override suspend fun getPokemonDetail(name: String): Pokemon {
        val dto = api.getPokemonDetail(name)
        return Pokemon(
            name = dto.name,
            imageUrl = dto.sprites.front_default,
            weight = dto.weight,
            height = dto.height,
            abilities = dto.abilities.map { com.example.pokemon.domain.model.Ability(it.ability.name, it.is_hidden) }
        )
    }

    override suspend fun toggleCatch(name: String): Boolean {
        val caught = caughtDao.isCaught(name)
        if (caught) {
            caughtDao.delete(CaughtPokemonEntity(name))
            return false
        } else {
            caughtDao.insert(CaughtPokemonEntity(name))
            return true
        }
    }

    override suspend fun getCaughtPokemons(): List<Pokemon> =
        caughtDao.getAll().map { Pokemon(name = it.name, imageUrl = null, weight = null, height = null, isCaught = true) }
}

