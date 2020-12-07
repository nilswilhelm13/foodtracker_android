package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.GsonBuildConfig
import kotlinx.android.synthetic.main.content_food_details.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.Eat
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.data.Transaction
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FoodDetailsActivity : BaseActivity(), Callback {

    private val TAG = "FoodDetailsActivity"
    var food: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)



        food = intent.getParcelableExtra<Food>(FOOD_TRANSFER) as Food

        food_name.text = food?.name
        food_energy.text = food?.nutrition?.energy.toString() + "kcal"
        food_carbohydrate.text = food?.nutrition?.carbohydrate.toString() + "g"
        food_protein.text = food?.nutrition?.protein.toString() + "g"
        food_fat.text = food?.nutrition?.fat.toString() + "g"
        var meta_fields = ""
        if (food?.meta_fields != null) {
            food_meta_fields.visibility = View.VISIBLE

            food?.meta_fields?.forEach {
                meta_fields += it
                meta_fields += "\n"
            }
        } else {
            food_meta_fields_label.setText(" ")
        }
        food_meta_fields.text = meta_fields



        food_button.setOnClickListener {

            postFood()
        }
    }

    private fun postFood() {
        val amount = food_amount.text.toString().toDouble()
        if (food != null) {
            val dateString = dateString(Date())
            Log.d(TAG, dateString)
            val json =
                "{\"foodId\":\"${food?.id!!}\",\"amount\":${amount},\"date\":\"$dateString\"}"
            val authData: AuthData = AuthHandler.getAuthData(this)
            val client = OkHttpClient()
            Log.d(TAG, json)
            val requestBody = json.toRequestBody()
            val request = Request.Builder()
                .url(getString(R.string.BASE_URL) + "intake")
                .method("POST", requestBody)
                .addHeader(
                    "Authorization",
                    authData.token
                )
                .addHeader("userId", authData.userId)
                .build()

            client.newCall(request).enqueue(this)
        }


    }

    override fun onFailure(call: Call, e: IOException) {
        runOnUiThread {
            Toast.makeText(this, "Error posting food", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResponse(call: Call, response: Response) {
        response.use {

            if (!response.isSuccessful) {

            }
            val resString = response.body!!.string()
            Log.d(TAG, resString)
            startActivity(Intent(this, MainActivity::class.java))
        }


    }

    fun dateString(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date) + "Z"
    }
}