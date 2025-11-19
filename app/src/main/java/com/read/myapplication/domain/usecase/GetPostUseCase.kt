package com.read.myapplication.domain.usecase

import com.read.myapplication.domain.repository.PostRepository

class GetPostUseCase(private val repo: PostRepository) {
    suspend operator fun invoke(id: Int) = repo.getPost(id)
}