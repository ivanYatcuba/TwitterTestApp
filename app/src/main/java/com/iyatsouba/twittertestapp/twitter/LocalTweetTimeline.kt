package com.iyatsouba.twittertestapp.twitter

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Response

class LocalTweetTimeline(private val twitterCore: TwitterCore,
                         private val userId: Long,
                         private val screenName: String?,
                         private val maxItemsPerRequest: Int,
                         private val includeReplies: Boolean?,
                         private val includeRetweets: Boolean?,
                         private val localTweetDao: LocalTweetDao) : Timeline<Tweet> {


    override fun next(sinceId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        convertLocalTweetsToNetworkTweets(localTweetDao.getNextTweets(userId, sinceId
                ?: -1, maxItemsPerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()))
                .subscribe({ localTweets ->
                    Log.e("TIMELINE_NEXT", "FOUND LOCAL TWEETS:" + localTweets.size)
                    val lastMaxTweetId = if (localTweets.isEmpty()) sinceId else localTweets.maxBy { tweet -> tweet.id }?.id
                    createUserTimelineRequest(lastMaxTweetId, null)
                            .enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    Log.e("TIMELINE_NEXT", Log.getStackTraceString(it))
                })

    }

    override fun previous(maxId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        convertLocalTweetsToNetworkTweets(
                localTweetDao.getPreviousTweets(userId, maxId ?: Long.MAX_VALUE, maxItemsPerRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()))
                .subscribe({ localTweets ->
                        Log.e("TIMELINE_PREV", "FOUND LOCAL TWEETS:" + localTweets.size)
                        val lastMaxTweetId = if (localTweets.isEmpty()) null else localTweets.maxBy { tweet -> tweet.id }?.id
                        createUserTimelineRequest(lastMaxTweetId, decrementMaxId(maxId))
                                .enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    Log.e("TIMELINE_PREV", Log.getStackTraceString(it))
                })
    }

    private fun convertLocalTweetsToNetworkTweets(maybe: Maybe<List<LocalTweet>>): Maybe<List<Tweet>> {
        return maybe.flatMap({ tweets ->
            Observable.fromIterable(tweets)
                    .map({ Gson().fromJson(it.tweetJsonData, Tweet::class.java) })
                    .toList().toMaybe()
        })
    }

    private fun dispatchSuccessfulTimelineInMainThread(tweets: List<Tweet>, cb: Callback<TimelineResult<Tweet>>) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post({
            TweetsCallback(cb).success(Result(tweets, Response.success(tweets)))
        })
    }

    private fun decrementMaxId(maxId: Long?): Long? {
        return if (maxId == null) null else maxId - 1
    }

    private fun createUserTimelineRequest(sinceId: Long?, maxId: Long?): Call<List<Tweet>> {
        return twitterCore.apiClient.statusesService.userTimeline(userId,
                screenName, maxItemsPerRequest, sinceId, maxId, false, !(includeReplies ?: false),
                null, includeRetweets)
    }

    inner class UserTimelineRequestCallback(private val localTweets: List<Tweet>,
                                            private val cb: Callback<TimelineResult<Tweet>>) : Callback<List<Tweet>>() {

        override fun success(result: Result<List<Tweet>>?) {
            if (result?.data != null) {
                Log.e("TIMELINE", "FOUND NET TWEETS:" + result.data.size)
                Thread({
                    result.data.forEach({
                        val localTweet = LocalTweet(userId, it.id, Gson().toJson(it))
                        localTweetDao.insertTweet(localTweet)
                    })
                    if (result.data.isEmpty()) {
                        dispatchSuccessfulTimelineInMainThread((localTweets).sortedByDescending { tweet -> tweet.id }, cb)
                    } else {
                        dispatchSuccessfulTimelineInMainThread((result.data!!).sortedByDescending { tweet -> tweet.id }, cb)
                    }

                }).start()
            } else {
                dispatchSuccessfulTimelineInMainThread(localTweets, cb)
            }
        }

        override fun failure(exception: TwitterException?) {
            Log.e("TIMELINE", Log.getStackTraceString(exception))
            dispatchSuccessfulTimelineInMainThread(localTweets, cb)
        }
    }

    class Builder {

        private val twitterCore: TwitterCore
        private var userId: Long? = null
        private var screenName: String? = null
        private var maxItemsPerRequest: Int? = 30
        private var includeReplies: Boolean? = null
        private var includeRetweets: Boolean? = null
        private var localTweetDao: LocalTweetDao? = null


        constructor() {
            twitterCore = TwitterCore.getInstance()
        }

        internal constructor(twitterCore: TwitterCore) {
            this.twitterCore = twitterCore
        }

        fun userId(userId: Long?): Builder {
            this.userId = userId
            return this
        }

        fun localTweetDao(localTweetDao: LocalTweetDao): Builder {
            this.localTweetDao = localTweetDao
            return this
        }

        fun screenName(screenName: String): Builder {
            this.screenName = screenName
            return this
        }

        fun maxItemsPerRequest(maxItemsPerRequest: Int?): Builder {
            this.maxItemsPerRequest = maxItemsPerRequest
            return this
        }

        fun includeReplies(includeReplies: Boolean?): Builder {
            this.includeReplies = includeReplies
            return this
        }

        fun includeRetweets(includeRetweets: Boolean?): Builder {
            this.includeRetweets = includeRetweets
            return this
        }


        fun build(): LocalTweetTimeline {
            if (userId == null || maxItemsPerRequest == null || localTweetDao == null) {
                throw IllegalArgumentException("No userId or maxItemsPerRequest specified or dao!")
            }
            return LocalTweetTimeline(twitterCore, userId!!, screenName, maxItemsPerRequest!!,
                    includeReplies, includeRetweets, localTweetDao!!)
        }
    }


    /**
     * Wrapper callback which unpacks a list of Tweets into a TimelineResult (cursor and items).
     */
    internal class TweetsCallback
    /**
     * Constructs a TweetsCallback
     * @param cb A callback which expects a TimelineResult
     */
    (private val cb: Callback<TimelineResult<Tweet>>?) : Callback<List<Tweet>>() {

        override fun success(result: Result<List<Tweet>>) {
            val tweets = result.data
            val minPosition = if (tweets.isNotEmpty()) tweets[tweets.size - 1].getId() else null
            val maxPosition = if (tweets.isNotEmpty()) tweets[0].getId() else null
            val timelineResult = TimelineResult(TimelineCursor(minPosition, maxPosition), tweets)
            cb?.success(Result(timelineResult, result.response))
        }

        override fun failure(exception: TwitterException) {
            cb?.failure(exception)
        }
    }
}