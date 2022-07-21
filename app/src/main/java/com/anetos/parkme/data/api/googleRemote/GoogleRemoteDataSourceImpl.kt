package com.anetos.parkme.data.api.googleRemote

import android.content.Context
import android.util.Log
import  com.anetos.parkme.data.UrlConstants.API_KEY
import  com.anetos.parkme.data.UrlConstants.GOOGLE_DIRECTION_URL
import  com.anetos.parkme.data.api.RemoteDataNotFoundException
import  com.anetos.parkme.data.db.AppDatabase
import  com.anetos.parkme.data.model.*
import  com.anetos.parkme.data.repository.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * GoogleRemoteDataSourceImpl impl for RemoteDataSource
 *
 * created by Thulasimanikandan on 20/11/2019
 */
class GoogleRemoteDataSourceImpl private constructor(
    private val apiService: GoogleApiService,
    private val appdatabase: AppDatabase
) :
    GoogleRemoteDataSource {
    override suspend fun getMapDirections(
        startPoint: String,
        endPoint: String
    ): Result<DirectionResponse> =
        withContext(Dispatchers.IO) {
            val request =
                apiService.getDirectionsAsync(
                    GOOGLE_DIRECTION_URL,
                    startPoint,
                    endPoint,
                    false,
                    "DRIVING",
                    API_KEY
                )

            Log.d("test", "request.data-getMapDirections : $request")


            try {
                val response = request.await()
                Log.d("test", "result.data-getMapDirections : $response")
                Result.Success(response)
            } catch (ex: Throwable) {
                Result.Error(RemoteDataNotFoundException())
            }
        }

    companion object {
        fun newInstance(context: Context) =
            GoogleRemoteDataSourceImpl(
                GoogleApiService.create(context),
                AppDatabase.getInstance(context)
            )


    }
}