package com.example.riderprotector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.riderprotector.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val hotspotsMapsFragment = HotspotsMapFragment()
    private val hospitalsFragment = HospitalsFragment()
    private val gardaStationMapFragment = GardaStationMapBlankFragment()
    private val bikeShopFragment = BikeShopFragment()
    private val accountFragment = Account()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {

            delay(6000)
            //initial fragment
            replaceFragment(hotspotsMapsFragment)
        }

        findViewById<BottomNavigationView>(R.id.bottom_nav)?.setOnItemSelectedListener{
            when(it.itemId){
                R.id.hotspot_map_title->{replaceFragment(hotspotsMapsFragment)}
                R.id.garda_station_map_title-> replaceFragment(gardaStationMapFragment)
                R.id.hospital_map_title-> replaceFragment(hospitalsFragment)
                R.id.bike_shop_map_title->replaceFragment(bikeShopFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if (fragment!= null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container,fragment)
            transaction.commit()
        }
    }

}