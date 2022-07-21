package com.anetos.parkme.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anetos.parkme.core.helper.viewModelFactoryWithSingleArg
import com.anetos.parkme.data.api.RemoteDataNotFoundException
import com.anetos.parkme.data.repository.DataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

/***
 *  created by Jaydeep Bhayani on 20/06/2022
 */
class DataViewModel(private val repository: DataRepository) : ViewModel() {

    companion object {
        val FACTORY =
            viewModelFactoryWithSingleArg(::DataViewModel)
    }

    /**
     *  Get the Map Directions from start pint to end point from google Direction Api
     */
    val googleMapDirections = repository.googleDirectionData

    fun refreshGoogleMapDirection(startPoint: String, endPoint: String) {
        launchDataLoad {
            repository.refreshGMapDirection(startPoint, endPoint)
        }
    }


    /**
     *  Enable/Disable legends
     */
    val isLegendsEnabled = MutableLiveData<Boolean>()


    fun enableLegends(isEnabled: Boolean) = isLegendsEnabled.postValue(isEnabled)

    /*  var locationDetail = Location()

      fun getDisplayLocation(lat: Double, lon: Double) {
          launchDataLoad {
              if (repository.locationData.value != null)
                  displayList.addAll(repository.locationData.value!!)
          }
      }*/

    /**
     * Deprecated.
     * */
    /*val searchLocationData = repository.searchLocationData
    fun searchLocationData(latitude: String, longitude: String, placeName: String) {
        launchDataLoad { repository.getSearchLocationData(latitude, longitude, placeName) }
    }*/

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
}