package net.nilswilhelm.foodtracker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.nilswilhelm.foodtracker.activities.CreateFood
import net.nilswilhelm.foodtracker.activities.EAN_TRANSFER

class FoodNotFoundDialog(private val ean: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Food does not exist. Do you want to create it?")
                .setPositiveButton("Create",
                    DialogInterface.OnClickListener { dialog, id ->
                        val intent = Intent(context, CreateFood::class.java)
                        intent.putExtra(EAN_TRANSFER, ean)
                        startActivity(intent)
                    })
                .setNegativeButton("Cancel"
                ) { dialog, id ->
                    dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}