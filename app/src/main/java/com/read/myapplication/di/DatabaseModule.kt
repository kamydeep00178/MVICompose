package com.read.myapplication.di

import android.content.Context
import androidx.room.Room
import com.read.myapplication.data.db.AppDatabase
import com.read.myapplication.data.db.PostDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase {
        return Room.databaseBuilder(ctx, AppDatabase::class.java, "app-db").build()
    }

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()
}