package com.example.fbenddemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class settimer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settimer)

        // Adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // References to input fields, button, and image view
        val minutesInput = findViewById<EditText>(R.id.minutesInput)
        val secondsInput = findViewById<EditText>(R.id.secondsInput)
        val startTimerButton = findViewById<Button>(R.id.startTimerButton)
        val resultImage = findViewById<ImageView>(R.id.resultImage)

        // Set up button click listener
        startTimerButton.setOnClickListener {
            val minutes = minutesInput.text.toString().toIntOrNull() ?: 0
            val seconds = secondsInput.text.toString().toIntOrNull() ?: 0

            // Calculate total time in milliseconds
            val totalTimeMillis = (minutes * 60 + seconds) * 1000L

            if (totalTimeMillis > 0) {
                // Start the timer
                Toast.makeText(this, "Timer started for $minutes minutes and $seconds seconds", Toast.LENGTH_SHORT).show()

                // Run a delayed task
                Handler(Looper.getMainLooper()).postDelayed({
                    // Show toast when time is up
                    Toast.makeText(this, "Oh no! Time is up!", Toast.LENGTH_LONG).show()

                    // Load and display the image
                    Glide.with(this)
                        .load("https://plus.unsplash.com/premium_photo-1667030474693-6d0632f97029?fm=jpg&q=60&w=3000&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8NXx8Y2F0fGVufDB8fDB8fHww")
                        .into(resultImage)
                    resultImage.visibility = ImageView.VISIBLE
                }, totalTimeMillis)
            } else {
                // Invalid input or zero time
                Toast.makeText(this, "Please enter a valid time", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
