package com.example.kotlinweather

import org.junit.Assert.*
import org.junit.Test

class ConstantsTest{
    @Test
    fun getCelcius() {
        val result = Constants.getCelcius(93.02)
        assertEquals("33.9 °C",result)
    }
}