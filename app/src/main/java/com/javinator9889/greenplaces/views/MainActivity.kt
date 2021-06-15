package com.javinator9889.greenplaces.views

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import com.google.maps.android.ktx.*
import com.google.maps.android.ktx.utils.heatmaps.heatmapTileProviderWithWeightedData
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.api.WAQIApi
import com.javinator9889.greenplaces.datamodels.Bounds
import com.javinator9889.greenplaces.utils.extensions.await
import com.javinator9889.greenplaces.viewmodels.HeatMapsModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    private lateinit var provider: HeatmapTileProvider
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val heatMapsModel: HeatMapsModel by viewModels()

    init {
        lifecycleScope.launchWhenCreated {
            Timber.d("Defining heatmap observer")
            heatMapsModel.heatMaps.observe(this@MainActivity) {
                Timber.d("Received weights: $it")
                if (it.isNotEmpty()) {
                    if (!::provider.isInitialized) {
                        provider = heatmapTileProviderWithWeightedData(it, radius = 50)
                        map.addTileOverlay { tileProvider(provider) }
                    } else provider.setWeightedData(it)
                }
            }
            map = mapFragment.awaitMap()
            val location = fusedLocationClient.lastLocation.await()
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude), 11F
                )
            )
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isZoomGesturesEnabled = true
            map.setOnCameraIdleListener {
                if (!::map.isInitialized)
                    return@setOnCameraIdleListener
                Timber.d("Updating visible stations to range: [${map.projection.visibleRegion.latLngBounds.northeast}, ${map.projection.visibleRegion.latLngBounds.southwest}]")
                heatMapsModel.toggleRun(map.projection.visibleRegion.latLngBounds)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun rectangleBounds(center: LatLng, halfWidth: Double, halfHeight: Double) =
        Bounds(
            LatLng(center.latitude + halfHeight, center.longitude + halfWidth),
            LatLng(center.latitude - halfHeight, center.longitude - halfHeight)
        )

//    override fun onCameraIdle() {
//        lifecycleScope.launch {
//            if (!::map.isInitialized)
//                return@launch
//            val bounds = map.projection.visibleRegion.latLngBounds
//            val api = withContext(Dispatchers.IO) {
//                WAQIApi.fromBounds(bounds)
//            }
//            if (api.data.isNotEmpty()) {
//                val heatmap = heatmapTileProviderWithWeightedData(api.data.map {
//                    WeightedLatLng(LatLng(it.lat, it.lon), it.aqi.toDouble())
//                }, radius = 50)
//                map.addTileOverlay { tileProvider(heatmap) }
//            }
////            with(HeatmapTileProvider.Builder()) {
//                heatmapTileProviderWithWeightedData()
//            }
//            for (data in api.data) {
//                map.addCircle {
//                    center(LatLng(data.lat, data.lon))
//                    radius(1e3)
//                    fillColor(if (data.aqi < 40) Color.GREEN else Color.RED)
//                }
//            }
//        }
}