package net.nilswilhelm.foodtracker.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.DashboardData
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}



private val _errorMessage = MutableLiveData<String>()
fun errorMessage(): LiveData<String> {
    return _errorMessage
}

