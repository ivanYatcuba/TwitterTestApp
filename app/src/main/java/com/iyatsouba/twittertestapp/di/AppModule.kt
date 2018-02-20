package com.iyatsouba.twittertestapp.di

import com.iyatsouba.twittertestapp.repository.UserRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider() = UserRepository()
}