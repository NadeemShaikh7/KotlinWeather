package com.example.kotlinweather

import android.app.Application

class WeatherApplication: Application() {
    lateinit var weatherService: WeatherService
    override fun onCreate() {
        super.onCreate()
        weatherService = WeatherService()
    }
}