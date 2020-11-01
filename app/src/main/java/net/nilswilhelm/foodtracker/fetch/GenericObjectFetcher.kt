package net.nilswilhelm.foodtracker.fetch

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.DailyGoals
import okhttp3.*
import java.io.IOException
import java.lang.reflect.Type


interface OnObjectAvailable<T> {
    fun <T>onObjectAvailable(data: T)
    fun onObjectFetchFailed(errorMessage: String)


}


class GenericObjectFetcher<T>(
    private val context: Context,
    private val url: String,
    private val listener: OnObjectAvailable<T>
) : Callback {

    private val TAG = "GenericObjectFetcher"

    fun fetchData() {

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
        client.newCall(request).enqueue(this)
    }

    override fun onFailure(call: Call, e: IOException) {
        listener.onObjectFetchFailed(e.message.toString())
    }

    override fun onResponse(call: Call, response: Response) {
        response.use {
            if (response.isSuccessful) {
                var body = response.body!!.string()
                body = "{\"test\":\"hallo\"}"
                Log.d(TAG, body)
//                val data = Gson().fromJson<T>(body, object : TypeToken<T>(){}.type)
                val data : T = Gson().fromJson<T>(body, object : TypeToken<T>(){}.type)
                Log.d(TAG, data.toString())
                listener.onObjectAvailable(data as T)
            }
        }
    }



}