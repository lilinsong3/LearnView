package com.github.lilinsong3.learnview.di

import com.github.lilinsong3.learnview.data.repository.AnimateTextRepository
import com.github.lilinsong3.learnview.data.repository.DefaultAnimateTextRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAnimateTextRepository(defaultAnimateTextRepository: DefaultAnimateTextRepository): AnimateTextRepository
}