package com.example.jetpackcomposepokedex3.pokemonList


import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.jetpackcomposepokedex3.pojo.model.PokedexListEntry
import com.example.jetpackcomposepokedex3.repository.PokemonRepository
import com.example.jetpackcomposepokedex3.util.Constants.PAGE_SIZE
import com.example.jetpackcomposepokedex3.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())

    // فيها البينات اللي هتظهر في القائمة وهي متغيره على حسب ال search world
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)


    private var cachedPokemonList = listOf<PokedexListEntry>()

    // يتم استخدامها ك temp لكي يتم حفظ الل list كاملة عند البحث عن pokemons معينه
    // لاحظ ان هذة ال property ليست mutable state لأننا لن نستخدمها في ال Screen وسنستخدمها هنا فقط
    private var isSearchStarting = true

    // show all pokemon
    // نستخدمها لنقل ال list بين ال cachedPokemonList and pokemonList
    var isSearching = mutableStateOf(false) // if the textField is not empty

    init {
        loadPokemonPaginated()
    }


    fun searchPokemonList(query: String) {
        val listToSearch =
            if (isSearchStarting) { // يدخلها عندما يكون ال textFild فارغا وندخل أول حرف فقط في الكلمه
                pokemonList.value
            } else {
                cachedPokemonList
            }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                // لا يدخل ابدا إلا في حالة أن ال query اللي أدخلها المستخدم مسحها أي أنه لا يدخلها في بداية التشغيل
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch // end the launch scope (viewModelScope.launch(){})
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {// يدخلها عندما يتم إدخال أول حرف أو عند مسح كل الحروف
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }


    fun loadPokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when (result) {
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    curPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }
                else->{}
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }


    companion object {
        const val TAG = "PokemonListViewModel"
    }

}