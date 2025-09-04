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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
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
                val drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        Column(modifier = Modifier
                            .fillMaxHeight()
                            .padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = "PokeAPI Documentation", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    },
                    scrimColor = Color(0x99000000)
                ) {
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
                                } else {
                                    IconButton(onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } }) {
                                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypesDropdown(modifier: Modifier = Modifier, vm: MainViewModel, onSelectPokemon: (String) -> Unit) {
    LaunchedEffect(Unit) { vm.loadTypes() }
    val types by vm.types.collectAsState(emptyList())
    val byType by vm.pokemonByType.collectAsState(emptyList())
    val searchResults by vm.searchResults.collectAsState(emptyList())
    val loading by vm.loading.collectAsState(false)
    val error by vm.error.collectAsState(null)
    val showCaughtOnly = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (loading) {
            CircularProgressIndicator()
        }
        error?.let { msg ->
            androidx.compose.material3.AssistChip(onClick = { /* could retry here if desired */ }, label = { Text(msg) })
        }
        Text(text = "Pokemon Types")
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text("Select…") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            androidx.compose.material3.ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                types.forEach { type ->
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
        // Header row
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)) {
            Text("Name", modifier = Modifier.weight(0.5f))
            Text("Type", modifier = Modifier.weight(0.3f))
            Text("Status", modifier = Modifier.weight(0.3f))
            Text(" ", modifier = Modifier.weight(0.3f))
        }
        val byTypeList = (if (showCaughtOnly.value) byType.filter { it.isCaught } else byType)
        if (byTypeList.isEmpty() && !loading) {
            Text("No Pokémon for this type.")
        }
        byTypeList.forEach { p ->
            val borderColor = if (p.isCaught) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color.Transparent
            androidx.compose.material3.Card(
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.TextButton(onClick = { onSelectPokemon(p.name) }, modifier = Modifier.weight(0.5f)) { Text(p.name) }
                    Text(text = p.type ?: "-", modifier = Modifier.weight(0.3f))
                    Text(text = if (p.isCaught) "Caught" else "-", modifier = Modifier.weight(0.3f))
                    val btnColor = if (p.isCaught) Color(0xFFFFD54F) else Color(0xFF1976D2)
                    val contentColor = if (p.isCaught) Color.Black else Color.White
                    androidx.compose.material3.Button(
                        onClick = { vm.toggleCatch(p.name) },
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor, contentColor = contentColor),
                        modifier = Modifier.weight(0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (p.isCaught) "Release" else "Catch")
                    }
                }
            }
        }
        Text(text = "Search results:")
        val searchList = (if (showCaughtOnly.value) searchResults.filter { it.isCaught } else searchResults)
        if (searchList.isEmpty() && query.isNotBlank() && !loading) {
            Text("No Pokémon match your search.")
        }
        searchList.forEach { p ->
            val borderColor = if (p.isCaught) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color.Transparent
            androidx.compose.material3.Card(
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.TextButton(onClick = { onSelectPokemon(p.name) }, modifier = Modifier.weight(0.5f)) { Text(p.name) }
                    Text(text = "-", modifier = Modifier.weight(0.3f))
                    Text(text = if (p.isCaught) "Caught" else "-", modifier = Modifier.weight(0.3f))
                    val btnColor = if (p.isCaught) Color(0xFFFFD54F) else Color(0xFF1976D2)
                    val contentColor = if (p.isCaught) Color.Black else Color.White
                    androidx.compose.material3.Button(
                        onClick = { vm.toggleCatch(p.name) },
                        colors = ButtonDefaults.buttonColors(containerColor = btnColor, contentColor = contentColor),
                        modifier = Modifier.weight(0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (p.isCaught) "Release" else "Catch")
                    }
                }
            }
        }
    }
}

@Composable
fun ExposedDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    TODO("Not yet implemented")
}
