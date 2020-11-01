package net.nilswilhelm.foodtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DashboardData(
    var intake: Intake,
    var dailyGoals: DailyGoals,
    var transactions: Array<Transaction>
) : Parcelable