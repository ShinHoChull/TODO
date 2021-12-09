package com.example.todo.a

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.a.service.MyService3
import com.example.todo.common.Defines
import com.example.todo.vm.AViewModel
import kotlinx.android.synthetic.main.fragment_dialog_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.daum.mf.map.api.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.lang.NullPointerException
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint








class FragmentDialogA : DialogFragment() {


    val viewModel: AViewModel by sharedViewModel()
    lateinit var mDialogV: View
    var mIdx: Long = -1L

    private lateinit var mapView: MapView
    private lateinit var polyLine: MapPolyline

    private val handler = Handler(Looper.getMainLooper())
    private var viewGroup : ViewGroup? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewGroup = container
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
        setUpViewInit()
        setUpMapView()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    private fun polyLineSetUp() {

        mapView.removeAllPolylines()

        GlobalScope.launch(Dispatchers.IO) {
            val rows = viewModel.getAllData()

            /*
            1. 1번 위치와 2번 위치 .
             -이동 거리를 구한다.
             -이동시간 / 평균속도를 구한다.

            2. 2번 위치와 3번 위치
             -이동 거리를 구한다.
             -이동시간 / 평균속도를 구한다.

             예외) 2번 3번 위치 차이가 1번 과 차이가 많이 난다면
                이동시간 체크와 평균속도 차이를 정한다.

             */
            //

            if (rows.isNotEmpty() && mIdx != -1L) {
                Defines.log("idx->${mIdx}")
                viewModel.getGpsList(mIdx).apply {
                    for (j in this.indices) {
                        val gpsRow = this[j]
                        val lat = gpsRow.latDataStr!!
                        val lng = gpsRow.lngDataStr!!
                        Defines.log("polyLAT->${gpsRow.regDateStr} / lat -> $lat / lng -> $lng")
                        polyLine.addPoint(
                            MapPoint.mapPointWithGeoCoord(
                                lat, lng
                            )
                        )

                        val marker = MapPOIItem()
                        marker.itemName = "aa"
                        marker.tag = 0
                        marker.mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)
                        marker.markerType = MapPOIItem.MarkerType.BluePin

                        val inflate  = LayoutInflater.from(requireContext())
                        val view = inflate.inflate(R.layout.item_map_point,
                            this@FragmentDialogA.viewGroup, false)
                        //marker.customCalloutBalloon = view
                       //marker.customImageResourceId = R.drawable.ic_remove



                        mapView.addPOIItem(marker)
                    }
                }

                launch {
                    mapView.addPolyline(polyLine)

                    val mapPointBounds = MapPointBounds(polyLine.mapPoints)
                    val padding = 100
                    mapView.moveCamera(
                        CameraUpdateFactory.newMapPointBounds(
                            mapPointBounds,
                            padding
                        )
                    )

                    //handler.postDelayed(runnable, 5000)
                }
            }
        }
    }


    private fun setUpMapView() {

        mapView = MapView(requireContext())
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5662079, 126.8343948), true);
        map_view.addView(mapView)

        polyLine = MapPolyline()
        polyLine.tag = 1000
        polyLine.lineColor = Color.argb(100, 255, 51, 0)

        this.polyLineSetUp()
    }

    private fun setUpViewInit() {
        if (dialog != null) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private val runnable = Runnable {
        polyLineSetUp()
    }

}