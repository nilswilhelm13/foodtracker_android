package net.nilswilhelm.foodtracker.utils

import android.content.Context
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class Utils {
    companion object{
        fun doMySearch(context: Context, query: String, listener: Callback) {
            val url = context.getString(R.string.BASE_URL) + "search/" + query
            getData(context, url, listener)
        }

        fun getFoodByEAN(context: Context, ean: String, listener: Callback){
            val url = context.getString(R.string.BASE_URL) + "foodlist/$ean?ean=true"
            getData(context, url, listener)
        }

        fun getData(context: Context, url: String, listener: Callback){
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
    }
}