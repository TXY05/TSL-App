package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.time.LocalDate

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val postId: Int = 0,
    val date: LocalDate,
    val title: String,
    val description: String,
    val customerShipment: String,
    val factoryShipment: String,
    val serviceCharge: Double,
    val r2mRate: String,
    val r2mPerson: String,
    val r2mDate: LocalDate?,
    val m2rRate: String,
    val m2rPerson: String,
    val m2rDate: LocalDate?,
    val totalProfit: Double
)

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["postId"],
            childColumns = ["postOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("postOwnerId")]
)
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    val postOwnerId: Int,
    val title: String,
    val amount: String,
    val customerPrice: String,
    val customerAmount: String,
    val factoryPrice: String,
    val factoryAmount: String
)

@Entity(
    tableName = "logistics",
    foreignKeys = [
        ForeignKey(
            entity = PostEntity::class,
            parentColumns = ["postId"],
            childColumns = ["postOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("postOwnerId")]
)
data class LogisticsEntity(
    @PrimaryKey(autoGenerate = true)
    val logisticsId: Int = 0,
    val postOwnerId: Int,
    val description: String,
    val person: String,
    val weight: String,
    val amount: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?
)



class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it) }
}

