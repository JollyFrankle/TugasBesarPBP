package com.example.tugasbesarpbp.main_ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.example.tugasbesarpbp.BuildConfig
import com.example.tugasbesarpbp.HomeActivity
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.databinding.FragmentMapsBinding
import com.example.tugasbesarpbp.other.CustomInfoWindow
import com.example.tugasbesarpbp.other.LocationModel
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.nio.charset.StandardCharsets

class MapsFragment : Fragment() {
    var locationList: MutableList<LocationModel> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        // agar peta bisa dibuat dari internet, bisa juga sih pake yg di atas ^^^
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID)

        // setup maps
        binding.map.setMultiTouchControls(true)
//        binding.map.controller.animateTo(geoPoint)
        binding.map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.DEFAULT_TILE_SOURCE)

        mapController = binding.map.controller as MapController
//        mapController.setCenter(geoPoint)
        mapController.setZoom(16.0)

        getLocationMarker()
    }

    private fun getLocationMarker() {
        try {
            val stream = requireActivity().assets.open("map_data.json")
            val size = stream.available()
            val buffer = ByteArray(size)

            stream.read(buffer)
            stream.close()

            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArrayResult = jsonObject.getJSONArray("results")
                for (i in 0 until jsonArrayResult.length()) {
                    val jsonObjectResult = jsonArrayResult.getJSONObject(i)
                    val modelMain = LocationModel()
                    modelMain.name = jsonObjectResult.getString("name")
                    modelMain.vicinity = jsonObjectResult.getString("vicinity")

                    // get lat lng
                    val jsonObjectGeometry = jsonObjectResult.getJSONObject("geometry")
                    val jsonObjectLocation = jsonObjectGeometry.getJSONObject("location")
                    modelMain.latLoc = jsonObjectLocation.getDouble("lat")
                    modelMain.lngLoc = jsonObjectLocation.getDouble("lng")

                    locationList.add(modelMain)
                }
                initMarker(locationList)

                if(locationList.isNotEmpty()) {
                    // get northenmost, southernmost, easternmost, westernmost
                    val northenmost = locationList.maxByOrNull { it.latLoc }
                    val southernmost = locationList.minByOrNull { it.latLoc }
                    val easternmost = locationList.maxByOrNull { it.lngLoc }
                    val westernmost = locationList.minByOrNull { it.lngLoc }

//                    AlertDialog.Builder(activity as HomeActivity)
//                        .setTitle("Debug Stick")
//                        .setMessage(
//                                    "Northenmost: ${northenmost?.latLoc}\r\n" +
//                                    "Southernmost: ${southernmost?.latLoc}\r\n" +
//                                    "Easternmost: ${easternmost?.lngLoc}\r\n" +
//                                    "Westernmost: ${westernmost?.lngLoc}\r\n"
//                        )
//                        .setPositiveButton("OK") { dialog, which ->
//                            dialog.dismiss()
//                        }
//                        .show()

                    val boundingBox = BoundingBox(northenmost!!.latLoc, easternmost!!.lngLoc, southernmost!!.latLoc, westernmost!!.lngLoc)  // north, east, south, west
                    binding.map.zoomToBoundingBox(boundingBox, false)

                    mapController.setZoom(18.0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // show using alert dialog
                AlertDialog.Builder(activity as HomeActivity)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                activity as HomeActivity,
                "Error: " + e.message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initMarker(modelList: List<LocationModel>) {
        for(i in modelList.indices) {
            overlayItem = ArrayList()
            overlayItem.add(
                OverlayItem(
                    modelList[i].name,
                    modelList[i].vicinity,
                    GeoPoint(modelList[i].latLoc, modelList[i].lngLoc)
                )
            )

            val model = LocationModel()
            model.name = modelList[i].name
            model.vicinity = modelList[i].vicinity

            val marker = Marker(binding.map)
            val markerIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_location_on_24, null)
            // set color of markerIcon to white
            markerIcon?.setTint(Color.DKGRAY)

            marker.icon = markerIcon
            marker.position = GeoPoint(modelList[i].latLoc, modelList[i].lngLoc)
            marker.relatedObject = model
            marker.infoWindow = CustomInfoWindow(binding.map)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }

            binding.map.overlayManager.add(marker)
            binding.map.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()

        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()

        binding.map.onPause()
    }
}