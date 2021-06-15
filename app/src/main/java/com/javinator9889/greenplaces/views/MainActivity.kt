package com.javinator9889.greenplaces.views

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), GoogleMap.OnCameraIdleListener {
    private lateinit var map: GoogleMap
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    init {
        lifecycleScope.launchWhenStarted {
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

            Timber.d("Adding marker...")
            map.addMarker {
                position(LatLng(40.8497241, -3.9720852))
                title("Peñalara")
                snippet("Montaña de gran altura con un parque con lagos, vegetación y fauna, como águilas y buitres")
//                icon(BitmapDescriptorFactory.fromResource(R.drawable.pe_alara))
            }
            map.setOnCameraIdleListener(this@MainActivity)
//            map.setOnCameraIdleListener {
//
//            }
//            Timber.d("Creating bounds...")
//            val bounds = with(LatLngBounds.Builder()) {
//                val pos = LatLng(location.latitude, location.longitude)
//                for (i in 0..270 step 90) {
//                    include(SphericalUtil.computeOffset(pos, 1e4, i.toDouble()))
//                }
//                build()
//            }
//
//            Timber.d("Northeast: ${bounds.northeast}")
//            Timber.d("Southwest: ${bounds.southwest}")
//            val rectangle =
//                createRectangle(LatLng(location.latitude, location.longitude), 1.0, 1.0)
//            Timber.d(rectangle.toString())
//            map.addPolygon {
//                addAll(rectangle)
//                strokeColor(Color.BLACK)
//            }
//            SphericalUtil.computeOffset()
//            val bounds = rectangleBounds(
//                LatLng(
//                    location.latitude,
//                    location.longitude
//                ), 1.0, 1.0
//            )
//            Timber.d("Northeast: $northeast")
//            Timber.d("Southwest: $southwest")
//            val api = withContext(Dispatchers.IO) {
//                WAQIApi.fromBounds(bounds)
////                Timber.d(api.toString())
//            }
//            for (data in api.data) {
//                map.addMarker {
//                    position(LatLng(data.lat, data.lon))
//                    title(data.station.name)
//                    snippet(data.aqi)
//                }
//            }
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

    override fun onCameraIdle() {
        lifecycleScope.launch {
            if (!::map.isInitialized)
                return@launch
            val bounds = map.projection.visibleRegion.latLngBounds
            val api = withContext(Dispatchers.IO) {
                WAQIApi.fromBounds(bounds)
            }
            if (api.data.isNotEmpty()) {
                val heatmap = heatmapTileProviderWithWeightedData(api.data.map {
                    WeightedLatLng(LatLng(it.lat, it.lon), it.aqi.toDouble())
                }, radius = 50)
                map.addTileOverlay { tileProvider(heatmap) }
            }
//            with(HeatmapTileProvider.Builder()) {
//                heatmapTileProviderWithWeightedData()
//            }
//            for (data in api.data) {
//                map.addCircle {
//                    center(LatLng(data.lat, data.lon))
//                    radius(1e3)
//                    fillColor(if (data.aqi < 40) Color.GREEN else Color.RED)
//                }
//            }
        }
    }
}