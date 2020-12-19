package pl.polsl.expensis_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.clearAllSharedPreferences

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
    }

    fun onExpensesClick(view: View) {
        //TODO: startActivity(Intent(this, ExpensesActivity::class.java))
    }

    fun onGraphsClick(view: View) {
        //TODO: startActivity(Intent(this, GraphsActivity::class.java))
    }

    fun onProfileClick(view: View) {
        //TODO: startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun onLogoutClicked(view: View) {
        clearAllSharedPreferences()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}