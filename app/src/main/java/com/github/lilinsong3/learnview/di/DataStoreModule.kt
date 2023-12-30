package com.github.lilinsong3.learnview.di

import android.content.Context
import com.github.lilinsong3.learnview.data.repository.animateTextDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideAnimateTextDataStore(@ApplicationContext context: Context) = context.animateTextDataStore
}