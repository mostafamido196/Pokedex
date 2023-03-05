package com.example.jetpackcomposepokedex3.data.remote

import com.example.jetpackcomposepokedex3.pojo.response.Pokemon
import com.example.jetpackcomposepokedex3.pojo.response.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokemonServices {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemon(
        @Path("name") name: String
    ): Pokemon


}