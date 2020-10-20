package net.nilswilhelm.foodtracker.data

import java.util.*

class Intake(var date: Date, var nutrition: Nutrition) {

    override fun toString(): String {
        return """
            date: $date
            nutrition: $nutrition
            """.trimIndent()
    }
}

