package com.anetos.parkme.data.api.remote

import android.content.Context
import android.util.Log
import  com.anetos.parkme.core.helper.getApiStringDate
import  com.anetos.parkme.core.helper.yesterday
import  com.anetos.parkme.data.UrlConstants
import  com.anetos.parkme.data.UrlConstants.APP_VERSION_URL
import  com.anetos.parkme.data.UrlConstants.FLOOD_URL
import  com.anetos.parkme.data.UrlConstants.FORECAST_URL
import  com.anetos.parkme.data.UrlConstants.LIGHTNING_URL
import  com.anetos.parkme.data.UrlConstants.LOCATION_SEARCH_RAINFALL
import  com.anetos.parkme.data.UrlConstants.LOCATION_SEARCH_WEATHER
import  com.anetos.parkme.data.UrlConstants.RAINFALL_URL
import  com.anetos.parkme.data.UrlConstants.RAIN_URL
import  com.anetos.parkme.data.UrlConstants.REGISTSER_URL
import  com.anetos.parkme.data.UrlConstants.STORM_URL
import  com.anetos.parkme.data.UrlConstants.TODAY_RAINFALL_URL
import  com.anetos.parkme.data.UrlConstants.WEATHER_URL
import  com.anetos.parkme.data.UrlConstants.YESTERDAY_RAINFALL_URL
import  com.anetos.parkme.data.api.ApiService
import  com.anetos.parkme.data.api.RemoteDataNotFoundException
import  com.anetos.parkme.data.db.AppDatabase
import  com.anetos.parkme.data.model.*
import  com.anetos.parkme.data.repository.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*

/**
 * RemoteDataSourceImpl impl for RemoteDataSource
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
class RemoteDataSourceImpl private constructor(
    private val apiService: ApiService,
    private val appdatabase: AppDatabase
) : RemoteDataSource {


    companion object {
        fun newInstance(context: Context) =
            RemoteDataSourceImpl(
                ApiService.create(context),
                AppDatabase.getInstance(context)
            )


    }
}