package pl.polsl.expensis_mobile.activities.stats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import kotlinx.android.synthetic.main.stats_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.activities.LoginActivity
import pl.polsl.expensis_mobile.activities.MenuActivity
import pl.polsl.expensis_mobile.dto.stats.StatsFilterDTO
import pl.polsl.expensis_mobile.dto.stats.StatsRequestDTO
import pl.polsl.expensis_mobile.dto.stats.StatsRequestFormDTO
import pl.polsl.expensis_mobile.others.LoadingAction
import pl.polsl.expensis_mobile.others.StatsName
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.TokenUtils
import pl.polsl.expensis_mobile.validators.StatsValidator

class StatsActivity : AppCompatActivity(), LoadingAction {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stats_activity)
        statsProgressBar.visibility = View.INVISIBLE
        initSpinner()
        onItemSelectedSpinnerListener()
        checkBoxesListener()
        onShowStatClickedCallback()
        onBackClicked()
    }

    private fun initSpinner() {
        val adapter = ArrayAdapter<StatsName>(
            this, R.layout.spinner_stats_name_layout,
            R.id.statsNameSpinnerTextView, StatsName.values()
        )
        adapter.setDropDownViewResource(R.layout.spinner_stats_name_layout)
        statsNameSpinner.adapter = adapter
    }

    private fun onShowStatClickedCallback() {
        onShowStatClicked(object : ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {
                val intent = Intent(applicationContext, DisplayStatsActivity::class.java)
                intent.putExtra(IntentKeys.DATA_STATS, response.toString())
                intent.putExtra(
                    IntentKeys.DATA_NAME,
                    (statsNameSpinner.selectedItem as StatsName).ordinal
                )
                startActivity(intent)
            }

            override fun onFailure(error: VolleyError) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
                    refreshTokenCallback()
                } else {
                    val serverError = ServerErrorResponse(error)
                    val messageError = serverError.getErrorResponse()
                    statsProgressBar.visibility = View.INVISIBLE
                    changeEditableFields(true)
                    Toast.makeText(applicationContext, messageError, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun refreshTokenCallback() {
        TokenUtils.refreshToken(object : ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                SharedPreferencesUtils.storeTokens(
                    response.get(SharedPreferencesUtils.accessTokenConst) as String,
                    TokenUtils.refreshToken,
                    null
                )
                startActivity(Intent(applicationContext, MenuActivity::class.java))
            }

            override fun onFailure(error: VolleyError) {
                TokenUtils.refreshTokenOnFailure(error)
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            }
        })
    }

    private fun onShowStatClicked(callback: ServerCallback<JSONArray>) {
        showStatsButton.setOnClickListener {
            val statForm = StatsRequestFormDTO(
                statsNameSpinner, incomeRangeCheckBox, ageRangeCheckBox, genderCheckBox
            )
            val statValidator = StatsValidator(statForm)
            val validationResult = statValidator.validateStats()
            if (validationResult.isValid) {
                val statRequest = StatsRequestDTO(
                    statsNameSpinner.selectedItem.toString(),
                    StatsFilterDTO(
                        incomeRangeCheckBox.isChecked,
                        ageRangeCheckBox.isChecked,
                        genderCheckBox.isChecked
                    )
                )
                val jsonObject = JSONObject(Gson().toJson(statRequest))
                println(jsonObject.toString())
                val url = BASE_URL + Endpoint.STATS
                changeEditableFields(false)
                showProgressBar()
                VolleyService().requestMixed(Request.Method.POST, url, jsonObject, callback, this)
            } else {
                Toast.makeText(this, validationResult.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun onItemSelectedSpinnerListener() {
        statsNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (statsNameSpinner.selectedItem == StatsName.SEPARATED) {
                    incomeRangeCheckBox.isChecked = false
                    ageRangeCheckBox.isChecked = false
                    genderCheckBox.isChecked = false
                }
            }
        }
    }

    private fun checkBoxesListener() {
        incomeRangeCheckBox.setOnClickListener {
            if (statsNameSpinner.selectedItem == StatsName.SEPARATED) {
                ageRangeCheckBox.isChecked = false
                genderCheckBox.isChecked = false
            }
        }

        ageRangeCheckBox.setOnClickListener {
            if (statsNameSpinner.selectedItem == StatsName.SEPARATED) {
                incomeRangeCheckBox.isChecked = false
                genderCheckBox.isChecked = false
            }
        }

        genderCheckBox.setOnClickListener {
            if (statsNameSpinner.selectedItem == StatsName.SEPARATED) {
                incomeRangeCheckBox.isChecked = false
                ageRangeCheckBox.isChecked = false
            }
        }
    }

    private fun onBackClicked() {
        backButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    override fun showProgressBar() {
        Thread(Runnable {
            this.runOnUiThread {
                statsProgressBar.visibility = View.VISIBLE
            }
        }).start()
    }

    override fun changeEditableFields(isEnabled: Boolean) {
        backButton.isEnabled = isEnabled
        statsNameSpinner.isClickable = isEnabled
        statsNameSpinner.isEnabled = isEnabled
        incomeRangeCheckBox.isEnabled = isEnabled
        genderCheckBox.isEnabled = isEnabled
        ageRangeCheckBox.isEnabled = isEnabled
        showStatsButton.isEnabled = isEnabled
    }
}