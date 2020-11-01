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
import kotlinx.android.synthetic.main.activity_create_meal.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.FoodListAdapter
import net.nilswilhelm.foodtracker.adapters.IngredientAdapter
import net.nilswilhelm.foodtracker.adapters.OnDeleteButtonPressed
import net.nilswilhelm.foodtracker.adapters.OnEditTextChanged
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.data.Ingredient
import net.nilswilhelm.foodtracker.data.Nutrition
import okhttp3.*
import java.io.IOException
import java.lang.Exception
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
        loadIngredients()
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
            if (!idAlreadyInIngredients(food)){
                val ingredient = Ingredient(food, 0.0)
                ingredients.add(ingredient)
            }

        }
    }

    private fun loadIngredients() {
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

        // Get the SearchView and set the searchable configuration
//        val searchItem = menu.findItem(R.id.search)
//        val searchView = searchItem?.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                getFoodByEAN("4353466")
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                doMySearch(newText!!)
//                return false
//            }
//        })

        return true
    }

    fun doMySearch(query: String) {
        Log.d(TAG, "doMySearch()")
        val authData = AuthHandler.getAuthData(this)
        Log.d(TAG, authData.token)
        Log.d(TAG, "Hallo")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(getString(R.string.BASE_URL) + "search/" + query)
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
                        runOnUiThread {
                            Toast.makeText(this@CreateMeal, "Response Failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        val resString = response.body!!.string()

                        val gson = Gson()
                        val itemType = object : TypeToken<ArrayList<Food>>() {}.type
                        val searchResults = gson.fromJson<ArrayList<Food>>(resString, itemType)

                        if (searchResults != null) {

                            foodList = searchResults

                            runOnUiThread {
                                val adapter = FoodListAdapter(
                                    this@CreateMeal,
                                    R.layout.item,
                                    searchResults
                                )
                            }
                        }
                    }
                }
            }
        })
    }

    fun calculateNutrition() {
        var energy = 0.0
        var carbohydrate = 0.0
        var protein = 0.0
        var fat = 0.0
        for (ingredient in ingredients) {
            energy += ingredient.food.nutrition.energy * ingredient.amount / 100
            carbohydrate += ingredient.food.nutrition.carbohydrate * ingredient.amount / 100
            protein += ingredient.food.nutrition.protein * ingredient.amount / 100
            fat += ingredient.food.nutrition.fat * ingredient.amount / 100
        }
        create_meal_energy.text = String.format("%.2f kcal", energy)
        create_meal_carbohydrate.text = String.format("%.2f g", carbohydrate)
        create_meal_protein.text = String.format("%.2f g", protein)
        create_meal_fat.text = String.format("%.2f g", fat)
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
//        create_meal_listView.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                create_meal_listView.adapter = null
//
//                val food = foodList[position]
//                Log.d(TAG, food.toString())
//                val ingredient = Ingredient(food, 0.0)
//
//                ingredients.add(ingredient)
//
//                val adapter = IngredientAdapter(
//                    this@CreateMeal,
//                    R.layout.ingredient,
//                    ingredients
//                )
//                create_meal_ingredients.adapter = adapter
//            }


    }

    override fun onTextChanged(position: Int, charSeq: String?) {
        try {
            ingredients[position].amount = charSeq!!.toDouble()
        } catch (e: NumberFormatException) {

        }
        recalcuralteNutrition()

    }

    private fun recalcuralteNutrition() {
        var energy = 0.0
        var carbs = 0.0
        var protein = 0.0
        var fat = 0.0


        for (ingredient in ingredients) {
            energy += ingredient.food.nutrition.energy * ingredient.amount / 100
            carbs += ingredient.food.nutrition.carbohydrate * ingredient.amount / 100
            protein += ingredient.food.nutrition.protein * ingredient.amount / 100
            fat += ingredient.food.nutrition.fat * ingredient.amount / 100
        }

        nutrition.energy = energy
        nutrition.carbohydrate = carbs
        nutrition.protein = protein
        nutrition.fat = fat
        Log.d(TAG, nutrition.energy.toString())
        updateNutritionView()
    }

    override fun onDeleteButtonPressed(position: Int) {
        ingredients.removeAt(position)
        recalcuralteNutrition()
        ingredientAdapter.loadNewData(ingredients)
    }

    fun idAlreadyInIngredients(food: Food): Boolean{
        for (ingredient in ingredients){
            if (ingredient.food.id == food.id){
                return true
            }
        }
        return false
    }
}