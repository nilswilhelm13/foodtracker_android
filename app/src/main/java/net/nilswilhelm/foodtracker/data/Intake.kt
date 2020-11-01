package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
@Parcelize
class Intake(var date: Date, var nutrition: Nutrition) : Parcelable {
    override fun toString(): String {
        return "Intake(date=$date, nutrition=${nutrition})"
    }
}

