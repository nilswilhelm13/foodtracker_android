package net.nilswilhelm.foodtracker.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.data.Ingredient
import net.nilswilhelm.foodtracker.viewholders.IngredientViewHolder

interface OnEditTextChanged {
    fun onTextChanged(position: Int, charSeq: String?)
}

interface OnDeleteButtonPressed {
    fun onDeleteButtonPressed(position: Int)
}

class IngredientAdapter(private var list: List<Ingredient>, private var onEditTextChanged: OnEditTextChanged, private var onDeleteButtonPressed: OnDeleteButtonPressed) : RecyclerView.Adapter<IngredientViewHolder>() {
    private val TAG = "IngredientAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        // Called by the layout manager when it needs a new view
        Log.d(TAG, ".onCreateViewHolder new view request")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient, parent, false)
        return IngredientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (list.isNotEmpty()) list.size else 0
    }

    fun getData(): List<Ingredient>{
        return list
    }

    fun loadNewData(newData: List<Ingredient>) {
        list = newData
        notifyDataSetChanged()
    }

    fun getIngredient(position: Int): Ingredient? {
        return if (list.isNotEmpty()) list[position] else null
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        // Called by the layout manager when it wants new data in existing view
        val ingredient = list[position]

        holder.button.setOnClickListener {
            onDeleteButtonPressed.onDeleteButtonPressed(position)
        }
        holder.amount.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.isNotEmpty()){
                    onEditTextChanged.onTextChanged(position, s.toString())
                }

            }

        })
        holder.name.text = ingredient.food.name
        holder.amount.setText(ingredient.amount.toString())
    }


}