package com.example.jetpackcomposepokedex3.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposepokedex3.pojo.response.Pokemon
import com.example.jetpackcomposepokedex3.repository.PokemonRepository
import com.example.jetpackcomposepokedex3.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokemonName)
    }
}