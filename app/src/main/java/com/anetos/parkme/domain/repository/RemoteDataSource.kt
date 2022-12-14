package com.anetos.parkme.domain.repository

import com.anetos.parkme.core.ResultState
import com.anetos.parkme.domain.model.ParkingSpot
import com.anetos.parkme.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Handle remote data
 * Add the data here and handle in the implementation
 *
 * created by Thulasimanikandan on 16/09/2019
 */
interface RemoteDataSource {
    suspend fun setRegisterData(user: User): Flow<ResultState<Boolean>>

    suspend fun getLoginData(): Flow<ResultState<User>>

    suspend fun getParkingSpotsData(): Flow<ResultState<List<ParkingSpot>>>

    suspend fun updateProfileData(user: User): Flow<ResultState<Boolean>>

    suspend fun addMoneyToWallet(user: User): Flow<ResultState<Boolean>>
}