package com.aoinc.w4d4_c_location101

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var latLongTextView : TextView
    
    private val LOCATION_REQUEST_CODE = 707

    private lateinit var overlay: ConstraintLayout
    private lateinit var openSettingsButton : Button

    private lateinit var locationManager: LocationManager

    /* Runtime Permissions
    * 1. check if you have the permission
    * 2. if you don't have the permission, request it
    * 3. override onRequestPermissionResult - handle user input accordingly
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latLongTextView = findViewById(R.id.latlang_textView)
        overlay = findViewById(R.id.permission_overlay)
        openSettingsButton = findViewById(R.id.open_settings_button)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        openSettingsButton.setOnClickListener {
            // implicit intent to open settings... *this specific app's permissions*
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            // uri = package://com.aoinc.w4d4_c_location101/Permissions
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // 1. check if you have the permission
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            registerLocationManager()
        } else {
            requestlocationPermission()
        }
    }

    // 2. if you don't have the permission, request it
    private fun requestlocationPermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // app has received user's response
        // must check if it's the same permission I requested for
        // TODO: flexible solution? seems too 'hardcoded' with array checks
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG_X", "Location Permission -> GRANTED")
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Log.d("TAG_X", "Location Permission -> DENIED, show again")
                        requestlocationPermission()
                    } else {
                        // at this point, let the user know they have to enable
                        // permissions in their settings to use the application
                        Log.d("TAG_X", "Location Permission -> DENIED, DON'T show again")
                        overlay.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationManager() {
        // GPS provider on emulator, Network provider on phone
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10.0f, this)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TAG_X", "My location is: ${location.latitude}, ${location.longitude}")
    }

    override fun onStop() {
        super.onStop()
        locationManager.removeUpdates(this)
    }
}