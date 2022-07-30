package  com.anetos.parkme.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import com.anetos.parkme.Application
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class InternetCheck(private val onInternetChecked: (Boolean) -> Unit) :
    AsyncTask<Void, Void, Boolean>() {
    init {
        execute()
    }

    override fun doInBackground(vararg voids: Void): Boolean {
        return try {
            val sock = Socket()
            sock.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }

    }

    override fun onPostExecute(internet: Boolean) {
        try{
        onInternetChecked(internet)}
        catch (e:Exception){

        }
    }
}

fun isNetworkAvailable(): Boolean {
    var result = false
    val cm =
        Application.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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