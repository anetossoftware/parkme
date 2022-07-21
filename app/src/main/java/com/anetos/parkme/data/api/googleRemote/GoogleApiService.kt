package com.anetos.parkme.data.api.googleRemote

import android.content.Context
import com.anetos.parkme.core.Networking
import com.anetos.parkme.data.model.*
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


/**
 * Google Api service interface to handle all the data from retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
interface GoogleApiService {

    @GET
    fun getDirectionsAsync(
        @Url url: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("alternatives") alternatives:Boolean,
        @Query("mode") mode:String,
        @Query("key") key: String
    ): Deferred<DirectionResponse>

    companion object {

        fun create(context: Context): GoogleApiService {
            return Networking.createGoogleApi(context)
        }
    }
}