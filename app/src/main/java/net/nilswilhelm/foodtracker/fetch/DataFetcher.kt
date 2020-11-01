package net.nilswilhelm.foodtracker.fetch

import android.content.Context
import android.provider.Settings.Global.getString
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

enum class DownloadStatus {
    OK, IDLE, NOT_INITIALISED, FAILED_OR_EMPTY, PERMISSIONS_ERROR, ERROR
}

class DataFetcher<T>(private val listener: OnDownloadComplete<T>, private val context: Context) {

    private val TAG = "DataFetcher"
    private var downloadStatus = DownloadStatus.IDLE

    interface OnDownloadComplete<T> {
        fun onDownloadComplete(data: T, status: DownloadStatus)
        fun onDownloadError(errorMessage: String, status: DownloadStatus)
    }

    fun doInBackground(url: String) {
        GlobalScope.launch {
//            if (url == "") {
//                downloadStatus = DownloadStatus.NOT_INITIALISED
//            }
//
//            try {
//                downloadStatus = DownloadStatus.OK
//                listener.onDownloadComplete(URL(url).readText(), downloadStatus)
//
//            } catch (e: Exception) {
//                val errorMessage = when (e) {
//                    is MalformedURLException -> {
//                        downloadStatus = DownloadStatus.NOT_INITIALISED
//                        "doInBackground: Invalid URL ${e.message}"
//                    }
//                    is IOException -> {
//                        downloadStatus = DownloadStatus.FAILED_OR_EMPTY
//                        "doInBackground: IO Exception reading dat: ${e.message}"
//                    }
//                    is SecurityException -> {
//                        downloadStatus = DownloadStatus.PERMISSIONS_ERROR
//                        "doInBackground: Security exception: Needs permission? ${e.message}"
//                    }
//                    else -> {
//                        DownloadStatus.ERROR
//                        "Unknown error: ${e.message}"
//                    }
//                }
//                Log.e(TAG, errorMessage)
//                listener.onDownloadError(errorMessage, downloadStatus)
//            }

            val authData: AuthData = AuthHandler.getAuthData(context)
            Log.d(TAG, authData.token)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader(
                    "Authorization",
                    authData.token
                )
                .addHeader("userId", authData.userId)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                println(response.body!!.string())
            }

        }


    }
}