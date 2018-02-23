package com.iyatsouba.twittertestapp.repository

import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import javax.inject.Inject

class TweetRepository @Inject constructor (private val twitterHelper: TwitterHelper,
                                           private val localTweetDao: LocalTweetDao) {


    fun getTweetTimeline(): LocalTweetTimeline {
        return LocalTweetTimeline.Builder().userId(twitterHelper.getUserId())
                .maxItemsPerRequest(12)
                .localTweetDao(localTweetDao)
                .build()
    }

}