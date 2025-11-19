package com.read.myapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id DESC")
    fun getAllFlow(): Flow<List<PostEntity>>
    @Query("SELECT * FROM posts ORDER BY id DESC")
    suspend fun getAll(): List<PostEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PostEntity>)
    @Query("DELETE FROM posts")
    suspend fun clearAll()
}