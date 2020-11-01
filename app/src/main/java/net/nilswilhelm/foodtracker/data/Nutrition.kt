package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Nutrition(
    var energy: Double,
    var carbohydrate: Double,
    var protein: Double,
    var fat: Double
) : Parcelable{

    override fun toString(): String {
        return "Nutrition(energy=${energy.toInt()}, carbohydrate=${carbohydrate.toInt()}, protein=${protein.toInt()}, fat=${fat.toInt()})"
    }
}