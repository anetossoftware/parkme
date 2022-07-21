package  com.anetos.parkme.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import  com.anetos.parkme.data.api.RemoteDataNotFoundException
import  com.anetos.parkme.data.api.googleRemote.GoogleRemoteDataSource
import  com.anetos.parkme.data.db.AppDatabase
import  com.anetos.parkme.data.model.DirectionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A data repo containing a weather, forecast, rainfall details.
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
class GoogleDataRepository(
    private val dataSource: GoogleRemoteDataSource,
    private val appDatabase: AppDatabase
) : DataMainRepository() {

    val directionData: MutableLiveData<DirectionResponse> = MutableLiveData()

    /**
     * [Live Data] to load map direction.  direction data's will be loaded from the repository cache.
     * Observing this will not cause the repos to be refreshed, use [map direction].
     */
    suspend fun refreshMapDirection(startPoint: String, endPoint: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = dataSource.getMapDirections(startPoint, endPoint)
                Log.d("test", "test(refreshMapDirection)--->$result")
                if (result is Result.Success) {
                    directionData.postValue(result.data)
                }

            } catch (error: RemoteDataNotFoundException) {
                throw DataRefreshError(error)
            }
        }
    }

    class DataRefreshError(cause: Throwable) : Throwable(cause.message, cause)
}