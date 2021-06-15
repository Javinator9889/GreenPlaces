package com.javinator9889.greenplaces.views

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.ktx.*
import com.google.maps.android.ktx.utils.heatmaps.heatmapTileProviderWithWeightedData
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.api.firebase.Storage
import com.javinator9889.greenplaces.databinding.ActivityMainBinding
import com.javinator9889.greenplaces.databinding.UploadImgBinding
import com.javinator9889.greenplaces.datamodels.ImageCatcher
import com.javinator9889.greenplaces.utils.extensions.await
import com.javinator9889.greenplaces.utils.extensions.latlng
import com.javinator9889.greenplaces.viewmodels.HeatMapsModel
import com.javinator9889.greenplaces.viewmodels.MarkersViewModel
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.color
import com.mikepenz.iconics.utils.paddingDp
import com.mikepenz.iconics.utils.sizeDp
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    companion object {
        val AQIGradient = Gradient(
            intArrayOf(
                Color.GREEN,
                Color.YELLOW,
                Color.rgb(255, 159, 17),
                Color.rgb(218, 0, 25),
                Color.rgb(125, 0, 152),
                Color.rgb(135, 0, 23)
            ),
            floatArrayOf(0.1F, 0.2F, 0.3F, 0.4F, 0.6F, 1F)
        )
    }

    private lateinit var map: GoogleMap
    private lateinit var img: ImageCatcher
    private lateinit var overlay: TileOverlay
    private lateinit var binding: ActivityMainBinding
    private lateinit var provider: HeatmapTileProvider
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val heatMapsModel: HeatMapsModel by viewModels()
    private val markersViewModel: MarkersViewModel by viewModels()


    init {
        lifecycleScope.launchWhenCreated {
            Timber.d("Defining heatmap observer")
            heatMapsModel.heatMaps.observe(this@MainActivity) {
                Timber.d("Received weights: $it")
                if (it.isNotEmpty()) {
                    if (!::provider.isInitialized) {
                        provider = heatmapTileProviderWithWeightedData(
                            it,
                            radius = 50,
                            maxIntensity = 500.0,
                            gradient = AQIGradient
                        )
                        overlay = map.addTileOverlay { tileProvider(provider) }!!
                    } else {
                        provider.setWeightedData(it)
                        overlay.clearTileCache()
                    }
                }
                binding.progressBar.visibility = View.INVISIBLE
            }
            markersViewModel.markers.observe(this@MainActivity) {
                Timber.d("Received new marker!")
                if (::map.isInitialized) {
                    map.addMarker {
                        title(it.title)
                        val bitmap =
                            ResourcesCompat.getDrawable(resources, it.icon, null)?.toBitmap()
                        bitmap?.let {
                            icon(BitmapDescriptorFactory.fromBitmap(it))
                        }
                        snippet(it.description?.take(16))
                        position(it.position)
                    }?.let { marker -> marker.tag = it.md5sum }
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
                binding.progressBar.visibility = View.VISIBLE
                Timber.d("Updating visible stations to range: [${map.projection.visibleRegion.latLngBounds.northeast}, ${map.projection.visibleRegion.latLngBounds.southwest}]")
                heatMapsModel.toggleRun(map.projection.visibleRegion.latLngBounds)
                markersViewModel.toggleRun(map.projection.visibleRegion.latLngBounds)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.progressBar.visibility = View.VISIBLE

        binding.bottomAppBar.navigationIcon =
            IconicsDrawable(this, GoogleMaterial.Icon.gmd_menu).apply {
                sizeDp = 24
                paddingDp = 1
                color = IconicsColor.colorInt(Color.WHITE)
            }
        binding.fab.setImageDrawable(
            IconicsDrawable(
                this,
                GoogleMaterial.Icon.gmd_add_a_photo
            ).apply {
                sizeDp = 24
                paddingDp = 1
            })
        supportActionBar?.hide()

        val takePic =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
                if (saved) {
                    val imgBinding = UploadImgBinding.inflate(layoutInflater)
                    Glide.with(this)
                        .load(img.file)
                        .into(imgBinding.imageView)

                    MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        customView(view = imgBinding.root)
                        negativeButton(R.string.cancel) {
                            it.dismiss()
                        }
                        lifecycleOwner(this@MainActivity)
                        noAutoDismiss()
                        positiveButton(R.string.send) {
                            if (imgBinding.imageTitle.editText!!.text.isEmpty()) {
                                Toast.makeText(
                                    this@MainActivity,
                                    R.string.missing_title,
                                    Toast.LENGTH_LONG
                                ).show()
                                return@positiveButton
                            } else {
                                lifecycleScope.launch {
                                    Storage.uploadImage(
                                        img = img.file,
                                        location = fusedLocationClient.lastLocation.await().latlng,
                                        title = imgBinding.imageTitle.editText!!.text.toString(),
                                        description = imgBinding.imageDescription.editText!!.text.toString()
                                    ) { upload ->
                                        val percentage =
                                            ((upload.bytesTransferred / upload.totalByteCount) * 100).toInt()
                                        with(binding.progressBar) {
                                            isIndeterminate = false
                                            visibility = View.VISIBLE
                                            max = 100
                                            progress = percentage
                                        }
                                        if (percentage >= 100) {
                                            binding.progressBar.visibility = View.GONE
                                            binding.progressBar.isIndeterminate = true
                                            Snackbar.make(
                                                binding.coordinator,
                                                R.string.upload_done,
                                                Snackbar.LENGTH_LONG
                                            ).show()
                                            markersViewModel.toggleRun(map.projection.visibleRegion.latLngBounds)
                                        }
                                    }
                                }
                                it.dismiss()
                            }
                        }
                    }
                }
            }
        binding.fab.setOnClickListener {
            img = ImageCatcher.createImageFile(this)
            takePic.launch(img.photoURI("com.javinator9889.greenplaces.fileprovider"))
//            runCatching { startActivityFor }
        }
    }
}