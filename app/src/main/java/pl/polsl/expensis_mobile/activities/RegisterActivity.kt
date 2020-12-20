package pl.polsl.expensis_mobile.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.register_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.SpinnerAdapter
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.models.User
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.IntentKeys
import pl.polsl.expensis_mobile.utils.Messages
import pl.polsl.expensis_mobile.utils.Utils.Companion.createUserJsonBuilder
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseDateToString
import pl.polsl.expensis_mobile.validators.UserValidator
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchIncomeRangesCallback()
    }

    private fun fetchIncomeRangesCallback() {
        fetchIncomeRanges(object: ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {
                setContentView(R.layout.register_activity)
                val type = object : TypeToken<List<IncomeRange>>() {}.type
                val incomeRanges = Gson().fromJson<List<IncomeRange>>(response.toString(), type)
                fillIncomeRangeSpinner(incomeRanges)
                fillGenderSpinner()
                pickDateListener()
                registerUserCallback()
            }

            override fun onFailure(error: VolleyError) {
                val serverError = ServerErrorResponse(error)
                val messageError = serverError.getErrorResponse()
                errorAction(messageError)

            }
        })
    }

    private fun errorAction(messageError: String?) {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra(IntentKeys.RESPONSE_ERROR, messageError)
        startActivity(intent)
    }


    private fun fetchIncomeRanges(callback: ServerCallback<JSONArray>) {

        val url = BASE_URL + Endpoint.INCOME_RANGES
        val volleyService = VolleyService(callback, this)
        volleyService.requestArray(Request.Method.GET, url, null)

    }

    private fun fillIncomeRangeSpinner(incomeRanges: List<IncomeRange>) {
        val items = incomeRanges.toMutableList()

        items.add(IncomeRange(0, 0, 0)) //add hint

        val adapter = SpinnerAdapter(
            this,
            R.layout.spinner_income_range_layout, R.id.incomeRangeSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_income_range_layout)
        incomeRangeSpinner.adapter = adapter
        incomeRangeSpinner.setSelection(adapter.count)
    }

    private fun fillGenderSpinner() {
        val items: MutableList<String> =
            this.resources.getStringArray(R.array.gender_array).toMutableList()
        items.add(this.resources.getString(R.string.gender_hint)) // add hint

        val adapter = SpinnerAdapter(
            this,
            R.layout.spinner_gender_layout, R.id.genderSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_gender_layout)
        genderSpinner.adapter = adapter
        genderSpinner.setSelection(adapter.count)
    }

    private fun pickDateListener() {
        val dateSetListener = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                val stringDate = parseDateToString(year, monthOfYear, dayOfMonth)
                dateInput.text = stringDate
            }
        }

        val dateNow = Calendar.getInstance()
        dateInput.setOnClickListener {
            val dialog = DatePickerDialog(
                this,
                R.style.Theme_Expensismobile,
                dateSetListener,
                dateNow.get(Calendar.YEAR),
                dateNow.get(Calendar.MONTH),
                dateNow.get(Calendar.DAY_OF_MONTH)

            )

            dialog.datePicker.maxDate = dateNow.timeInMillis
            dialog.show()
        }

    }

    private fun registerUser(callback: ServerCallback<JSONObject>) {

        registerButton.setOnClickListener {
            val userFormDTO = UserFormDTO(
                emailInput, genderSpinner, dateInput,
                monthlyLimitInput, incomeRangeSpinner,
                passwordInput, passwordConfirmInput, getString(R.string.gender_hint)
            )

            val userValidator = UserValidator()
            val validationResult = userValidator.validate(userFormDTO)
            if (validationResult.isValid) {
                val user = User(userFormDTO)
                val userJson = createUserJsonBuilder().toJson(user)
                println(userJson)
                val url = BASE_URL + Endpoint.USERS
                val userJsonObject = JSONObject(userJson)
                val volleyService = VolleyService(this, callback)
                volleyService.requestObject(Request.Method.POST, url, userJsonObject)
            } else {
                Toast.makeText(this, validationResult.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun registerUserCallback() {
        registerUser(object: ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.putExtra(IntentKeys.REGISTERED, Messages.SUCCESSFULLY_REGISTERED)
                startActivity(intent)
            }

            override fun onFailure(error: VolleyError) {
                val serverResponse = ServerErrorResponse(error)
                val messageError = serverResponse.getErrorResponse()
                if (messageError != null)
                    showToast(messageError)

            }
        })
    }

    private fun showToast(messageError: String?) {
        Toast.makeText(this, messageError, Toast.LENGTH_SHORT).show()
    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}