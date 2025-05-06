package com.android.edugrade.di

import com.android.edugrade.data.user.UserRepository
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import dagger.Module
import dagger.Lazy
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference = Firebase.database.reference

    @Provides
    @Singleton
    fun provideUserRepository(
        subjectStorage: Lazy<SubjectStorage>,
        auth: FirebaseAuth,
        database: DatabaseReference
    ): UserRepository {
        return UserRepository(subjectStorage, auth, database)
    }

    @Provides
    @Singleton
    fun provideScoreStorage(
        userRepository: UserRepository,
        subjectStorage: Lazy<SubjectStorage>,
        auth: FirebaseAuth,
        database: DatabaseReference
    ): ScoreStorage {
        return ScoreStorage(userRepository, subjectStorage, auth, database)
    }

    @Provides
    @Singleton
    fun provideSubjectStorage(
        scoreStorage: ScoreStorage,
        userRepository: UserRepository,
        auth: FirebaseAuth,
        database: DatabaseReference
    ): SubjectStorage {
        return SubjectStorage(scoreStorage, userRepository, auth, database)
    }
}