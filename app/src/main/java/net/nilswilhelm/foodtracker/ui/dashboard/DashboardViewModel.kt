package net.nilswilhelm.foodtracker.ui.dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.progress_bars.*
import net.nilswilhelm.foodtracker.activities.AuthenticatorActivity
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException

class DashboardViewModel() : ViewModel() {

    private val _data = MutableLiveData<DashboardData>()
    fun data(): LiveData<DashboardData> {
        return _data
    }
    private val _progressData = MutableLiveData<ProgressData>()
    fun progressData(): LiveData<ProgressData> {
        return _progressData
    }
    private val _errorMessage = MutableLiveData<String>()
    fun errorMessage(): LiveData<String> {
        return _errorMessage
    }

    fun fetchData(url: String, context: Context) {

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

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                _errorMessage.postValue(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    if (!response.isSuccessful) {
                        if (response.code == 401) {
                            _errorMessage.postValue("Unauthorized")
                        } else {
                            _errorMessage.postValue("Fetching data failed")
                        }
                    } else {
                        val resString = response.body!!.string()
                        val gson = Gson()
                        try {
                            val data = gson.fromJson<DashboardData>(resString, DashboardData::class.java)
                            updateProgressBars(data.intake, data.dailyGoals)
                            _data.postValue(data)
                        }catch (e: Exception){
                            _errorMessage.postValue(e.message.toString())
                        }
                    }
                }
            }
        })
    }

    fun updateProgressBars(intake: Intake, goals: DailyGoals) {

        val proteinActual = intake.nutrition.protein.toInt()
        val proteinDesired = goals.nutrition.protein.toInt()
        var proteinProgress = 0
        if (proteinDesired > 0) {
            proteinProgress = proteinActual * 100 / proteinDesired
        }

        val carbohydrateActual = intake.nutrition.carbohydrate.toInt()
        val carbohydrateDesired = goals.nutrition.carbohydrate.toInt()
        var carbohydrateProgress = 0
        if (carbohydrateDesired > 0) {
            carbohydrateProgress = carbohydrateActual * 100 / carbohydrateDesired
        }

        val fatActual = intake.nutrition.fat.toInt()
        val fatDesired = goals.nutrition.fat.toInt()
        var fatProgress = 0
        if (fatDesired > 0) {
            fatProgress = fatActual * 100 / fatDesired
        }

        val p = ProgressData(
            proteinProgress = proteinProgress.coerceAtMost(100),
            proteinProgressString = "%d / %d".format(proteinActual, proteinDesired),
            carbohydrateProgress = carbohydrateProgress.coerceAtMost(100),
            carbohydrateProgressString = "%d / %d".format(carbohydrateActual, carbohydrateDesired),
            fatProgress = fatProgress.coerceAtMost(100),
            fatProgressString = "%d / %d".format(fatActual, fatDesired)
        )

        _progressData.postValue(p)
    }

}