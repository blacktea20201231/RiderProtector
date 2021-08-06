package com.example.riderprotector.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import com.example.riderprotector.util.Constants.ZOOM_LEVEL_DEFAULT
import com.example.riderprotector.util.Constants.ZOOM_LEVEL_MAP_START
import com.example.riderprotector.util.Permissions.hasLocationPermission
import com.example.riderprotector.util.Permissions.requestLocationPermission
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.riderprotector.HotspotsObject.Coordinate
import com.example.riderprotector.HotspotsObject.Details
import com.example.riderprotector.HotspotsObject.Hotspot
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
import java.text.SimpleDateFormat
import java.util.*


class HotspotsMapFragment : Fragment(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks,
    ClusterManager.OnClusterClickListener<MyClusterItem>, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {


    private lateinit var googleMap: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val dublin = LatLng(53.3498, -6.2603)
    private var database = FirebaseDatabase.getInstance().getReference("hotspots")
    private var clusterManager: ClusterManager<MyClusterItem>? = null
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //request location permission, override onRequestPermission result
        if (!hasLocationPermission(requireContext())) {
            requestLocationPermission(this)
        }
        //Initialize View
        val view = inflater.inflate(R.layout.fragment_hotspots_map, container, false)
        //Initialize Map Fragment
        supportMapFragment =
            childFragmentManager.findFragmentById(R.id.hotspot_map) as SupportMapFragment
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission(this)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(
            "location_permission", "permission " +
                    "granted!"
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //assign to google map var
        this.googleMap = googleMap
        //enable location
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission(this)
            return
        }
        googleMap.isMyLocationEnabled = true

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
        googleMap.setOnMapClickListener(this)
        googleMap.setOnMapLongClickListener(this)
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

    // add items onto the map, NOT　MARKERS!!!
    private fun addItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    Log.d("Hotspots",
                        "Latitude:" + i.child("coordinate").child("latitude").value.toString()
                            .toDouble())
                    val offsetItem =
                        MyClusterItem(
                            i.child("coordinate").child("latitude").value.toString().toDouble(),
                            i.child("coordinate").child("longitude").value.toString().toDouble(),
                            i.child("details").child("title").value.toString(),
                            i.child("details").child("brief").value.toString()
                        )
                    clusterManager?.addItem(offsetItem)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("firebaseTesting", error.message)
            }
        })
    }

    private fun addNewItems(title: Editable, brief: Editable, p0: LatLng) {
        clusterManager?.addItem(
            MyClusterItem(p0.latitude, p0.longitude, title.toString(), brief.toString())
        )
    }
    
    override fun onClusterClick(cluster: Cluster<MyClusterItem>?): Boolean {
        return true
    }


    override fun onMarkerClick(p0: Marker): Boolean {
        return false
    }

    override fun onMapClick(p0: LatLng) {
        Log.d("Hotpsots_map", "mapClicked $p0")
    }

    override fun onMapLongClick(p0: LatLng) {
        Log.d("Hotpsots_map", "map Long Clicked $p0")
        showReportDialog(p0)
    }

    private fun showReportDialog(p0: LatLng) {
        val view = View.inflate(context, R.layout.report_dialog, null)
        val builder = AlertDialog.Builder(context).setView(view)
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.white)
        val title = view.findViewById<EditText>(R.id.report_title).text
        val brief = view.findViewById<EditText>(R.id.report_brief).text

        val reportBTN = view.findViewById<Button>(R.id.btn_report)
        reportBTN.setOnClickListener{
            //test for the value get from user
            Log.d("Report","Title: $title")
            Log.d("Report","Brief: $brief")
            uploadNewSpots(title,brief,p0)
            dialog.cancel()

        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun uploadNewSpots(title: Editable, brief: Editable, p0: LatLng) {
        val hotspot = Hotspot(
            Coordinate(p0.latitude,p0.longitude),
            Details(
                null,
                brief.toString(),
                SimpleDateFormat("dd-MM-yyyy HH:MM:SS").format(Date()),
            null,
            title.toString())
        )
        val key = ("${p0.latitude}+${p0.longitude}").replace('.', '_')
        database.child(key).setValue(hotspot)
            .addOnSuccessListener {
            Log.d("firebase_database","hotspot added successfully")
                addNewItems(title, brief, p0)
                title.clear()
                brief.clear()

        }.addOnFailureListener{
                Log.d("firebase_database","hotspot adding Failed: $it")
        }
    }
}