package pl.polsl.expensis_mobile.activities.expenses

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.expense_add_activity.*
import kotlinx.android.synthetic.main.register_activity.*
import org.json.JSONArray
import org.json.JSONObject
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.adapters.SpinnerAdapter
import pl.polsl.expensis_mobile.dto.ExpenseDTO
import pl.polsl.expensis_mobile.dto.ExpenseFormDTO
import pl.polsl.expensis_mobile.models.Category
import pl.polsl.expensis_mobile.others.DecimalDigitsInputFilter
import pl.polsl.expensis_mobile.others.LoadingAction
import pl.polsl.expensis_mobile.others.LoggedUser
import pl.polsl.expensis_mobile.rest.*
import pl.polsl.expensis_mobile.utils.Messages
import pl.polsl.expensis_mobile.utils.SharedPreferencesUtils
import pl.polsl.expensis_mobile.utils.TokenUtils
import pl.polsl.expensis_mobile.utils.Utils
import pl.polsl.expensis_mobile.validators.ExpenseValidator
import java.time.LocalDate
import java.util.*

class AddExpenseActivity : AppCompatActivity(), LoadingAction {

    private lateinit var expenseJsonObject: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expense_add_activity)
        showProgressBar()
        fetchCategoriesCallback()
        expenseAddDatePicker.text = LocalDate.now().toString()
        addExpenseValue.filters = arrayOf<InputFilter>(DecimalDigitsInputFilter(6, 2))
    }

    override fun showProgressBar() {
        Thread(Runnable {
            this.runOnUiThread {
                addExpenseProgressBar.visibility = View.VISIBLE
            }
        }).start()
    }

    override fun changeEditableFields(isEnabled: Boolean) {
        expenseTitleAdd.isEnabled = isEnabled
        expenseDescriptionAdd.isEnabled = isEnabled
        expenseAddDatePicker.isClickable = isEnabled
        addExpenseCategorySpinner.isEnabled = isEnabled
        addExpenseCategorySpinner.isClickable = isEnabled
        addExpenseValue.isEnabled = isEnabled
    }

    fun onGoBackClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        onBackPressed()
    }

    private fun fetchCategoriesCallback() {
        fetchCategories(getFetchCategoriesCallback())
    }

    private fun getFetchCategoriesCallback() : ServerCallback<JSONArray> {
        return object : ServerCallback<JSONArray> {
            override fun onSuccess(response: JSONArray) {
                val type = object : TypeToken<List<Category>>() {}.type
                val categories = Gson().fromJson<List<Category>>(response.toString(), type)
                fillCategorySpinner(categories)
                addExpenseProgressBar.visibility = View.INVISIBLE
            }

            override fun onFailure(error: VolleyError) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
                    refreshTokenCallback(false)
                } else {
                    val serverError = ServerErrorResponse(error)
                    val messageError = serverError.getErrorResponse()
                    registerProgressBar.visibility = View.INVISIBLE
                    changeEditableFields(false)
                    errorAction(messageError)
                }
            }
        }
    }

    private fun errorAction(messageError: String?) {
        val intent = Intent(applicationContext, ExpensesActivity::class.java)
        showToast(messageError)
        startActivity(intent)
    }

    private fun fillCategorySpinner(categories: List<Category>) {
        val items = categories.toMutableList()
        items.add(Category(0, applicationContext.getString(R.string.category_add)))
        val adapter = SpinnerAdapter(
            this,
            R.layout.spinner_category_layout, R.id.categorySpinnerTextView, items.toList()
        )
        adapter.setDropDownViewResource(R.layout.spinner_category_layout)
        addExpenseCategorySpinner.adapter = adapter
        addExpenseCategorySpinner.setSelection(adapter.count)
    }

    private fun fetchCategories(callback: ServerCallback<JSONArray>) {
        val url = BASE_URL + Endpoint.CATEGORIES
        VolleyService().requestArray(Request.Method.GET, url, null, callback, this)
    }

    fun onCalendarClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                run {
                    val stringDate = Utils.parseDateToString(year, monthOfYear, dayOfMonth)
                    expenseAddDatePicker.text = stringDate
                }
            }

        val dateNow = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            R.style.Theme_Expensismobile,
            dateSetListener,
            dateNow.get(Calendar.YEAR),
            dateNow.get(Calendar.MONTH),
            dateNow.get(Calendar.DAY_OF_MONTH)

        )
        dialog.datePicker.maxDate = dateNow.timeInMillis

        val loggedUserDateJoined = LoggedUser().serialize()!!.dateJoined
        val firstDayOfRegisteredMonth = Calendar.getInstance()
        firstDayOfRegisteredMonth.set(loggedUserDateJoined.year, loggedUserDateJoined.monthValue-1, 1)
        dialog.datePicker.minDate = firstDayOfRegisteredMonth.timeInMillis
        dialog.show()
    }

    fun onAddExpenseClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        val expenseFormDTO = ExpenseFormDTO(
            expenseTitleAdd,
            expenseDescriptionAdd,
            expenseAddDatePicker,
            addExpenseCategorySpinner,
            addExpenseValue
        )
        val expenseValidator = ExpenseValidator(expenseFormDTO)
        val validationResult = expenseValidator.validateAddExpenseAction()
        if (validationResult.isValid) {
            val expenseDTO = ExpenseDTO(expenseFormDTO)
            expenseJsonObject = JSONObject(Utils.getGsonWithLocalDate().toJson(expenseDTO))
            changeEditableFields(false)
            showProgressBar()
            postExpenseCallback()
        } else {
            Toast.makeText(this, validationResult.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun postExpenseCallback() {
        postExpense(getPostExpenseCallback())
    }

    private fun getPostExpenseCallback() : ServerCallback<JSONObject> {
        return object : ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                val intent = Intent(applicationContext, ExpensesActivity::class.java)
                startActivity(intent)
                showToast(Messages.SUCCESSFULLY_CREATED)
            }

            override fun onFailure(error: VolleyError) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 403) {
                    refreshTokenCallback(true)
                } else {
                    val serverResponse = ServerErrorResponse(error)
                    val messageError = serverResponse.getErrorResponse()
                    if (messageError != null)
                        showToast(messageError)
                    changeEditableFields(true)
                    registerProgressBar.visibility = View.INVISIBLE
                }

            }
        }
    }

    private fun postExpense(callback: ServerCallback<JSONObject>) {
        val url = BASE_URL + Endpoint.EXPENSES
        VolleyService().requestObject(Request.Method.POST, url, expenseJsonObject, callback, this)
    }

    private fun showToast(messageError: String?) {
        Toast.makeText(this, messageError, Toast.LENGTH_SHORT).show()
    }

    private fun refreshTokenCallback(isPostExpenseRequest: Boolean) {
        TokenUtils.refreshToken(object : ServerCallback<JSONObject> {
            override fun onSuccess(response: JSONObject) {
                SharedPreferencesUtils.storeTokens(
                    response.get(SharedPreferencesUtils.accessTokenConst) as String,
                    TokenUtils.refreshToken,
                    null
                )

                if (isPostExpenseRequest) {
                    postExpense(getPostExpenseCallback())
                }
                else {
                    fetchCategories(getFetchCategoriesCallback())
                }
            }

            override fun onFailure(error: VolleyError) {
                TokenUtils.refreshTokenOnFailure(error)
            }
        })
    }
}