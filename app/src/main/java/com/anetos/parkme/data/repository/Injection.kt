package com.anetos.parkme.data.repository

import android.content.Context
import  com.anetos.parkme.data.api.googleRemote.GoogleRemoteDataSourceImpl
import  com.anetos.parkme.data.api.remote.RemoteDataSourceImpl
import  com.anetos.parkme.data.db.AppDatabase

/**
 * All the viewModel Injections will go here.
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
object Injection {

    fun provideDataRepository(context: Context) =
        DataRepository(
            RemoteDataSourceImpl.newInstance(context),
            AppDatabase.getInstance(context),
            GoogleRemoteDataSourceImpl.newInstance(context)
        )

    fun provideGoogleDataRepository(context: Context) =
        GoogleDataRepository(
            GoogleRemoteDataSourceImpl.newInstance(context),
            AppDatabase.getInstance(context)
        )
}