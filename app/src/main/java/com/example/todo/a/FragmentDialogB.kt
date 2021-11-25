package com.example.todo.a

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.a.service.MyService
import com.example.todo.a.service.MyService2
import com.example.todo.common.Defines
import com.example.todo.common.getNowTimeToStr
import com.example.todo.model.domain.GPS
import com.example.todo.vm.AViewModel
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_dialog_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.daum.mf.map.api.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NullPointerException
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.TaskCompletionSource

import com.google.android.gms.tasks.OnTokenCanceledListener

import com.google.android.gms.tasks.CancellationToken


class FragmentDialogB : DialogFragment() {


    val viewModel: AViewModel by sharedViewModel()
    lateinit var mDialogV: View
    var mIdx : Long = -1L

    private val handler = Handler(Looper.getMainLooper())


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDialogV = inflater.inflate(R.layout.fragment_dialog_a, container, false)
        return mDialogV
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {

            try {
                mIdx = requireArguments().getLong("position", -1)
            } catch (err: NullPointerException) {
                Defines.log(err.toString())
            }
        }

        setUpGPS()
        setUpViewInit()
        //setUpMapView()
    }


    private fun setUpGPS() {

        val mapView = MapView(requireContext())
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5662079, 126.8343948), true);
        map_view.addView(mapView)

        val polyLine = MapPolyline()
        polyLine.tag = 1000
        polyLine.lineColor = Color.argb(128, 255, 51, 0)


        currentLocationComponent = CurrentLocationComponent(requireContext(),
            {
                Defines.log("lat->${it.latitude} / lng -> ${it.longitude}")
                //위경도 저장.
                polyLine.addPoint(
                    MapPoint.mapPointWithGeoCoord(
                        it.latitude
                        , it.longitude
                    )
                )
                //라인 입력.
                mapView.addPolyline(polyLine)

                handler.postDelayed(runnable, 10000)

            },
            {
                Toast.makeText(requireContext() , "맵 Error",Toast.LENGTH_SHORT).show()
                Defines.log("${it}")
            }
        )

        getCurrentLocation()

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult?) {
//                locationResult ?: return
//                for (location in locationResult.locations) {
//                    // Update UI with location data
//                    if (location != null) {
//
//                        Defines.log("lat.. -> ${location.latitude} lng.. -> ${location.longitude} ${getNowTimeToStr()}")
//
//                        polyLine.addPoint(
//                            MapPoint.mapPointWithGeoCoord(
//                                location.latitude
//                                , location.longitude
//                            )
//                        )
//
//                        mapView.addPolyline(polyLine)
//                    }
//                }
//            }
//        }



    }

    private fun setUpMapView() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Toast.makeText(
                requireContext(), "ACCESS_FINE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()
        } else if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(), "ACCESS_COARSE_LOCATION not permission", Toast.LENGTH_SHORT
            ).show()
        } else {

            /**
             * PRIORITY_BALANCED_POWER_ACCURACY - 이 설정을 사용하여 도시 블록 내의 위치 정밀도를 요청합니다.
             * 정확성은 대략 100미터입니다. 이는 대략적인 수준의 정확성으로 간주되며 전력을 더 적게 소비할 수 있습니다.
             * 이 설정을 사용하면 위치 서비스에서 Wi-Fi와 휴대폰 기지국 위치를 사용할 수 있습니다. 그러나 위치 정보 제공자의 선택은 사용할 수 있는 소스 등 다른 많은 요소에 따라 달라집니다.
             *
            PRIORITY_HIGH_ACCURACY - 이 설정을 사용하여 가장 정확한 위치를 요청합니다.
            이 설정을 사용하면 위치 서비스가 GPS를 사용하여 위치를 확인할 가능성이 높습니다.

            PRIORITY_LOW_POWER - 이 설정을 사용하여 도시 수준의 정밀도를 요청합니다.
            대략 10킬로미터의 정확성입니다.
            이는 대략적인 수준의 정확성으로 간주되며 전력을 더 적게 소비할 수 있습니다.

            PRIORITY_NO_POWER - 전력 소비에 별다른 영향을 미치지 않으면서 사용 가능한 경우 위치 업데이트를
            수신하려면 이 설정을 사용합니다.
            이 설정을 사용하면 앱에서 위치 업데이트를 트리거하지 않고 다른 앱에서 트리거한 위치를 수신합니다.
             *
             */
//            val locationRequest = LocationRequest.create().apply {
//                interval = 10000
//                fastestInterval = 10000
//                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            }
//
//            fusedLocationClient.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
        }

    }

    private fun setUpViewInit() {
        if (dialog != null) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }



    private lateinit var currentLocationComponent: CurrentLocationComponent
    private fun getCurrentLocation() {

        currentLocationComponent.getCurrentLocation()
    }


    private val runnable = Runnable {
        getCurrentLocation()
    }

}