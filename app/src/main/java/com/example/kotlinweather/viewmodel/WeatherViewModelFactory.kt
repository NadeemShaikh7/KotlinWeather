package com.example.kotlinweather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinweather.WeatherService

class WeatherViewModelFactory(private val weatherService: WeatherService) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherService) as T
    }
}