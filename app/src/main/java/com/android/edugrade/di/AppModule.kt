package com.android.edugrade.di

import android.content.Context
import com.android.edugrade.data.auth.AuthRepository
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSubjectStorage(): SubjectStorage = SubjectStorage(provideScoreStorage())

    @Provides
    @Singleton
    fun provideScoreStorage(): ScoreStorage = ScoreStorage()

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = AuthRepository()
}