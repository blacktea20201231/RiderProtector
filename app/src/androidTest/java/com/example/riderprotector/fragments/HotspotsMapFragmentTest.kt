package com.example.riderprotector.fragments

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.riderprotector.hotspotsObject.Hotspot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class HotspotsMapFragmentTest {

    private lateinit var database: DatabaseReference

    @Before
    fun setUp(){
        database = FirebaseDatabase.getInstance().getReference("hotspots")
    }

    @Test
    fun uploadNewSpot() {

    }
}