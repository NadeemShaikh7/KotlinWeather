package com.example.kotlinweather

import com.example.kotlinweather.models.WeatherResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("2.5/weather")
    fun getWeather(
        @Query("lat")lat: Double,
        @Query("lon")lon: Double,
        @Query("units")units: String,
        @Query("appid")appid: String,
    ): Single<WeatherResponse>
}