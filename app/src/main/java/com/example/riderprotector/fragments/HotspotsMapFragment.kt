package com.example.riderprotector.fragments

import Constants.LOCATION_UPDATE_INTERVAL
import Constants.PERMISSION_LOCATION_REQUEST_CODE
import Constants.ZOOM_LEVEL_DEFAULT
import Constants.ZOOM_LEVEL_MAP_START
import Constants.button_deny
import Constants.button_enable
import Constants.permission_request_message
import Constants.permission_request_message2
import Constants.permission_request_title
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.riderprotector.R
import com.example.riderprotector.util.LocationsUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HotspotsMapFragment : Fragment(),OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val dublin = LatLng(53.3498, -6.2603)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Initialize View
        var view = inflater.inflate(R.layout.fragment_hotspots_map,container,false)
        //Initialize Map Fragment
        supportMapFragment = childFragmentManager.findFragmentById(R.id.hotspot_map) as SupportMapFragment
        //Async Map
        supportMapFragment.getMapAsync(this)
        //return view
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //basic UI settings
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = false // enlarge/shrink button
            isZoomGesturesEnabled = true //double tap to enlarge/shrink
            isScrollGesturesEnabled = true //map scrolling
            isMapToolbarEnabled = true // to google map,
            isCompassEnabled = true //Compass
        }

//        googleMap.addMarker(MarkerOptions().title("testing Marker").position(dublin))

        lifecycleScope.launch {
            //location, zoom level
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dublin, ZOOM_LEVEL_MAP_START))
            delay(2500)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(dublin, ZOOM_LEVEL_DEFAULT),
                1000,
                null
            )
        }
    }
}