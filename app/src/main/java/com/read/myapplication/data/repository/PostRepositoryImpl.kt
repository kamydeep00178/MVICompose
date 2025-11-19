package com.read.myapplication.data.repository

import com.read.myapplication.data.api.CreatePostRequest
import com.read.myapplication.data.api.JsonPlaceHolderApi
import com.read.myapplication.data.db.PostDao
import com.read.myapplication.data.mapper.toDomain
import com.read.myapplication.data.mapper.toEntity
import com.read.myapplication.di.IoDispatcher
import com.read.myapplication.domain.repository.PostRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.read.myapplication.domain.model.Post

class PostRepositoryImpl @Inject constructor(
    private val api: JsonPlaceHolderApi,
    private val dao: PostDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : PostRepository {

    override suspend fun getCachedPosts(): List<Post> =
        withContext(ioDispatcher) { dao.getAll().map { it.toDomain() } }

    override suspend fun refreshPosts(): List<Post> = withContext(ioDispatcher) {
        val dtos = api.getPosts()
        val entities = dtos.map { it.toEntity() }
        dao.insertAll(entities)
        entities.map { it.toDomain() }
    }

    override suspend fun getPost(id: Int): Post = withContext(ioDispatcher) {
        val cached = dao.getAll().firstOrNull { it.id == id }
        return@withContext cached?.toDomain() ?: api.getPost(id).toDomain().also {
            dao.insertAll(listOf(it.toEntity()))
        }
    }

    override suspend fun createPostAndReturnId(title: String, body: String): Int =
        withContext(ioDispatcher) {
            val created = api.createPost(CreatePostRequest(title = title, body = body))
            val id = created.id ?: -1
            if (id > 0) dao.insertAll(listOf(created.toEntity()))
            id
        }

}