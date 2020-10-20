package net.nilswilhelm.foodtracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Transaction

class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val foodname: TextView = view.findViewById(R.id.transaction_foodname)

}

class TransactionRecyclerViewAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionViewHolder>() {

    private val TAG = "foodRecyclerViewAdapt"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (transactions.isNotEmpty()) transactions.size else 0
    }

    fun loadNewData(newData: List<Transaction>) {
        transactions = newData
        notifyDataSetChanged()
    }

    fun getFood(position: Int): Transaction? {
        return if (transactions.isNotEmpty()) transactions[position] else null
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Called by the layout manager when it wants new data in existing view
        val transaction = transactions[position]
        holder.foodname.text = transaction.foodName + "(${transaction.amount}g)${transaction.nutrition.energy.toString()}kcal"
    }
}