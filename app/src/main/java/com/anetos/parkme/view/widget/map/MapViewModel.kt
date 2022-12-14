package com.anetos.parkme.view.widget.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anetos.parkme.core.ResultState
import com.anetos.parkme.domain.model.ParkingSpot
import com.anetos.parkme.domain.repository.RemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private var remoteDataSource: RemoteDataSource
) : ViewModel() {

    private var mutableParkingSpots = MutableStateFlow(emptyList<ParkingSpot>())
    val parkingSpots = mutableParkingSpots.asStateFlow()

    init {
        viewModelScope.launch {
            remoteDataSource.getParkingSpotsData().collect { result ->
                when (result) {
                    is ResultState.Success -> mutableParkingSpots.value = result.data
                    else -> {}
                }
            }
        }
    }
}