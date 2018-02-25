package com.iyatsouba.twittertestapp.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.iyatsouba.twittertestapp.db.TwitterTestAppDatabse
import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.rx.SchedulersFacade
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesAppDatabase(context: Context): TwitterTestAppDatabse =
            Room.databaseBuilder(context, TwitterTestAppDatabse::class.java, "twitterdb")
                    .build()

    @Provides
    @Singleton
    fun providesLocalTweetDao(database: TwitterTestAppDatabse) = database.localTweetDao()

    @Provides
    @Singleton
    fun providesSchedulersFacade() = SchedulersFacade()

    @Provides
    @Singleton
    fun providesTweetRepository(twitterHelper: TwitterHelper, localTweetDao: LocalTweetDao, schedulersFacade: SchedulersFacade)
            = TweetRepository(twitterHelper, localTweetDao, schedulersFacade)

    @Provides
    @Singleton
    fun provideTwitterHelper() = TwitterHelper()
}