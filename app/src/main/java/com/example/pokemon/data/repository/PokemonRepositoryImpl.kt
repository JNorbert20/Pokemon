package com.example.pokemon.data.repository

import com.example.pokemon.data.local.dao.CaughtPokemonDao
import com.example.pokemon.data.local.entity.CaughtPokemonEntity
import com.example.pokemon.data.remote.api.PokeApi
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.repository.PokemonRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.example.pokemon.di.NetworkModule.IoDispatcher

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokeApi,
    private val caughtDao: CaughtPokemonDao,
    @IoDispatcher private val io: CoroutineDispatcher
) : PokemonRepository {
    override suspend fun getTypes(): List<PokemonType> = withContext(io) {
        api.getTypes().results.map { PokemonType(name = it.name) }
    }

    override suspend fun getPokemonsByType(typeName: String): List<Pokemon> = withContext(io) {
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
    }

    private var allPokemonCache: List<String>? = null

    override suspend fun searchPokemonsByName(query: String): List<Pokemon> = withContext(io) {
        val list = allPokemonCache ?: api.getAllPokemon().results.map { it.name }.also { allPokemonCache = it }
        list
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
    }

    override suspend fun getPokemonDetail(name: String): Pokemon = withContext(io) {
        val dto = api.getPokemonDetail(name)
        val caught = caughtDao.isCaught(name)
        Pokemon(
            name = dto.name,
            imageUrl = dto.sprites.front_default,
            weight = dto.weight,
            height = dto.height,
            abilities = dto.abilities.map { com.example.pokemon.domain.model.Ability(it.ability.name, it.is_hidden) },
            isCaught = caught
        )
    }

    override suspend fun toggleCatch(name: String): Boolean = withContext(io) {
        val caught = caughtDao.isCaught(name)
        if (caught) {
            caughtDao.delete(CaughtPokemonEntity(name))
            false
        } else {
            caughtDao.insert(CaughtPokemonEntity(name))
            true
        }
    }

    override suspend fun getCaughtPokemons(): List<Pokemon> = withContext(io) {
        caughtDao.getAll().map { Pokemon(name = it.name, imageUrl = null, weight = null, height = null, isCaught = true) }
    }
}

