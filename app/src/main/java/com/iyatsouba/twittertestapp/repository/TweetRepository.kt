package com.iyatsouba.twittertestapp.repository

import android.util.Log
import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.Maybe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TweetRepository @Inject constructor (private val twitterHelper: TwitterHelper,
                                           private val localTweetDao: LocalTweetDao) {


    fun getTweetTimeline(): LocalTweetTimeline {
        return LocalTweetTimeline.Builder().userId(twitterHelper.getUserId())
                .maxItemsPerRequest(12)
                .tweetRepository(this)
                .build()
    }

    fun insertTweet(localTweet: LocalTweet) {
        localTweetDao.insertTweet(localTweet)
    }

    fun getPreviousTweets(maxId: Long, limit: Int): Maybe<List<LocalTweet>> {
        return localTweetDao.getPreviousTweets(twitterHelper.getUserId(), maxId, limit)
    }

    fun getNextTweets(sinceId: Long, limit: Int): Maybe<List<LocalTweet>> {
        return localTweetDao.getNextTweets(twitterHelper.getUserId(), sinceId, limit)
    }

    fun createUserTimelineRequest(maxItemsPerRequest: Int, sinceId: Long?, maxId: Long?): Call<MutableList<Tweet>>? {
        return twitterHelper.getApiClient()?.statusesService?.userTimeline(twitterHelper.getUserId(),
                null, maxItemsPerRequest, sinceId, maxId, false, true,
                null, false)
    }

    fun publishTweet(text: String, mediaIds: String) {
        twitterHelper.getApiClient()?.statusesService?.update(text, null,
                null, null, null,
                null,
                null,
                null, mediaIds)?.enqueue(object: Callback<Tweet> {

            override fun onFailure(call: Call<Tweet>?, t: Throwable?) {
                Log.e("PUBLISH", "OK")
            }

            override fun onResponse(call: Call<Tweet>?, response: Response<Tweet>?) {
                Log.e("PUBLISH", "FAIL")
            }

        })
    }


}