package net.nilswilhelm.foodtracker.utils

import android.content.Context
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import okhttp3.*
import java.io.IOException

class Utils {
    companion object {

        interface OnDeleteListener {
            fun onDeleted(responseMessage: String)
            fun onDeleteFailed(errorMessage: String)
        }

        fun doMySearch(context: Context, query: String, listener: Callback) {
            val url = context.getString(R.string.BASE_URL) + "search/" + query
            getData(context, url, listener)
        }

        fun getFoodByEAN(context: Context, ean: String, listener: Callback) {
            val url = context.getString(R.string.BASE_URL) + "foodlist/$ean?ean=true"
            getData(context, url, listener)
        }

        fun getData(context: Context, url: String, listener: Callback) {
            val authData: AuthData = AuthHandler.getAuthData(context)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader(
                    "Authorization",
                    authData.token
                )
                .addHeader("userId", authData.userId)
                .build()

            client.newCall(request).enqueue(listener)
        }

        fun delete(context: Context, url: String, listener: OnDeleteListener) {

            val authData: AuthData = AuthHandler.getAuthData(context)
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("DELETE", null)
                .addHeader(
                    "Authorization",
                    authData.token
                )
                .addHeader("userId", authData.userId)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    listener.onDeleteFailed("Delete failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            listener.onDeleteFailed("Delete failed")
                        }
                        if (response.code == 200) {
                            listener.onDeleted("Delete successful")
                        } else {
                            listener.onDeleteFailed("Delete failed")
                        }
                    }
                }

            })


        }
    }
}