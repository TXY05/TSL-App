package com.example.myapplication.data

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithRelations(
    @Embedded val post: PostEntity,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postOwnerId"
    )
    val items: List<ItemEntity>,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postOwnerId"
    )
    val logistics: List<LogisticsEntity>,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postOwnerId"
    )
    val peoples: List<PeopleEntity>
)
