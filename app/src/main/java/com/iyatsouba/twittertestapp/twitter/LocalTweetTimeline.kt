package com.iyatsouba.twittertestapp.twitter

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import com.iyatsouba.twittertestapp.repository.DataLoadingState
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.rx.SchedulersFacade
import com.jakewharton.rxrelay2.BehaviorRelay
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.Timeline
import com.twitter.sdk.android.tweetui.TimelineResult
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import retrofit2.Response

class LocalTweetTimeline(internal var userId: Long,
                         internal var maxItemsPerRequest: Int,
                         internal var tweetRepository: TweetRepository,
                         internal var schedulersFacade: SchedulersFacade) : Timeline<Tweet> {

    private val networkStateRelay = BehaviorRelay.create<DataLoadingState>()
    private val networkStateRelayObservable = networkStateRelay.share()

    fun subscribeForLoadingState(consumer: Consumer<DataLoadingState>): Disposable {
        return networkStateRelayObservable.subscribe(consumer)
    }

    override fun next(sinceId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        networkStateRelay?.accept(DataLoadingState.IN_PROGRESS)
        convertLocalTweetsToNetworkTweets(tweetRepository.getNextTweets(sinceId ?: -1,
                                                                            maxItemsPerRequest)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.io()))
                .subscribe({ localTweets ->
                    val lastMaxTweetId = if (localTweets.isEmpty())
                        sinceId else localTweets.maxBy { tweet -> tweet.id }?.id
                    tweetRepository.createUserTimelineRequest(maxItemsPerRequest, lastMaxTweetId, null)
                            ?.enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    dispatchErrorOnMainThread()
                })

    }

    override fun previous(maxId: Long?, cb: Callback<TimelineResult<Tweet>>) {
        networkStateRelay?.accept(DataLoadingState.IN_PROGRESS)
        convertLocalTweetsToNetworkTweets(
                tweetRepository.getPreviousTweets(maxId ?: Long.MAX_VALUE, maxItemsPerRequest)
                        .subscribeOn(schedulersFacade.io())
                        .observeOn(schedulersFacade.io()))
                        .subscribe({ localTweets ->
                            val lastMaxTweetId = if (localTweets.isEmpty()) null
                                else localTweets.maxBy { tweet -> tweet.id }?.id
                            tweetRepository.createUserTimelineRequest(maxItemsPerRequest,
                                    lastMaxTweetId, decrementMaxId(maxId))
                                    ?.enqueue(UserTimelineRequestCallback(localTweets, cb))
                }, {
                    dispatchErrorOnMainThread()
                })
    }

    private fun convertLocalTweetsToNetworkTweets(maybe: Maybe<List<LocalTweet>>): Maybe<List<Tweet>> {
        return maybe.flatMap({ tweets -> Observable.fromIterable(tweets)
                    .map({ Gson().fromJson(it.tweetJsonData, Tweet::class.java) })
                    .toList().toMaybe()
        })
    }

    private fun dispatchErrorOnMainThread() {
        networkStateRelay?.accept(DataLoadingState.ERROR)
    }

    internal fun dispatchSuccessfulTimelineInMainThread(tweets: List<Tweet>,
                                                       cb: Callback<TimelineResult<Tweet>>) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post({
            TweetsCallback(cb).success(Result(tweets, Response.success(tweets)))
            networkStateRelay?.accept(DataLoadingState.SUCCESS)
        })
    }

    private fun decrementMaxId(maxId: Long?): Long? {
        return if (maxId == null) null else maxId - 1
    }


    inner class UserTimelineRequestCallback(private val localTweets: List<Tweet>,
                                            private val cb: Callback<TimelineResult<Tweet>>) : Callback<MutableList<Tweet>>() {

        override fun success(result: Result<MutableList<Tweet>>?) {
            if (result?.data != null) {
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
            dispatchSuccessfulTimelineInMainThread(localTweets, cb)
        }
    }

    class Builder {

        private var userId: Long? = null
        private var maxItemsPerRequest: Int? = 20
        private var tweetRepository: TweetRepository? = null
        private var schedulersFacade: SchedulersFacade? = null

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

        fun schedulerFacade(schedulersFacade: SchedulersFacade?): Builder {
            this.schedulersFacade = schedulersFacade
            return this
        }

        fun build(): LocalTweetTimeline {
            return LocalTweetTimeline(userId!!,  maxItemsPerRequest!!, tweetRepository!!, schedulersFacade!!)
        }
    }

    /**
     * TimelineCursor represents the position and containsLastItem data from a Timeline response.
     */
    class TimelineCursor(minPosition: Long?, maxPosition: Long?) : com.twitter.sdk.android.tweetui.TimelineCursor(minPosition, maxPosition)

    /**
     * Wrapper callback which unpacks a list of Tweets into a TimelineResult (cursor and items).
     */
    internal inner class TweetsCallback (private val cb: Callback<TimelineResult<Tweet>>?) : Callback<List<Tweet>>() {

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