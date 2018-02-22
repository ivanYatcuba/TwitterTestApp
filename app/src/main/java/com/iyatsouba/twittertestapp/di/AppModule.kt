package com.iyatsouba.twittertestapp.di

import com.iyatsouba.twittertestapp.repository.UserRepository
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider() = UserRepository()

    @Provides
    @Singleton
    fun provideTwitterHelper() = TwitterHelper()
}