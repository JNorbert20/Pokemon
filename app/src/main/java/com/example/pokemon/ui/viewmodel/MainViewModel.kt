package com.example.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.model.PokemonType
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.usecase.GetPokemonsByTypeUseCase
import com.example.pokemon.domain.usecase.GetTypesUseCase
import com.example.pokemon.domain.usecase.SearchPokemonsUseCase
import com.example.pokemon.domain.usecase.ToggleCatchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTypes: GetTypesUseCase,
    private val getPokemonsByType: GetPokemonsByTypeUseCase,
    private val searchPokemons: SearchPokemonsUseCase,
    private val toggleCatch: ToggleCatchUseCase
) : ViewModel() {
    private val _types: MutableStateFlow<List<PokemonType>> = MutableStateFlow(emptyList())
    val types: StateFlow<List<PokemonType>> = _types
    private val _pokemonByType: MutableStateFlow<List<Pokemon>> = MutableStateFlow(emptyList())
    val pokemonByType: StateFlow<List<Pokemon>> = _pokemonByType
    private val _searchResults: MutableStateFlow<List<Pokemon>> = MutableStateFlow(emptyList())
    val searchResults: StateFlow<List<Pokemon>> = _searchResults

    private var typesLoaded: Boolean = false
    private var lastTypeRequested: String? = null

    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadTypes() {
        viewModelScope.launch {
            if (typesLoaded) return@launch
            _loading.value = true
            try {
                _types.value = getTypes()
                typesLoaded = true
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadPokemonsForType(typeName: String) {
        viewModelScope.launch {
            if (lastTypeRequested == typeName && _pokemonByType.value.isNotEmpty()) return@launch
            _loading.value = true
            try {
                _pokemonByType.value = getPokemonsByType(typeName)
                lastTypeRequested = typeName
            } finally {
                _loading.value = false
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _searchResults.value = searchPokemons(query)
            } finally {
                _loading.value = false
            }
        }
    }

    fun toggleCatch(name: String) {
        viewModelScope.launch {
            val nowCaught = try { toggleCatch(name) } catch (e: Exception) { false }
            _pokemonByType.value = _pokemonByType.value.map { if (it.name == name) it.copy(isCaught = nowCaught) else it }
            _searchResults.value = _searchResults.value.map { if (it.name == name) it.copy(isCaught = nowCaught) else it }
        }
    }
}

