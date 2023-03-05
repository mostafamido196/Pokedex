package com.example.jetpackcomposepokedex3.pokemonList


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import ir.kaaveh.sdpcompose.ssp
import ir.kaaveh.sdpcompose.sdp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.jetpackcomposepokedex3.R
import com.example.jetpackcomposepokedex3.pojo.model.PokedexListEntry
import com.example.jetpackcomposepokedex3.ui.theme.RobotoCondensed

import androidx.compose.ui.focus.onFocusChanged
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PokemonListScreen(
    navController: NavController, viewModel: PokemonListViewModel = hiltViewModel()
) {
     Surface(
         color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()
     ) {
         Column {
             Spacer(modifier = Modifier.height(18.sdp))
             Image(
                 painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                 contentDescription = "Pokemon",
                 modifier = Modifier
                     .fillMaxWidth()
                     .align(CenterHorizontally)
             )
             SearchBar(
                 hint = "Search...", modifier = Modifier
                     .fillMaxWidth()
                     .padding(top = 14.sdp, start =  14.sdp, end =  14.sdp, bottom =  8.sdp)
             ) {
                 //onValueChange on textField
                 viewModel.searchPokemonList(it)
             }

             Spacer(modifier = Modifier.height(8.sdp))
             PokemonList(navController = navController)
         }
     }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier, hint: String = "", onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.sdp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 18.sdp, vertical = 8.sdp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused || text.isNotEmpty()
                })
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 18.sdp, vertical = 8.sdp)
            )
        }
    }
}


@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(contentAlignment = Center,
        modifier = modifier
            .shadow(5.sdp, RoundedCornerShape(8.sdp))
            .clip(RoundedCornerShape(8.sdp))
            .aspectRatio(1f)// square shape
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }) {
        Column {
            SubcomposeAsyncImage(modifier = Modifier
                .size(100.sdp)
                .align(CenterHorizontally),
                model = entry.imageUrl,
                contentDescription = entry.pokemonName,
                error = { ImageBitmap.imageResource(R.drawable.error_image_24) },
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary, modifier = Modifier.scale(0.5f)
                    )
                },
                onSuccess = {
                    viewModel.calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                })

            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 18.ssp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokedexRow(
    rowIndex: Int, entries: List<PokedexListEntry>, navController: NavController
) {
    Column {
        Row {
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.sdp))
            if (entries.size >= rowIndex * 2 + 2) {
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(14.sdp))
    }
}

@Composable
fun PokemonList(
    navController: NavController, viewModel: PokemonListViewModel = hiltViewModel()
) {
    val pokemonList by remember { viewModel.pokemonList }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }

    val isSearching by remember { viewModel.isSearching }

    LazyColumn(contentPadding = PaddingValues(12.sdp)) {
        val itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        } else {
            pokemonList.size / 2 + 1
        }
        items(itemCount) {
            if (it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
                viewModel.loadPokemonPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(
        contentAlignment = Center, modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }

}

@Composable
fun RetrySection(
    error: String, onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 16.ssp)
        Spacer(modifier = Modifier.height(8.sdp))
        Button(
            onClick = { onRetry() }, modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

const val TAG = "PokemonListScreen"
