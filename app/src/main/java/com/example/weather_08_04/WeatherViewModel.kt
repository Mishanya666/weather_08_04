package com.example.weather_08_04

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _weatherData = MutableLiveData<String>()
    val weatherData: LiveData<String> get() = _weatherData

    private val prefs: SharedPreferences = application.getSharedPreferences("weather_prefs", Application.MODE_PRIVATE)

    fun loadWeatherData() {
        val output = StringBuilder()
        listOf("London", "Paris", "Tokyo").forEach { city ->
            val weather = prefs.getString("weather_$city", null)
            if (weather != null) {
                output.append("$weather\n")
            }
        }

        if (output.isNotEmpty()) {
            _weatherData.value = output.toString()
        } else {
            _weatherData.value = "No weather data available"
        }
    }
}
