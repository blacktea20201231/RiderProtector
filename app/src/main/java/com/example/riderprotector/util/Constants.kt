package com.example.riderprotector.util

import com.google.android.gms.maps.model.LatLng

object Constants {
    const val button_deny = "Deny"
    const val button_enable = "Enable"
    const val permission_request_title = "Enable Location"
    const val permission_request_message = "Some core functionality will not be working if the location permission is denied."
    const val permission_request_message2 = "This app will need your current location for core functionality."
    const val PERMISSION_LOCATION_REQUEST_CODE = 10101
    const val PERMISSION_BACKGROUND_LOCATION_REQUEST_CODE = 20202
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val ZOOM_LEVEL_DEFAULT = 15f
    const val ZOOM_LEVEL_MAP_START = 10f
    const val ZOOM_LEVEL_1 = 12f
    const val ZOOM_LEVEL_2 = 13f
    const val ZOOM_LEVEL_3 = 14f
    const val ZOOM_LEVEL_4 = 15f
    const val ZOOM_LEVEL_5 = 16f
    const val REQUEST_PHONE_PERMISSION = 30303
    const val SPLASH_DISPLAY_LENGTH = 5000
    const val DATAPATH_GARDA_STATION = "gardai_station"
    const val DATAPATH_BIKE_SHOP = "bike_shop"
    var dublin = LatLng(53.3498, -6.2603)
}