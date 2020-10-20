package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Ingredient(
    var food: Food,
    var amount: Double
) : Parcelable