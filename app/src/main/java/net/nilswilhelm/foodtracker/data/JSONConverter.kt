package net.nilswilhelm.foodtracker.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class JSONConverter {
    companion object {
        fun convertDailyGoals(json: String) : DailyGoals {
            val itemType = object : TypeToken<DailyGoals>() {}.type
            return Gson().fromJson(json, itemType)
        }
        fun convertIntake(json: String) : Intake {
            val itemType = object : TypeToken<Intake>() {}.type
            return Gson().fromJson(json, itemType)
        }

    }

}