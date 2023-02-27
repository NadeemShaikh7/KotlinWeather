package com.example.kotlinweather.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Weather (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("main"        ) var main        : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("icon"        ) var icon        : String? = null

) : Serializable