package com.anetos.parkme.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.anetos.parkme.data.api.RemoteDataNotFoundException
import com.anetos.parkme.data.api.googleRemote.GoogleRemoteDataSource
import com.anetos.parkme.data.api.remote.RemoteDataSource
import com.anetos.parkme.data.db.AppDatabase
import com.anetos.parkme.data.model.DirectionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class DataMainRepository

/**
 * A data repo containing a weather, forecast, rainfall details.
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
class DataRepository(
    private val dataSource: RemoteDataSource,
    private val appDatabase: AppDatabase,
    private val googleDataSource: GoogleRemoteDataSource
) : DataMainRepository() {

    //GoogleRepo
    val googleDirectionData: MutableLiveData<DirectionResponse> = MutableLiveData()


    /**
     * [Live Data] to load map direction.  direction data's will be loaded from the repository cache.
     * Observing this will not cause the repos to be refreshed, use [map direction].
     */
    suspend fun refreshGMapDirection(startPoint: String, endPoint: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = googleDataSource.getMapDirections(startPoint, endPoint)
                Log.d("test", "test(refreshMapDirection)--->$result")
                if (result is Result.Success) {
                    googleDirectionData.postValue(result.data)
                }

            } catch (error: RemoteDataNotFoundException) {
                throw GoogleDataRepository.DataRefreshError(error)
            }
        }
    }

    class DataRefreshError(cause: Throwable) : Throwable(cause.message, cause)
}