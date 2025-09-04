package com.example.pokemon.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemon.domain.model.Pokemon
import com.example.pokemon.domain.usecase.GetPokemonDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDetail: GetPokemonDetailUseCase
) : ViewModel() {
    private val _pokemon: MutableStateFlow<Pokemon?> = MutableStateFlow(null)
    val pokemon: StateFlow<Pokemon?> = _pokemon

    fun load(name: String) {
        viewModelScope.launch {
            _pokemon.value = getDetail(name)
        }
    }
}
