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
import com.example.riderprotector.cluster.MyClusterItem
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HotspotsMapFragment : Fragment(),OnMapReadyCallback , EasyPermissions.PermissionCallbacks, ClusterManager.OnClusterClickListener<MyClusterItem>{

    private lateinit var googleMap: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val dublin = LatLng(53.3498, -6.2603)
    private var database = FirebaseDatabase.getInstance().getReference("hotspots")
    private var clusterManager: ClusterManager<MyClusterItem>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //request location permission, override onRequestPermission result
        if (!hasLocationPermission(requireContext())){
            requestLocationPermission(this)
        }
        //Initialize View
        val view = inflater.inflate(R.layout.fragment_hotspots_map,container,false)
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
        Log.d("location_permission","permission " +
                "granted!")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        //add item to cluster
        setUpClusterer()
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
            delay(1500)
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(dublin, 15f),
                1000,
                null
            )
        }
    }

    private fun setUpClusterer() {
        // Position the map.
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(53.3498, -6.2603),
                ZOOM_LEVEL_DEFAULT
            )
        )

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        clusterManager = ClusterManager(context, googleMap)

        // Point the map's listeners at the listeners implemented by the cluster manager.
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        // Add cluster items (markers) to the cluster manager.
        addItems()
    }

    private fun addItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    Log.d("Hotspots","Latitude:"+ i.child("coordinate").child("latitude").value.toString().toDouble())
                    val offsetItem =
                        MyClusterItem(
                            i.child("coordinate").child("latitude").value.toString().toDouble(),
                            i.child("coordinate").child("longitude").value.toString().toDouble(),
                            i.child("details").child("title").value.toString(),
                            i.child("details").child("brief").value.toString())
                    clusterManager?.addItem(offsetItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("firebaseTesting", error.message)
            }
        })
    }

    override fun onClusterClick(cluster: Cluster<MyClusterItem>?): Boolean {
        return true
    }

}