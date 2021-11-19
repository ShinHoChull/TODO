package com.example.todo.a

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todo.R
import com.example.todo.vm.AViewModel
import kotlinx.android.synthetic.main.fragment_dialog_a.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPolyline
import net.daum.mf.map.api.MapView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FragmentDialogA : DialogFragment() {


    val viewModel: AViewModel by sharedViewModel()
    lateinit var mDialogV: View

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
        setUpViewInit()
        setUpMapView()
    }

    private fun setUpMapView() {
        val mapView = MapView(requireContext())
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5662079, 126.8343948), true);
        map_view.addView(mapView)

        val polyLine = MapPolyline()
        polyLine.tag = 1000
        polyLine.lineColor = Color.parseColor("#000000")

        GlobalScope.launch(Dispatchers.IO) {
            val rows = viewModel.getAllData()
            if (rows.isNotEmpty()) {
                for (i in rows.indices) {
                    val todoRow = rows[i]
                    viewModel.getGpsList(todoRow.id!!).apply {
                        for (j in this.indices) {
                            val gpsRow = this[j]
                            val lat = gpsRow.latDataStr!!
                            val lng = gpsRow.lngDataStr!!

                            polyLine.addPoint(
                                MapPoint.mapPointWithGeoCoord(
                                    lat, lng
                                )
                            )
                        }
                    }
                }
            }

            launch {
                mapView.addPolyline(polyLine)
            }
        }


    }

    private fun setUpViewInit() {
        if (dialog != null) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

}