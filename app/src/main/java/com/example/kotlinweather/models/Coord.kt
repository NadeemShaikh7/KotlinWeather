package com.example.kotlinweather.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Coord (

    @SerializedName("lon" ) var lon : Double? = null,
    @SerializedName("lat" ) var lat : Double? = null

) : Serializable