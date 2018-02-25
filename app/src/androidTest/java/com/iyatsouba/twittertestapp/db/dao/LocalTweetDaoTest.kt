package com.iyatsouba.twittertestapp.db.dao

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.iyatsouba.twittertestapp.db.TwitterTestAppDatabse
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class LocalTweetDaoTest {

    private val userId = 12L
    private val tweetId = 101L
    private val dumpJson = "{}"

    private lateinit var twitterTestAppDatabase: TwitterTestAppDatabse

    @Before
    fun initDb() {
        twitterTestAppDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                TwitterTestAppDatabse::class.java).build()
    }

    @After
    fun closeDb() {
        twitterTestAppDatabase.close()
    }

    @Test
    fun insertTweetTest() {
        val testTweet = LocalTweet(userId, tweetId, dumpJson)
        val insertedId = twitterTestAppDatabase.localTweetDao().insertTweet(testTweet)

        Assert.assertEquals(tweetId, insertedId)
    }

    @Test
    fun updateTweetTest() {
        val testTweet = LocalTweet(userId, tweetId, dumpJson)
        twitterTestAppDatabase.localTweetDao().insertTweet(testTweet)
        val updateCount = twitterTestAppDatabase.localTweetDao().updateTweet(LocalTweet(userId, tweetId, "{test}"))

        Assert.assertEquals(1, updateCount)
    }

    @Test
    fun removeTweetTest() {
        val testTweet = LocalTweet(userId, tweetId, dumpJson)
        twitterTestAppDatabase.localTweetDao().insertTweet(testTweet)
        val removedCount = twitterTestAppDatabase.localTweetDao().deleteTweet(testTweet)

        Assert.assertEquals(1, removedCount)
    }

    @Test
    fun getNextTweetsTest() {
        var tweetIdInc = tweetId
        var testTweet = LocalTweet(userId, tweetId, dumpJson)
        while (tweetIdInc < 111) {
            twitterTestAppDatabase.localTweetDao().insertTweet(testTweet)
            tweetIdInc++
            testTweet = LocalTweet(userId, tweetIdInc, dumpJson)
        }

        val nextTweets = twitterTestAppDatabase.localTweetDao()
                .getNextTweets(userId, 103, 3)
                .blockingGet()

        Assert.assertEquals(3, nextTweets.size)
        nextTweets.sortedByDescending { userId }
        Assert.assertEquals(listOf(LocalTweet(userId, 110, dumpJson),
                LocalTweet(userId, 109, dumpJson),
                LocalTweet(userId, 108, dumpJson)), nextTweets)
    }

    @Test
    fun getPreviousTweetsTest() {
        var tweetIdInc = tweetId
        var testTweet = LocalTweet(userId, tweetId, dumpJson)
        while (tweetIdInc < 111) {
            twitterTestAppDatabase.localTweetDao().insertTweet(testTweet)
            tweetIdInc++
            testTweet = LocalTweet(userId, tweetIdInc, dumpJson)
        }

        val previousTweets = twitterTestAppDatabase.localTweetDao()
                .getPreviousTweets(userId, 110, 3)
                .blockingGet()

        Assert.assertEquals(3, previousTweets.size)
        previousTweets.sortedByDescending { userId }
        Assert.assertEquals(listOf(LocalTweet(userId, 109, dumpJson),
                LocalTweet(userId, 108, dumpJson),
                LocalTweet(userId, 107, dumpJson)), previousTweets)
    }


}