package pl.polsl.expensis_mobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import pl.polsl.expensis_mobile.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }

    fun onRegisterClicked(view: View) {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(this, MenuActivity::class.java))
    }
}