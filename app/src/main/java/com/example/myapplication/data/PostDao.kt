package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    // --- 🔹 Basic Post CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)


    // --- 🔹 Basic Queries ---
    @Query("SELECT * FROM posts ORDER BY date DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE postId = :id")
    suspend fun getPostById(id: Int): PostEntity?


    // --- 🔹 Relations ---
    @Transaction
    @Query("SELECT * FROM posts WHERE postId = :id")
    suspend fun getPostWithRelations(id: Int): PostWithRelations?

    // ✅ NEW: Get all posts with items + logistics (for Search screen)
    @Transaction
    @Query("SELECT * FROM posts ORDER BY date DESC")
    fun getAllPostsWithRelations(): LiveData<List<PostWithRelations>>
}

