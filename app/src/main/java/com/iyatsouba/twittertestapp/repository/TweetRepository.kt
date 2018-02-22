package com.iyatsouba.twittertestapp.repository

import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import javax.inject.Inject

class TweetRepository @Inject constructor (val twitterHelper: TwitterHelper,
                                           val localTweetDao: LocalTweetDao) {



}