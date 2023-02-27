package com.example.kotlinweather

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.kotlinweather.databinding.ActivityMainBinding
import com.example.kotlinweather.models.WeatherResponse
import com.example.kotlinweather.viewmodel.WeatherViewModel
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var viewModel: WeatherViewModel
    private lateinit var mainLayout: LinearLayout
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tv_main: TextView
    private lateinit var tv_mainDesp: TextView
    private lateinit var tv_temp: TextView
    private lateinit var tv_humid: TextView
    private lateinit var tv_min: TextView
    private lateinit var tv_max: TextView
    private lateinit var tv_wind: TextView
    private lateinit var tv_speed: TextView
    private lateinit var tv_name: TextView
    private lateinit var tv_country: TextView
    private lateinit var tv_sunrise: TextView
    private lateinit var tv_sunset: TextView
    private lateinit var iv_main: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBindings()
        setupUI(null,true)
        if (!isLocEnabled()) {
            Toast.makeText(this, "Location Service turned off", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withContext(this).withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(object : MultiplePermissionsListener {

                @RequiresApi(Build.VERSION_CODES.S)
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
//                        viewModel.getWeatherData()
                        requestLocationData()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            this@MainActivity,
                            "You should grant all requested permissions to run this app",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showPermissionRationale()
                }
            }).onSameThread().check()
        }
    }

    private fun getBindings() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainLayout = binding.mainLayout
        tv_main = binding.tvMain
        tv_mainDesp = binding.tvMainDescription
        tv_min = binding.tvMin
        tv_max = binding.tvMax
        tv_country = binding.tvCountry
        tv_humid = binding.tvHumidity
        tv_name = binding.tvName
        tv_wind = binding.tvSpeed
        tv_speed = binding.tvSpeedUnit
        tv_sunrise = binding.tvSunriseTime
        tv_sunset = binding.tvSunsetTime
        tv_temp = binding.tvTemp
        iv_main = binding.ivMain
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(Constants.WEATHER_APP_PREFS, MODE_PRIVATE)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        viewModel.fusedClient(mFusedLocationProviderClient)
        observeViewModel()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 50000)
            .build()
        mFusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val mLocation: Location? = locationResult.lastLocation
            Log.i("Nads lats = ", "${mLocation!!.latitude} &&& ${mLocation.longitude}")
            getWeatherData(mLocation.latitude, mLocation.longitude)
        }
    }

    private fun getWeatherData(latitude: Double, longitude: Double) {
        if (Constants.isNetworkAvailable(this)) {
            Constants.getToast(this, "Network connected")

            viewModel.getWeatherData(latitude,longitude,Constants.METRIC_UNIT,Constants.APP_ID)
//            val retrofit = Retrofit.Builder()
//                .baseUrl(Constants.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create()).build()
//            val weatherService = retrofit.create<WeatherApi>(WeatherApi::class.java)
//            val getCall: Call<WeatherResponse> = weatherService.getWeather(
//                latitude,
//                longitude,
//                Constants.METRIC_UNIT,
//                Constants.APP_ID
//            )
//            getCall.enqueue(object : Callback<WeatherResponse> {
//                override fun onResponse(
//                    call: Call<WeatherResponse>,
//                    response: Response<WeatherResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val weatherResponse: WeatherResponse? = response.body()
//
//                        val weatherResponseJsonData = Gson().toJson(weatherResponse)
//                        val editor = mSharedPreferences.edit()
//                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonData)
//                        editor.apply()
//                        setupUI()
//                        Log.i("nads success", "$weatherResponse")
//                    } else {
//                        when (response.code()) {
//                            400 -> Log.e("Error 400", "Bas connection")
//                            404 -> Log.e("Error 404", "Not found connection")
//                            else -> Log.e("Error", "Genreic error")
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
//                    Log.e("nads error", t.message.toString())
//                }
//            })
        } else {
            Constants.getToast(this, "Network Disconnected")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                Constants.getToast(this, "Refreshing Data")
                requestLocationData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun observeViewModel() {
        viewModel.weatherData.observe(this,androidx.lifecycle.Observer{weatherResponse ->
            weatherResponse?.let {
                Log.e("nads ","response updated $it")
                setupUI(it,false)
            }

        })

        viewModel.weatherDataError.observe(this, androidx.lifecycle.Observer { error ->
            error?.let{
                binding.tvError.visibility = if(it) View.VISIBLE else View.GONE
            }
        })

        viewModel.loading.observe(this, androidx.lifecycle.Observer {loading ->
            loading?.let{
                binding.loader.visibility = if(it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun setupUI(weatherResponseFromViewModel: WeatherResponse?, isFromSharedPref: Boolean) {
        if (isFromSharedPref) {
            val weatherResponseJson = mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")
            val weatherResponse = Gson().fromJson(weatherResponseJson, WeatherResponse::class.java)
            if(!weatherResponseJson.isNullOrEmpty()){
                populateData(weatherResponse)
            }
        }else{
            populateData(weatherResponseFromViewModel!!)
        }
    }

    private fun populateData(weatherResponse: WeatherResponse){
        for (i in weatherResponse.weather.indices) {
            tv_main.text = weatherResponse.weather[i].main
            tv_mainDesp.text = weatherResponse.weather[i].description

            when (weatherResponse.weather[i].icon) {
                "01d" -> iv_main.setImageResource(R.drawable.sunny)
                "02d" -> iv_main.setImageResource(R.drawable.cloud)
                "03d" -> iv_main.setImageResource(R.drawable.cloud)
                "04d" -> iv_main.setImageResource(R.drawable.cloud)
                "04n" -> iv_main.setImageResource(R.drawable.cloud)
                "10d" -> iv_main.setImageResource(R.drawable.rain)
                "11d" -> iv_main.setImageResource(R.drawable.storm)
                "13d" -> iv_main.setImageResource(R.drawable.snowflake)
                "01n" -> iv_main.setImageResource(R.drawable.cloud)
                "02n" -> iv_main.setImageResource(R.drawable.cloud)
                "03n" -> iv_main.setImageResource(R.drawable.cloud)
                "10n" -> iv_main.setImageResource(R.drawable.cloud)
                "11n" -> iv_main.setImageResource(R.drawable.rain)
                "13n" -> iv_main.setImageResource(R.drawable.snowflake)
            }
        }
        tv_temp.text = weatherResponse.main!!.temp?.let { getCelcius(it) }
        tv_wind.text = weatherResponse.wind!!.speed.toString()
        tv_min.text = weatherResponse.main!!.tempMin?.let { getCelcius(it) + " min" }
        tv_max.text = weatherResponse.main!!.tempMax?.let { getCelcius(it) + " max" }
        tv_name.text = weatherResponse.name.toString()
        tv_humid.text = weatherResponse.main!!.humidity.toString() + "%"
        tv_country.text = weatherResponse.sys!!.country.toString()
        tv_sunrise.text = weatherResponse.sys!!.sunrise?.let { getTime(it).toString() }
        tv_sunset.text = weatherResponse.sys!!.sunset?.let { getTime(it).toString() }
    }

    private fun getTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("hh:mm", Locale.ENGLISH).apply {
            timeZone = TimeZone.getDefault()
        }
        return sdf.format(date)
    }

    private fun getCelcius(value: Double): String {
        val doubleValue = String.format("%2f", (value - 32) * 5 / 9).toDouble()
        return "$doubleValue °C"
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setMessage("Looks like you have Location turned off. You need to enable Location services to get the weather!!")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun isLocEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )
    }
}