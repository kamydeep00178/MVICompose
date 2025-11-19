package com.read.myapplication.domain.repository

import com.read.myapplication.domain.model.Post

interface PostRepository {
    suspend fun getCachedPosts(): List<Post>
    suspend fun refreshPosts(): List<Post>
    suspend fun getPost(id: Int): Post
    suspend fun createPostAndReturnId(title: String, body: String): Int
}