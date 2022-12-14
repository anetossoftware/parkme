package com.anetos.parkme.data.repository

import com.anetos.parkme.core.DispatcherProvider
import com.anetos.parkme.core.ResultState
import com.anetos.parkme.core.helper.DataHelper
import com.anetos.parkme.core.helper.SharedPreferenceHelper
import com.anetos.parkme.data.ConstantFirebase
import com.anetos.parkme.data.remote.ApiService
import com.anetos.parkme.data.remote.FirebaseService
import com.anetos.parkme.domain.model.ParkingSpot
import com.anetos.parkme.domain.model.User
import com.anetos.parkme.domain.repository.RemoteDataSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


/**
 * RemoteDataSourceImpl impl for RemoteDataSource
 *
 * created by Jaydeep Bhayani on 16/07/2022
 */
@Singleton
class RemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService,
    private val firebaseService: FirebaseService,
    private val dispatcher: DispatcherProvider,
) : RemoteDataSource {
    override suspend fun setRegisterData(user: User): Flow<ResultState<Boolean>> {
        return flow {
            emit(ResultState.Loading(true))
            try {
                val result = firebaseService.getFirebaseFirestore
                    .collection(ConstantFirebase.COLLECTION_USERS)
                    .document(DataHelper.getUserIndex(user))
                    .set(user)
                    .addOnCompleteListener {
                    }
                    .addOnFailureListener {
                    }
                emit(ResultState.Success(true))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(ResultState.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(ResultState.Error("Couldn't load data"))
            } catch (e: Throwable) {
                emit(ResultState.Error("Couldn't load data"))
            }
        }.flowOn(dispatcher.io)
    }

    override suspend fun getLoginData(): Flow<ResultState<User>> {
        return flow {

        }
    }

    override suspend fun getParkingSpotsData(): Flow<ResultState<List<ParkingSpot>>> {
        return callbackFlow {
            trySend(ResultState.Loading(true))
            val snapshotListener =
                firebaseService.getFirebaseFirestore.collection(ConstantFirebase.COLLECTION_PARKING_SPOT)
                    .addSnapshotListener { value, error ->
                        try {
                            val result = value?.toObjects(ParkingSpot::class.java) ?: emptyList()
                            trySend(ResultState.Success(result))
                        } catch (e: IOException) {
                            e.printStackTrace()
                            trySend(ResultState.Error("Couldn't load data"))
                        } catch (e: HttpException) {
                            e.printStackTrace()
                            trySend(ResultState.Error("Couldn't load data"))
                        } catch (e: Throwable) {
                            trySend(ResultState.Error("Couldn't load data"))
                        }
                    }
            awaitClose {
                snapshotListener.remove()
            }
        }.flowOn(dispatcher.io)
    }

    override suspend fun updateProfileData(user: User): Flow<ResultState<Boolean>> {
        return callbackFlow {
            trySend(ResultState.Loading(true))
            try {
                firebaseService.getFirebaseFirestore.collection(ConstantFirebase.COLLECTION_USERS)
                    .document(DataHelper.getUserIndex(SharedPreferenceHelper().getUser()))
                    .set(user)
                    .addOnSuccessListener {
                        trySend(ResultState.Success(true))
                    }.addOnFailureListener {
                        ResultState.Error(it.printStackTrace())
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            } catch (e: Throwable) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            }
            awaitClose { channel.close() }
        }.flowOn(dispatcher.io)
    }

    override suspend fun addMoneyToWallet(user: User): Flow<ResultState<Boolean>> {
        return callbackFlow {
            trySend(ResultState.Loading(true))
            try {
                firebaseService.getFirebaseFirestore.collection(ConstantFirebase.COLLECTION_USERS)
                    .document(DataHelper.getUserIndex(SharedPreferenceHelper().getUser()))
                    .set(user)
                    .addOnSuccessListener {
                        trySend(ResultState.Success(true))
                    }.addOnFailureListener {
                        ResultState.Error(it.printStackTrace())
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            } catch (e: Throwable) {
                e.printStackTrace()
                trySend(ResultState.Error("Couldn't load data"))
            }
            awaitClose { channel.close() }
        }.flowOn(dispatcher.io)
    }
}