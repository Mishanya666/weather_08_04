package com.example.weather_08_04

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weather_08_04.network.RetrofitClient
import com.example.weather_08_04.network.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val city = inputData.getString("city") ?: return@withContext Result.failure()
        val apiKey = "24664d677196183d8f6a0d12e063a503"

        Log.d("WeatherWorker", "Requesting weather for city: $city")

        return@withContext try {
            val response: WeatherResponse = RetrofitClient.api.getWeather(city, apiKey)
            Log.d("WeatherWorker", "Weather response: $response")


            val temperatureInCelsius = response.main.temp
            val weather = "${response.name}: ${"%.2f".format(temperatureInCelsius)}°C"
            Log.d("WeatherWorker", "Formatted weather: $weather")

            val prefs = applicationContext.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("weather_$city", weather).apply()

            Log.d("WeatherWorker", "Saved weather to SharedPreferences: $weather")

            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherWorker", "Error fetching weather data", e)
            Result.retry()
        }
    }
}
