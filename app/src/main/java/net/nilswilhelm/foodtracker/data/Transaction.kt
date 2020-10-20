package net.nilswilhelm.foodtracker.data

import java.util.*

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
)