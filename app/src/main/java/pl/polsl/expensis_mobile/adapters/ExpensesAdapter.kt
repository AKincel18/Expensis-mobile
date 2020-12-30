package pl.polsl.expensis_mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.polsl.expensis_mobile.R
import pl.polsl.expensis_mobile.models.Expense
import java.time.format.DateTimeFormatter

class ExpensesAdapter :
    ListAdapter<Expense, ExpensesAdapter.ExpenseViewHolder>(ExpenseDiffCallback) {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var expenseTitle: TextView = itemView.findViewById(R.id.expense_title)
        private var expenseDescription: TextView = itemView.findViewById(R.id.expense_description)
        private var expenseValue: TextView = itemView.findViewById(R.id.expense_value)
        private var expenseDate: TextView = itemView.findViewById(R.id.expense_date)
        private var currentExpense: Expense? = null

        fun bind(expense: Expense) {
            currentExpense = expense
            expenseTitle.text = expense.title
            expenseDescription.text = if (expense.description != null) expense.description else ""
            expenseValue.text = "-" + expense.value.toString()
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss")
            expenseDate.text = expense.date.format(formatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        return ExpenseViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense)
    }
}

object ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }
}