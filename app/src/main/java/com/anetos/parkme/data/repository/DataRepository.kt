package com.anetos.parkme.data.repository

import com.anetos.parkme.data.api.remote.RemoteDataSource
import com.anetos.parkme.data.db.AppDatabase

open class DataMainRepository

/**
 * A data repo containing a weather, forecast, rainfall details.
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
class DataRepository(
    private val dataSource: RemoteDataSource,
    private val appDatabase: AppDatabase
) : DataMainRepository() {

    class DataRefreshError(cause: Throwable) : Throwable(cause.message, cause)
}