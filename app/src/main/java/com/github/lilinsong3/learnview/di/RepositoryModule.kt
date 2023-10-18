package com.github.lilinsong3.learnview.di

import com.github.lilinsong3.learnview.data.repository.DazzlingBoardRepository
import com.github.lilinsong3.learnview.data.repository.DefaultDazzlingBoardRepository
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
    abstract fun bindDazzlingBoardRepository(defaultDazzlingBoardRepository: DefaultDazzlingBoardRepository): DazzlingBoardRepository
}