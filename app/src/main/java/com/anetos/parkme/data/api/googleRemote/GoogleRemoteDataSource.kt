package com.anetos.parkme.data.api.googleRemote

import com.anetos.parkme.data.model.*
import com.anetos.parkme.data.repository.Result


/**
 * Handle google remote data
 * Add the google calls here and handle in the implementation
 *
 * created by Thulasimanikandan on 20/11/2019
 */
interface GoogleRemoteDataSource {
    suspend fun getMapDirections(startPoint:String, endPoint:String) : Result<DirectionResponse>
}