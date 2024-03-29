package net.nilswilhelm.foodtracker.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.results.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.FoodRecyclerViewAdapter
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.utils.RecyclerItemCLickListener
import net.nilswilhelm.foodtracker.utils.Utils
import okhttp3.*
import java.io.IOException


class SearchIngredientsActivityActivity : BaseActivity(), Callback, RecyclerItemCLickListener.OnRecyclerClickListener{

    private val TAG = "SearchActivity"
    private var searchView: SearchView? = null
    private var foodList = arrayListOf<Food>()
    private val foodRecyclerViewAdapter = FoodRecyclerViewAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addOnItemTouchListener(RecyclerItemCLickListener(this, recycler_view, this))
        recycler_view.adapter = foodRecyclerViewAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView

        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(searchableInfo)
        searchView?.isIconified = false

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Utils.doMySearch(this@SearchIngredientsActivityActivity, query!!, this@SearchIngredientsActivityActivity)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Utils.doMySearch(this@SearchIngredientsActivityActivity, newText!!, this@SearchIngredientsActivityActivity)
                return true
            }
        })
        return true
    }

    override fun onFailure(call: Call, e: IOException) {
        Log.d(TAG, "onFailure()")
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        Log.d(TAG, "onResponse()")
        response.use {
            if (!response.isSuccessful) {
                runOnUiThread {
                    Toast.makeText(this@SearchIngredientsActivityActivity, "Response Failed", Toast.LENGTH_SHORT)
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
                        foodRecyclerViewAdapter.loadNewData(foodList)
                    }
                }
            }
        }
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d(TAG, ".onItemClick: starts")
        Toast.makeText(this, "Normal tap at position $position", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, position: Int) {
        Log.d(TAG, ".onItemLongClick: starts")
        val food = foodRecyclerViewAdapter.getFood(position)
        if (food != null){
            val intent = Intent(this, CreateMeal::class.java)
            Log.d(TAG, food.name)
            intent.putExtra(FOOD_TRANSFER, food)
            startActivity(intent)
        }
    }
}