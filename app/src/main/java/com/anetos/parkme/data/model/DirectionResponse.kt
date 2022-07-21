package com.anetos.parkme.data.model

import com.google.gson.annotations.SerializedName

data class DirectionResponse(
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyLine,
    @SerializedName("routes")
    val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline")
    val overviewPolyLine: OverviewPolyLine,
    @SerializedName("legs")
    val legs: List<Legs>
)

data class Legs(
    @SerializedName("start_location")
    val startLocation: MapLocation,
    @SerializedName("end_location")
    val endLocation: MapLocation,
    @SerializedName("steps") val steps: List<Steps>
)

data class OverviewPolyLine(
    @SerializedName("points") val points: String
)

data class Steps(
    @SerializedName("start_location")
    val start_location: MapLocation,
    @SerializedName("end_location")
    val end_location: MapLocation,
    @SerializedName("polyline")
    val polyline: OverviewPolyLine,
    @SerializedName("travel_mode")
    val travelMode: String
)

data class MapLocation(val lat: Double, val lng: Double)