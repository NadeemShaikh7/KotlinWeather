package com.example.kotlinweather

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherApiTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var weatherApi: WeatherApi

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        weatherApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Test
    fun getCAll() = runTest{
        val weatherResp = MockResponse()
        weatherResp.setBody("{}")
        mockWebServer.enqueue(weatherResp)

        val response = weatherApi.getWeatherForCoroutine(12.2,3.3,Constants.METRIC_UNIT,Constants.APP_ID)
        mockWebServer.takeRequest()
        Assert.assertEquals("{\"coord\":{},\"weather\":[],\"main\":{},\"wind\":{},\"rain\":{},\"clouds\":{},\"sys\":{}}",Gson().toJson(response.body()))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

}