package com.example.myapplication.data

import androidx.room.PrimaryKey
import java.time.LocalDate

data class PeopleData(
    val description: String = "",
    val amount : String = "",
    val weight: String = "",
    val peopleAmount : String = "",
    val arrivePay : String = "",
    val departDate: String = "",
    val arriveDate: String = "",
    val departPeople: String = "",
    val arrivePeople: String = ""
)
