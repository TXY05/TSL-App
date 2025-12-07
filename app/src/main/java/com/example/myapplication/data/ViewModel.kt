package com.example.myapplication.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repo = PostRepository(db.postDao(), db.itemDao(), db.logisticsDao())

    val allPosts = repo.getAllPosts().asLiveData()

    val allPostsWithRelations = repo.allPostsWithRelations

    fun savePost(post: PostEntity, items: List<ItemEntity>, logistics: List<LogisticsEntity>) =
        viewModelScope.launch {
            repo.savePost(post, items, logistics)
        }

    fun getPostWithRelations(postId: Int): PostWithRelations? = runBlocking {
        db.postDao().getPostWithRelations(postId)
    }

    fun deletePostById(postId: Int) = viewModelScope.launch {
        val post = db.postDao().getPostById(postId)
        post?.let { repo.deletePost(it) }
    }


}
