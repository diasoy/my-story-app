package com.example.mystoryapp.view.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystoryapp.R
import com.example.mystoryapp.data.model.StoryDetail
import com.example.mystoryapp.databinding.ActivityMapsBinding
import com.example.mystoryapp.helper.AppPreferences
import com.example.mystoryapp.view.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var userToken: String
    private lateinit var pref: AppPreferences
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = AppPreferences.getInstance(applicationContext.dataStore)
        userToken = runBlocking { pref.getToken().first() }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel.getLocation(userToken)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mapsViewModel.location.observe(this) { locations ->
            addManyMarkers(locations)
        }

        mapsViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addManyMarkers(locations: List<StoryDetail>) {
        if (locations.isNotEmpty()) {
            locations.forEach { location ->
                val position = LatLng(location.lat, location.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(location.name)
                        .snippet(location.description)
                )
            }
            val firstLocation = LatLng(locations[0].lat, locations[0].lon)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 10f))
        }
    }
}
