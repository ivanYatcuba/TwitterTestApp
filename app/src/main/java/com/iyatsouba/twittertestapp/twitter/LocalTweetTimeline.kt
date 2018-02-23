package com.iyatsouba.twittertestapp.twitter

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.gson.Gson
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class LocalTweetTimeline(private val userId: Long,
                         private val maxItemsPerRequest: Int,
                         private val tweetRepository: TweetRepository) : Timeline<Tweet> {


    override fun next(sinceId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        convertLocalTweetsToNetworkTweets(tweetRepository.getNextTweets(sinceId ?: -1,
                                                                            maxItemsPerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()))
                .subscribe({ localTweets ->
                    Log.d("TIMELINE_NEXT", "FOUND LOCAL TWEETS:" + localTweets.size)
                    val lastMaxTweetId = if (localTweets.isEmpty()) sinceId else localTweets.maxBy { tweet -> tweet.id }?.id
                    tweetRepository.createUserTimelineRequest(maxItemsPerRequest, lastMaxTweetId, null)
                            ?.enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    Log.d("TIMELINE_NEXT", Log.getStackTraceString(it))
                })

    }

    override fun previous(maxId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        convertLocalTweetsToNetworkTweets(
                tweetRepository.getPreviousTweets(maxId ?: Long.MAX_VALUE, maxItemsPerRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()))
                .subscribe({ localTweets ->
                        Log.d("TIMELINE_PREV", "FOUND LOCAL TWEETS:" + localTweets.size)
                        val lastMaxTweetId = if (localTweets.isEmpty()) null else localTweets.maxBy { tweet -> tweet.id }?.id
                        tweetRepository.createUserTimelineRequest(maxItemsPerRequest, lastMaxTweetId, decrementMaxId(maxId))
                                ?.enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    Log.d("TIMELINE_PREV", Log.getStackTraceString(it))
                })
    }

    private fun convertLocalTweetsToNetworkTweets(maybe: Maybe<List<LocalTweet>>): Maybe<List<Tweet>> {
        return maybe.flatMap({ tweets -> Observable.fromIterable(tweets)
                    .map({ Gson().fromJson(it.tweetJsonData, Tweet::class.java) })
                    .toList().toMaybe()
        })
    }

    private fun dispatchSuccessfulTimelineInMainThread(tweets: List<Tweet>, cb: Callback<TimelineResult<Tweet>>) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post({ TweetsCallback(cb).success(Result(tweets, Response.success(tweets))) })
    }

    private fun decrementMaxId(maxId: Long?): Long? {
        return if (maxId == null) null else maxId - 1
    }


    inner class UserTimelineRequestCallback(private val localTweets: List<Tweet>,
                                            private val cb: Callback<TimelineResult<Tweet>>) : Callback<MutableList<Tweet>>() {

        override fun success(result: Result<MutableList<Tweet>>?) {
            if (result?.data != null) {
                Log.e("TIMELINE", "FOUND NET TWEETS:" + result.data.size)
                Thread({
                    result.data.forEach({
                        val localTweet = LocalTweet(userId, it.id, Gson().toJson(it))
                        tweetRepository.insertTweet(localTweet)
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

        private var userId: Long? = null
        private var maxItemsPerRequest: Int? = 20
        private var tweetRepository: TweetRepository? = null

        fun userId(userId: Long?): Builder {
            this.userId = userId
            return this
        }

        fun tweetRepository(tweetRepository: TweetRepository): Builder {
            this.tweetRepository = tweetRepository
            return this
        }

        fun maxItemsPerRequest(maxItemsPerRequest: Int?): Builder {
            this.maxItemsPerRequest = maxItemsPerRequest
            return this
        }

        fun build(): LocalTweetTimeline {
            return LocalTweetTimeline(userId!!,  maxItemsPerRequest!!, tweetRepository!!)
        }
    }

    /**
     * TimelineCursor represents the position and containsLastItem data from a Timeline response.
     */
    class TimelineCursor(minPosition: Long?, maxPosition: Long?) : com.twitter.sdk.android.tweetui.TimelineCursor(minPosition, maxPosition)

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