package com.anetos.parkme.data.remote

/**
 * Api service interface to handle all the data from retrofit
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
interface ApiService {

    /*@GET
    fun getAppVersionDataAsync(@Url url: String): Deferred<AppVersionResponse>*/

    companion object {
        const val BASE_URL = "https://engineering.league.dev/challenge/api/"
    }
}