package net.nilswilhelm.foodtracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Intake

class BarChartViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val bar: View = view.findViewById(R.id.bar_entry_bar)
    val day: TextView = view.findViewById(R.id.bar_entry_day)
}

class BarChartAdapter(private var list: List<Intake>) :
    RecyclerView.Adapter<BarChartViewHolder>() {

    private val TAG = "foodRecyclerViewAdapt"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction, parent, false)
        return BarChartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) list.size else 0
    }

    fun loadNewData(newData: List<Intake>) {
        list = newData
        notifyDataSetChanged()
    }

    fun getFood(position: Int): Intake? {
        return if (list.isNotEmpty()) list[position] else null
    }

    override fun onBindViewHolder(holder: BarChartViewHolder, position: Int) {
        // Called by the layout manager when it wants new data in existing view
        val intake = list[position]
        holder.bar.layoutParams.height = intake.nutrition.energy.toInt()
        holder.day.text = intake.date.toString()
    }
}