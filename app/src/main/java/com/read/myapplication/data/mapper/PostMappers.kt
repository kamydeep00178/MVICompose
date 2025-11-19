package com.read.myapplication.data.mapper

import com.read.myapplication.data.api.PostDto
import com.read.myapplication.data.db.PostEntity
import com.read.myapplication.domain.model.Post

fun PostDto.toDomain() = Post(id = id ?: 0, userId = userId, title = title, body = body)
fun PostEntity.toDomain() = Post(id = id, userId = userId, title = title, body = body)
fun Post.toEntity() = PostEntity(id = id, userId = userId, title = title, body = body)
fun PostDto.toEntity() = PostEntity(id = id ?: 0, userId = userId, title = title, body = body)