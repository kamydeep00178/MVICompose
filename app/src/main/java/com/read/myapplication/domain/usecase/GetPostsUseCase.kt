package com.read.myapplication.domain.usecase

import com.read.myapplication.domain.model.Post
import com.read.myapplication.domain.repository.PostRepository

class GetPostsUseCase(private val repo: PostRepository) {
    suspend operator fun invoke(): List<Post> = repo.refreshPosts()
}