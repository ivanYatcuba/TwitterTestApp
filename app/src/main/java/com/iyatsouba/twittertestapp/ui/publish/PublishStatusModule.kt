package com.iyatsouba.twittertestapp.ui.publish

import com.iyatsouba.twittertestapp.repository.TweetRepository
import dagger.Module
import dagger.Provides

@Module
class PublishStatusModule {

    @Provides
    fun provideViewModel(twitterRepository: TweetRepository) = PublishStatusViewModel(twitterRepository)

}