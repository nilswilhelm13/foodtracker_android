package net.nilswilhelm.foodtracker.fetch

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.gson.Gson
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.DailyGoals
import okhttp3.*
import java.io.IOException

interface OnGoalsDataListener{
    fun onGoalsDataAvailable(goals: DailyGoals)
}

class GoalFetcher(val context: Context, private val listener: OnGoalsDataListener) : Callback {

    private val TAG = "GoalFetcher"

     fun fetchData(url: String) {

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
        Log.d("Fail", "Fail")
    }

    override fun onResponse(call: Call, response: Response) {
        response.use {
            if (response.isSuccessful){
                val body = response.body!!.string()
                Log.d(TAG, body)
                val goals = Gson().fromJson<DailyGoals>(body, DailyGoals::class.java)
                listener.onGoalsDataAvailable(goals)
            }

        }
    }
}