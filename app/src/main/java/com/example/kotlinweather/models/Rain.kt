package com.example.kotlinweather.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Rain (

    @SerializedName("1h" ) var h : Double? = null

) : Serializable
