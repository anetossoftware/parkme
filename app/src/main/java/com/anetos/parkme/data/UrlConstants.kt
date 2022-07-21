package  com.anetos.parkme.data

import  com.anetos.parkme.BuildConfig

/**
 * All the viewModel Injections will go here.
 *
 * created by Thulasimanikandan on 16/09/2019
 */
object UrlConstants {

    const val isOffline:Boolean = true
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    //const val BASE_URL = "http://27.34.245.77:8088/api/"
    const val WEATHER_URL = "twsHobli/{location_twshoblicode}/{date}"
    const val FORECAST_URL = "forecast/{date}"
    const val FLOOD_URL = "bbmp_floodthreshold/locations"

    /**
     * shows Morning 8:30 am to Current time rainfall
     * */
    const val TODAY_RAINFALL_URL = "rain"
    /**
     * shows Yesterday 8:30am to Today 8:30 am result
     * */
    const val YESTERDAY_RAINFALL_URL = "todays"

    const val RAINFALL_URL = "trgHobli/{location_trghoblicode}/{date}"

    /**
     * Deprecated.
     * */
    //const val LOCATION_SEARCH_URL = "search/{location_lat}/{location_lon}"
    /**
     * Deprecated.
     * */
    const val LOCATION_URL = "WLS/BBMP"

    const val LOCATION_SEARCH_RAINFALL = "trgsearch/{location_lat}/{location_lon}"
    const val LOCATION_SEARCH_WEATHER = "twssearch/{location_lat}/{location_lon}"

    const val APP_VERSION_URL = "version"
    const val URL_WATER_LEVEL = "waterlevel/{date}"

    //For Google Direction Api
    const val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/"
    const val GOOGLE_DIRECTION_URL = "directions/json"
    const val API_KEY = "AIzaSyD9yjr3CGT9snA3q_2teyScTKJhuZUDJaY"

    const val STORM_URL = "http://117.216.42.179:817/api/Storm"
    const val LIGHTNING_URL = "http://117.216.42.179:817/api/Lightning"
    const val RAIN_URL = "http://117.216.42.179:817/api/pulserad"
    const val REGISTSER_URL = "http://117.216.42.179:817/api/Register?Name={name}&Number={number}"
}