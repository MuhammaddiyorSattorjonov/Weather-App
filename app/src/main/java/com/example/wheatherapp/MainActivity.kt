package com.example.wheatherapp

import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.wheatherapp.databinding.ActivityMainBinding
import com.example.wheatherapp.models.Weather
import com.example.wheatherapp.models.WeatherData
import com.example.wheatherapp.utils.getWeatherData
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URI
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val latitude = intent.getDoubleExtra("latitude",34.0522)
        val longitude = intent.getDoubleExtra("longitude",118.2437)

        CoroutineScope(Dispatchers.IO).launch {
            val weatherDataDeferred: Deferred<WeatherData?> = async {
                getWeatherData(latitude, longitude)
            }
            val weatherData = weatherDataDeferred.await()

            withContext(Dispatchers.Main) {
                binding.apply {
                    txtLocation.text = weatherData?.name

                    txtGradus.text = (weatherData?.main?.temp?.minus(274.15))?.toInt().toString()+"°"
                    txtUpdateAt.text = "Updated at: "+SimpleDateFormat("hh:mm a").format(Date())
                    txtAbout.text = weatherData?.weather!![0].description
                    Glide.with(this@MainActivity).load("https://openweathermap.org/img/wn/${ weatherData?.weather!![0].icon}@2x.png").into(binding.weatherIcon)

                    progress.visibility = View.GONE
                }
            }

        }
    }
}