package com.iyatsouba.twittertestapp.repository

import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import com.iyatsouba.twittertestapp.rx.SchedulersFacade
import com.iyatsouba.twittertestapp.twitter.LocalTweetTimeline
import com.iyatsouba.twittertestapp.twitter.TwitterHelper
import com.jakewharton.rxrelay2.BehaviorRelay
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.Maybe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TweetRepository @Inject constructor (private val twitterHelper: TwitterHelper,
                                           private val localTweetDao: LocalTweetDao,
                                           private val schedulersFacade: SchedulersFacade) {


    fun getTweetTimeline(): LocalTweetTimeline {
        return LocalTweetTimeline.Builder().userId(twitterHelper.getUserId())
                .maxItemsPerRequest(5)
                .tweetRepository(this)
                .schedulerFacade(schedulersFacade)
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

    fun publishTweet(text: String, mediaIds: String, tweetPublishRelay: BehaviorRelay<DataLoadingState>) {
        tweetPublishRelay.accept(DataLoadingState.IN_PROGRESS)
        twitterHelper.getApiClient()?.statusesService?.update(text, null,
                null, null, null,
                null,
                null,
                null, mediaIds)?.enqueue(object: Callback<Tweet> {

            override fun onResponse(call: Call<Tweet>?, response: Response<Tweet>?) {
                if(response?.code() == 200) {
                    tweetPublishRelay.accept(DataLoadingState.SUCCESS)
                } else {
                    tweetPublishRelay.accept(DataLoadingState.ERROR)
                }

            }

            override fun onFailure(call: Call<Tweet>?, t: Throwable?) {
                tweetPublishRelay.accept(DataLoadingState.ERROR)
            }

        })
    }


}