package com.read.myapplication.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class PostDto(val userId: Int?, val id: Int?, val title: String, val body: String)
data class CreatePostRequest(val title: String, val body: String, val userId: Int = 1)
interface JsonPlaceHolderApi {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): PostDto
    @POST("posts")
    suspend fun createPost(@Body req: CreatePostRequest): PostDto
}