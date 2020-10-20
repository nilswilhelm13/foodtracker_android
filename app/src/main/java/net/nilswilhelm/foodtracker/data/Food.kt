package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
class Food(
    var name: String,
    var nutrition: Nutrition,
    var isMeal: Boolean = false,
    var ean: String? = null,
    var id: String? = null,
    var userId: String? = null,
    var ingredients: Array<Ingredient>? = null
    ) : Parcelable