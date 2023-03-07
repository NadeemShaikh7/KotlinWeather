package com.example.kotlinweather

import com.example.kotlinweather.models.WeatherResponse
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherService {
    var api: WeatherApi

    init {

        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client : OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()

        api = Retrofit
            .Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
            .create(WeatherApi::class.java)
    }

    fun getData(lat: Double, long: Double,units: String, appId: String): Call<WeatherResponse>{
//        return withContext(Dispatchers.IO){
           return api.getWeather(lat,long,units,appId)
//        }
    }

    fun getDataSingle(lat: Double, long: Double,units: String, appId: String): Single<WeatherResponse>{
        return api.getWeatherSingle(lat,long,units,appId)
    }

    fun getDataFlow(lat: Double, long: Double,units: String, appId: String): Flow<WeatherResponse> = flow{
        val response = api.getWeatherFlow(lat,long,units,appId)
        emit(response)
    }
}