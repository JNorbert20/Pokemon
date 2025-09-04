package com.example.pokemon.ui.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokemon.domain.model.Pokemon

@Composable
fun PokemonDetailScreen(pokemon: Pokemon, onToggleCatch: (String) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        AsyncImage(model = pokemon.imageUrl, contentDescription = pokemon.name, modifier = Modifier.fillMaxWidth())
        Text(text = pokemon.name)
        Text(text = "Weight: ${pokemon.weight ?: "-"}")
        Text(text = "Height: ${pokemon.height ?: "-"}")
        Text(text = "Abilities: ${pokemon.abilities.filter { !it.isHidden }.joinToString { it.name }}")
        Button(onClick = { onToggleCatch(pokemon.name) }, modifier = Modifier.padding(top = 12.dp)) {
            Text(if (pokemon.isCaught) "Release" else "Catch")
        }
    }
}

