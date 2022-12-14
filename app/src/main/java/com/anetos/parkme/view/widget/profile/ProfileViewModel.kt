package com.anetos.parkme.view.widget.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anetos.parkme.core.ResultState
import com.anetos.parkme.domain.model.User
import com.anetos.parkme.domain.repository.RemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
) : ViewModel() {
    private var mutableProfileData =
        MutableStateFlow<ResultState<Boolean>>(ResultState.Loading(false))
    val profileData = mutableProfileData.asStateFlow()

    fun updateProfileData(user: User) {
        viewModelScope.launch {
            remoteDataSource.updateProfileData(user).collect { result ->
                mutableProfileData.value = when (result) {
                    is ResultState.Loading -> ResultState.Loading(result.isLoading)
                    is ResultState.Success -> ResultState.Success(result.data)
                    is ResultState.Error -> ResultState.Error(result.exception)
                }
            }
        }
    }
}