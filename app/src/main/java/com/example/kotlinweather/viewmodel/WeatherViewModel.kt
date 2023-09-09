package com.example.kotlinweather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlinweather.Constants
import com.example.kotlinweather.NetworkResult
import com.example.kotlinweather.WeatherService
import com.example.kotlinweather.models.WeatherResponse
import com.google.android.gms.location.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel(private val weatherService: WeatherService) : ViewModel() {
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    val weatherData = MutableLiveData<WeatherResponse>()
    val weatherDataNetworkResult = MutableLiveData<NetworkResult<WeatherResponse?>>()
    val liveweather: LiveData<NetworkResult<WeatherResponse?>>
        get() = weatherDataNetworkResult

    val weatherDataError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    private val disposable = CompositeDisposable()
//    private lateinit var weatherService: WeatherService

    fun fusedClient(value: FusedLocationProviderClient){
        mFusedLocationProviderClient = value
    }
    init {
//        this.weatherService = WeatherService()
    }

    fun getWeatherData(latitude: Double, longitude: Double, metricUnit: String, appId: String) {
        loading.value = true
        disposable.add(
            weatherService.getDataSingle(latitude,longitude,metricUnit,appId)
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

    fun getWeatherFlow(latitude: Double, longitude: Double, metricUnit: String, appId: String) {
        loading.value = true
        viewModelScope.launch {
            weatherService.getDataFlow(latitude,
            longitude,
            Constants.METRIC_UNIT,
            Constants.APP_ID
            ).flowOn(Dispatchers.IO).catch {
                it.message.toString()
                weatherDataError.value = true
                loading.value = false
            }.collect{
                weatherData.value = it
                weatherDataError.value = false
                loading.value = false
            }

        }
    }

    fun getWeatherDataFromCoroutineTest(latitude: Double, longitude: Double, metricUnit: String, appId: String) {

        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val getCall = weatherService.getDataForCoroutine(
                latitude,
                longitude,
                Constants.METRIC_UNIT,
                Constants.APP_ID
            )
            weatherDataNetworkResult.postValue(getCall)
        }
    }
    fun getWeatherDataFromCoroutine(latitude: Double, longitude: Double, metricUnit: String, appId: String) {

        loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val getCall: Call<WeatherResponse> = weatherService.getData(
                latitude,
                longitude,
                Constants.METRIC_UNIT,
                Constants.APP_ID
            )
            getCall.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherResponse: WeatherResponse? = response.body()
                        weatherData.value = weatherResponse
                        weatherDataError.value = false
                        loading.value = false
                        Log.i("nads success", "$weatherResponse")
                    } else {
                        when (response.code()) {
                            400 -> Log.e("Error 400", "Bas connection")
                            404 -> Log.e("Error 404", "Not found connection")
                            else -> Log.e("Error", "Genreic error")
                        }
                        weatherDataError.value = true
                        loading.value = false
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("nads error", t.message.toString())
                }
            })
        }
    }
    override fun onCleared() {
        super.onCleared()
        Log.e("nads ", "observer cleared")
        disposable.clear()
    }
}