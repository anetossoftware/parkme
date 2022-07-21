package com.anetos.parkme.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anetos.parkme.core.helper.viewModelFactoryWithSingleArg
import com.anetos.parkme.data.api.RemoteDataNotFoundException
import com.anetos.parkme.data.repository.GoogleDataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GoogleDataViewModel(private val repository: GoogleDataRepository) : ViewModel() {

    /**
     *  Get the Map Directions from start pint to end point from google Direction Api
     */
    val mapDirections = repository.directionData

    fun refreshMapDirection(startPoint: String, endPoint: String) {
        launchDataLoad {
            repository.refreshMapDirection(startPoint, endPoint)
        }
    }

    //--------------------------------------------------------------------------------------------//
    /***
     * SnackBar and Spinner common for all datas
     */
    private var _snackBar: MutableLiveData<String> = MutableLiveData()
    val snackbar: LiveData<String> get() = _snackBar
    var spinner: MutableLiveData<Boolean> = MutableLiveData()
    val spinner1: LiveData<Boolean> get() = spinner

    private fun launchDataLoad(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                spinner.value = true
                block()
            } catch (error: RemoteDataNotFoundException) {
                _snackBar.value = error.message
            } finally {
                spinner.value = false
            }
        }
    }
    //--------------------------------------------------------------------------------------------//
    
    companion object {
        val FACTORY =
            viewModelFactoryWithSingleArg(::GoogleDataViewModel)
    }
}