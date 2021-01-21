package pl.polsl.expensis_mobile.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.menu_activity.*
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.activities.expenses.ExpensesActivity
import pl.polsl.expensis_mobile.activities.stats.StatsActivity
import pl.polsl.expensis_mobile.others.LoggedUser
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.Messages

class MenuActivity : AppCompatActivity(){

    private lateinit var loggedUser: LoggedUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        checkIntent()
        checkPermission()
    }

    fun onExpensesClick(view: View) {
        startActivity(Intent(this, ExpensesActivity::class.java))
    }

    fun onStatsClick(view: View) {
        if (loggedUser.allowDataCollection)
            startActivity(Intent(this, StatsActivity::class.java))
        else
            Toast.makeText(this, Messages.NO_PERMISSION, Toast.LENGTH_SHORT).show()
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

    private fun checkPermission() {
        loggedUser = LoggedUser().serialize()!!
        if (loggedUser.allowDataCollection) {
            menuStatsButton.alpha = 1f
        }
        else {
            menuStatsButton.alpha = 0.25f
        }
    }
}