package net.nilswilhelm.foodtracker.activities

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import net.nilswilhelm.foodtracker.R



open class BaseActivity : AppCompatActivity(){
    private val TAG = "BaseActivity"

    internal fun activateToolbar(enableHome: Boolean){
        Log.d(TAG, ".activateToolbar")

//        var toolbar = findViewById<View>(R.id.toolbar) as Toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(enableHome)
    }

}