package net.nilswilhelm.foodtracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.nilswilhelm.foodtracker.viewholders.SimpleViewHolder
import net.nilswilhelm.foodtracker.data.Transaction
import kotlin.collections.List




class EatenTodayListAdapter(context: Context, private val resource: Int, private val list: List<Transaction>) : ArrayAdapter<Transaction>(context, resource) {
    private val TAG = "IntakeAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(TAG, "getCount() called")
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val simpleViewHolder: SimpleViewHolder
        if (convertView == null){
            Log.d(TAG, "getView called with null convertView")
            view = inflater.inflate(resource, parent, false)
            simpleViewHolder =
                SimpleViewHolder(view)
            view.tag = simpleViewHolder
        } else{
            Log.d(TAG, "getView provided a convertView")
            view = convertView
            simpleViewHolder = view.tag as SimpleViewHolder
        }

        val currentEntry = list[position]
        simpleViewHolder.content.text =
            """
            Date: ${currentEntry.date}
            Food: ${currentEntry.foodName}
            Amount: ${currentEntry.amount}
            Energy: ${String.format("%.2f",currentEntry.nutrition.energy)}
            Protein: ${String.format("%.2f",currentEntry.nutrition.protein)}
            Fat: ${String.format("%.2f",currentEntry.nutrition.fat)}
            Carbs: ${String.format("%.2f",currentEntry.nutrition.carbohydrate)}
            
            """


        return view

    }


}