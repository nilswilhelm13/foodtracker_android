package net.nilswilhelm.foodtracker.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_create_food.*
import kotlinx.android.synthetic.main.activity_create_meal.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.IngredientAdapter
import net.nilswilhelm.foodtracker.adapters.OnDeleteButtonPressed
import net.nilswilhelm.foodtracker.adapters.OnEditTextChanged
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.data.Ingredient
import net.nilswilhelm.foodtracker.data.Nutrition
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.lang.NumberFormatException

const val INGREDIENT_LIST = "ingredients"
const val NUTRITION = "nutrition"
const val INGREDIENT_TRANSFER = "ingredient_transfer"

class CreateMeal : AppCompatActivity(), OnEditTextChanged, OnDeleteButtonPressed {

    private val TAG = "CreateMeal"
    private lateinit var nutrition: Nutrition
    private var ingredients = arrayListOf<Ingredient>()
    private var foodList = arrayListOf<Food>()
    private val ingredientAdapter: IngredientAdapter = IngredientAdapter(ArrayList(), this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meal)
        nutrition = Nutrition(0.0, 0.0, 0.0, 0.0)
        loadIngredientsFromSharedPrefs()
        checkIntendExtras()
        initRecyclerView()
        updateNutritionView()

        setListeners()
    }

    private fun updateNutritionView() {
        create_meal_energy.text = "Energy %.2f".format(nutrition.energy)
        create_meal_carbohydrate.text = "Carbohydrate %.2f".format(nutrition.carbohydrate)
        create_meal_protein.text = "Protein %.2f".format(nutrition.protein)
        create_meal_fat.text = "Fat %.2f".format(nutrition.fat)
    }

    private fun initRecyclerView() {
        create_meal_ingredients.layoutManager = LinearLayoutManager(this)
        create_meal_ingredients.adapter = ingredientAdapter
        ingredientAdapter.loadNewData(ingredients)
    }

    private fun checkIntendExtras() {
        if (intent.hasExtra(FOOD_TRANSFER)) {
            val food = intent.getParcelableExtra<Food>(FOOD_TRANSFER) as Food
            if (!isAlreadyInIngredients(food)) {
                val ingredient = Ingredient(food, 0.0)
                ingredients.add(ingredient)
            }
        }
        if (intent.hasExtra(TEMPLATE_TRANSFER)) {
            val ingredientsJSON =
                intent.getStringExtra(TEMPLATE_TRANSFER)
            val itemType = object : TypeToken<ArrayList<Ingredient>>() {}.type
            try {
                ingredients = Gson().fromJson<ArrayList<Ingredient>>(ingredientsJSON, itemType)
            } catch (e: Exception) {
            }
            Log.d(TAG +" Size", ingredients.size.toString())
            ingredientAdapter.loadNewData(ingredients)



        }
    }

    private fun loadIngredientsFromSharedPrefs() {
        val sharedPref: SharedPreferences =
            getSharedPreferences("FOODTRACKER", Context.MODE_PRIVATE)
        val ingredientsJSON = sharedPref.getString(INGREDIENT_LIST, "")
        val nutritionJSON = sharedPref.getString(NUTRITION, "")
        if (ingredientsJSON != "") {
            val itemType = object : TypeToken<ArrayList<Ingredient>>() {}.type
            try {
                ingredients = Gson().fromJson<ArrayList<Ingredient>>(ingredientsJSON, itemType)
                nutrition = Gson().fromJson(nutritionJSON, Nutrition::class.java)
            } catch (e: Exception) {
            }
        }
    }


    override fun onPause() {
        super.onPause()
        storeIngredients()
    }

    override fun onDestroy() {
        super.onDestroy()
        storeIngredients()
    }

    private fun storeIngredients() {
        val ingredientsJSON = Gson().toJson(ingredients)
        val nutritionJSON = Gson().toJson(nutrition)
        val sharedPref: SharedPreferences =
            getSharedPreferences("FOODTRACKER", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(INGREDIENT_LIST, ingredientsJSON)
        editor.putString(NUTRITION, nutritionJSON)
        editor.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_create_meal_search -> {
                startActivity(Intent(this, SearchIngredientsActivityActivity::class.java))
                true
            }
            R.id.menu_create_meal_scan_button -> {
                val intentIntegrator = IntentIntegrator(this)
                intentIntegrator.setBeepEnabled(false)
                intentIntegrator.setCameraId(0)
                intentIntegrator.setPrompt("SCAN")
                intentIntegrator.setBarcodeImageEnabled(true)
                intentIntegrator.setOrientationLocked(true)
                intentIntegrator.captureActivity = CaptureActivityPortrait::class.java
                intentIntegrator.initiateScan()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_meal, menu)
        return true
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
                getFoodByEAN(String.format(result.contents.toString()))
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getFoodByEAN(ean: String) {
        val authData: AuthData = AuthHandler.getAuthData(this)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(getString(R.string.BASE_URL) + "foodlist/" + ean + "?ean=true")
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
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "onResponse()")
                response.use {

                    if (!response.isSuccessful) {
                        val message = when (response.code) {
                            404 -> "Food not found"
                            401 -> "Unauthorized"
                            400 -> "Bad request"
                            else -> "Request failed"
                        }
                        runOnUiThread {
                            Toast.makeText(
                                this@CreateMeal,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val resString = response.body!!.string()

                        val gson = Gson()
                        val food = gson.fromJson<Food>(resString, Food::class.java)

                        if (food != null) {
                            val ingredient = Ingredient(food, 0.0)
                            ingredients.add(ingredient)
                            runOnUiThread {
                                ingredientAdapter.loadNewData(ingredients)
                            }
                        }
                    }

                }
            }
        })
    }


    private fun setListeners() {
        create_meal_submit.setOnClickListener {
            postMeal(buildMeal())
            cleanup()
        }
    }

    private fun cleanup() {
        ingredients.clear()
        nutrition = Nutrition(0.0, 0.0, 0.0, 0.0)
        create_meal_name.setText("Meal Name")
    }

    override fun onTextChanged(position: Int, charSeq: String?) {
        try {
            Log.d(TAG, "SIZE: ${ingredients.size}")
            Log.d(TAG, "INDEX: $position")
            ingredients[position].amount = charSeq!!.toDouble()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: Exception){
            e.printStackTrace()
        }
        recalculateNutrition()
    }

    private fun recalculateNutrition() {
        if (ingredients.size > 0) {
            var energy = 0.0
            var carbs = 0.0
            var protein = 0.0
            var fat = 0.0
            var amount = 0.0
            Log.d(TAG, ingredients.toString())

            for (ingredient in ingredients) {
                energy += ingredient.food.nutrition.energy / 100 * ingredient.amount
                carbs += ingredient.food.nutrition.carbohydrate / 100 * ingredient.amount
                protein += ingredient.food.nutrition.protein / 100 * ingredient.amount
                fat += ingredient.food.nutrition.fat / 100 * ingredient.amount
                amount += ingredient.amount
            }

            nutrition.energy = energy / amount * 100
            nutrition.carbohydrate = carbs / amount * 100
            nutrition.protein = protein / amount * 100
            nutrition.fat = fat / amount * 100
        } else {
            nutrition = Nutrition(0.0, 0.0, 0.0, 0.0)
        }

        Log.d(TAG, nutrition.energy.toString())
        updateNutritionView()
    }

    override fun onDeleteButtonPressed(position: Int) {
        Log.d(TAG, "DELETE POSITION: $position")
        ingredients.removeAt(position)
        ingredientAdapter.loadNewData(ingredients)
        try {
            recalculateNutrition()
        } catch (e: Exception) {
        }
    }

    fun isAlreadyInIngredients(food: Food): Boolean {
        for (ingredient in ingredients) {
            if (ingredient.food.id == food.id) {
                return true
            }
        }
        return false
    }

    fun buildMeal(): Food {
        return Food(
            name = create_meal_name.text.toString(),
            nutrition = nutrition,
            isMeal = true,
            ean = "",
            ingredients = ingredients.toTypedArray()
        )
    }

    fun postMeal(food: Food) {

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
                        startActivity(Intent(this@CreateMeal, MainActivity::class.java))
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@CreateMeal,
                                "could not create meal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }
}