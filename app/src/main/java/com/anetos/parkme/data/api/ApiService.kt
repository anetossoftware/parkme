package com.anetos.parkme.data.api

import android.content.Context

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