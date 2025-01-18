package com.example.fbenddemo

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class serverinfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serverinfo)

        // Get references to TextViews
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val localTimeTextView = findViewById<TextView>(R.id.localTimeTextView)
        val ipTextView = findViewById<TextView>(R.id.ipTextView)

        // Get logged-in user's name from Intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Unknown User"
        userNameTextView.text = "Logged in as: $userName"

        // Display local time
        val currentTime = getCurrentLocalTime()
        localTimeTextView.text = "Local Time: $currentTime"

        // Fetch and display public IP address
        fetchPublicIp(ipTextView)
    }

    // Fetch the public IP address from a public API
    private fun fetchPublicIp(ipTextView: TextView) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ip = URL("https://api.ipify.org").readText()
                withContext(Dispatchers.Main) {
                    ipTextView.text = "Public IP: $ip"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@serverinfo, "Failed to fetch IP address", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Get the current local time in a formatted string
    private fun getCurrentLocalTime(): String {
        val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        return formatter.format(Date())
    }
}
