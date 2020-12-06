package pl.polsl.expensis_mobile.activities

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.SpinnerAdapter
import pl.polsl.expensis_mobile.utils.Utils.Companion.parseDateToString
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        fillSpinner(
            R.layout.spinner_gender_layout, R.id.genderSpinnerTextView,
            R.id.genderSpinner, R.array.gender_array, R.string.gender_hint
        )

        fillSpinner(
            R.layout.spinner_income_range_layout, R.id.incomeRangeSpinnerTextView,
            R.id.incomeRangeSpinner, R.array.income_range_array, R.string.income_range_hint
        )
        pickDateListener()
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

    private fun fillSpinner(
        layoutId: Int,
        textViewId: Int,
        spinnerId: Int,
        arrayId: Int,
        hintId: Int
    ) {
        val spinner: Spinner = findViewById(spinnerId)
        val items: MutableList<String> = this.resources.getStringArray(arrayId).toMutableList()
        items.add(this.resources.getString(hintId)) // add hint

        val adapter = SpinnerAdapter(
            this,
            layoutId, textViewId, items.toList()
        )
        adapter.setDropDownViewResource(layoutId)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)
    }

}