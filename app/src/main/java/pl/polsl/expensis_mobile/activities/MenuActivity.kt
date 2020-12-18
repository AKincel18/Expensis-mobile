package pl.polsl.expensis_mobile.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.polsl.expensis_mobile.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        getTokenForTesting()
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
        startActivity(Intent(this, LoginActivity::class.java))
    }

    /**
     * only for testing, todo remove after testing
     */
    private fun getTokenForTesting() {
        val pref = applicationContext.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val token = pref.getString("access_token", null)
        println("TOKEN = $token")
    }
}