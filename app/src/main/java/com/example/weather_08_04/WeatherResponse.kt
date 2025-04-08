package com.example.weather_08_04.network

data class WeatherResponse(
    val name: String,
    val main: Main
)

data class Main(
    val temp: Double
)
