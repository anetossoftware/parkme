package  com.anetos.parkme.core

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import  com.anetos.parkme.BuildConfig
import  com.anetos.parkme.data.UrlConstants
import  com.anetos.parkme.data.api.ApiService
import  com.anetos.parkme.data.api.googleRemote.GoogleApiService
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object Networking {
    private const val NETWORK_CALL_TIMEOUT = 60

    fun create(context: Context): ApiService {
        val cacheDir = File(context.cacheDir, "offlineCache")

        //10 MB
        val cacheSize = Cache(cacheDir, 10 * 1024 * 1024)

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .cache(cacheSize)
                    .addInterceptor { chain ->
                        var request = chain.request()
                        InternetCheck { internet ->
                            if (internet) {
                                request.newBuilder().header("Cache-Control", "public, max-age=" + 5)
                                    .build()
                            } else {
                                request.newBuilder().header(
                                    "Cache-Control",
                                    "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                                ).build()
                            }
                        }

                        chain.proceed(request)
                    }
                    .addInterceptor(HttpLoggingInterceptor()
                        .apply {
                            level = if (BuildConfig.DEBUG)
                                HttpLoggingInterceptor.Level.BODY
                            else
                                HttpLoggingInterceptor.Level.NONE
                        })
                    .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(ApiService::class.java)
    }

    fun createGoogleApi(context: Context): GoogleApiService {
        val cacheDir = File(context.cacheDir, "offlineCache")

        //10 MB
        val cacheSize = Cache(cacheDir, 10 * 1024 * 1024)

        return Retrofit.Builder()
            .baseUrl(UrlConstants.GOOGLE_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .cache(cacheSize)
                    .addInterceptor(HttpLoggingInterceptor()
                        .apply {
                            level = if (BuildConfig.DEBUG)
                                HttpLoggingInterceptor.Level.BODY
                            else
                                HttpLoggingInterceptor.Level.NONE
                        })
                    .readTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(NETWORK_CALL_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(GoogleApiService::class.java)
    }
}