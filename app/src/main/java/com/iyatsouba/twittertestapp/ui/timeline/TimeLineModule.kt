package com.iyatsouba.twittertestapp.ui.timeline

import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import dagger.Module
import dagger.Provides

@Module
class TimeLineModule {

    @Provides
    fun provideViewModel(twitterRepository: TweetRepository, twitterHelper: TwitterHelper)
            = TimeLineViewModel(twitterRepository, twitterHelper)

}