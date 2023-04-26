package com.example.wheatherapp

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.wheatherapp.databinding.ActivityMapsBinding
import com.example.wheatherapp.models.WeatherData
import com.example.wheatherapp.utils.getWeatherData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

private const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var marker: Marker
    private lateinit var mSearchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mSearchView = binding.searchView
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val addressList: List<Address>?
                val markerOptions = MarkerOptions()

                if (query != null && query.isNotBlank()) {
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        addressList = geocoder.getFromLocationName(query, 1)
                        if (addressList?.isNotEmpty()!!) {
                            val address = addressList[0]
                            val latLng = LatLng(address.latitude, address.longitude)
                            markerOptions.position(latLng)
                            marker.position = markerOptions.position
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                        } else {
                            Toast.makeText(this@MapsActivity,
                                "No Results Found",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    @SuppressLint("MissingPermission")
    private fun findLocation() {
        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        val location = fusedLocationProvider.lastLocation

        location.addOnSuccessListener {
            Log.d(TAG, "findLocation: $it")
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()

            marker = mMap.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))!!

            val camera = CameraPosition.Builder()
                .target(LatLng(it.latitude, it.longitude))
                .zoom(18f)
                .bearing(it.bearing)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera))
        }.addOnFailureListener {
            Toast.makeText(this, "Error${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        findLocation()

        mMap.setOnMapClickListener {
            Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            marker.position = it
            CoroutineScope(IO).launch {
                val weatherDataDeffered: Deferred<WeatherData?> = async {
                    getWeatherData(it.latitude, it.longitude)

                }
            }
            val intent = Intent(this@MapsActivity, MainActivity::class.java).apply {
                putExtra("latitude", it.latitude)
                putExtra("longitude", it.longitude)
            }
            startActivity(intent)
        }
        /** marker clickListener
         *
        mMap.setOnMarkerClickListener(object : OnMarkerClickListener{
        override fun onMarkerClick(p0: Marker): Boolean {

        return true
        }
        })
         **/
    }
}
