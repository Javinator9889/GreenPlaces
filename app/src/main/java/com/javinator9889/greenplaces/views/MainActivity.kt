package com.javinator9889.greenplaces.views

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.ktx.*
import com.google.maps.android.ktx.utils.heatmaps.heatmapTileProviderWithWeightedData
import com.javinator9889.greenplaces.R
import com.javinator9889.greenplaces.databinding.ActivityMainBinding
import com.javinator9889.greenplaces.databinding.UploadImgBinding
import com.javinator9889.greenplaces.datamodels.ImageCatcher
import com.javinator9889.greenplaces.utils.extensions.await
import com.javinator9889.greenplaces.viewmodels.HeatMapsModel
import com.mikepenz.iconics.IconicsColor
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial
import com.mikepenz.iconics.utils.color
import com.mikepenz.iconics.utils.paddingDp
import com.mikepenz.iconics.utils.sizeDp
import timber.log.Timber
import java.io.File

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity() {
    private lateinit var map: GoogleMap
    private lateinit var img: ImageCatcher
    private lateinit var binding: ActivityMainBinding
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
                    binding.progressBar.visibility = View.INVISIBLE
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
                    val stream = File(img.currentPhotoPath).inputStream()
                    imgBinding.imageView.setImageBitmap(BitmapFactory.decodeStream(stream))
                    imgBinding.submit.setOnClickListener {
                        Toast.makeText(
                            this,
                            "Submit",
                            Toast.LENGTH_LONG
                        ).show()
                    }
//                    val v = layoutInflater.inflate(R.layout.upload_img)
                    MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                        customView(view = imgBinding.root)
                        positiveButton(text = "Enviar")
                        negativeButton(text = "Cancelar")
                        lifecycleOwner(this@MainActivity)
                    }
                }
//                if (saved) {
//                    startActivity(Intent(this, ImageUploadActivity::class.java).apply {
//                        putExtra(ImageUploadActivity.IMAGE_EXTRA, img.currentPhotoPath)
//                    })
//                }
            }
        binding.fab.setOnClickListener {
            img = ImageCatcher.createImageFile(this)
            takePic.launch(img.photoURI("com.javinator9889.greenplaces.fileprovider"))
//            runCatching { startActivityFor }
        }
    }
}