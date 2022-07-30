package com.anetos.parkme.core.maphelper

import android.content.Context
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.vectorToBitmap
import com.anetos.parkme.data.model.ParkingSpot
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MarkerClusterRenderer(
    var context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MapClusterItem>
) : DefaultClusterRenderer<MapClusterItem>(context, map, clusterManager) {

    /*override fun onBeforeClusterRendered(
        cluster: Cluster<MapClusterItem>,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(
            vectorToBitmap(
                context,
                R.drawable.ic_round_person_24,
                ContextCompat.getColor(context, R.color.colorBlue)
            )
        )
    }*/

    //Remove this to avoid clustering
    override fun onBeforeClusterItemRendered(item: MapClusterItem, markerOptions: MarkerOptions) {
        markerOptions.icon(vectorToBitmap(context, R.drawable.ic_parking_red))
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