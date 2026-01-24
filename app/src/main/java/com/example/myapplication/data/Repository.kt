package com.example.myapplication.data

import androidx.lifecycle.LiveData

class PostRepository(
    private val postDao: PostDao,
    private val itemDao: ItemDao,
    private val logisticsDao: LogisticsDao,
    private val peopleDao: PeopleDao

) {
    fun getAllPosts() = postDao.getAllPosts()
    val allPostsWithRelations: LiveData<List<PostWithRelations>> = postDao.getAllPostsWithRelations()

    suspend fun savePost(
        post: PostEntity,
        items: List<ItemEntity>,
        logistics: List<LogisticsEntity>,
        peoples: List<PeopleEntity>,
    ) {
        val postId = postDao.insertPost(post).toInt()

        items.forEach {
            itemDao.insertItem(it.copy(postOwnerId = postId))
        }
        logistics.forEach {
            logisticsDao.insertLogistics(it.copy(postOwnerId = postId))
        }

        peoples.forEach {
            peopleDao.insertPeople(it.copy(postOwnerId = postId))
        }

    }

    suspend fun deletePost(post: PostEntity) {
        postDao.deletePost(post)
        itemDao.deleteItemsForPost(post.postId)
        logisticsDao.deleteLogisticsForPost(post.postId)
        peopleDao.deletePeoplesForPost(post.postId)
    }
}
