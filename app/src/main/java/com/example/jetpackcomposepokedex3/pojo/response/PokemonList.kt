package com.example.jetpackcomposepokedex3.pojo.response


import com.google.gson.annotations.SerializedName

data class PokemonList(
    val count: Int,
    val next: String,
    val previous: Any,
    val results: List<Result>
){


    data class Animated(
        @SerializedName("back_default")
        val backDefault: String,
        @SerializedName("back_female")
        val backFemale: Any,
        @SerializedName("back_shiny")
        val backShiny: String,
        @SerializedName("back_shiny_female")
        val backShinyFemale: Any,
        @SerializedName("front_default")
        val frontDefault: String,
        @SerializedName("front_female")
        val frontFemale: Any,
        @SerializedName("front_shiny")
        val frontShiny: String,
        @SerializedName("front_shiny_female")
        val frontShinyFemale: Any
    )

    data class Result(
        val name: String,
        val url: String
    )


}