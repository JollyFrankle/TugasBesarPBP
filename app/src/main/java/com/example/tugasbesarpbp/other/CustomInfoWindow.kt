package com.example.tugasbesarpbp.other

import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.databinding.LayoutMapTooltipBinding
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(mapView: MapView?): InfoWindow(R.layout.layout_map_tooltip, mapView) {
    private var _binding: LayoutMapTooltipBinding? = null
    private val binding get() = _binding!!

    override fun onOpen(item: Any?) {
        _binding = LayoutMapTooltipBinding.bind(view)

        val marker = item as Marker
        val infoWindowData = marker.relatedObject as LocationModel

        val tvNamaLokasi = binding.tvNamaLokasi
        val tvAlamat = binding.tvAlamat
        val imageClose = binding.imageClose

        tvNamaLokasi.text = infoWindowData.name
        tvAlamat.text = infoWindowData.vicinity
        imageClose.setOnClickListener {
//            close()
            marker.closeInfoWindow()
        }
    }

    override fun onClose() {

    }
}