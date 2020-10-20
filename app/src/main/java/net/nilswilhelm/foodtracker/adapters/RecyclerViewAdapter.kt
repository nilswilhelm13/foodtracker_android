package net.nilswilhelm.foodtracker.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Food

class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val content: TextView = view.findViewById(R.id.title)
}

class FoodRecyclerViewAdapter(private var foodList: List<Food>) :
    RecyclerView.Adapter<SimpleViewHolder>() {

    private val TAG = "foodRecyclerViewAdapt"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.browse, parent, false)
        return SimpleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (foodList.isNotEmpty()) foodList.size else 0
    }

    fun loadNewData(newData: List<Food>) {
        foodList = newData
        notifyDataSetChanged()
    }

    fun getFood(position: Int): Food? {
        return if (foodList.isNotEmpty()) foodList[position] else null
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        // Called by the layout manager when it wants new data in existing view
        val foodItem = foodList[position]
        holder.content.text = foodItem.name
    }
}