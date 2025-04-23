package com.android.edugrade.di

import com.android.edugrade.data.auth.UserRepository
import com.android.edugrade.data.score.ScoreStorage
import com.android.edugrade.data.subject.SubjectStorage
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSubjectStorage(
        scoreStorage: ScoreStorage,
        userRepository: UserRepository
    ): SubjectStorage = SubjectStorage(scoreStorage, userRepository)

    @Provides
    @Singleton
    fun provideScoreStorage(): ScoreStorage = ScoreStorage()

    @Provides
    @Singleton
    fun provideUserRepository(
        subjectStorage: Lazy<SubjectStorage>
    ): UserRepository = UserRepository(subjectStorage)
}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NotificationsModule {
//    @Singleton
//    @Provides
//    fun provideNotificationBuilder(
//        @ApplicationContext context: Context
//    ): NotificationCompat.Builder{
//        return NotificationCompat.Builder(context, "EduGrade")
//            .setContentTitle("Reminders")
//            .setContentText("Class")
//            .setSmallIcon(R.drawable.bell)
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//    }
//
//    @Singleton
//    @Provides
//    fun provideNotificationManager(
//        @ApplicationContext context: Context
//    ): NotificationManagerCompat {
//        val notificationManager = NotificationManagerCompat.from(context)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val channel = NotificationChannel(
//                "Main Channel id",
//                "Main Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        return notificationManager
//    }
//}