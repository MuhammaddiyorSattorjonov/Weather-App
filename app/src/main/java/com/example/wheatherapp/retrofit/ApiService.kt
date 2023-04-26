package com.example.wheatherapp.retrofit

import com.example.wheatherapp.models.WeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "b91c3d388a4353fa2ae870703adc60cb"

interface ApiService {

    @GET("weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") appId: String = API_KEY,
    ): Response<WeatherData>
}