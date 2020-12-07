package net.nilswilhelm.foodtracker.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import net.nilswilhelm.foodtracker.utils.Utils

class DeleteTransactionDialog(
    private val listener: Utils.Companion.OnDeleteListener,
    private val transactionId: String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Delete this Entry?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        Utils.delete(
                            requireContext(),
                            "https://backend.nilswilhelm.net/intake/$transactionId",
                            listener
                        )
                    })
                .setNegativeButton(
                    "Cancel"
                ) { dialog, id ->
                    dismiss()
                }
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}