package net.nilswilhelm.foodtracker.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import net.nilswilhelm.foodtracker.viewholders.IngredientViewHolder
import net.nilswilhelm.foodtracker.data.Ingredient

class IngredientAdapter(context: Context, private val resource: Int, private val list: List<Ingredient>) : ArrayAdapter<Ingredient>(context, resource) {
    private val TAG = "IngredientAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(TAG, "getCount() called")
        return list.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val ingredientViewHolder: IngredientViewHolder
        if (convertView == null){
            Log.d(TAG, "getView called with null convertView")
            view = inflater.inflate(resource, parent, false)
            ingredientViewHolder =
                IngredientViewHolder(view)
            view.tag = ingredientViewHolder
        } else{
            Log.d(TAG, "getView provided a convertView")
            view = convertView
            ingredientViewHolder = view.tag as IngredientViewHolder
        }

        val currentEntry = list[position]
        ingredientViewHolder.name.text = currentEntry.food.name
        ingredientViewHolder.amount.setText(String.format("%.2f",currentEntry.amount))

        return view

    }


}