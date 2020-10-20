package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.utils.Utils
import okhttp3.*
import java.io.IOException

class MainActivity : BaseActivity(), Callback {

    private var TAG = "MainActivity"
    private var pieChart: PieChart? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AuthHandler.autoAuth(this)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)



        floatingActionButton.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(true)
            intentIntegrator.initiateScan()
            intentIntegrator.setOrientationLocked(true)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchItem = menu.findItem(R.id.search)
//        val searchView = searchItem?.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
////                getFoodByEAN("4353466")
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
////                doMySearch(newText!!)
//                return false
//            }
//        })

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
//                Toast.makeText(this, "Scanned -> " + result.contents, Toast.LENGTH_SHORT)
//                    .show()
                Log.d(TAG, result.contents)
                Utils.getFoodByEAN(this, result.contents, this)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
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
                    Toast.makeText(this@MainActivity, "Response Failed", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val resString = response.body!!.string()
                val gson = Gson()
                val food = gson.fromJson<Food>(resString, Food::class.java)

                if (food != null) {
                    val intent = Intent(this, FoodDetailsActivity::class.java)
                    Log.d(TAG, food.name)
                    intent.putExtra(FOOD_TRANSFER, food)
                    startActivity(intent)
                }
            }
        }
    }




}