package pl.polsl.expensis_mobile.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.LocalDateTypeAdapter
import pl.polsl.expensis_mobile.adapters.SpinnerAdapter
import pl.polsl.expensis_mobile.models.IncomeRange
import pl.polsl.expensis_mobile.models.User
import pl.polsl.expensis_mobile.rest.BASE_URL
import pl.polsl.expensis_mobile.rest.Endpoint
import pl.polsl.expensis_mobile.rest.VolleySingleton
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseDateToString
import pl.polsl.expensis_mobile.validators.UserValidator
import java.time.LocalDate
import java.util.*

@SuppressLint("NewApi")
class RegisterActivity : AppCompatActivity() {

    private var incomeRanges: List<IncomeRange> = listOf()
    private lateinit var genderSpinner: Spinner
    private lateinit var incomeRangesSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        fetchIncomeRanges()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        fillGenderSpinner()
        pickDateListener()
        registerUser()

    }

    private fun fillIncomeRangeSpinner() {
        incomeRangesSpinner = findViewById(R.id.incomeRangeSpinner)
        val items = incomeRanges.toMutableList()


        items.add(IncomeRange(0, 0, 0)) //add hint

        val adapter = SpinnerAdapter(
                this,
                R.layout.spinner_income_range_layout, R.id.incomeRangeSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_income_range_layout)
        incomeRangesSpinner.adapter = adapter
        incomeRangesSpinner.setSelection(adapter.count)
    }

    private fun pickDateListener() {
        val dateInput = findViewById<TextView>(R.id.dateInput)

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
            dialog.show()
        }

    }

    fun onLoginClicked(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun fillGenderSpinner() {

        genderSpinner = findViewById(R.id.genderSpinner)
        val items: MutableList<String> = this.resources.getStringArray(R.array.gender_array).toMutableList()
        items.add(this.resources.getString(R.string.gender_hint)) // add hint

        val adapter = SpinnerAdapter(
                this,
                R.layout.spinner_gender_layout, R.id.genderSpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_gender_layout)
        genderSpinner.adapter = adapter
        genderSpinner.setSelection(adapter.count)
    }

    private fun fetchIncomeRanges() {

        val url = BASE_URL + Endpoint.INCOME_RANGES
        val objectRequest = JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener { response ->
                    println("Rest response = $response")

                    val type = object : TypeToken<List<IncomeRange>>() {}.type
                    incomeRanges = Gson().fromJson<List<IncomeRange>>(response.toString(), type)

                    fillIncomeRangeSpinner()

                    println(incomeRanges.toString())
                },
                Response.ErrorListener { error ->
                    println("error! $error")
                }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(objectRequest)
    }


    private fun registerUser() {
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val userValidator = UserValidator(
                    findViewById(R.id.emailInput),genderSpinner, findViewById(R.id.dateInput),
                    findViewById(R.id.monthlyLimitInput), incomeRangesSpinner,
                    findViewById(R.id.passwordInput), findViewById(R.id.passwordConfirmInput),
                    getString(R.string.gender_hint))

            val user: User? = userValidator.validateUser()
            if (user != null) {
                val userJson = createJsonBuilder().toJson(user)
                println(userJson)
                val url = BASE_URL + Endpoint.USERS
                val userJsonObject = JSONObject(userJson)
                val objectRequest = JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        userJsonObject,
                        Response.Listener { response ->
                            println("Rest response = $response")
                            Toast.makeText(this, userValidator.messageValidation, Toast.LENGTH_SHORT).show()

                        },
                        Response.ErrorListener { error ->
                            println("error! $error")
                        }
                )
                VolleySingleton.getInstance(this).addToRequestQueue(objectRequest)
            } else {
                Toast.makeText(this, userValidator.messageValidation, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createJsonBuilder() : Gson {
        return Gson()
                .newBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter().nullSafe())
                .create()
    }


}