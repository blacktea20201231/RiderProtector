package com.example.riderprotector.util

import com.example.riderprotector.util.Constants.LOCATION_UPDATE_INTERVAL
import com.example.riderprotector.util.Constants.PERMISSION_LOCATION_REQUEST_CODE
import com.example.riderprotector.util.Constants.button_deny
import com.example.riderprotector.util.Constants.button_enable
import com.example.riderprotector.util.Constants.permission_request_message2
import com.example.riderprotector.util.Constants.permission_request_title
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

object LocationsUtil {

    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        context: Context,
        googleMap: GoogleMap){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            //update frequency
            locationRequest.interval = LOCATION_UPDATE_INTERVAL
            //update times
            //locationRequest.numUpdates = 1
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    @SuppressLint("MissingPermission")  // already asked
                    override fun onLocationResult(result: LocationResult?) {
                        result ?: return

                        val currentLocation =
                            LatLng(
                                result.lastLocation.latitude,
                                result.lastLocation.longitude
                            )
                        Log.d(
                            "current_location",
                            currentLocation.toString()
                        )
                        //clear all marker
                        //googleMap?.clear()
                        googleMap?.isMyLocationEnabled = true
                        // move to current location
//            mMap?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    currentLocation, 15f
//                )
//            )
                    }

                },
                null
            )
        } else {
            requestLocationPermission(context)
        }
    }

    fun checkLocationPermission(context: Context) {
        //Check for location permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            //location permission not granted
            requestLocationPermission(context)
        }
    }

    private fun requestLocationPermission(context: Context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity, Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(context)
                .setTitle(permission_request_title)
                .setMessage(permission_request_message2)
                .setPositiveButton(button_enable) { _, _ ->
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        PERMISSION_LOCATION_REQUEST_CODE
                    )
                }
                .setNegativeButton(button_deny) { _, _ -> requestLocationPermission(context) }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_LOCATION_REQUEST_CODE
            )
        }
    }
}