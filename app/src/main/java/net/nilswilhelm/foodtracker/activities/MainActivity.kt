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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.Food
import net.nilswilhelm.foodtracker.dialogs.FoodNotFoundDialog
import net.nilswilhelm.foodtracker.dialogs.MealOrFoodDialog
import net.nilswilhelm.foodtracker.utils.Utils
import okhttp3.*
import java.io.IOException

class MainActivity : BaseActivity(), Callback{

    private var TAG = "MainActivity"
    private var ean = ""

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
            intentIntegrator.captureActivity = CaptureActivityPortrait::class.java
            intentIntegrator.setOrientationLocked(true)
            intentIntegrator.initiateScan()

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
            R.id.add_button -> {
                MealOrFoodDialog(this).show(this.supportFragmentManager, "")
                true
            }
            R.id.goals_button -> {
                startActivity(Intent(this, EditGoals::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
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
                ean = result.contents
                Utils.getFoodByEAN(this, ean, this)
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
                if(response.code == 404){
                    FoodNotFoundDialog(ean).show(this.supportFragmentManager, "")
                }
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