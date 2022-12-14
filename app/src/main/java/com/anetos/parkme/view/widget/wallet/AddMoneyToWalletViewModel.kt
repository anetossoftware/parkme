package com.anetos.parkme.view.widget.wallet

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
class AddMoneyToWalletViewModel @Inject constructor(
    private var remoteDataSource: RemoteDataSource,
) : ViewModel() {

    private var mutableIsMoneyAdded = MutableStateFlow<ResultState<Boolean>>(ResultState.Loading(false))
    val isMoneyAdded = mutableIsMoneyAdded.asStateFlow()

    fun addMoney(user: User) {
        viewModelScope.launch {
            remoteDataSource.addMoneyToWallet(user).collect { result ->
                mutableIsMoneyAdded.value = when (result) {
                    is ResultState.Loading -> ResultState.Loading(result.isLoading)
                    is ResultState.Success -> ResultState.Success(result.data)
                    is ResultState.Error -> ResultState.Error(result.exception)
                }
            }
        }
    }
}