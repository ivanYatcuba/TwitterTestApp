package com.iyatsouba.twittertestapp.ui.timeline

import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline

class TimeLineViewModel(private val twitterRepository: TweetRepository) {

    fun getTweetTimeline(): LocalTweetTimeline {
        return twitterRepository.getTweetTimeline()
    }
}