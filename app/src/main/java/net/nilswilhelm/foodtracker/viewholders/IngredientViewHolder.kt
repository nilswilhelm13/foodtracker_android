package net.nilswilhelm.foodtracker.viewholders

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.nilswilhelm.foodtracker.R

class IngredientViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val name: TextView = v.findViewById(R.id.ingredient_name)
    val amount: EditText = v.findViewById(R.id.ingredient_amount)
    val button: ImageButton = v.findViewById(R.id.ingredient_button)
}