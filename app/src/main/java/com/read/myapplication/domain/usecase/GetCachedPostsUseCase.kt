package com.read.myapplication.domain.usecase

import com.read.myapplication.domain.model.Post
import com.read.myapplication.domain.repository.PostRepository

class GetCachedPostsUseCase(private val repo: PostRepository) {
    suspend operator fun invoke(): List<Post> = repo.getCachedPosts()
}