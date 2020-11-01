package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_edit_goals.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.DailyGoals
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.data.Nutrition
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class EditGoals : AppCompatActivity() {

    private val TAG = "EditGoals"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goals)

        edit_goals_button.setOnClickListener {
            val calories = edit_goals_calories.text.toString().toDouble()
            val protein = edit_goals_protein.text.toString().toDouble()
            val carbohydrate = edit_goals_carbohydrate.text.toString().toDouble()
            val fat = edit_goals_fat.text.toString().toDouble()
            val water = edit_goals_water.text.toString().toDouble()

            val goals = DailyGoals("", Nutrition(calories, carbohydrate, protein, fat), water)

            postGoals(goals)

        }
    }

    private fun postGoals(goals: DailyGoals) {

        val authData = AuthHandler.getAuthData(this)

        val json = Gson().toJson(goals)

        Log.d(TAG, json)
        val client = OkHttpClient()
        val requestBody = json.toRequestBody()
        val request = Request.Builder()
            .url(getString(R.string.BASE_URL) + "goals")
            .method("POST", requestBody)
            .addHeader(
                "Authorization",
               authData.token
            )
            .addHeader("userId", authData.userId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    if (response.isSuccessful) {
                        val resString = response.body!!.string()
                        Log.d("Response", resString)
                        startActivity(Intent(this@EditGoals, MainActivity::class.java))
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@EditGoals,
                                response.code.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }
        })
    }
}