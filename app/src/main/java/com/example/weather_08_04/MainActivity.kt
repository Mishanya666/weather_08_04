package com.example.weather_08_04

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class MainActivity : AppCompatActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherOutput = findViewById<TextView>(R.id.weather_output)

        weatherViewModel.weatherData.observe(this, Observer { weather ->
            if (weather.isNotEmpty()) {
                weatherOutput.text = weather
            } else {
                weatherOutput.text = "No weather data available"
            }
        })

        weatherViewModel.loadWeatherData()

        enqueueWeatherChain()
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("weather_prefs", MODE_PRIVATE)
        val output = StringBuilder()
        listOf("London", "Paris", "Tokyo").forEach { city ->
            val weather = prefs.getString("weather_$city", null)
            Log.d("MainActivity", "Weather for $city: $weather")

            if (weather != null) {
                output.append("$weather\n")
            }
        }

        val textView = findViewById<TextView>(R.id.weather_output)
        textView.text = if (output.isNotEmpty()) output.toString() else "Waiting for weather data..."
    }

    private fun enqueueWeatherChain() {
        val cities = listOf("London", "Paris", "Tokyo")
        var continuation = WorkManager.getInstance(this).beginWith(
            OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(workDataOf("city" to cities[0]))
                .build()
        )

        for (index in 1 until cities.size) {
            val inputData = workDataOf("city" to cities[index])
            val workRequest = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setInputData(inputData)
                .build()
            continuation = continuation.then(workRequest)
        }

        continuation.enqueue()
    }
}
