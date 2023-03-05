package com.example.jetpackcomposepokedex3.repository

import android.util.Log
import com.example.jetpackcomposepokedex3.data.remote.PokemonServices
import com.example.jetpackcomposepokedex3.pojo.response.Pokemon
import com.example.jetpackcomposepokedex3.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import com.example.jetpackcomposepokedex3.pojo.response.PokemonList

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokemonServices
) {

    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch(e: Exception) {
            Log.e(TAG, "getPokemonList: error $e")
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            Log.d(TAG, "getPokemonInfo: api.getCharmander(pokemonName) ${api.getPokemon(pokemonName)}")
            api.getPokemon(pokemonName)
        } catch(e: Exception) {
            Log.d(TAG, "getPokemonInfo: error $e")
            return Resource.Error(" error $e")//"An unknown error occured."
        }
        return Resource.Success(response)
    }
    companion object{
        const val TAG = "PokemonRepository"
    }
}