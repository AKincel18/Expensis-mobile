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

class ExpensesAdapter(private val onClick: (Expense) -> Unit) :
    ListAdapter<Expense, ExpensesAdapter.ExpenseViewHolder>(ExpenseDiffCallback) {

    class ExpenseViewHolder(itemView: View, val onClick: (Expense) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private var expenseTitle: TextView = itemView.findViewById(R.id.expenseTitle)
        private var expenseDescription: TextView = itemView.findViewById(R.id.expenseDescription)
        private var expenseValue: TextView = itemView.findViewById(R.id.expenseValue)
        private var expenseDate: TextView = itemView.findViewById(R.id.expenseDate)
        private var currentExpense: Expense? = null
        private var expenseCategory: TextView = itemView.findViewById(R.id.expenseCategory)

        init {
            itemView.setOnClickListener {
                currentExpense?.let {
                    onClick(it)
                }
            }
        }

        fun bind(expense: Expense) {
            currentExpense = expense
            expenseTitle.text = expense.title
            expenseDescription.text = if (expense.description != null) expense.description else ""
            val expenseValueString = "-" + expense.value.toString()
            expenseValue.text = expenseValueString
            expenseDate.text = expense.date.toString()
            expenseCategory.text = expense.category
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item, parent, false)
        return ExpenseViewHolder(view, onClick)
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