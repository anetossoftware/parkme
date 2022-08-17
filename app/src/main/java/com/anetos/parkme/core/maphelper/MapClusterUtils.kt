package com.anetos.parkme.core.maphelper

import android.content.Context
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.vectorToBitmap
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.model.ParkingSpot
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerClusterRenderer(
    var context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MapClusterItem>
) : DefaultClusterRenderer<MapClusterItem>(context, map, clusterManager) {

    //Remove this to avoid clustering
    override fun onBeforeClusterItemRendered(item: MapClusterItem, markerOptions: MarkerOptions) {
        val parkingSpot = Gson().fromJson(item.snippet, ParkingSpot::class.java)
        if (parkingSpot.availabilityStatus?.equals(
                ConstantFirebase.AVAILABILITY_STATUS.AVAILABLE.name, true
            ) == true
        ) {
            markerOptions.icon(vectorToBitmap(context, R.drawable.ic_parking_red))
        } else {
            markerOptions.icon(vectorToBitmap(context, R.drawable.ic_parking_occupied))
        }
        markerOptions.snippet(item.snippet)
    }
}

class MapClusterItem(private val place: ParkingSpot, snippet: String) : ClusterItem {
    private var mSnippet: String = snippet

    override fun getPosition(): LatLng {
        return LatLng(place.latitude, place.longitude)
    }

    override fun getTitle(): String {
        return place.address.toString()
    }

    override fun getSnippet(): String {
        return mSnippet
    }
}