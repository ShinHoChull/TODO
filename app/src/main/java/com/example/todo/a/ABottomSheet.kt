package com.example.todo.a

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.todo.R
import com.example.todo.common.Defines

import com.google.android.gms.location.*

import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_a_fragment.*
import net.daum.mf.map.api.MapView

import org.koin.android.ext.android.inject

class ABottomSheet : BottomSheetDialogFragment() {


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_a_fragment, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpLocation()
        setUpMap()
    }

    private fun setUpMap() {
        val mapView = MapView(requireContext())
        map_view.addView(mapView)
    }

    private fun setUpLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        this.createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    // ...
                    Defines.log("lat -> ${location.latitude} lng -> ${location.longitude}")
                }
            }
        }

    }


    override fun onResume() {
        super.onResume()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ) {

            Toast.makeText(requireContext()
                ,"ACCESS_FINE_LOCATION not permission"
                , Toast.LENGTH_SHORT
            ).show()
        }
         else if ( ActivityCompat.checkSelfPermission (
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext()
                ,"ACCESS_COARSE_LOCATION not permission"
                , Toast.LENGTH_SHORT
            ).show()

        }
        else {

            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 15000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())



            fusedLocationClient.requestLocationUpdates(locationRequest ,
                locationCallback,
            Looper.getMainLooper())

//            fusedLocationClient.lastLocation.addOnSuccessListener {
//                Defines.log("lat -> ${it.latitude} lng -> ${it.longitude}")
//            }
        }
    }

    override fun onPause() {
        super.onPause()
        Defines.log("locationRemove")

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun createLocationRequest() {



    }




}