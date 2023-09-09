package com.example.kotlinweather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.kotlinweather.Constants
import com.example.kotlinweather.NetworkResult
import com.example.kotlinweather.WeatherService
import com.example.kotlinweather.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.*

import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class WeatherViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var weatherServiceMock: WeatherService

    @get:Rule
    val instantScheduler = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun getWeatherPass() = runTest{
        Mockito.`when`(weatherServiceMock.getDataForCoroutine(21.2,23.4,Constants.METRIC_UNIT,Constants.APP_ID))
            .thenReturn(NetworkResult.Success(null))

        val sut = WeatherViewModel(weatherServiceMock)
        sut.getWeatherDataFromCoroutineTest(21.2,23.4,Constants.METRIC_UNIT,Constants.APP_ID)
        val result = sut.liveweather.getOrAwaitValue()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(true,result is NetworkResult.Success)
    }

    @Test
    fun getWeatherFail() = runTest{
        Mockito.`when`(weatherServiceMock.getDataForCoroutine(21.2,23.4,Constants.METRIC_UNIT,Constants.APP_ID))
            .thenReturn(NetworkResult.Error("Wrong"))

        val sut = WeatherViewModel(weatherServiceMock)
        sut.getWeatherDataFromCoroutineTest(21.2,23.4,Constants.METRIC_UNIT,Constants.APP_ID)
        val result = sut.liveweather.getOrAwaitValue()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Wrong",result.message)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}