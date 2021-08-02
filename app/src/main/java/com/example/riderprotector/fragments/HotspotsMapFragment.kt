package com.example.riderprotector.fragments

import com.example.riderprotector.util.Constants.ZOOM_LEVEL_DEFAULT
import com.example.riderprotector.util.Constants.ZOOM_LEVEL_MAP_START
import com.example.riderprotector.util.Permissions.hasLocationPermission
import com.example.riderprotector.util.Permissions.requestLocationPermission
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.riderprotector.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HotspotsMapFragment : Fragment(),OnMapReadyCallback , EasyPermissions.PermissionCallbacks{

    private lateinit var googleMap: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val dublin = LatLng(53.3498, -6.2603)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //request location permission, override onRequestPermission result
        if (!hasLocationPermission(requireContext())){
            requestLocationPermission(this)
        }
        //Initialize View
        var view = inflater.inflate(R.layout.fragment_hotspots_map,container,false)
        //Initialize Map Fragment
        supportMapFragment = childFragmentManager.findFragmentById(R.id.hotspot_map) as SupportMapFragment
        //Async Map
        supportMapFragment.getMapAsync(this)
        //return view
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            SettingsDialog.Builder(requireActivity()).build().show()
        }else{
            requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d("location_permission","permission granted!")
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