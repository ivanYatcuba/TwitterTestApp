package com.iyatsouba.twittertestapp.db.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.iyatsouba.twittertestapp.db.model.LocalTweet
import io.reactivex.Maybe

@Dao
interface LocalTweetDao {

    @Query("select * from tweets where userId = :userId AND tweeterId < :maxId ORDER BY tweeterId DESC  LIMIT :limit")
    fun getPreviousTweets(userId: Long, maxId: Long, limit: Int): Maybe<List<LocalTweet>>

    @Query("select * from tweets where userId = :userId AND tweeterId > :sinceId ORDER BY tweeterId DESC LIMIT :limit")
    fun getNextTweets(userId: Long, sinceId: Long, limit: Int): Maybe<List<LocalTweet>>

    @Insert(onConflict = REPLACE)
    fun insertTweet(tweet: LocalTweet): Long

    @Update(onConflict = REPLACE)
    fun updateTweet(tweet: LocalTweet): Int

    @Delete
    fun deleteTweet(tweet: LocalTweet): Int

}