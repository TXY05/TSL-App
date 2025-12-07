package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Query("DELETE FROM items WHERE postOwnerId = :postId")
    suspend fun deleteItemsForPost(postId: Int)
}
