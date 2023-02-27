package com.example.kotlinweather.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kotlinweather.WeatherService
import com.example.kotlinweather.models.WeatherResponse
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class WeatherViewModel: ViewModel() {
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val weatherData = MutableLiveData<WeatherResponse>()
    val weatherDataError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    private val disposable = CompositeDisposable()
    private lateinit var weatherService: WeatherService

    fun fusedClient(value: FusedLocationProviderClient){
        mFusedLocationProviderClient = value
    }
    init {
        weatherService = WeatherService()
    }

    fun getWeatherData(latitude: Double, longitude: Double, metricUnit: String, appId: String) {
        loading.value = true
        disposable.add(
            weatherService.getData(latitude,longitude,metricUnit,appId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherResponse>(){
                    override fun onSuccess(t: WeatherResponse) {
                        Log.e("nads ","onSuccess")
                        weatherData.value = t
                        weatherDataError.value = false
                        loading.value = false
                    }

                    override fun onError(e: Throwable) {
                        Log.e("nads onfailure", e.message.toString())
                        weatherDataError.value = true
                        loading.value = false
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("nads ", "observer cleared")
        disposable.clear()
    }
}