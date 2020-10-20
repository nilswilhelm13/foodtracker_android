package net.nilswilhelm.foodtracker.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import net.nilswilhelm.foodtracker.activities.AuthenticatorActivity
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.AuthResponse

class AuthHandler {

    companion object{

        val BASE_URL = "https://backend.nilswilhelm.net/"
        val TAG = "AuthHandler"

        fun login(accountName: String, password: String): String {
            val json = "{\"email\":\"${accountName}\",\"password\":\"${password}\"}"
            var token = ""
            val requestBody = json.toRequestBody()
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(BASE_URL + "login")
                .method("POST", requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                for ((name, value) in response.headers) {
                    println("$name: $value")
                }

                token = response.body!!.string()
            }

            return token
        }

        fun autoAuth(context: Context){
            Log.d(TAG, "autoAuth called")
            val sharedPref: SharedPreferences = context.getSharedPreferences("FOODTRACKER", Context.MODE_PRIVATE)

            val expireTime = sharedPref.getLong("expireTime", 0)
            val nowTime = Date().time

            // Check if token is expired
            if(nowTime > expireTime){
                Log.d(TAG, "nowTime: $nowTime")
                Log.d(TAG, "expireTime: $expireTime")
                // redirect to login activity
                val myIntent = Intent(context, AuthenticatorActivity::class.java)
                Toast.makeText(context, "Expired", Toast.LENGTH_SHORT).show()
                startActivity(context, myIntent, null)
            }
        }

        fun getAuthData(context: Context): AuthData{
            var token = ""
            var userId = ""

            val sharedPref: SharedPreferences = context.getSharedPreferences("FOODTRACKER", Context.MODE_PRIVATE)

            val tokenEntry: String? = sharedPref.getString("token", "")
            if (tokenEntry != null){
                token = tokenEntry.toString()
            }

            val userIdEntry: String? = sharedPref.getString("userId", "")
            if (userIdEntry != null){
                userId = userIdEntry.toString()
            }

            val expireTime: Long = sharedPref.getLong("expireTime", 0)
            if (userIdEntry != null){
                userId = userIdEntry.toString()
            }

            return AuthData(token, userId, expireTime)
        }

        fun storeAuthData(context: Context, authData: AuthResponse){
            val sharedPref: SharedPreferences = context.getSharedPreferences("FOODTRACKER", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()


            val now = Date()
            val then = Date(now.time + authData.expiresIn * 1000 * 60)

            Log.d(TAG, "now: ${now.time}")
            Log.d(TAG, "expireTime: ${then.time}")

            editor.putLong("expireTime", then.time)
            editor.putString("token", authData.token)
            editor.putString("userId", authData.userId)

            editor.apply()
        }

        fun login(context: Context ,accountName: String, password: String, listener: Callback) {
            val json = "{\"email\":\"${accountName}\",\"password\":\"${password}\"}"
            var token = ""
            val requestBody = json.toRequestBody()
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(context.getString(R.string.BASE_URL) + "login")
                .method("POST", requestBody)
                .build()
            client.newCall(request).enqueue(listener)
        }
    }


}