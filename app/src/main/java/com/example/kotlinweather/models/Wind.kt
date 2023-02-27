package com.example.kotlinweather.models
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Wind (

    @SerializedName("speed" ) var speed : Double? = null,
    @SerializedName("deg"   ) var deg   : Int?    = null,
    @SerializedName("gust"  ) var gust  : Double? = null

) : Serializable