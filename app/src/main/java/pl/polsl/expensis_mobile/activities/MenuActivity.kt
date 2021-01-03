package pl.polsl.expensis_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils.Companion.clearAllSharedPreferences

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        checkIntent()
    }

    fun onExpensesClick(view: View) {
        startActivity(Intent(this, ExpensesActivity::class.java))
    }

    fun onGraphsClick(view: View) {
        //TODO: startActivity(Intent(this, GraphsActivity::class.java))
    }

    fun onProfileClick(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    private fun checkIntent() {
        val intent: Intent = intent
        intent.extras
        if (intent.hasExtra(IntentKeys.PROFILE_EDITED)) {
            Toast.makeText(this, intent.getStringExtra(IntentKeys.PROFILE_EDITED), Toast.LENGTH_SHORT).show()
        }
        else if (intent.hasExtra(IntentKeys.RESPONSE_ERROR))
            Toast.makeText(this, intent.getStringExtra(IntentKeys.RESPONSE_ERROR), Toast.LENGTH_SHORT).show()
    }
}