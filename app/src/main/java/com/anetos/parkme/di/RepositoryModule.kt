package com.anetos.parkme.di

import com.anetos.parkme.data.repository.RemoteDataSourceImpl
import com.anetos.parkme.domain.repository.RemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * * [RepositoryModule]
 * implementation for Bind Repository Injection
 * @author
 * created by Jaydeep Bhayani on 09/08/2022
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRemoteRepository(
        remoteDataSourceImpl: RemoteDataSourceImpl
    ): RemoteDataSource

    /*@Binds
    @Singleton
    abstract fun bindDispatcher(
        remoteRepositoryImpl: RemoteRepositoryImpl
    ): DispatcherProvider*/
}