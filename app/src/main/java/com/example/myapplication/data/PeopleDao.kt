package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PeopleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeople(peoples: PeopleEntity)

    @Query("DELETE FROM people WHERE postOwnerId = :postId")
    suspend fun deletePeoplesForPost(postId: Int)
}
