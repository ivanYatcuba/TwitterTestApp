package com.iyatsouba.twittertestapp.ui.timeline

import com.iyatsouba.twittertestapp.repository.TweetRepository
import dagger.Module
import dagger.Provides

@Module
class TimeLineModule {

    @Provides
    fun provideViewModel(twitterRepository: TweetRepository) = TimeLineViewModel(twitterRepository)

}