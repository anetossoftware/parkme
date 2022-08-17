package  com.anetos.parkme.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.anetos.parkme.Application

fun isNetworkAvailable(): Boolean {
    var result = false
    val cm =
        Application.getAppContext()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    cm?.run {
        cm.getNetworkCapabilities(cm.activeNetwork)?.run {
            result = when {
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
    return result
}