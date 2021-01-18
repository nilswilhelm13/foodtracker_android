package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_food_details.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.*
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.System.out
import java.text.SimpleDateFormat
import java.util.*

internal const val TEMPLATE_TRANSFER = "TEMPLATE_TRANSFER"

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
        if (!food?.isMeal!!){
            hideMealFields()
        }
        food_meta_fields.text = meta_fields

        food_actual.addTextChangedListener {
            try {
                updateAmount()
            } catch (e: Exception){

            }

        }
        food_total.addTextChangedListener {
            try {
                updateAmount()
            } catch (e: Exception){

            }
        }

        food_button.setOnClickListener {
            postFood()
        }
        load_as_template.setOnClickListener {
            loadTemplate()
        }
    }
    private fun hideMealFields(){
        food_actual_label.visibility = View.GONE
        food_actual.visibility = View.GONE
        food_total_label.visibility = View.GONE
        food_total.visibility = View.GONE
        load_as_template.visibility = View.GONE
    }

    private fun updateAmount(){
        food_amount.setText(relativeAmount().toString())
    }

    private fun relativeAmount(): Double{
        val total = food_total.text.toString().toDouble()
        val factor = totalOfIngredients() / total
        val actual = food_actual.text.toString().toDouble()
        return actual * factor
    }

    private fun totalOfIngredients(): Double{
        return food?.ingredients!!.fold(0.0) { acc, ingredient -> acc + ingredient.amount }
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

    fun loadTemplate(){

        val data = Gson().toJson(food?.ingredients?.toList())
        val intent = Intent(this, CreateMeal::class.java)
        Log.d(TAG, food?.ingredients.toString())
        intent.putExtra(TEMPLATE_TRANSFER, data)
        startActivity(intent)
    }
}