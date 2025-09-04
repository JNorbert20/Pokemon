package com.example.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.usecase.GetPokemonsByTypeUseCase
import com.example.pokemon.domain.usecase.GetTypesUseCase
import com.example.pokemon.domain.usecase.SearchPokemonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTypes: GetTypesUseCase,
    private val getPokemonsByType: GetPokemonsByTypeUseCase,
    private val searchPokemons: SearchPokemonsUseCase
) : ViewModel() {
    private val _types: MutableStateFlow<List<PokemonType>> = MutableStateFlow(emptyList())
    val types: StateFlow<List<PokemonType>> = _types
    private val _pokemonByType: MutableStateFlow<List<Pokemon>> = MutableStateFlow(emptyList())
    val pokemonByType: StateFlow<List<Pokemon>> = _pokemonByType
    private val _searchResults: MutableStateFlow<List<Pokemon>> = MutableStateFlow(emptyList())
    val searchResults: StateFlow<List<Pokemon>> = _searchResults

    fun loadTypes() {
        viewModelScope.launch {
            _types.value = getTypes()
        }
    }

    fun loadPokemonsForType(typeName: String) {
        viewModelScope.launch {
            _pokemonByType.value = getPokemonsByType(typeName)
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _searchResults.value = searchPokemons(query)
        }
    }
}

