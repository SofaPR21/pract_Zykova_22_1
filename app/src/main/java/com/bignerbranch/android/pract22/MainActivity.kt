package com.bignerbranch.android.pract22

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnGetWeather: Button
    private lateinit var tvResult: TextView
    private lateinit var queue: RequestQueue

    private val apiKey = "eeba78c2f0099d8c0343d0a37bbffba5"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.editCity)
        btnGetWeather = findViewById(R.id.btnGetWeather)
        tvResult = findViewById(R.id.tvResult)

        queue = Volley.newRequestQueue(this)

        btnGetWeather.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                getWeatherData(city)
            }
        }
    }

    private fun getWeatherData(city: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric&lang=ru"
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            {
                    response ->
                Log.d("WeatherAPI", "Response: $response")
                parseJsonResponse(response)
            },
            {
                    error ->
                Log.e("WeatherAPI", "Error: ${error.message}")
                tvResult.text = "Ошибка загрузки данных"
            }
        )

        queue.add(stringRequest)
    }

    private fun parseJsonResponse(jsonString: String) {
        try {
            val gson = Gson()
            val weather = gson.fromJson(jsonString, WeatherResponse::class.java)

            val result = """
                Город: ${weather.name}
                Температура: ${weather.main.temp}°C
                Давление: ${weather.main.pressure} гПа
                Влажность: ${weather.main.humidity}%
                Скорость ветра: ${weather.wind.speed} м/с
            """.trimIndent()

            tvResult.text = result
            Log.d("ParsedWeather", result)

        } catch (e: Exception) {
            Log.e("JSON Parse", "Ошибка парсинга: ${e.message}")
            tvResult.text = "Ошибка при обработке данных"
        }
    }
}

data class WeatherResponse(
    val name: String,
    val main: Main,
    val wind: Wind
)

data class Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double
)