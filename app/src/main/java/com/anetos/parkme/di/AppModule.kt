package com.anetos.parkme.di

import com.anetos.parkme.core.DefaultDispatchers
import com.anetos.parkme.core.DispatcherProvider
import com.anetos.parkme.data.remote.ApiService
import com.anetos.parkme.data.remote.FirebaseRepo
import com.anetos.parkme.data.remote.FirebaseService
import com.google.firebase.firestore.Query
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * * [AppModule]
 * implementation for AppModule Injection
 * @author
 * created by Jaydeep Bhayani on 09/08/2022
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(ApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseService(): FirebaseService {
        return FirebaseRepo()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider {
        return DefaultDispatchers()
    }
}