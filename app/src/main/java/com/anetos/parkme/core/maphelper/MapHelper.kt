package com.anetos.parkme.core.maphelper

import android.content.Context
import android.util.Log
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.anetos.parkme.R
import com.anetos.parkme.core.helper.PermissionHelper
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.data.geojson.GeoJsonLayer
import org.json.JSONException
import java.io.IOException

fun Context.configureMap(maps: GoogleMap, isGeoJsonRequired: Boolean = true) {
    maps.mapType = GoogleMap.MAP_TYPE_NORMAL
    maps.uiSettings.isZoomControlsEnabled = true
    if (PermissionHelper.checkLocationPermission(this))
    maps.isMyLocationEnabled = true
    maps.uiSettings.isMyLocationButtonEnabled = true
    maps.uiSettings.isIndoorLevelPickerEnabled = true
    maps.isIndoorEnabled = false
    maps.uiSettings.isMapToolbarEnabled = false
    if (isGeoJsonRequired) {
        addGeoJson(maps)
    }
}

fun Context.addGeoJson(maps: GoogleMap) {
    val context = this
    try {
        setupGeoJson(context, maps, R.raw.parkinglots_polygon, R.color.lightPrimaryColor)
    } catch (e: IOException) {
        Log.e("test", "GeoJSON file could not be read")
    } catch (e: JSONException) {
        Log.e("test", "GeoJSON file could not be converted to a JSONObject")
    }
}

private fun setupGeoJson(
    context: Context,
    maps: GoogleMap,
    resourceId: Int, @ColorRes colorId: Int
) {
    val layer = GeoJsonLayer(maps, resourceId, context)
    val layerStyle = layer.defaultPolygonStyle
    layerStyle.strokeWidth = 3f
    layerStyle.strokeColor =
        ContextCompat.getColor(context, R.color.colorPrimary)
    layerStyle.fillColor = ContextCompat.getColor(context, colorId)
    layer.addLayerToMap()
}
