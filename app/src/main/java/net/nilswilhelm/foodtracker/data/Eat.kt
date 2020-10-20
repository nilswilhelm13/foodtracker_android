package net.nilswilhelm.foodtracker.data

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Eat(var foodId: String, var amount: Double, var date: Date) {
    override fun toString(): String {
        return "Eat(foodId='$foodId', amount=$amount, date=$date)"
    }
}