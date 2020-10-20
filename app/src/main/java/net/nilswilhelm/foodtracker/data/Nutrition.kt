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
        return """
            energy: $energy
            carbohydrate: $carbohydrate
            protein: $protein
            fat: $fat
            """.trimIndent()
    }
}