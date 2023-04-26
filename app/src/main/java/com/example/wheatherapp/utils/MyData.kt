package com.example.wheatherapp.utils

import android.app.Activity
import android.widget.Toast
import com.example.wheatherapp.models.WeatherData
import com.example.wheatherapp.retrofit.ApiClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Activity.showToast(toastMessage: String) {
    Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
}

private val apiService = ApiClient.getApiService()

suspend fun getWeatherData(lat: Double, lon: Double): WeatherData? {
    return withContext(Dispatchers.IO) {
        val response = ApiClient.getApiService().getWeatherData(lat, lon)
        response.body()
    }
}