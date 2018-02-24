package com.iyatsouba.twittertestapp.ui.timeline

import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.twitter.TwitterHelper

class TimeLineViewModel(private val twitterRepository: TweetRepository, private val twitterHelper: TwitterHelper) {

    fun getTweetTimeline(): LocalTweetTimeline {
        return twitterRepository.getTweetTimeline()
    }

    fun getUsername(): String {
        return twitterHelper.getUserName()
    }
}