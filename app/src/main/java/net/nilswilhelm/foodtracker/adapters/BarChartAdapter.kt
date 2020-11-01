package net.nilswilhelm.foodtracker.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Intake
import java.time.format.DateTimeFormatter

interface BarChartClickListener {
    fun onItemClick(intake: Intake)
}

class BarChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val bar: View = view.findViewById(R.id.bar_entry_bar)
    val day: TextView = view.findViewById(R.id.bar_entry_day)
    val carbohydrate: View = view.findViewById(R.id.bar_entry_carbohydrate)
    val protein: View = view.findViewById(R.id.bar_entry_protein)
    val fat: View = view.findViewById(R.id.bar_entry_fat)
}

class BarChartAdapter(private var list: List<Intake>, private val listener: BarChartClickListener) :
    RecyclerView.Adapter<BarChartViewHolder>() {

    private val TAG = "foodRecyclerViewAdapt"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.bar_entry, parent, false)
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
        holder.bar.layoutParams.height = (intake.nutrition.energy.toInt() / 2).coerceAtLeast(1)
        holder.carbohydrate.layoutParams.height =
            (intake.nutrition.carbohydrate.toInt() * 2).coerceAtLeast(1)
        holder.protein.layoutParams.height = (intake.nutrition.protein.toInt() * 2).coerceAtLeast(1)
        holder.fat.layoutParams.height = (intake.nutrition.fat.toInt() * 2).coerceAtLeast(1)
//        val formatter = android.text.format.DateFormat.format("EEE, d MMM", intake.date)
        val formatter = android.text.format.DateFormat.format("EEE", intake.date)
        holder.day.text = formatter
        holder.itemView.setOnClickListener {
            listener.onItemClick(intake)
        }
    }
}