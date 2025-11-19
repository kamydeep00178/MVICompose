package com.read.myapplication.domain.usecase

import com.read.myapplication.domain.model.Post
import com.read.myapplication.domain.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetCachedThenRefreshUseCase(private val repo: PostRepository) {
    // returns cached posts if available, triggers background refresh and returns cached
    suspend operator fun invoke(): List<Post> {
        val cached = repo.getCachedPosts()
        if (cached.isNotEmpty()) {
            // trigger refresh in background (do not await)
            CoroutineScope(Dispatchers.IO).launch {
                try { repo.refreshPosts() } catch (e : Exception) {}
            }
            return cached
        }
        // no cache -> perform synchronous fetch and return fresh
        return repo.refreshPosts()
    }
}