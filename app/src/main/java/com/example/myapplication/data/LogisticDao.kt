package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LogisticsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogistics(logistics: LogisticsEntity)

    @Query("DELETE FROM logistics WHERE postOwnerId = :postId")
    suspend fun deleteLogisticsForPost(postId: Int)
}
