package com.example.kotlinweather

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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

    @SuppressLint("MissingPermission")
    fun getLocationParams(mFusedLocationProviderClient: FusedLocationProviderClient): Flow<Location> = callbackFlow {
        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val mLocation: Location? = locationResult.lastLocation
                trySend(mLocation!!).isSuccess
            }
        }
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 50000)
            .build()
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

        awaitClose {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
        }
    }


    fun Context.getToast(message: String) {
        return Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    fun getCelcius(value: Double): String {
//        var doubleValue = String.format("%1f", (value - 32) * 5 / 9).toDouble()
//        doubleValue = (doubleValue*100.0).roundToInt()/100.0
        val df = DecimalFormat("#.#")
//        df.roundingMode = RoundingMode.DOWN
        val doubleValue = df.format((value - 32) * 5 / 9)
        return "$doubleValue °C"
    }

    fun getTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("hh:mm", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        return sdf.format(date)
    }
}