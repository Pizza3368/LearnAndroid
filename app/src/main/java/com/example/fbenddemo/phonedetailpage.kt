package com.example.fbenddemo

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class phonedetailpage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_phonedetailpage)

        // Adjust for system bars (Edge-to-edge UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the city name passed from MainActivity
        val cityName = intent.getStringExtra("CITY_NAME") ?: "Unknown City"

        // Get phone manufacturer and model
        val phoneManufacturer = android.os.Build.MANUFACTURER
        val phoneModel = android.os.Build.MODEL

        // Set city name, manufacturer, and model to TextViews
        findViewById<TextView>(R.id.cityNameTextView).text = "Your City: $cityName"
        findViewById<TextView>(R.id.phoneManufacturerTextView).text = "Manufacturer: $phoneManufacturer"
        findViewById<TextView>(R.id.phoneModelTextView).text = "Model: $phoneModel"
    }
}
