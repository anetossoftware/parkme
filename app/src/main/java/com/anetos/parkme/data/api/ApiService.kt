package com.anetos.parkme.data.api

import android.content.Context
import  com.anetos.parkme.core.Networking
import  com.anetos.parkme.data.model.*
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Api service interface to handle all the data from retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
interface ApiService {

    /*@GET
    fun getAppVersionDataAsync(@Url url: String): Deferred<AppVersionResponse>*/

    companion object {

        fun create(context: Context): ApiService {
            return Networking.create(context)
        }
    }
}