package net.nilswilhelm.foodtracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Intake

interface BarChartClickListener {
    fun onItemClick(intake: Intake)
}

class BarChartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val bar: View = view.findViewById(R.id.bar_entry_bar)
    val day: TextView = view.findViewById(R.id.bar_entry_day)
    val carbohydrate: View = view.findViewById(R.id.bar_entry_carbohydrate)
    val protein: View = view.findViewById(R.id.bar_entry_protein)
    val fat: View = view.findViewById(R.id.bar_entry_fat)
    val energyValue: TextView = view.findViewById(R.id.energy_value)
}

class BarChartAdapter(private var list: List<Intake>, private val listener: BarChartClickListener, private val height: Int) :
    RecyclerView.Adapter<BarChartViewHolder>() {

    private val TAG = "foodRecyclerViewAdapt"
    private var currentMax = 0
    private var view: View? = null
    private val clippingValue = 4000

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        view = LayoutInflater.from(parent.context).inflate(R.layout.bar_entry, parent, false)
        return BarChartViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) list.size else 0
    }

    fun loadNewData(newData: List<Intake>) {
        var max = 0.0
        for (intake in newData){

            val energy = intake.nutrition.energy
            if (energy > max && energy <= clippingValue){
                max = intake.nutrition.energy
            }
        }
        currentMax = max.toInt()
        list = newData
        notifyDataSetChanged()
    }

    fun getFood(position: Int): Intake? {
        return if (list.isNotEmpty()) list[position] else null
    }

    override fun onBindViewHolder(holder: BarChartViewHolder, position: Int) {
        // Called by the layout manager when it wants new data in existing view
        val energyHeightConst = height.toDouble() * 0.5
        Log.d(TAG, "Height: $energyHeightConst")
        val macroHeightConst = height.toDouble() * 2
        val intake = list[position]
        holder.bar.layoutParams.height = (intake.nutrition.energy / currentMax * energyHeightConst).coerceAtLeast(1.0).toInt()
        holder.carbohydrate.layoutParams.height =
            (intake.nutrition.carbohydrate  / currentMax * macroHeightConst).coerceAtLeast(1.0).toInt()
        holder.protein.layoutParams.height = (intake.nutrition.protein  / currentMax * macroHeightConst).coerceAtLeast(1.0).toInt()
        holder.fat.layoutParams.height = (intake.nutrition.fat  / currentMax * macroHeightConst).coerceAtLeast(1.0).toInt()
//        val formatter = android.text.format.DateFormat.format("EEE, d MMM", intake.date)
        val formatter = android.text.format.DateFormat.format("EEE", intake.date)
        holder.day.text = formatter
        holder.energyValue.setText(intake.nutrition.energy.toInt().toString())
        holder.itemView.setOnClickListener {
            listener.onItemClick(intake)
        }
    }
}