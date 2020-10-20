package net.nilswilhelm.foodtracker.viewholders

import android.view.View
import android.widget.EditText
import android.widget.TextView
import net.nilswilhelm.foodtracker.R

class IngredientViewHolder(v: View) {
    val name: TextView = v.findViewById(R.id.ingredient_name)
    val amount: EditText = v.findViewById(R.id.ingredient_amount)
}