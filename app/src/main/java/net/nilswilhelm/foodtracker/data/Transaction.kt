package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Transaction(
    var id: String = "",
    var foodId: String,
    var food: Food,
    var foodName:  String = "",
    var amount:    Double,
    var nutrition: Nutrition,
    var isMeal:    Boolean = false,
    var date:     Date,
    var userId:    String = ""
): Parcelable