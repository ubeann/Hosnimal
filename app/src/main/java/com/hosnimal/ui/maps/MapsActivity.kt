package com.hosnimal.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hosnimal.R
import com.hosnimal.databinding.ActivityMapsBinding
import com.hosnimal.model.Hospital

class MapsActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityMapsBinding? = null
    private val binding get() = _binding!!

    // Google Maps
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Receive data
        val hospital = intent.getParcelableExtra<Hospital>(EXTRA_HOSPITAL)

        // Prepare View Binding
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Data Hospital
        hospital?.let { dataHospital ->
            binding.navbar.title.text = dataHospital.name
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            // Add a marker in Hospital and move the camera
            hospital?.let { dataHospital ->
                val location = LatLng(hospital.x, hospital.y)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(dataHospital.name)
                )
                mMap.setMinZoomPreference(16.0f)
                mMap.setMaxZoomPreference(20.0f)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
            }
        }

        // Setting Button Back
        binding.navbar.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_HOSPITAL = "extra_hospital"
    }
}