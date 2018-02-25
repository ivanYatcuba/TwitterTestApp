package com.iyatsouba.twittertestapp.twitter

import com.iyatsouba.twittertestapp.db.dao.LocalTweetDao
import com.iyatsouba.twittertestapp.repository.TweetRepository
import com.iyatsouba.twittertestapp.rx.SchedulersFacade
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.services.StatusesService
import com.twitter.sdk.android.tweetui.TimelineResult
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*


class LocalTweetTimelineTest {

    companion object {
        private val TEST_ITEMS_PER_REQUEST = 100
        private val TEST_SINCE_ID = 1000L
        private val TEST_MAX_ID = 1111L
    }

    var tweetRepository: TweetRepository? = null
    var localTweetDao: LocalTweetDao? = null
    var tweeterHelper: TwitterHelper? = null
    var apiClient: TwitterApiClient? = null
    var statusesService: StatusesService? = null
    var schedulersFacade: SchedulersFacade? = null

    @Before
    fun setUp() {
        localTweetDao = mock<LocalTweetDao>(LocalTweetDao::class.java)
        tweeterHelper = mock<TwitterHelper>(TwitterHelper::class.java)

        apiClient = mock(TwitterApiClient::class.java)
        statusesService = mock(StatusesService::class.java, MockCallAnswer())

        schedulersFacade = mock(SchedulersFacade::class.java)

        `when`(apiClient!!.statusesService).thenReturn(statusesService)
        `when`(localTweetDao!!.getPreviousTweets(TestFixtures.TEST_USER.id, TEST_MAX_ID,
                TEST_ITEMS_PER_REQUEST)).thenReturn(Maybe.just(TestFixtures.getLocalTweetList(10)))
        `when`(localTweetDao!!.getNextTweets(TestFixtures.TEST_USER.id, TEST_SINCE_ID,
                TEST_ITEMS_PER_REQUEST)).thenReturn(Maybe.just(TestFixtures.getLocalTweetList(10)))
        `when`(tweeterHelper!!.getApiClient()).thenReturn(apiClient)
        `when`(tweeterHelper!!.getUserId()).thenReturn(TestFixtures.TEST_USER.id)
        `when`(schedulersFacade!!.io()).thenReturn(Schedulers.trampoline())

        tweetRepository = TweetRepository(tweeterHelper!!, localTweetDao!!, schedulersFacade!!)
    }

    @Test
    fun testConstructor() {
        val timeline = LocalTweetTimeline(TestFixtures.TEST_USER.id,
                TEST_ITEMS_PER_REQUEST, tweetRepository!!, schedulersFacade!!)
        assertEquals(TestFixtures.TEST_USER.id, timeline.userId)
        assertEquals(tweetRepository, timeline.tweetRepository)
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest)

    }

    @Test
    fun testNext_createsCorrectRequest() {
        tweetRepository = spy(tweetRepository)
        val timeline = LocalTweetTimeline(TestFixtures.TEST_USER.id,
                TEST_ITEMS_PER_REQUEST, tweetRepository!!, schedulersFacade!!)

        timeline.next(TEST_SINCE_ID, mock(Callback::class.java) as Callback<TimelineResult<Tweet>>)

        verify(tweetRepository, times(1))?.getNextTweets(ArgumentMatchers.eq(TEST_SINCE_ID),
                ArgumentMatchers.eq(TEST_ITEMS_PER_REQUEST))
        verify(tweetRepository, times(1))?.createUserTimelineRequest(ArgumentMatchers.eq(TEST_ITEMS_PER_REQUEST), ArgumentMatchers.eq(TEST_SINCE_ID),
                ArgumentMatchers.isNull())
    }

    @Test
    fun testPrevious_createsCorrectRequest() {
        tweetRepository = spy(tweetRepository)
        val timeline = LocalTweetTimeline(TestFixtures.TEST_USER.id,
                TEST_ITEMS_PER_REQUEST, tweetRepository!!, schedulersFacade!!)

        val callback = mock(Callback::class.java) as Callback<TimelineResult<Tweet>>

        timeline.previous(TEST_MAX_ID, callback)

        verify(tweetRepository, times(1))?.getPreviousTweets(ArgumentMatchers.eq(TEST_MAX_ID),
                ArgumentMatchers.eq(TEST_ITEMS_PER_REQUEST))
        verify(tweetRepository, times(1))?.createUserTimelineRequest(ArgumentMatchers.eq(TEST_ITEMS_PER_REQUEST), ArgumentMatchers.isNull(),
                ArgumentMatchers.eq(TEST_MAX_ID - 1))
    }

    /* Builder */
    @Test
    fun testBuilder() {
        val timeline = LocalTweetTimeline.Builder()
                .userId(TestFixtures.TEST_USER.id)
                .maxItemsPerRequest(TEST_ITEMS_PER_REQUEST)
                .tweetRepository(tweetRepository!!)
                .schedulerFacade(schedulersFacade)
                .build()
        assertEquals(TestFixtures.TEST_USER.id, timeline.userId)
        assertEquals(tweetRepository, timeline.tweetRepository)
        assertEquals(schedulersFacade, timeline.schedulersFacade)
        assertEquals(TEST_ITEMS_PER_REQUEST, timeline.maxItemsPerRequest)
    }
}