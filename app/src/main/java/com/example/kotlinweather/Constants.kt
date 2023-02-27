package com.example.kotlinweather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

object Constants {
    const val APP_ID = "1c536dc1d284c4f3398f4bc139729d9c"
    const val BASE_URL = "https://api.openweathermap.org/data/"
    const val METRIC_UNIT = "imperial"
    const val WEATHER_APP_PREFS = "WeatherAppPreferences"
    const val WEATHER_RESPONSE_DATA = "WeatherAppResponse"
    fun isNetworkAvailable(context: Context): Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when{
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> return false
        }
    }

    fun getToast(context: Context,message: String) {
        return Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
}