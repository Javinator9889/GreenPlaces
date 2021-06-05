package com.javinator9889.greenplaces.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.javinator9889.greenplaces.R

class MainActivity : AppCompatActivity() {
    private lateinit var mapFragment: SupportMapFragment

    init {
        lifecycleScope.launchWhenStarted {
            val map = mapFragment.awaitMap()
            map.addMarker {
                position(LatLng(.0, .0))
                title("Maker")
            }
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}