package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_create_food.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.data.Ingredient
import net.nilswilhelm.foodtracker.data.Nutrition
import net.nilswilhelm.foodtracker.utils.Utils
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

internal const val EAN_TRANSFER = "EAN_TRANSFER"

class CreateFood : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_food)
        checkIntendExtras()
        setListerners()
    }

    private fun setListerners() {
        create_food_buttonScan.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(true)
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.captureActivity = CaptureActivityPortrait::class.java
            intentIntegrator.initiateScan()

        }
        create_food_button.setOnClickListener {
            Log.d("CreateFood", "button clicked")
            val food = buildFood()
            postFood(food)
        }
    }

    private fun buildFood(): Food {
        // TODO validate
        val name = create_food_name.text.toString().trim()
        val ean = create_food_ean.text.toString().trim()
        val calories = create_food_calories.text.toString().trim().toDouble()
        val carbs = create_food_carbs.text.toString().trim().toDouble()
        val protein = create_food_protein.text.toString().trim().toDouble()
        val fat = create_food_fat.text.toString().trim().toDouble()
        val isMeal = false

        val nutrition = Nutrition(calories, carbs, protein, fat)

        return Food(name, nutrition, isMeal, ean)
    }

    fun postFood(food: Food) {

        val authData = AuthHandler.getAuthData(this)

        val json = Gson().toJson(food)
        val client = OkHttpClient()
        val requestBody = json.toRequestBody()
        val request = Request.Builder()
            .url(getString(R.string.BASE_URL) + "foodlist/1")
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
                        startActivity(Intent(this@CreateFood, MainActivity::class.java))
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@CreateFood,
                                "could not create food",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("MainActivity", "Scanned")
                Toast.makeText(this, "Scanned -> " + result.contents, Toast.LENGTH_SHORT)
                    .show()
                create_food_ean.setText(String.format(result.contents.toString()))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkIntendExtras() {
        if (intent.hasExtra(EAN_TRANSFER)) {
            val ean = intent.getStringExtra(EAN_TRANSFER)
            create_food_ean.setText(ean)
        }
    }

}