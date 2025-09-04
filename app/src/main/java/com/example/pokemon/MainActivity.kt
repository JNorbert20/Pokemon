package com.example.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.menuAnchor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.pokemon.ui.theme.PokemonTheme
import com.example.pokemon.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokemon.ui.screen.detail.PokemonDetailScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokemon.ui.viewmodel.DetailViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Pokemon") },
                            navigationIcon = {
                                val canPop = navController.previousBackStackEntry != null
                                if (canPop) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                    }
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            TypesDropdown(
                                vm = viewModel,
                                onSelectPokemon = { name -> navController.navigate("detail/$name") }
                            )
                        }
                        composable("detail/{name}") { backStackEntry ->
                            val name = backStackEntry.arguments?.getString("name").orEmpty()
                            val dvm: DetailViewModel = hiltViewModel()
                            LaunchedEffect(name) { if (name.isNotEmpty()) dvm.load(name) }
                            val p = dvm.pokemon
                            p.value?.let { PokemonDetailScreen(it) { caughtName -> viewModel.toggleCatch(caughtName); dvm.load(caughtName) } }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypesDropdown(modifier: Modifier = Modifier, vm: MainViewModel, onSelectPokemon: (String) -> Unit) {
    LaunchedEffect(Unit) { vm.loadTypes() }
    val types = vm.types
    val byType = vm.pokemonByType
    val searchResults = vm.searchResults
    val loading = vm.loading
    val error = vm.error
    val showCaughtOnly = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (loading.value) {
            CircularProgressIndicator()
        }
        error.value?.let { msg ->
            androidx.compose.material3.AssistChip(onClick = { /* could retry here if desired */ }, label = { Text(msg) })
        }
        Text(text = "Pokemon Types")
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select…") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.value.forEach { type ->
                    androidx.compose.material3.DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            selected = type.name
                            expanded = false
                            vm.loadPokemonsForType(type.name)
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                vm.search(it)
            },
            label = { Text("Search all Pokémon by name") },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        )

        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            androidx.compose.material3.Checkbox(checked = showCaughtOnly.value, onCheckedChange = { showCaughtOnly.value = it })
            Text(text = "Only show caught Pokemon")
        }

        Text(text = "By type:")
        val byTypeList = (if (showCaughtOnly.value) byType.value.filter { it.isCaught } else byType.value)
        if (byTypeList.isEmpty() && !loading.value) {
            Text("No Pokémon for this type.")
        }
        byTypeList.forEach { p ->
            val borderColor = if (p.isCaught) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color.Transparent
            androidx.compose.material3.Card(
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                modifier = Modifier
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.TextButton(onClick = { onSelectPokemon(p.name) }) { Text(p.name) }
                    androidx.compose.material3.Button(onClick = { vm.toggleCatch(p.name) }) {
                        Text(if (p.isCaught) "Release" else "Catch")
                    }
                }
            }
        }
        Text(text = "Search results:")
        val searchList = (if (showCaughtOnly.value) searchResults.value.filter { it.isCaught } else searchResults.value)
        if (searchList.isEmpty() && query.isNotBlank() && !loading.value) {
            Text("No Pokémon match your search.")
        }
        searchList.forEach { p ->
            val borderColor = if (p.isCaught) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color.Transparent
            androidx.compose.material3.Card(
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.TextButton(onClick = { onSelectPokemon(p.name) }) { Text(p.name) }
                    androidx.compose.material3.Button(onClick = { vm.toggleCatch(p.name) }) {
                        Text(if (p.isCaught) "Release" else "Catch")
                    }
                }
            }
        }
    }
}
