package net.nilswilhelm.foodtracker.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_authenticator.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.auth.AuthHandler.Companion.login
import net.nilswilhelm.foodtracker.auth.AuthHandler.Companion.storeAuthData
import net.nilswilhelm.foodtracker.data.AuthResponse
import okhttp3.*
import java.io.IOException


class AuthenticatorActivity : AppCompatActivity(), Callback {

    private val TAG = "AuthenticatorActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticator)

        login_button.setOnClickListener {
            login(this, login_email.text.toString(), login_password.text.toString(), this)
        }
    }

    fun toast() {
        runOnUiThread {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
    }

    override fun onResponse(call: Call, response: Response) {
        response.use {
            if (!response.isSuccessful) {
                toast()
            }
            if (response.code == 200) {
                val responseString = response.body!!.string()
                val gson = Gson()
                val authResponse =
                    gson.fromJson<AuthResponse>(responseString, AuthResponse::class.java)
                // store auth data in shared preferences
                storeAuthData(this@AuthenticatorActivity, authResponse)
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                toast()
            }
        }
    }


}