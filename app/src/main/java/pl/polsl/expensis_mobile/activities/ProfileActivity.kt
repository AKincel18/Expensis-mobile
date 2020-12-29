package pl.polsl.expensis_mobile.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.profile_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.models.user.UserExtension
import pl.polsl.expensis_mobile.models.user.UserBase
import pl.polsl.expensis_mobile.others.LoadingAction
import pl.polsl.expensis_mobile.others.LoggedUser
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.Messages
import pl.polsl.expensis_mobile.utils.Messages.Companion.PASSWORD_CHANGED
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.Utils.Companion.createUserJsonBuilder
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseFullDateToString
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseDateToString
import pl.polsl.expensis_mobile.validators.UserValidator
import java.util.*

class ProfileActivity : AppCompatActivity(), LoadingAction {

    private lateinit var loggedUser: LoggedUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        loggedUser = LoggedUser().serialize()!!
        initIncomeRangeSpinnerHint()
        changeEditableFields(false)
        showProgressBar()
        setFields()
    }

    private fun setFields() {
        emailInput.hint = loggedUser.email
        dateInput.hint = parseFullDateToString(loggedUser.birthDate)

        if (loggedUser.monthlyLimit != null) //if null -> default hint
            monthlyLimitInput.hint = loggedUser.monthlyLimit.toString()

        fillGenderSpinner()
        fetchIncomeRangesCallback()
    }

    private fun onEditProfileButtonClicked(callback: ServerCallback<JSONObject>) {
        editButtonProfile.setOnClickListener {
            val userFormDTO = UserFormDTO(emailInput, genderSpinner, dateInput, monthlyLimitInput,
                    incomeRangeSpinner, passwordInput, passwordConfirmInput, null)

            val userValidator = UserValidator(userFormDTO)
            val validationResult = userValidator.validateEditProfileAction()
            if (validationResult.isValid) {
                val userJson: String?
                if (validationResult.extraMessage == PASSWORD_CHANGED) {
                    val user = UserExtension()
                    user.prepareToUpdatingExtension(userFormDTO)
                    userJson = createUserJsonBuilder().toJson(user)
                    println(userJson)
                } else {
                    val user = UserBase()
                    user.prepareToUpdatingBase(userFormDTO)
                    userJson = createUserJsonBuilder().toJson(user)
                    println(userJson)
                }

                val url = BASE_URL + Endpoint.USERS + loggedUser.id + "/"
                val userJsonObject = JSONObject(userJson!!)
                changeEditableFields(false)
                showProgressBar()
                val volleyService = VolleyService(this, callback)
                volleyService.requestObject(Request.Method.PUT, url, userJsonObject)
            } else {
                showToast(validationResult.message)
            }
        }
    }

    private fun editProfileCallback() {
        onEditProfileButtonClicked(object : ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                SharedPreferencesUtils.setUser(response.toString())
                val intent = Intent(applicationContext, MenuActivity::class.java)
                intent.putExtra(IntentKeys.PROFILE_EDITED, Messages.SUCCESSFULLY_EDITED_PROFILE)
                startActivity(intent)
            }

            override fun onFailure(error: VolleyError) {
                val serverResponse = ServerErrorResponse(error)
                val messageError = serverResponse.getErrorResponse()
                if (messageError != null)
                    showToast(messageError)
                changeEditableFields(true)
                profileProgressBar.visibility = View.INVISIBLE

            }
        })
    }

    private fun onBackButtonClicked() {
        backButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }
    }

    private fun pickDateListener() {
        val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    run {
                        val stringDate = parseDateToString(year, monthOfYear, dayOfMonth)
                        dateInput.text = stringDate
                    }
                }
        dateInput.setOnClickListener {
            val dialog = DatePickerDialog(
                    this,
                    R.style.Theme_Expensismobile,
                    dateSetListener,
                    loggedUser.birthDate.year,
                    loggedUser.birthDate.monthValue - 1,
                    loggedUser.birthDate.dayOfMonth
            )
            val dateNow = Calendar.getInstance()
            dialog.datePicker.maxDate = dateNow.timeInMillis
            dialog.show()
        }

    }

    private fun fillGenderSpinner() {
        val items = this.resources.getStringArray(R.array.gender_array)

        val adapter = ArrayAdapter(
                this,
                R.layout.spinner_gender_layout, R.id.genderSpinnerTextView, items
        )
        adapter.setDropDownViewResource(R.layout.spinner_gender_layout)
        genderSpinner.adapter = adapter
        genderSpinner.setSelection(if (loggedUser.gender == 'F') 0 else 1)
    }

    private fun fetchIncomeRangesCallback() {
        fetchIncomeRanges(object : ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {

                val type = object : TypeToken<List<IncomeRange>>() {}.type
                val incomeRanges = Gson().fromJson<List<IncomeRange>>(response.toString(), type)
                fillIncomeRangeSpinner(incomeRanges)
                pickDateListener()
                onBackButtonClicked()
                editProfileCallback()
                onLogoutButtonClicked()
                changeEditableFields(true)
                profileProgressBar.visibility = View.INVISIBLE
            }

            override fun onFailure(error: VolleyError) {
                val serverError = ServerErrorResponse(error)
                val messageError = serverError.getErrorResponse()
                profileProgressBar.visibility = View.INVISIBLE
                changeEditableFields(false)
                errorAction(messageError)

            }
        })
    }

    private fun fillIncomeRangeSpinner(incomeRanges: List<IncomeRange>) {
        val items = incomeRanges.toMutableList()

        val adapter = ArrayAdapter(
                this,
                R.layout.spinner_income_range_layout, R.id.incomeRangeSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_income_range_layout)
        incomeRangeSpinner.adapter = adapter
        incomeRangeSpinner.setSelection(loggedUser.incomeRange - 1)
    }

    private fun fetchIncomeRanges(callback: ServerCallback<JSONArray>) {

        val url = BASE_URL + Endpoint.INCOME_RANGES
        val volleyService = VolleyService(callback, this)
        volleyService.requestArray(Request.Method.GET, url, null)

    }

    private fun onLogoutButtonClicked() {
        menuLogoutButton.setOnClickListener {
            SharedPreferencesUtils.clearAllSharedPreferences()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun errorAction(messageError: String?) {
        val intent = Intent(applicationContext, MenuActivity::class.java)
        intent.putExtra(IntentKeys.RESPONSE_ERROR, messageError)
        startActivity(intent)
    }

    private fun initIncomeRangeSpinnerHint() {
        val items = arrayListOf(IncomeRange(0, 0, 0)) //add only hint

        val adapter = ArrayAdapter(
                this,
                R.layout.spinner_income_range_layout, R.id.incomeRangeSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_income_range_layout)
        incomeRangeSpinner.adapter = adapter
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        Thread(Runnable {
            this.runOnUiThread {
                profileProgressBar.visibility = View.VISIBLE
            }
        }).start()
    }

    override fun changeEditableFields(isEnabled: Boolean) {
        emailInput.isEnabled = isEnabled
        genderSpinner.isClickable = isEnabled
        genderSpinner.isEnabled = isEnabled
        dateInput.isClickable = isEnabled
        monthlyLimitInput.isEnabled = isEnabled
        incomeRangeSpinner.isClickable = isEnabled
        incomeRangeSpinner.isEnabled = isEnabled
        passwordInput.isEnabled = isEnabled
        passwordConfirmInput.isEnabled = isEnabled
        editButtonProfile.isEnabled = isEnabled
        backButton.isEnabled = isEnabled
        menuLogoutButton.isEnabled = isEnabled
    }
}