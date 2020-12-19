package pl.polsl.expensis_mobile.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.register_activity.*
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.SpinnerAdapter
import pl.polsl.expensis_mobile.dto.UserFormDTO
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.models.User
import pl.polsl.expensis_mobile.rest.BASE_URL
import pl.polsl.expensis_mobile.rest.Endpoint
import pl.polsl.expensis_mobile.rest.VolleySingleton
import pl.polsl.expensis_mobile.utils.Utils.Companion.createUserJsonBuilder
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseDateToString
import pl.polsl.expensis_mobile.validators.UserValidator
import java.util.*


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        fetchIncomeRanges()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        fillGenderSpinner()
        pickDateListener()
        registerUser()

    }


    private fun fetchIncomeRanges() {

        val url = BASE_URL + Endpoint.INCOME_RANGES
        val objectRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                println("Rest response = $response")

                val type = object : TypeToken<List<IncomeRange>>() {}.type
                val incomeRanges = Gson().fromJson<List<IncomeRange>>(response.toString(), type)
                fillIncomeRangeSpinner(incomeRanges)

                println(incomeRanges.toString())
            },
            { error ->
                println("error! $error")
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(objectRequest)
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

    private fun registerUser() {
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
                val objectRequest = JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    userJsonObject,
                    { response ->
                        println("Rest response = $response")
                        startActivity(Intent(this, LoginActivity::class.java))
                    },
                    { error ->
                        println("error! $error")
                    }
                )
                VolleySingleton.getInstance(this).addToRequestQueue(objectRequest)
            }
            Toast.makeText(this, validationResult.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}