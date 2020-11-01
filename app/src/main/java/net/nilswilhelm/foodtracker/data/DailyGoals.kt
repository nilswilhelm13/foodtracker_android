package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DailyGoals(
    var userId: String,
    var nutrition: Nutrition,
    var water: Double

) : Parcelable {
    override fun toString(): String {
        return "Pimmel"
    }
}

