package net.nilswilhelm.foodtracker.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.Intake
import okhttp3.*
import java.io.IOException

class HistoryViewModel : ViewModel() {
    private val TAG = "HomeViewModel"

    private val _data = MutableLiveData<List<Intake>>()
    fun data(): LiveData<List<Intake>> {
        return _data
    }

    private val _errorMessage = MutableLiveData<String>()
    fun errorMessage(): LiveData<String> {
        return _errorMessage
    }


    fun fetchData(context: Context, skip: Int, limit: Int) {

        val authData = AuthHandler.getAuthData(context)

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(context.getString(R.string.BASE_URL) + "history?skip=${skip}&limit=${limit}")
            .addHeader(
                "Authorization",
                authData.token
            )
            .addHeader("userId", authData.userId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure()")
                e.printStackTrace()
                _errorMessage.postValue(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse()")
                response.use {

                    if (!response.isSuccessful) {
                        _errorMessage.postValue("not successful")
                    } else {
                        val resString = response.body!!.string()

                        val gson = Gson()
                        val itemType = object : TypeToken<ArrayList<Intake>>() {}.type
                        val data = gson.fromJson<ArrayList<Intake>>(resString, itemType)

                        if (data != null) {
                            val current =_data.value
                            val empty = ArrayList<Intake>()
                            if (current != null) {
                                    empty.addAll(current as ArrayList<Intake>)
                            }
                            empty.addAll(data as ArrayList<Intake>)
                            _data.postValue(empty)
                        }
                    }
                }
            }
        })
    }
}





