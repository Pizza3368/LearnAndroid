package com.example.fbenddemo

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//button 2
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.security.MessageDigest

//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
import java.util.Locale
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cityName: String = "Unknown City"

    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //init location client
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button 1 listener
        findViewById<Button>(R.id.favoritcity).setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        // button 2 listner
        findViewById<Button>(R.id.detail).setOnClickListener{
            Log.d("MainActivity", "location permission button clicked")
            Toast.makeText(this, "Location permission button", Toast.LENGTH_SHORT).show()
            checkLocationPermission()

        }

        // button 3 listner
        findViewById<Button>(R.id.Login).setOnClickListener{
            Log.d("MainActivity", "Login button clicked")
            Log.d("MainActivity", "Web CLIENT ID: ${BuildConfig.WEB_CLIENT_ID}")

            val credentialManager = CredentialManager.create(this)
            val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(BuildConfig.WEB_CLIENT_ID)
                .setNonce(generateHashedNonce())
            .build()

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            activityScope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@MainActivity,
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    handleFailure(e)
                }
            }

        }

        // button 4 listner
        findViewById<Button>(R.id.settimer).setOnClickListener{
            Log.d("MainActivity", "set timer button clicked")
            val intent = Intent(this, settimer::class.java)
            startActivity(intent)

        }

    }

    private fun handleFailure(e: GetCredentialException) {
        Log.e("MainActivity", "Error getting credential", e)
        Toast.makeText(this, "Error getting credential", Toast.LENGTH_SHORT).show()

    }

    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        updateWelcomMessage(googleIdTokenCredential.displayName.toString())
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("MainActivity", "Received an invalid google id token response", e)
                       
                    }
                }
                else {
                    // Catch any unrecognized credential type here.
                    Log.e("MainActivity", "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e("MainActivity", "Unexpected type of credential")
            }
        }
    }

    private fun updateWelcomMessage(name: String) {
        //something about building a welcome text
    //val welcomeTextView = findViewById<TextView>(R.id.)

    }

    private fun generateHashedNonce(): String{
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold(""){str, it -> str+ "%02x".format(it)}
    }

    fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted
            getLastKnownLocation()
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                getLastKnownLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Get the last known location
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    cityName = getCityName(location)
                    navigateToPhoneDetailPage(cityName)
                } else {
                    Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCityName(location: Location): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

        return if (!addresses.isNullOrEmpty()) { // Check for null or empty list
            addresses[0].locality ?: "Unknown City" // Get locality or default to "Unknown City"
        } else {
            "Unknown City" // Default if no addresses found
        }
    }


    // Navigate to the phone detail page
    private fun navigateToPhoneDetailPage(cityName: String) {
        val intent = Intent(this, phonedetailpage::class.java)
        intent.putExtra("CITY_NAME", cityName)
        startActivity(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}